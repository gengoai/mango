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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * The type Forwarding index.
 * @param <TYPE>  the type parameter
 * @author David B. Bracewell
 */
public abstract class ForwardingIndex<TYPE> implements Index<TYPE>, Serializable {
  private static final long serialVersionUID = 1L;


  /**
   * Delegate index.
   *
   * @return the index
   */
  protected abstract Index<TYPE> delegate();

  @Override
  public int add(TYPE item) {
    return delegate().add(item);
  }

  @Override
  public void addAll(Iterable<TYPE> items) {
    delegate().addAll(items);
  }

  @Override
  public int indexOf(TYPE item) {
    return delegate().indexOf(item);
  }

  @Override
  public TYPE get(int id) {
    return delegate().get(id);
  }

  @Override
  public void clear() {
    delegate().clear();
  }

  @Override
  public int size() {
    return delegate().size();
  }

  @Override
  public boolean isEmpty() {
    return delegate().isEmpty();
  }

  @Override
  public TYPE remove(int id) {
    return delegate().remove(id);
  }

  @Override
  public int remove(TYPE item) {
    return delegate().remove(item);
  }

  @Override
  public boolean contains(TYPE item) {
    return delegate().contains(item);
  }

  @Override
  public List<TYPE> asList() {
    return delegate().asList();
  }

  @Override
  public TYPE set(int index, TYPE newValue) {
    return delegate().set(index, newValue);
  }

  @Override
  public Iterator<TYPE> iterator() {
    return delegate().iterator();
  }

}//END OF ForwardingIndex
