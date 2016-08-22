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
 * <p>A enum value associated with a {@link DynamicEnum}. Standard usage is for enum types to to extend
 * {@link EnumValue} and have a static <code>DynamicEnum</code> field in the extended class. </p>
 *
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public abstract class EnumValue implements Tag, Serializable, Comparable<EnumValue> {
  private static final long serialVersionUID = 1L;
  private final String name;
  private final String fullName;

  /**
   * Instantiates a new Enum value.
   *
   * @param name the name of the enum value
   */
  protected EnumValue(String name) {
    this.name = DynamicEnum.normalize(name);
    this.fullName = getClass().getSimpleName() + "." + name;
  }

  @Override
  public String name() {
    return name;
  }

  public String fullName() {
    return fullName;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(EnumValue o) {
    if (o == null) {
      return -1;
    }
    return this.fullName.compareTo(o.fullName);
  }

  @Override
  public boolean isInstance(Tag value) {
    return value != null && this.equals(value);
  }

}//END OF EnumValue
