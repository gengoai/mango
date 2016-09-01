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

package com.davidbracewell.string;

import lombok.NonNull;

import java.io.Serializable;
import java.util.regex.Pattern;

import static com.davidbracewell.Validations.validateArgument;

/**
 * The interface Char predicate.
 *
 * @author David B. Bracewell
 */
@FunctionalInterface
public interface CharPredicate extends Serializable {

  /**
   * Matches boolean.
   *
   * @param c the c
   * @return the boolean
   */
  boolean matches(char c);

  /**
   * Matches any of boolean.
   *
   * @param charSequence the char sequence
   * @return the boolean
   */
  default boolean matchesAnyOf(CharSequence charSequence) {
    return charSequence != null && charSequence.chars().anyMatch(i -> matches((char) i));
  }

  /**
   * Matches all of boolean.
   *
   * @param charSequence the char sequence
   * @return the boolean
   */
  default boolean matchesAllOf(CharSequence charSequence) {
    return charSequence != null && charSequence.chars().allMatch(i -> matches((char) i));
  }

  /**
   * Matches none of boolean.
   *
   * @param charSequence the char sequence
   * @return the boolean
   */
  default boolean matchesNoneOf(CharSequence charSequence) {
    return charSequence != null && charSequence.chars().noneMatch(i -> matches((char) i));
  }

  /**
   * Negate char predicate.
   *
   * @return the char predicate
   */
  default CharPredicate negate() {
    return c -> !matches(c);
  }

  /**
   * And char predicate.
   *
   * @param other the other
   * @return the char predicate
   */
  default CharPredicate and(@NonNull CharPredicate other) {
    return c -> matches(c) && other.matches(c);
  }

  /**
   * Or char predicate.
   *
   * @param other the other
   * @return the char predicate
   */
  default CharPredicate or(@NonNull CharPredicate other) {
    return c -> matches(c) || other.matches(c);
  }

  /**
   * Count in int.
   *
   * @param sequence the sequence
   * @return the int
   */
  default int countIn(CharSequence sequence) {
    if (sequence == null) {
      return 0;
    }
    return (int) sequence.chars().filter(i -> matches((char) i)).count();
  }

  /**
   * Index in int.
   *
   * @param sequence the sequence
   * @return the int
   */
  default int indexIn(CharSequence sequence) {
    return indexIn(sequence, 0);
  }

  /**
   * Index in int.
   *
   * @param sequence the sequence
   * @param start    the start
   * @return the int
   */
  default int indexIn(CharSequence sequence, int start) {
    validateArgument(start >= 0);
    if (sequence == null) {
      return 0;
    }
    for (int i = start; i < sequence.length(); i++) {
      if (matches(sequence.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Last index in int.
   *
   * @param sequence the sequence
   * @return the int
   */
  default int lastIndexIn(CharSequence sequence) {
    if (sequence == null) {
      return 0;
    }
    for (int i = sequence.length() - 1; i >= 0; i--) {
      if (matches(sequence.charAt(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Trim leading from string.
   *
   * @param sequence the sequence
   * @return the string
   */
  default String trimLeadingFrom(CharSequence sequence) {
    if (sequence == null) {
      return null;
    }
    for (int start = 0; start < sequence.length(); start++) {
      if (!matches(sequence.charAt(start))) {
        return sequence.subSequence(start, sequence.length()).toString();
      }
    }
    return StringUtils.EMPTY;
  }

  /**
   * Trim trailing from string.
   *
   * @param sequence the sequence
   * @return the string
   */
  default String trimTrailingFrom(CharSequence sequence) {
    if (sequence == null) {
      return null;
    }
    for (int end = sequence.length() - 1; end >= 0; end--) {
      if (!matches(sequence.charAt(end))) {
        return sequence.subSequence(0, end + 1).toString();
      }
    }
    return StringUtils.EMPTY;
  }

  /**
   * Trim from string.
   *
   * @param sequence the sequence
   * @return the string
   */
  default String trimFrom(CharSequence sequence) {
    return trimTrailingFrom(trimLeadingFrom(sequence));
  }

  /**
   * Replace from string.
   *
   * @param sequence    the sequence
   * @param replacement the replacement
   * @return the string
   */
  default String replaceFrom(CharSequence sequence, char replacement) {
    if (sequence == null) {
      return null;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < sequence.length(); i++) {
      if (matches(sequence.charAt(i))) {
        builder.append(replacement);
      } else {
        builder.append(sequence.charAt(i));
      }
    }
    return builder.toString();
  }

  /**
   * Replace from string.
   *
   * @param sequence    the sequence
   * @param replacement the replacement
   * @return the string
   */
  default String replaceFrom(CharSequence sequence, @NonNull CharSequence replacement) {
    if (sequence == null) {
      return null;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < sequence.length(); i++) {
      if (matches(sequence.charAt(i))) {
        builder.append(replacement);
      } else {
        builder.append(sequence.charAt(i));
      }
    }
    return builder.toString();
  }

  /**
   * Remove from string.
   *
   * @param sequence the sequence
   * @return the string
   */
  default String removeFrom(CharSequence sequence) {
    if (sequence == null) {
      return null;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < sequence.length(); i++) {
      if (!matches(sequence.charAt(i))) {
        builder.append(sequence.charAt(i));
      }
    }
    return builder.toString();
  }

  /**
   * Retain from string.
   *
   * @param sequence the sequence
   * @return the string
   */
  default String retainFrom(CharSequence sequence) {
    if (sequence == null) {
      return null;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < sequence.length(); i++) {
      if (matches(sequence.charAt(i))) {
        builder.append(sequence.charAt(i));
      }
    }
    return builder.toString();
  }

  /**
   * Any of char predicate.
   *
   * @param sequence the sequence
   * @return the char predicate
   */
  static CharPredicate anyOf(@NonNull final CharSequence sequence) {
    return new CharPredicate() {
      private static final long serialVersionUID = 1L;
      String s = sequence.toString();

      @Override
      public boolean matches(char c) {
        return s.indexOf(c) >= 0;
      }
    };
  }

  /**
   * None of char predicate.
   *
   * @param sequence the sequence
   * @return the char predicate
   */
  static CharPredicate noneOf(@NonNull final CharSequence sequence) {
    return anyOf(sequence).negate();
  }

  /**
   * Any char predicate.
   *
   * @return the char predicate
   */
  static CharPredicate any() {
    return ANY;
  }

  /**
   * None char predicate.
   *
   * @return the char predicate
   */
  static CharPredicate none() {
    return NONE;
  }


  /**
   * Range char predicate.
   *
   * @param low  the low
   * @param high the high
   * @return the char predicate
   */
  static CharPredicate range(int low, int high) {
    validateArgument(high > low);
    return c -> c >= low && c < high;
  }

  /**
   * Pattern char predicate.
   *
   * @param pattern the pattern
   * @return the char predicate
   */
  static CharPredicate pattern(@NonNull String pattern) {
    final Pattern regex = Pattern.compile(pattern);
    return c -> regex.matcher(Character.toString(c)).find();
  }

  /**
   * Range char predicate.
   *
   * @param start the start
   * @param end   the end
   * @return the char predicate
   */
  static CharPredicate range(final char[] start, final char end[]) {
    validateArgument(start.length == end.length);
    validateArgument(start.length > 0);
    return c -> {
      for (int i = 0; i < start.length; i++) {
        if (c >= start[i] && c <= end[i]) {
          return true;
        }
      }
      return false;
    };
  }

  /**
   * The constant ASCII.
   */
  CharPredicate ASCII = range(0, 127);
  /**
   * The constant EXTENDED_ASCII.
   */
  CharPredicate EXTENDED_ASCII = range(0, 255);
  /**
   * The constant NONE.
   */
  CharPredicate NONE = c -> false;
  /**
   * The constant ANY.
   */
  CharPredicate ANY = c -> true;
  /**
   * The constant WHITESPACE.
   */
  CharPredicate WHITESPACE = Character::isWhitespace;
  /**
   * The constant JAVA_LETTER.
   */
  CharPredicate JAVA_LETTER = Character::isLetter;
  /**
   * The constant JAVA_DIGIT.
   */
  CharPredicate JAVA_DIGIT = Character::isDigit;
  /**
   * The constant IDEOGRAPHIC.
   */
  CharPredicate IDEOGRAPHIC = Character::isIdeographic;
  /**
   * The constant ALPHABETIC.
   */
  CharPredicate ALPHABETIC = Character::isAlphabetic;
  /**
   * The constant LETTER_OR_DIGIT.
   */
  CharPredicate LETTER_OR_DIGIT = Character::isLetterOrDigit;
  /**
   * The constant NOT_LETTER_OR_DIGIT.
   */
  CharPredicate NOT_LETTER_OR_DIGIT = LETTER_OR_DIGIT.negate();
  /**
   * The constant UPPERCASE.
   */
  CharPredicate UPPERCASE = Character::isUpperCase;
  /**
   * The constant LOWERCASE.
   */
  CharPredicate LOWERCASE = Character::isLowerCase;
  /**
   * Taken from Guava (c) Google
   */
  CharPredicate BREAKING_WHITESPACE = c -> {
    switch (c) {
      case '\t':
      case '\n':
      case '\u000b':
      case '\f':
      case '\r':
      case ' ':
      case '\u0085':
      case ' ':
      case '\u2028':
      case '\u2029':
      case ' ':
      case '　':
        return true;
      case ' ':
        return false;
      default:
        return c >= 8192 && c <= 8202;
    }
  };

  /**
   * Taken from Guava (c) Google
   */
  CharPredicate INVISIBLE = range(
    "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f\u1680\u180e\u2000\u2028\u205f\u2066\u2067\u2068\u2069\u206a\u3000\ud800\ufeff\ufff9\ufffa"
      .toCharArray(),
    "\u0020\u00a0\u00ad\u0604\u061c\u06dd\u070f\u1680\u180e\u200f\u202f\u2064\u2066\u2067\u2068\u2069\u206f\u3000\uf8ff\ufeff\ufff9\ufffb"
      .toCharArray()
                                 );

}//END OF CharPredicate
