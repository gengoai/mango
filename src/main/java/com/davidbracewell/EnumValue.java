/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>A value in a {@link DynamicEnum}</p>
 *
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public abstract class EnumValue implements Tag, Serializable, Comparable<EnumValue> {

  private static final long serialVersionUID = 1L;
  private final String name;

  /**
   * Instantiates a new Enum value.
   *
   * @param name the name of the enum value
   */
  protected EnumValue(String name) {
    this.name = DynamicEnum.normalize(name);
  }


  @Override
  public String name() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(EnumValue o) {
    return o == null ? 1 : name.compareTo(o.name);
  }

  @Override
  public boolean isInstance(Tag value) {
    return value != null && this.equals(value);
  }

}//END OF EnumValue
