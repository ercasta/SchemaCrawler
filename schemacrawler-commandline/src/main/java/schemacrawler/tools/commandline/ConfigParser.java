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

package schemacrawler.tools.commandline;


import java.io.IOException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public class ConfigParser
  extends BaseConfigOptionsParser
{

  public ConfigParser(final Config config)
  {
    super(config);
    normalizeOptionName("configfile", "g");
    normalizeOptionName("additionalconfigfile", "p");
  }

  @Override
  public void loadConfig()
    throws SchemaCrawlerException
  {
    try
    {
      final String configfile = config
        .getStringValue("configfile", "schemacrawler.config.properties");
      final String additionalconfigfile = config
        .getStringValue("additionalconfigfile",
                        "schemacrawler.additional.config.properties");
      config.putAll(Config.load(configfile, additionalconfigfile));

      consumeOption("configfile");
      consumeOption("additionalconfigfile");
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not load config files", e);
    }
  }

}
