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

package com.davidbracewell.collection;

import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Concurrent abstract map counter.
 *
 * @param <TYPE> the type parameter
 * @author David B. Bracewell
 */
public class ConcurrentMapCounter<TYPE> extends AbstractMapCounter<TYPE> {

  private static final long serialVersionUID = 1278617623455939349L;

  /**
   * Instantiates a new Concurrent abstract map counter.
   */
  public ConcurrentMapCounter() {
    super(new ConcurrentHashMap<>());
  }

  /**
   * Instantiates a new Concurrent abstract map counter.
   *
   * @param items the items
   */
  public ConcurrentMapCounter(@NonNull Iterable<? extends TYPE> items) {
    super(new ConcurrentHashMap<>(), items);
  }

  /**
   * Instantiates a new Concurrent abstract map counter.
   *
   * @param items the items
   */
  public ConcurrentMapCounter(@NonNull Counter<? extends TYPE> items) {
    super(new ConcurrentHashMap<>(), items);
  }

  /**
   * Instantiates a new Concurrent abstract map counter.
   *
   * @param items the items
   */
  public ConcurrentMapCounter(@NonNull Map<? extends TYPE, ? extends Number> items) {
    super(new ConcurrentHashMap<>(), items);
  }

  @Override
  protected Counter<TYPE> newInstance() {
    return new ConcurrentMapCounter<>();
  }

}//END OF ConcurrentMapCounter
