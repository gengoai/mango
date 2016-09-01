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
import com.davidbracewell.conversion.Cast;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>A enum like object that can have elements created at runtime as needed and which have a parent associated with
 * them. As with EnumValues, elements are singleton objects and can have their equality safely checked using the
 * <code>==</code> operator. Hierarchical enums may have multiple roots, but each element may only have one parent.
 * Their implementation of {@link Tag#isInstance(Tag)} returns true if the element is equal to or a descendant of the
 * tag being compared against. Elements can have their parents assigned at later time as long up until a non-null
 * parent
 * has been set.</p>
 *
 * <p>The python script in the mango tools directory (<code>tools/enumGen.py</code>) bootstraps the creation of basic
 * HierarchicalEnumValue. As with enum values the names associated with EnumValues are normalized to be uppercase and
 * have all whitespace replaced by underscores with consecutive whitespace becoming a  single underscore.</p>
 *
 * <p>Examples of common usage patterns for HierarchicalEnumValue types generated using <code>tools/enumGen.py</code>
 * are as
 * follows:</p>
 *
 * <pre>
 * {@code
 *    //Enum values can be retrieved or created using the create method.
 *    MyEnum animal = MyEnum.create("animal");
 *    MyEnum dog = MyEnum.create("dog", animal);
 *    MyEnum pug = MyEnum.create("pug", dog);
 *
 *    MyEnum thing = MyEnum.create("thing");
 *
 *    //Now we want to set the parent of animal to thing. This is ok, because we did not set the parent yet, and
 *    // do not have one defined via a configuration property.
 *    MyEnum.create("animal", thing);
 *
 *    //Will evaluate isInstance using the hierarchy
 *    boolean isAnimal = pug.isInstance(animal);
 *
 *    //A leaf element is one that doesn't have children
 *    boolean isLeaf = pug.isLeaf();
 *
 *    //A root element is one that doesn't have a parent.
 *    //Note: this can change since parents can be updated.
 *    boolean isRoot = thing.isRoot();
 *
 *    //Can get the children of an element using the getChildren method
 *    List<MyEnum> typesOfAnimals = animal.getChildren();
 *
 *    //Can emulate Java enum using the valueOf method
 *    MyEnum cat = MyEnum.valueOf("cat");
 *
 *    //Can retrieve all instances in an unmodifiable set using the values method
 *    Set<MyEnum> allThings = MyEnum.values();
 *
 *    //Will result in [dog, animal, thing]
 *    List<MyEnum> ancestors = pug.getAncestors();
 * }*
 * </pre>
 *
 *
 * <p>
 * If your HierarchicalEnumValue stores other information and want to ensure that declared instances are loaded in
 * memory you can use Mango's {@link com.davidbracewell.config.Preloader} to load during application startup.
 * </p>
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public abstract class HierarchicalEnumValue<T extends HierarchicalEnumValue> extends EnumValue {
  private static final long serialVersionUID = 1L;
  /**
   * The Parent.
   */
  protected volatile T parent = null;


  /**
   * Instantiates a new Hierarchical enum value.
   *
   * @param name   the specified name of the element
   * @param parent the parent of element (possibly null)
   */
  protected HierarchicalEnumValue(String name, T parent) {
    super(name);
    this.parent = parent;
  }

  /**
   * <p>Determines if this element is as root.</p>
   *
   * @return True if it is a root, False otherwise
   */
  public final boolean isRoot() {
    return !getParent().isPresent();
  }


  /**
   * <p>Gets the immediate children of this element or an empty list if none.</p>
   *
   * @return the immediate children of this element.
   */
  public abstract List<T> getChildren();

  /**
   * <p>Determines if this element is a leaf, i.e. has no children.</p>
   *
   * @return True if it is a leaf, False otherwise
   */
  public final boolean isLeaf() {
    return getChildren().isEmpty();
  }

  /**
   * <p>Gets the parent of this element. It first checks if a parent has been explicitly set and if not will attempt to
   * determine the parent using the configuration property <code>canonical.name.parent</code> where the canonical name
   * is determined using {@link #canonicalName()}.</p>
   *
   * @return the parent of this element as an Optional
   */
  public final Optional<T> getParent() {
    if (parent == null) {
      synchronized (this) {
        if (parent == null) {
          T ev = getParentFromConfig();
          if (ev != null) {
            parent = ev;
          }
        }
      }
    }
    return Optional.ofNullable(parent);
  }

  @Override
  public final boolean isInstance(@NonNull Tag value) {
    HierarchicalEnumValue hev = this;
    while (hev != null) {
      if (hev.equals(value)) {
        return true;
      }
      final HierarchicalEnumValue pHev = hev;
      hev = Cast.as(hev.getParent()
                       .filter(v -> v != pHev)
                       .orElse(null));
    }
    return false;
  }

  /**
   * Determines the parent via a configuration setting.
   *
   * @return the parent via the configuration property or null
   */
  protected T getParentFromConfig() {
    return Cast.as(Config.get(canonicalName(), "parent").as(getClass(), null));
  }


  /**
   * <p>Gets the path from this element's parent to a root, i.e. its ancestors in the tree.</p>
   *
   * @return the list of ancestors with this element's parent in position 0 or an empty list if this element is a root.
   */
  public final List<T> getAncestors() {
    List<T> path = new ArrayList<>();
    T hev = Cast.as(this);
    do {
      final HierarchicalEnumValue pHev = hev;
      hev = Cast.as(hev.getParent()
                       .filter(v -> v != pHev)
                       .orElse(null));

      if (hev != null) {
        path.add(hev);
      }

    } while (hev != null);
    return path;
  }


}//END OF HierarchicalEnumValue
