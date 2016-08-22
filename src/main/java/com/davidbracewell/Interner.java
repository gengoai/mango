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
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * <p>Mimics {@link String#intern()} with any object using heap memory. Uses weak references so that objects no longer
 * in memory can be reclaimed.</p>
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
@EqualsAndHashCode
@ToString(includeFieldNames = false)
public final class Interner<E> implements Serializable {
  private static final long serialVersionUID = 1L;
  private volatile WeakHashMap<E, E> map = new WeakHashMap<>();

  /**
   * Adds or gets the cannoical version of the incoming object.
   *
   * @param object The object to itern
   * @return The interned value
   */
  public synchronized E intern(final E object) {
    if (object == null) {
      return null;
    }
    return map.computeIfAbsent(object, o -> object);
  }

  /**
   * Intern all set.
   *
   * @param set the set
   * @return the set
   */
  public Set<E> internAll(final Set<E> set) {
    if (set == null) {
      return Collections.emptySet();
    }
    Set<E> rval = new LinkedHashSet<>();
    set.forEach(e -> rval.add(intern(e)));
    return rval;
  }

  /**
   * Size int.
   *
   * @return the int
   */
  public int size() {
    return map.size();
  }

}//END OF Interner
