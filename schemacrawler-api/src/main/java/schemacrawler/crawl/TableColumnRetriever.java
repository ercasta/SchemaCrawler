/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import static sf.util.DatabaseUtility.executeSql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.Column;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerSQLException;
import sf.util.StringFormat;

/**
 * A retriever uses database metadata to get the details about the
 * database table columns.
 *
 * @author Sualeh Fatehi
 */
final class TableColumnRetriever
  extends AbstractRetriever
{

  private static final Logger LOGGER = Logger
    .getLogger(TableColumnRetriever.class.getName());

  TableColumnRetriever(final RetrieverConnection retrieverConnection,
                       final MutableCatalog catalog)
    throws SQLException
  {
    super(retrieverConnection, catalog);
  }

  void retrieveColumns(final NamedObjectList<MutableTable> allTables,
                       final InclusionRule columnInclusionRule)
    throws SQLException
  {
    requireNonNull(allTables);

    final InclusionRuleFilter<Column> columnFilter = new InclusionRuleFilter<>(columnInclusionRule,
                                                                               true);
    if (columnFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving columns, since this was not requested");
      return;
    }

    final MetadataRetrievalStrategy tableColumnRetrievalStrategy = getRetrieverConnection()
      .getTableColumnRetrievalStrategy();
    switch (tableColumnRetrievalStrategy)
    {
      case data_dictionary_all:
        LOGGER
          .log(Level.INFO,
               "Retrieving table columns, using fast data dictionary retrieval");
        retrieveColumnsFromDataDictionary(allTables, columnFilter);
        break;

      case metadata_all:
        LOGGER.log(Level.INFO,
                   "Retrieving table columns, using fast meta-data retrieval");
        retrieveColumnsFromMetadataForAllTables(allTables, columnFilter);
        break;

      case metadata:
        LOGGER.log(Level.INFO, "Retrieving table columns");
        retrieveColumnsFromMetadata(allTables, columnFilter);
        break;

      default:
        break;
    }

  }

  void retrieveHiddenColumns(final NamedObjectList<MutableTable> allTables,
                             final InclusionRule columnInclusionRule)
    throws SQLException
  {
    requireNonNull(allTables);

    final InclusionRuleFilter<Column> columnFilter = new InclusionRuleFilter<>(columnInclusionRule,
                                                                               true);
    if (columnFilter.isExcludeAll())
    {
      LOGGER.log(Level.INFO,
                 "Not retrieving columns, since this was not requested");
      return;
    }

    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasExtHiddenTableColumnsSql())
    {
      LOGGER.log(Level.INFO, "No hidden table columns SQL provided");
      return;
    }
    final String hiddenColumnsSql = informationSchemaViews
      .getExtHiddenTableColumnsSql();
    LOGGER.log(Level.FINER,
               new StringFormat("Executing SQL to retrieve hidden table columns: %n%s",
                                hiddenColumnsSql));
    final Connection connection = getDatabaseConnection();
    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet("retrieveHiddenColumns",
                                                                executeSql(statement,
                                                                           hiddenColumnsSql));)
    {
      while (results.next())
      {
        final MutableColumn column = createTableColumn(results,
                                                       allTables,
                                                       columnFilter);
        if (column != null)
        {
          column.setHidden(true);
        }
      }
    }
  }

  private MutableColumn createTableColumn(final MetadataResultSet results,
                                          final NamedObjectList<MutableTable> allTables,
                                          final InclusionRuleFilter<Column> columnFilter)
    throws SQLException
  {
    // Get the "COLUMN_DEF" value first as it the Oracle drivers
    // don't handle it properly otherwise.
    // https://community.oracle.com/message/5940745#5940745
    // NOTE: Still an issue with Oracle JDBC driver 11.2.0.3.0
    final String defaultValue = results.getString("COLUMN_DEF");
    //

    final String columnCatalogName = quotedName(results.getString("TABLE_CAT"));
    final String schemaName = quotedName(results.getString("TABLE_SCHEM"));
    final String tableName = quotedName(results.getString("TABLE_NAME"));
    final String columnName = quotedName(results.getString("COLUMN_NAME"));
    LOGGER.log(Level.FINER,
               new StringFormat("Retrieving column, %s.%s%s.%s",
                                columnCatalogName,
                                schemaName,
                                tableName,
                                columnName));

    final Optional<MutableTable> optionalTable = allTables
      .lookup(new SchemaReference(columnCatalogName, schemaName), tableName);
    if (!optionalTable.isPresent())
    {
      return null;
    }

    final MutableTable table = optionalTable.get();
    MutableColumn column;

    column = lookupOrCreateColumn(table, columnName, /* add? */false);
    if (columnFilter.test(column)
        && belongsToSchema(table, columnCatalogName, schemaName))
    {
      column = lookupOrCreateColumn(table, columnName, /* add? */true);

      final int ordinalPosition = results.getInt("ORDINAL_POSITION", 0);
      final int dataType = results.getInt("DATA_TYPE", 0);
      final String typeName = results.getString("TYPE_NAME");
      final int size = results.getInt("COLUMN_SIZE", 0);
      final int decimalDigits = results.getInt("DECIMAL_DIGITS", 0);
      final boolean isNullable = results
        .getInt("NULLABLE",
                DatabaseMetaData.columnNullableUnknown) == DatabaseMetaData.columnNullable;
      final boolean isAutoIncremented = results.getBoolean("IS_AUTOINCREMENT");
      final boolean isGenerated = results.getBoolean("IS_GENERATEDCOLUMN");
      final String remarks = results.getString("REMARKS");

      column.setOrdinalPosition(ordinalPosition);
      column
        .setColumnDataType(lookupOrCreateColumnDataType(table.getSchema(),
                                                        dataType,
                                                        typeName));
      column.setSize(size);
      column.setDecimalDigits(decimalDigits);
      column.setNullable(isNullable);
      column.setAutoIncremented(isAutoIncremented);
      column.setGenerated(isGenerated);
      column.setRemarks(remarks);
      if (defaultValue != null)
      {
        column.setDefaultValue(defaultValue);
      }

      column.addAttributes(results.getAttributes());

      table.addColumn(column);
    }

    return column;
  }

  private MutableColumn lookupOrCreateColumn(final MutableTable table,
                                             final String columnName,
                                             final boolean add)
  {
    final Optional<MutableColumn> columnOptional = table
      .lookupColumn(columnName);
    final MutableColumn column;
    if (columnOptional.isPresent())
    {
      column = columnOptional.get();
    }
    else
    {
      column = new MutableColumn(table, columnName);
      if (add)
      {
        LOGGER.log(Level.FINER,
                   new StringFormat("Adding column to table, %s",
                                    column.getFullName()));
        table.addColumn(column);
      }
    }
    return column;
  }

  private void retrieveColumnsFromDataDictionary(final NamedObjectList<MutableTable> allTables,
                                                 final InclusionRuleFilter<Column> columnFilter)
    throws SchemaCrawlerSQLException, SQLException
  {
    final InformationSchemaViews informationSchemaViews = getRetrieverConnection()
      .getInformationSchemaViews();
    if (!informationSchemaViews.hasTableColumnsSql())
    {
      throw new SchemaCrawlerSQLException("No table columns SQL provided",
                                          null);
    }
    final String tableColumnsSql = informationSchemaViews.getTableColumnsSql();
    LOGGER.log(Level.FINER,
               new StringFormat("Executing SQL to retrieve table columns: %n%s",
                                tableColumnsSql));
    final Connection connection = getDatabaseConnection();
    try (final Statement statement = connection.createStatement();
        final MetadataResultSet results = new MetadataResultSet("retrieveColumnsFromDataDictionary",
                                                                executeSql(statement,
                                                                           tableColumnsSql));)
    {
      while (results.next())
      {
        createTableColumn(results, allTables, columnFilter);
      }
    }
  }

  private void retrieveColumnsFromMetadata(final NamedObjectList<MutableTable> allTables,
                                           final InclusionRuleFilter<Column> columnFilter)
    throws SchemaCrawlerSQLException
  {
    for (final MutableTable table: allTables)
    {
      LOGGER.log(Level.FINE, "Retrieving columns for " + table);
      try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
        .getColumns(unquotedName(table.getSchema().getCatalogName()),
                    unquotedName(table.getSchema().getName()),
                    unquotedName(table.getName()),
                    null));)
      {
        while (results.next())
        {
          createTableColumn(results, allTables, columnFilter);
        }
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerSQLException("Could not retrieve columns for table "
                                            + table, e);
      }
    }
  }

  private void retrieveColumnsFromMetadataForAllTables(final NamedObjectList<MutableTable> allTables,
                                                       final InclusionRuleFilter<Column> columnFilter)
    throws SQLException
  {
    try (final MetadataResultSet results = new MetadataResultSet(getMetaData()
      .getColumns(null,
                  null,
                  "%",
                  "%"));)
    {
      while (results.next())
      {
        createTableColumn(results, allTables, columnFilter);
      }
    }
  }

}
