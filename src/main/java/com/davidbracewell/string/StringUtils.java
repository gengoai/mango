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

import com.davidbracewell.collection.Collect;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.structured.csv.CSVReader;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.NonNull;

import java.io.StringReader;
import java.util.*;
import java.util.stream.Stream;

/**
 * String utility methods
 *
 * @author David B. Bracewell
 */
public class StringUtils {

  /**
   * CharMatcher combining INVISIBLE, BREAKING_WHITESPACE, and WHITESPACE
   */
  public static final CharMatcher WHITESPACE = CharMatcher.INVISIBLE.and(CharMatcher.BREAKING_WHITESPACE).and(CharMatcher.WHITESPACE);
  /**
   * Empty String
   */
  public static final String EMPTY = "";

  /**
   * A CharMatcher that matches anything that is a letter or digit
   */
  public static final CharMatcher LETTER_OR_DIGIT = CharMatcher.forPredicate(Character::isLetterOrDigit);

  /**
   * A CharMatcher that matches anything that is not a letter or digit
   */
  public static final CharMatcher NOT_LETTER_OR_DIGIT = CharMatcher.forPredicate(Predicates.not(LETTER_OR_DIGIT));


  /**
   * <p>Abbreviates a string to a desired length and adds "..." at the end.</p>
   *
   * @param input  The input string
   * @param length The length of the abbreviation
   * @return The abbreviated string
   */
  public static String abbreviate(String input, int length) {
    if (input == null) {
      return null;
    }
    if (input.length() <= length) {
      return input;
    }
    return input.substring(0, length) + "...";
  }

  /**
   * Null safe comparison of strings
   *
   * @param s1         string 1
   * @param s2         string 2
   * @param ignoreCase True perform case insensitive comparison, False perform case sensitive comparison
   * @return as long as neither are null
   */
  public static int compare(String s1, String s2, boolean ignoreCase) {
    if (s1 == null && s2 == null) {
      return 0;
    } else if (s1 == null) {
      return -1;
    } else if (s2 == null) {
      return 1;
    }
    return ignoreCase ? s1.compareToIgnoreCase(s2) : s1.compareTo(s2);
  }

  /**
   * Capitalizes the first character in a string.
   *
   * @param word the string to capitalize
   * @return A new string with the first letter capitalized
   */
  public static String capitalize(CharSequence word) {
    if (word == null) {
      return null;
    }
    if (word.length() == 0) {
      return EMPTY;
    }
    return Character.toUpperCase(word.charAt(0)) + (word.length() > 1 ? word.subSequence(1, word.length()).toString() : EMPTY);
  }

  /**
   * Safe equals.
   *
   * @param s1            the s 1
   * @param s2            the s 2
   * @param caseSensitive the case sensitive
   * @return the boolean
   */
  public static boolean safeEquals(String s1, String s2, boolean caseSensitive) {
    if (s1 == s2) {
      return true;
    } else if (s1 == null || s2 == null) {
      return false;
    } else if (caseSensitive) {
      return s1.equals(s2);
    }
    return s1.equalsIgnoreCase(s2);
  }

  /**
   * Determines if a string is only made up of letters or digits
   *
   * @param string the string to check
   * @return True if the string is only made up of letter or digits
   */
  public static boolean isAlphaNumeric(CharSequence string) {
    return StringPredicates.IS_LETTER_OR_DIGIT.test(string);
  }

  /**
   * Determines if a string has at least one letter
   *
   * @param string the string to check
   * @return True if the string has at least one letter
   */
  public static boolean hasLetter(CharSequence string) {
    return StringPredicates.HAS_LETTER.test(string);
  }

  /**
   * Determines if a string has at least one digit
   *
   * @param string the string to check
   * @return True if the string has at least one digit
   */
  public static boolean hasDigit(CharSequence string) {
    return StringPredicates.HAS_LETTER.test(string);
  }

  /**
   * Determines if a string is only made up of numbers.
   *
   * @param string the string to check
   * @return True if the string is only made up of numbers.
   */
  public static boolean isDigit(CharSequence string) {
    return StringPredicates.IS_DIGIT.test(string);
  }

  /**
   * Determines if a string is only made up of letters.
   *
   * @param string the string to check
   * @return True if the string is only made up of letters.
   */
  public static boolean isLetter(CharSequence string) {
    return StringPredicates.IS_LETTER.test(string);
  }

  /**
   * Checks if character is in a unicode block that is letters or numbers
   *
   * @param c the character
   * @return whether or not it is a letter or digit
   */
  public static boolean isLetterOrDigit(char c) {
    return Character.isAlphabetic((int) c) || Character.isDigit((int) c);
  }


  /**
   * Determines if an entire string is lower case or not
   *
   * @param input The input string
   * @return True if the string is lower case, False if not
   */
  public static boolean isLowerCase(CharSequence input) {
    return StringPredicates.IS_LOWER_CASE.test(input);
  }

  /**
   * Determines if a string is only made up of non letters and digits
   *
   * @param string the string to check
   * @return True if the string is only made up of non letters and digits
   */
  public static boolean isNonAlphaNumeric(CharSequence string) {
    return !StringPredicates.IS_LETTER_OR_DIGIT.test(string);
  }

  /**
   * Determines if a string is null or blank (trimmed string is empty).
   *
   * @param input The input string
   * @return True when the input string is null or the trimmed version of the string is empty.
   */
  public static boolean isNullOrBlank(CharSequence input) {
    return StringPredicates.IS_NULL_OR_BLANK.test(input);
  }

  /**
   * Determines if a given string is only made up of punctuation characters.
   *
   * @param string the string to check
   * @return True if the string is all punctuation
   */
  public static boolean isPunctuation(CharSequence string) {
    return StringPredicates.IS_PUNCTUATION.test(string);
  }

  /**
   * Determines if a given string has one or more punctuation characters.
   *
   * @param string the string to check
   * @return True if the string has one or more punctuation characters
   */
  public static boolean hasPunctuation(CharSequence string) {
    return StringPredicates.HAS_PUNCTUATION.test(string);
  }


  /**
   * Determines if a character represents a punctuation mark
   *
   * @param c The character
   * @return True if it is punctuation false otherwise
   */
  public static boolean isPunctuation(char c) {
    switch (Character.getType((int) c)) {
      case Character.CONNECTOR_PUNCTUATION:
      case Character.DASH_PUNCTUATION:
      case Character.END_PUNCTUATION:
      case Character.FINAL_QUOTE_PUNCTUATION:
      case Character.INITIAL_QUOTE_PUNCTUATION:
      case Character.START_PUNCTUATION:
      case Character.OTHER_PUNCTUATION:
        return true;
    }
    return false;
  }

  /**
   * Determines if an entire string is upper case or not
   *
   * @param input The input string
   * @return True if the string is upper case, False if not
   */
  public static boolean isUpperCase(CharSequence input) {
    return StringPredicates.IS_UPPER_CASE.test(input);
  }

  /**
   * Left trim.
   *
   * @param input the input
   * @return the string
   */
  public static String leftTrim(CharSequence input) {
    return StringFunctions.LEFT_TRIM.apply(input.toString());
  }

  /**
   * Generates a random string of given length made up of valid hexadecimal characters.
   *
   * @param length the length of the string
   * @return the random string
   */
  public static String randomHexString(int length) {
    return randomString(length, CharMatcher.anyOf("ABCDEF1234567890"));
  }

  /**
   * Generates a random string of a given length
   *
   * @param length The length of the string
   * @param min    The min character in the string
   * @param max    The max character in the string
   * @return A string of random characters
   */
  public static String randomString(int length, int min, int max) {
    return randomString(length, min, max, CharMatcher.ANY);
  }

  /**
   * Generates a random string of a given length
   *
   * @param length    The length of the string
   * @param validChar CharMatcher that must match for a character to be returned in the string
   * @return A string of random characters
   */
  public static String randomString(int length, CharMatcher validChar) {
    return randomString(length, 0, Integer.MAX_VALUE, validChar);
  }

  /**
   * Generates a random string of a given length
   *
   * @param length    The length of the string
   * @param min       The min character in the string
   * @param max       The max character in the string
   * @param validChar CharMatcher that must match for a character to be returned in the string
   * @return A string of random characters
   */
  public static String randomString(int length, int min, int max, CharMatcher validChar) {
    Preconditions.checkArgument(length >= 0, "Length must be non-negative");
    if (length == 0) {
      return EMPTY;
    }
    Random random = new Random();
    int maxRandom = max - min;
    char[] array = new char[length];
    for (int i = 0; i < array.length; i++) {
      char c;
      do {
        c = (char) (random.nextInt(maxRandom) + min);
      } while (Character.isLowSurrogate(c) ||
        Character.isHighSurrogate(c) ||
        !validChar.matches(c));
      array[i] = c;
    }
    return new String(array);
  }

  /**
   * Normalizes a string by removing the diacritics.
   *
   * @param input the input string
   * @return Resulting string without diacritic marks
   */
  public static String removeDiacritics(CharSequence input) {
    return StringFunctions.DIACRITICS_NORMALIZATION.apply(input.toString());
  }

  /**
   * Right trim.
   *
   * @param input the input
   * @return the string
   */
  public static String rightTrim(CharSequence input) {
    return StringFunctions.RIGHT_TRIM.apply(input.toString());
  }

  /**
   * Properly splits a delimited separated string.
   *
   * @param input     The input
   * @param separator The separator
   * @return A list of all the cells in the input
   */
  public static List<String> split(CharSequence input, char separator) {
    if (input == null) {
      return Lists.newArrayList();
    }
    Preconditions.checkArgument(separator != '"', "Separator cannot be a quote");
    try (CSVReader reader = CSV.builder().delimiter(separator).reader(new StringReader(input.toString()))) {
      List<String> all = Lists.newArrayList();
      List<String> row;
      while ((row = reader.nextRow()) != null) {
        all.addAll(row);
      }
      return all;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Normalize to canonical form.
   *
   * @param input the input string
   * @return the normalized string
   */
  public static String toCanonicalForm(CharSequence input) {
    return StringFunctions.CANONICAL_NORMALIZATION.apply(input.toString());
  }

  /**
   * Converts an input string to title case
   *
   * @param input The input string
   * @return The title cased version of the input
   */
  public static String toTitleCase(CharSequence input) {
    return StringFunctions.TITLE_CASE.apply(input.toString());
  }

  /**
   * Trims a string of unicode whitespace and invisible characters
   *
   * @param input Input string
   * @return Trimmed string or null if input was null
   */
  public static String trim(CharSequence input) {
    return StringFunctions.TRIM.apply(input.toString());
  }


  /**
   * Join string.
   *
   * @param <T>       the type parameter
   * @param separator the separator
   * @param items     the items
   * @return the string
   */
  public static <T> String join(@NonNull String separator, T[] items) {
    if (items == null) {
      return join(separator, Collections.emptyList(), null);
    }
    return join(separator, Arrays.asList(items), null);
  }

  /**
   * Join string.
   *
   * @param <T>        the type parameter
   * @param separator  the separator
   * @param items      the items
   * @param nullString the null string
   * @return the string
   */
  public static <T> String join(@NonNull String separator, T[] items, String nullString) {
    if (items == null) {
      return join(separator, Collections.emptyList(), nullString);
    }
    return join(separator, Arrays.asList(items), nullString);
  }

  /**
   * Join string.
   *
   * @param separator the separator
   * @param items     the items
   * @return the string
   */
  public static String join(@NonNull String separator, Iterator<?> items) {

    return join(separator, Collect.from(items), null);
  }

  /**
   * Join string.
   *
   * @param separator  the separator
   * @param items      the items
   * @param nullString the null string
   * @return the string
   */
  public static String join(@NonNull String separator, Iterator<?> items, String nullString) {
    return join(separator, Collect.from(items), nullString);
  }

  /**
   * Join string.
   *
   * @param separator the separator
   * @param items     the items
   * @return the string
   */
  public static String join(@NonNull String separator, Iterable<?> items) {
    return join(separator, items, null);
  }

  /**
   * Join string.
   *
   * @param separator  the separator
   * @param items      the items
   * @param nullString the null string
   * @return the string
   */
  public static String join(@NonNull String separator, Iterable<?> items, String nullString) {
    if (items == null) {
      return StringUtils.EMPTY;
    }
    StringBuilder builder = new StringBuilder();
    for (Object o : items) {
      if (builder.length() > 0) {
        builder.append(separator);
      }
      String str = o == null ? nullString : o.toString();
      builder.append(str);
    }
    return builder.toString();
  }

  /**
   * Join string.
   *
   * @param <T>       the type parameter
   * @param separator the separator
   * @param items     the items
   * @return the string
   */
  public static <T> String join(@NonNull String separator, Stream<T> items) {
    return join(separator, items, null);
  }

  /**
   * Join string.
   *
   * @param <T>        the type parameter
   * @param separator  the separator
   * @param items      the items
   * @param nullString the null string
   * @return the string
   */
  public static <T> String join(@NonNull String separator, Stream<T> items, String nullString) {
    if (items == null) {
      return StringUtils.EMPTY;
    }
    StringBuilder builder = new StringBuilder();
    items.forEach(o -> {
      if (builder.length() > 0) {
        builder.append(separator);
      }
      String str = o == null ? nullString : o.toString();
      builder.append(str);
    });
    return builder.toString();
  }

}// END OF StringUtils

