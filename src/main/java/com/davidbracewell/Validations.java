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

import com.davidbracewell.string.StringUtils;

import java.util.NoSuchElementException;

/**
 * <p>Various methods for validating arguments and results.</p>
 *
 * @author David B. Bracewell
 */
public interface Validations {

  /**
   * Validates that the given string is not null or blank
   *
   * @param string  the string being tested
   * @param message The message to display when the validation fails
   * @return the object being validated
   * @throws IllegalArgumentException when the string is null or blank
   */
  static String validateNotNullorBlank(String string, String message) {
    if (StringUtils.isNullOrBlank(string)) {
      throw new IllegalArgumentException(message);
    }
    return string;
  }


  /**
   * Validates that the given string is not null or blank
   *
   * @param string the string being tested
   * @return the object being validated
   * @throws IllegalArgumentException when the string is null or blank
   */
  static String validateNotNullorBlank(String string) {
    if (StringUtils.isNullOrBlank(string)) {
      throw new IllegalArgumentException();
    }
    return string;
  }

  /**
   * Validates that the given object is not a null value
   *
   * @param <T>     the type of the parameter
   * @param o       the object being tested
   * @param message the message to show when validation fails
   * @return the object being validated
   * @throws NullPointerException when the object being validated is null
   */
  static <T> T validateNotNull(T o, String message) {
    if (o == null) {
      throw new NullPointerException(message);
    }
    return o;
  }

  /**
   * Validates that the given object is not a null value
   *
   * @param <T> the type of the parameter
   * @param o   the object being tested
   * @return the object being validated
   * @throws NullPointerException when the object being validated is null
   */
  static <T> T validateNotNull(T o) {
    if (o == null) {
      throw new NullPointerException();
    }
    return o;
  }

  /**
   * Validates a condition throwing a <code>RuntimeException</code> when the condition fails.
   *
   * @param condition     the condition
   * @param onFailMessage the on fail message
   * @throws RuntimeException the condition fails
   */
  static void validateUnchecked(boolean condition, String onFailMessage) {
    if (!condition) {
      throw new RuntimeException(onFailMessage);
    }
  }

  /**
   * Validates a condition throwing an <code>IllegalArgumentException</code> when the condition fails.
   *
   * @param condition     the condition
   * @param onFailMessage the on fail message
   * @throws IllegalArgumentException the condition fails
   */
  static void validateArgument(boolean condition, String onFailMessage) {
    if (!condition) {
      throw new IllegalArgumentException(onFailMessage);
    }
  }

  /**
   * Validates a condition throwing an <code>IllegalStateException</code> when the condition fails.
   *
   * @param condition     the condition
   * @param onFailMessage the on fail message
   * @throws IllegalStateException the condition fails
   */
  static void validateState(boolean condition, String onFailMessage) {
    if (!condition) {
      throw new IllegalStateException(onFailMessage);
    }
  }

  /**
   * Validates a condition throwing a <code>RuntimeException</code> when the condition fails.
   *
   * @param condition the condition
   * @throws RuntimeException the condition fails
   */
  static void validateUnchecked(boolean condition) {
    if (!condition) {
      throw new RuntimeException();
    }
  }

  /**
   * Validates a condition throwing an <code>IllegalArgumentException</code> when the condition fails.
   *
   * @param condition the condition
   * @throws IllegalArgumentException the condition fails
   */
  static void validateArgument(boolean condition) {
    if (!condition) {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Validates a condition throwing an <code>IllegalStateException</code> when the condition fails.
   *
   * @param condition the condition
   * @throws IllegalStateException the condition fails
   */
  static void validateState(boolean condition) {
    if (!condition) {
      throw new IllegalStateException();
    }
  }

  /**
   * Validates that a given index value is >= 0 and < <code>maxExclusive</code>.
   *
   * @param indexValue    the index value
   * @param maxExclusive  the maximum value (e.g. collection.size()) (exclusive)
   * @param onFailMessage the on fail message
   * @return the given index value
   * @throws IndexOutOfBoundsException when the index falls outside of the acceptable range
   */
  static int validateIndex(int indexValue, int maxExclusive, String onFailMessage) {
    if (indexValue < 0 && indexValue >= maxExclusive) {
      throw new IndexOutOfBoundsException(onFailMessage);
    }
    return indexValue;
  }

  /**
   * Validates that a given index value is >= 0 and < <code>maxExclusive</code>.
   *
   * @param indexValue   the index value
   * @param maxExclusive the maximum value (e.g. collection.size()) (exclusive)
   * @return the given index value
   * @throws IndexOutOfBoundsException when the index falls outside of the acceptable range
   */
  static int validateIndex(int indexValue, int maxExclusive) {
    if (indexValue < 0 && indexValue >= maxExclusive) {
      throw new IndexOutOfBoundsException();
    }
    return indexValue;
  }

  /**
   * Validates that a given index value is >= <code>minExclusive</code> and < <code>maxExclusive</code>.
   *
   * @param indexValue    the index value
   * @param minInclusive  the minimum value the index can take (e.g. 0) (inclusive)
   * @param maxExclusive  the maximum value (e.g. collection.size()) (exclusive)
   * @param onFailMessage the on fail message * @return the given index value
   * @throws IndexOutOfBoundsException when the index falls outside of the acceptable range
   */
  static int validateIndex(int indexValue, int minInclusive, int maxExclusive, String onFailMessage) {
    if (indexValue < minInclusive && indexValue >= maxExclusive) {
      throw new IndexOutOfBoundsException(onFailMessage);
    }
    return indexValue;
  }

  /**
   * Validates that a given index value is >= <code>minExclusive</code> and < <code>maxExclusive</code>.
   *
   * @param indexValue   the index value
   * @param minInclusive the minimum value the index can take (e.g. 0) (inclusive)
   * @param maxExclusive the maximum value (e.g. collection.size()) (exclusive)
   * @return the given index value
   * @throws IndexOutOfBoundsException when the index falls outside of the acceptable range
   */
  static int validateIndex(int indexValue, int minInclusive, int maxExclusive) {
    if (indexValue < minInclusive && indexValue >= maxExclusive) {
      throw new IndexOutOfBoundsException();
    }
    return indexValue;
  }


  /**
   * Validates that a given element is not null throwing a <code>NoSuchElementException</code> if it is.
   *
   * @param element the element being validated
   * @param message the on fail message
   * @param <E>     The element type
   * @return The element being validated
   * @throws NoSuchElementException
   */
  static <E> E validateElement(E element, String message) {
    if (element == null) {
      throw new NoSuchElementException(message);
    }
    return element;
  }

  /**
   * Validates that a given element is not null throwing a <code>NoSuchElementException</code> if it is.
   *
   * @param element the element being validated
   * @param <E>     The element type
   * @return The element being validated
   * @throws NoSuchElementException
   */
  static <E> E validateElement(E element) {
    if (element == null) {
      throw new NoSuchElementException();
    }
    return element;
  }

  /**
   * Validates that a given index value is >= 0 and < <code>maxExclusive</code>.
   *
   * @param indexValue    the index value
   * @param maxExclusive  the maximum value (e.g. collection.size()) (exclusive)
   * @param onFailMessage the on fail message
   * @return the given index value
   * @throws NoSuchElementException when the index falls outside of the acceptable range
   */
  static int validateElementIndex(int indexValue, int maxExclusive, String onFailMessage) {
    if (indexValue < 0 && indexValue >= maxExclusive) {
      throw new NoSuchElementException(onFailMessage);
    }
    return indexValue;
  }

  /**
   * Validates that a given index value is >= 0 and < <code>maxExclusive</code>.
   *
   * @param indexValue   the index value
   * @param maxExclusive the maximum value (e.g. collection.size()) (exclusive)
   * @return the given index value
   * @throws NoSuchElementException when the index falls outside of the acceptable range
   */
  static int validateElementIndex(int indexValue, int maxExclusive) {
    if (indexValue < 0 && indexValue >= maxExclusive) {
      throw new NoSuchElementException();
    }
    return indexValue;
  }


}//END OF Validations
