/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.schema;


import java.io.Serializable;
import java.time.LocalDateTime;

public interface CrawlInfo
  extends Serializable
{

  /**
   * Gets the timestamp of when the database was crawled.
   *
   * @return Timestamp
   */
  LocalDateTime getCrawlTimestamp();

  /**
   * Gets the name of the RDBMS vendor and product.
   *
   * @return Name of the RDBMS vendor and product
   */
  String getDatabaseInfo();

  /**
   * Gets the name of the JDBC driver.
   *
   * @return Driver name
   */
  String getJdbcDriverInfo();

  /**
   * Gets the SchemaCrawler version.
   *
   * @return SchemaCrawler version
   */
  String getSchemaCrawlerInfo();

  /**
   * Gets the title.
   *
   * @return Title
   */
  String getTitle();

}