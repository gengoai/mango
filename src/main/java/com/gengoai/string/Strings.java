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

package com.gengoai.string;

import com.gengoai.Validation;
import com.gengoai.collection.Streams;
import com.gengoai.io.CSV;
import com.gengoai.io.CSVReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * String utility methods
 *
 * @author David B. Bracewell
 */
public final class Strings {

   /**
    * Empty String
    */
   public static final String EMPTY = "";

   /**
    * The constant BLANK.
    */
   public static final String BLANK = " ";

   private Strings() {
      throw new IllegalAccessError();
   }



   /**
    * Null to empty string.
    *
    * @param input the input
    * @return the string
    */
   public static String nullToEmpty(String input) {
      return input == null ? EMPTY : input;
   }

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
    * Center string.
    *
    * @param s      the s
    * @param length the length
    * @return the string
    */
   public static String center(String s, int length) {
      if (s == null) {
         return null;
      }
      int start = (int) Math.floor(Math.max(0, (length - s.length()) / 2d));
      return padEnd(repeat(' ', start) + s, length, ' ');
   }

   /**
    * <p>Replaces repeated characters with a single instance. e.g. <code>Gooooood</code> would become
    * <code>God</code>.</p>
    *
    * @param sequence The character sequence
    * @return The compacted string
    * @throws NullPointerException when the sequence is null
    */
   public static String compactRepeatedChars(CharSequence sequence) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < sequence.length(); i++) {
         char c = sequence.charAt(i);
         if (builder.length() == 0 || builder.charAt(builder.length() - 1) != c) {
            builder.append(c);
         }
      }
      return builder.toString();
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
    * First non null or blank string.
    *
    * @param strings the strings
    * @return the string
    */
   public static String firstNonNullOrBlank(String... strings) {
      if (strings == null || strings.length == 0) {
         return null;
      }
      for (String s : strings) {
         if (isNotNullOrBlank(s)) {
            return s;
         }
      }
      return null;
   }

   /**
    * Determines if a string has at least one digit
    *
    * @param string the string to check
    * @return True if the string has at least one digit
    */
   public static boolean hasDigit(CharSequence string) {
      return StringMatcher.HasDigit.test(string);
   }

   /**
    * Determines if a string has at least one letter
    *
    * @param string the string to check
    * @return True if the string has at least one letter
    */
   public static boolean hasLetter(CharSequence string) {
      return StringMatcher.HasLetter.test(string);
   }

   /**
    * Determines if a given string has one or more punctuation characters.
    *
    * @param string the string to check
    * @return True if the string has one or more punctuation characters
    */
   public static boolean hasPunctuation(CharSequence string) {
      return StringMatcher.HasPunctuation.test(string);
   }

   /**
    * Determines if a string is only made up of letters or digits
    *
    * @param string the string to check
    * @return True if the string is only made up of letter or digits
    */
   public static boolean isAlphaNumeric(CharSequence string) {
      return StringMatcher.LetterOrDigit.test(string);
   }

   /**
    * Determines if a string is only made up of numbers.
    *
    * @param string the string to check
    * @return True if the string is only made up of numbers.
    */
   public static boolean isDigit(CharSequence string) {
      return StringMatcher.Digit.test(string);
   }

   /**
    * Determines if a string is only made up of letters.
    *
    * @param string the string to check
    * @return True if the string is only made up of letters.
    */
   public static boolean isLetter(CharSequence string) {
      return StringMatcher.Letter.test(string);
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
      return StringMatcher.LowerCase.test(input);
   }

   /**
    * Determines if a string is only made up of non letters and digits
    *
    * @param string the string to check
    * @return True if the string is only made up of non letters and digits
    */
   public static boolean isNonAlphaNumeric(CharSequence string) {
      return !StringMatcher.LetterOrDigit.test(string);
   }

   /**
    * Determines if a string is not null or blank (trimmed string is empty).
    *
    * @param input The input string
    * @return True when the input string is not null and the trimmed version of the string is not empty.
    */
   public static boolean isNotNullOrBlank(CharSequence input) {
      return StringMatcher.NotNullOrBlank.test(input);
   }

   /**
    * Determines if a string is null or blank (trimmed string is empty).
    *
    * @param input The input string
    * @return True when the input string is null or the trimmed version of the string is empty.
    */
   public static boolean isNullOrBlank(CharSequence input) {
      return StringMatcher.NullOrBlank.test(input);
   }

   /**
    * Determines if a given string is only made up of punctuation characters.
    *
    * @param string the string to check
    * @return True if the string is all punctuation
    */
   public static boolean isPunctuation(CharSequence string) {
      return StringMatcher.Punctuation.test(string);
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
         case '=':
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
      return StringMatcher.UpperCase.test(input);
   }

   /**
    * Join string.
    *
    * @param iterable  the iterable
    * @param delimiter the delimiter
    * @param prefix    the prefix
    * @param suffix    the suffix
    * @return the string
    */
   public static String join(Iterable<?> iterable, CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
      return Streams.asStream(iterable).map(Object::toString).collect(Collectors.joining(delimiter, prefix, suffix));
   }

   /**
    * Join string.
    *
    * @param iterable  the iterable
    * @param delimiter the delimiter
    * @return the string
    */
   public static String join(Iterable<?> iterable, CharSequence delimiter) {
      return Streams.asStream(iterable).map(Object::toString).collect(Collectors.joining(delimiter));
   }

   /**
    * Join string.
    *
    * @param <T>       the type parameter
    * @param values    the values
    * @param delimiter the delimiter
    * @return the string
    */
   public static <T> String join(T[] values, CharSequence delimiter) {
      return Streams.asStream(values).map(Object::toString).collect(Collectors.joining(delimiter));
   }

   /**
    * Join string.
    *
    * @param <T>       the type parameter
    * @param values    the values
    * @param delimiter the delimiter
    * @param prefix    the prefix
    * @param suffix    the suffix
    * @return the string
    */
   public static <T> String join(T[] values, CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
      return Streams.asStream(values).map(Object::toString).collect(Collectors.joining(delimiter, prefix, suffix));
   }

   /**
    * Left trim.
    *
    * @param input the input
    * @return the string
    */
   public static String leftTrim(CharSequence input) {
      if (input == null) {
         return null;
      }
      return StringFunctions.LEFT_TRIM.apply(input.toString());
   }

   /**
    * Pad end string.
    *
    * @param sequence         the sequence
    * @param desiredLength    the desired length
    * @param paddingCharacter the padding character
    * @return the string
    */
   public static String padEnd(CharSequence sequence, int desiredLength, char paddingCharacter) {
      if (sequence == null) {
         return repeat(paddingCharacter, desiredLength);
      } else if (sequence.length() == desiredLength) {
         return sequence.toString();
      }
      return sequence.toString() + repeat(paddingCharacter, desiredLength - sequence.length());
   }

   /**
    * Pad start string.
    *
    * @param sequence         the sequence
    * @param desiredLength    the desired length
    * @param paddingCharacter the padding character
    * @return the string
    */
   public static String padStart(CharSequence sequence, int desiredLength, char paddingCharacter) {
      if (sequence == null) {
         return repeat(paddingCharacter, desiredLength);
      } else if (sequence.length() == desiredLength) {
         return sequence.toString();
      }
      return repeat(paddingCharacter, desiredLength - sequence.length()) + sequence.toString();
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
      return randomString(length, min, max, CharMatcher.Any);
   }

   /**
    * Generates a random string of a given length
    *
    * @param length    The length of the string
    * @param validChar CharPredicate that must match for a character to be returned in the string
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
    * @param validChar CharPredicate that must match for a character to be returned in the string
    * @return A string of random characters
    */
   public static String randomString(int length, int min, int max, CharMatcher validChar) {
      if (length <= 0) {
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
                     !validChar.test(c));
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
      if (input == null) {
         return null;
      }
      return StringFunctions.DIACRITICS_NORMALIZATION.apply(input.toString());
   }

   /**
    * Repeat string.
    *
    * @param s     the s
    * @param count the count
    * @return the string
    */
   public static String repeat(String s, int count) {
      if (s == null) {
         return null;
      }
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < count; i++) {
         builder.append(s);
      }
      return builder.toString();
   }

   /**
    * Repeat string.
    *
    * @param c     the c
    * @param count the count
    * @return the string
    */
   public static String repeat(char c, int count) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < count; i++) {
         builder.append(c);
      }
      return builder.toString();
   }

   /**
    * Right trim.
    *
    * @param input the input
    * @return the string
    */
   public static String rightTrim(CharSequence input) {
      if (input == null) {
         return null;
      }
      return StringFunctions.RIGHT_TRIM.apply(input.toString());
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
    * Properly splits a delimited separated string.
    *
    * @param input     The input
    * @param separator The separator
    * @return A list of all the cells in the input
    */
   public static List<String> split(CharSequence input, char separator) {
      if (input == null) {
         return new ArrayList<>();
      }
      Validation.checkArgument(separator != '"', "Separator cannot be a quote");
      try (CSVReader reader = CSV.builder().delimiter(separator).reader(new StringReader(input.toString()))) {
         List<String> all = new ArrayList<>();
         List<String> row;
         while ((row = reader.nextRow()) != null) {
            all.addAll(row);
         }
         return all;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }


   /**
    * Escapes the unicode in the given string using the Java specification
    *
    * @param string The string to escape
    * @return The escaped string
    */
   public static String javaStringEscape(String string) {
      StringBuilder b = new StringBuilder();
      for (char c : string.toCharArray()) {
         if (c >= 128) {
            b.append("\\u").append(String.format("%04X", (int) c));
         } else {
            b.append(c);
         }
      }
      return b.toString();
   }


   /**
    * Normalize to canonical form.
    *
    * @param input the input string
    * @return the normalized string
    */
   public static String toCanonicalForm(CharSequence input) {
      if (input == null) {
         return null;
      }
      return StringFunctions.CANONICAL_NORMALIZATION.apply(input.toString());
   }

   /**
    * Converts an input string to title case
    *
    * @param input The input string
    * @return The title cased version of the input
    */
   public static String toTitleCase(CharSequence input) {
      if (input == null) {
         return null;
      }
      return StringFunctions.TITLE_CASE.apply(input.toString());
   }

   /**
    * Trims a string of unicode whitespace and invisible characters
    *
    * @param input Input string
    * @return Trimmed string or null if input was null
    */
   public static String trim(CharSequence input) {
      if (input == null) {
         return null;
      }
      return StringFunctions.TRIM.apply(input.toString());
   }

   /**
    * Unescape string.
    *
    * @param input           the input
    * @param escapeCharacter the escape character
    * @return the string
    */
   public static String unescape(String input, char escapeCharacter) {
      if (input == null) {
         return null;
      }
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < input.length(); ) {
         if (input.charAt(i) == escapeCharacter) {
            builder.append(input.charAt(i + 1));
            i = i + 2;
         } else {
            builder.append(input.charAt(i));
            i++;
         }
      }
      return builder.toString();
   }


}// END OF StringUtils

