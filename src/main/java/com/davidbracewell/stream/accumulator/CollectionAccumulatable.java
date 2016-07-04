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

package com.davidbracewell.stream.accumulator;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.conversion.Cast;

import java.util.Collection;

/**
 * @author David B. Bracewell
 */
public class CollectionAccumulatable<E, C extends Collection<E>> implements Accumulatable<C> {
  private static final long serialVersionUID = 1L;

  @Override
  public C addAccumulator(C t1, C t2) {
    Collection<E> cnew = Cast.as(Collect.create(t1.getClass()));
    cnew.addAll(t1);
    cnew.addAll(t2);
    return Cast.as(cnew);
  }

  @Override
  public C addInPlace(C t1, C t2) {
    t1.addAll(t2);
    return t1;
  }

  @Override
  public C zero(C zeroValue) {
    return zeroValue;
  }

}//END OF CollectionAccumulatable
