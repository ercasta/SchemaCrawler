package schemacrawler.tools.integration.xml;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;

import com.thoughtworks.xstream.XStream;

/**
 * Decorates a database to allow for serialization to and from XML.
 * 
 * @author sfatehi
 */
public final class XmlDatabase
  implements Database
{

  private static final long serialVersionUID = 5314326260124511414L;

  private final Database database;

  public XmlDatabase(final Database database)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;
  }

  public XmlDatabase(final Reader reader)
  {
    this((Database) new XStream().fromXML(reader));
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final NamedObject o)
  {
    return database.compareTo(o);
  }

  @Override
  public boolean equals(final Object obj)
  {
    return database.equals(obj);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(final String name)
  {
    return database.getAttribute(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getAttributes()
   */
  public Map<String, Object> getAttributes()
  {
    return database.getAttributes();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getDatabaseInfo()
   */
  public DatabaseInfo getDatabaseInfo()
  {
    return database.getDatabaseInfo();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getFullName()
   */
  public String getFullName()
  {
    return database.getFullName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getJdbcDriverInfo()
   */
  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return database.getJdbcDriverInfo();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getName()
   */
  public String getName()
  {
    return database.getName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getRemarks()
   */
  public String getRemarks()
  {
    return database.getRemarks();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchema(java.lang.String)
   */
  public Schema getSchema(final String schemaName)
  {
    return database.getSchema(schemaName);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchemaCrawlerInfo()
   */
  public SchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return database.getSchemaCrawlerInfo();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchemas()
   */
  public Schema[] getSchemas()
  {
    return database.getSchemas();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataType(java.lang.String)
   */
  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return database.getSystemColumnDataType(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataTypes()
   */
  public ColumnDataType[] getSystemColumnDataTypes()
  {
    return database.getSystemColumnDataTypes();
  }

  @Override
  public int hashCode()
  {
    return database.hashCode();
  }

  /**
   * Serializes the database to the writer, as XML.
   */
  public void save(final Writer writer)
    throws SchemaCrawlerException
  {
    if (writer == null)
    {
      throw new SchemaCrawlerException("Writer not provided");
    }
    final XStream xStream = new XStream();
    xStream.toXML(database, writer);
    try
    {
      writer.flush();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not flush writer", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#setAttribute(java.lang.String,
   *      java.lang.Object)
   */
  public void setAttribute(final String name, final Object value)
  {
    database.setAttribute(name, value);
  }

}