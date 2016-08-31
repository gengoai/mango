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

import com.davidbracewell.config.Config;

import java.util.List;

/**
 * <p>An {@link EnumValue} which has a tree-like structure. Note, that while individual enum values only have one
 * parent, there may be multiple root values.</p>
 *
 * @author David B. Bracewell
 */
public abstract class HierarchicalEnumValue extends EnumValue {
  private static final long serialVersionUID = 1L;
  protected volatile HierarchicalEnumValue parent = null;

  /**
   * Instantiates a new Hierarchical enum value.
   *
   * @param name   the name
   * @param parent the parent
   */
  protected HierarchicalEnumValue(String name, HierarchicalEnumValue parent) {
    super(name);
    this.parent = parent;
  }

  /**
   * Determines if this enum is the root
   *
   * @return True if it is the root, False otherwise
   */
  public boolean isRoot() {
    return getParent() == null;
  }


  /**
   * Gets children.
   *
   * @param <T> the type parameter
   * @return the children
   */
  public abstract <T extends HierarchicalEnumValue> List<T> getChildren();

  /**
   * Is leaf boolean.
   *
   * @return the boolean
   */
  public boolean isLeaf() {
    return getChildren().isEmpty();
  }

  /**
   * Gets parent.
   *
   * @return the parent
   */
  public HierarchicalEnumValue getParent() {
    if (parent == null) {
      synchronized (this) {
        if (parent == null) {
          HierarchicalEnumValue ev = getParentConfig();
          if (ev != null) {
            parent = ev;
          }
          return ev;
        }
      }
    }
    return parent;
  }

  @Override
  public final boolean isInstance(Tag value) {
    if (value == null) {
      return false;
    }
    HierarchicalEnumValue hev = this;
    while (hev != null && hev != hev.getParent()) {
      if (hev.equals(value)) {
        return true;
      }
      hev = hev.getParent();
    }
    return false;
  }

  /**
   * Determines the parent via a configuration setting.
   *
   * @return the parent config
   */
  protected HierarchicalEnumValue getParentConfig() {
    return Config.get(canonicalName(), "parent").as(getClass(), null);
  }


}//END OF HierarchicalEnumValue
