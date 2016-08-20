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

import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.function.SerializablePredicate;
import lombok.NonNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.davidbracewell.Validations.validateState;

/**
 * @author David B. Bracewell
 */
public interface Iterators {


  static <E> Iterator<E> unmodifiableIterator(@NonNull final Iterator<E> backing) {
    return new Iterator<E>() {
      @Override
      public boolean hasNext() {
        return backing.hasNext();
      }

      @Override
      public E next() {
        return backing.next();
      }
    };
  }

  static <E, R> Iterator<R> transformedIterator(@NonNull final Iterator<E> backing, @NonNull SerializableFunction<? super E, ? extends R> mapper) {
    return new Iterator<R>() {
      @Override
      public boolean hasNext() {
        return backing.hasNext();
      }

      @Override
      public R next() {
        return mapper.apply(backing.next());
      }

      @Override
      public void remove() {
        backing.remove();
      }
    };
  }


  static <E> Iterator<E> filteredIterator(@NonNull final Iterator<E> backing, @NonNull SerializablePredicate<? super E> predicate) {
    return new Iterator<E>() {
      List<E> buffer;

      @Override
      public boolean hasNext() {
        while (buffer == null && backing.hasNext()) {
          E next = backing.next();
          if (predicate.test(next)) {
            buffer = Collections.singletonList(next);
          }
        }
        return buffer != null && !buffer.isEmpty();
      }

      @Override
      public E next() {
        validateState(hasNext(), "No such element");
        E rval = buffer.get(0);
        buffer = null;
        return rval;
      }
    };
  }

  static int size(Iterator<?> iterator) {
    return (int) Streams.asStream(iterator).count();
  }


}//END OF Iterators
