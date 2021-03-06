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
package schemacrawler.tools.lint;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.NamedObject;
import sf.util.ObjectToString;

public final class Lint<V extends Serializable>
  implements Serializable, Comparable<Lint<? extends Serializable>>
{

  private static final long serialVersionUID = -8627082144974643415L;

  private final String lintId;
  private final String linterId;
  private final String linterInstanceId;
  private final String objectName;
  private final LintSeverity severity;
  private final String message;
  private final V value;

  public <N extends NamedObject & AttributedObject> Lint(final String linterId,
                                                         final String linterInstanceId,
                                                         final N namedObject,
                                                         final LintSeverity severity,
                                                         final String message,
                                                         final V value)
  {
    lintId = UUID.randomUUID().toString();

    if (isBlank(linterId))
    {
      throw new IllegalArgumentException("Linter id not provided");
    }
    this.linterId = linterId;

    if (isBlank(linterInstanceId))
    {
      throw new IllegalArgumentException("Linter instance id not provided");
    }
    this.linterInstanceId = linterInstanceId;

    requireNonNull(namedObject, "Named object not provided");
    this.objectName = namedObject.getFullName();

    if (severity == null)
    {
      this.severity = LintSeverity.critical;
    }
    else
    {
      this.severity = severity;
    }

    if (isBlank(message))
    {
      throw new IllegalArgumentException("Lint message not provided");
    }
    this.message = message;

    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int compareTo(final Lint<?> lint)
  {
    if (lint == null)
    {
      return -1;
    }

    int compareTo = 0;
    compareTo = objectName.compareTo(lint.getObjectName());
    if (compareTo != 0)
    {
      return compareTo;
    }
    compareTo = severity.compareTo(lint.getSeverity());
    compareTo *= -1; // Reverse
    if (compareTo != 0)
    {
      return compareTo;
    }
    compareTo = linterId.compareTo(lint.getLinterId());
    if (compareTo != 0)
    {
      return compareTo;
    }
    compareTo = message.compareTo(lint.getMessage());

    return compareTo;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof Lint))
    {
      return false;
    }
    final Lint<?> other = (Lint<?>) obj;
    if (linterId == null)
    {
      if (other.linterId != null)
      {
        return false;
      }
    }
    else if (!linterId.equals(other.linterId))
    {
      return false;
    }
    if (message == null)
    {
      if (other.message != null)
      {
        return false;
      }
    }
    else if (!message.equals(other.message))
    {
      return false;
    }
    if (objectName == null)
    {
      if (other.objectName != null)
      {
        return false;
      }
    }
    else if (!objectName.equals(other.objectName))
    {
      return false;
    }
    if (severity != other.severity)
    {
      return false;
    }
    if (value == null)
    {
      if (other.value != null)
      {
        return false;
      }
    }
    else if (!value.equals(other.value))
    {
      return false;
    }
    return true;
  }

  public String getLinterId()
  {
    return linterId;
  }

  public String getLinterInstanceId()
  {
    return linterInstanceId;
  }

  public String getLintId()
  {
    return lintId;
  }

  public String getMessage()
  {
    return message;
  }

  public String getObjectName()
  {
    return objectName;
  }

  public LintSeverity getSeverity()
  {
    return severity;
  }

  public V getValue()
  {
    return value;
  }

  public String getValueAsString()
  {
    if (value != null)
    {
      final Class<? extends Object> valueClass = value.getClass();
      Object valueObject = value;

      if (valueClass.isArray()
          && NamedObject.class.isAssignableFrom(valueClass.getComponentType()))
      {
        valueObject = Arrays.asList(Arrays.copyOf((Object[]) value,
                                                  ((Object[]) value).length,
                                                  NamedObject[].class));
      }

      if (NamedObject.class.isAssignableFrom(valueClass))
      {
        valueObject = ((NamedObject) valueObject).getName();
      }
      else if (Iterable.class.isAssignableFrom(valueObject.getClass()))
      {
        final List<String> list = new ArrayList<>();
        for (final Object valuePart: (Iterable<?>) valueObject)
        {
          if (valuePart instanceof NamedObject)
          {
            list.add(((NamedObject) valuePart).getName());
          }
          else
          {
            list.add(valuePart.toString());
          }
        }
        valueObject = list;
      }
      else
      {
        valueObject = value;
      }
      return ObjectToString.toString(valueObject);
    }
    else
    {
      return "";
    }
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (linterId == null? 0: linterId.hashCode());
    result = prime * result + (message == null? 0: message.hashCode());
    result = prime * result + (objectName == null? 0: objectName.hashCode());
    result = prime * result + (severity == null? 0: severity.hashCode());
    result = prime * result + (value == null? 0: value.hashCode());
    return result;
  }

  public boolean hasValue()
  {
    return value == null;
  }

  @Override
  public String toString()
  {
    final String valueString;
    if (value != null && !(value instanceof Boolean))
    {
      valueString = ": " + getValueAsString();
    }
    else
    {
      valueString = "";
    }
    return String.format("[%s] %s%s", objectName, message, valueString);
  }

}
