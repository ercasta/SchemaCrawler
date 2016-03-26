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
package schemacrawler.test;


import org.junit.Ignore;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import schemacrawler.schema.JavaSqlType;
import sf.util.graph.DirectedEdge;
import sf.util.graph.Vertex;

public class SchemaEqualsHashCodeTest
{
  @Test
  public void equalsContract1()
  {
    EqualsVerifier.forClass(JavaSqlType.class).verify();
  }

  @Ignore
  @Test
  public void equalsContract2()
  {
    EqualsVerifier.forClass(Vertex.class).suppress(Warning.NULL_FIELDS)
      .verify();
    EqualsVerifier.forClass(DirectedEdge.class).suppress(Warning.NULL_FIELDS)
      .verify();
  }

}