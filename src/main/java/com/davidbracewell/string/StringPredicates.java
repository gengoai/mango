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

import com.google.common.base.Predicates;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * Commonly used predicates over strings.
 *
 * @author David B. Bracewell
 */
public enum StringPredicates implements Predicate<CharSequence> {
  /**
   * True if the input string is null
   */
  IS_NULL {
    @Override
    public boolean test(CharSequence input) {
      return input == null;
    }
  },
  /**
   * Determines if the string is null or empty, i.e. length of 0
   */
  IS_NULL_OR_EMPTY {
    @Override
    public boolean test(CharSequence input) {
      return Strings.isNullOrEmpty(input.toString());
    }
  },
  /**
   * True if the input string is null or blank (trimmed version is empty)
   */
  IS_NULL_OR_BLANK {
    @Override
    public boolean test(CharSequence input) {
      return IS_NULL.test(input) || StringUtils.trim(input.toString()).isEmpty();
    }
  },
  /**
   * True if the input string is in all lower case
   */
  IS_LOWER_CASE {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        if (!Character.isLowerCase(input.charAt(i))) {
          return false;
        }
      }
      return true;
    }
  },
  /**
   * True if the input string is in all upper case
   */
  IS_UPPER_CASE {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        if (!Character.isUpperCase(input.charAt(i))) {
          return false;
        }
      }
      return true;
    }
  },
  /**
   * True if the input string is all letter or digits
   */
  IS_LETTER_OR_DIGIT {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        if (!Character.isAlphabetic((int) c) && !Character.isDigit((int) c)) {
          return false;
        }
      }
      return true;
    }
  },
  /**
   * True if the input string is all letter or digits
   */
  IS_LETTER_OR_WHITESPACE {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        if (!Character.isAlphabetic((int) c) && !Character.isWhitespace((int) c)) {
          return false;
        }
      }
      return true;
    }
  },
  /**
   * True if the input string is all letters
   */
  IS_LETTER {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        if (!Character.isAlphabetic((int) c)) {
          return false;
        }
      }
      return true;
    }
  },
  /**
   * True if the input string is all digits
   */
  IS_DIGIT {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        if (!Character.isDigit((int) c)) {
          return false;
        }
      }
      return true;
    }
  },
  /**
   * True if the input string is all punctuation
   */
  IS_PUNCTUATION {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        char c = input.charAt(i);
        if (!StringUtils.isPunctuation(c)) {
          return false;
        }
      }
      return true;
    }
  },
  /**
   * True if the first character in the string is upper case
   */
  HAS_INITIAL_CAPITAL_LETTER {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      return Character.isUpperCase(input.charAt(0));
    }
  },
  /**
   * True if any character in the string is upper case
   */
  HAS_CAPITAL_LETTER {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        if (Character.isUpperCase(input.charAt(i))) {
          return true;
        }
      }
      return false;
    }
  },
  /**
   * True if any character in the string is punctuation
   */
  HAS_PUNCTUATION {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        if (StringUtils.isPunctuation(input.charAt(i))) {
          return true;
        }
      }
      return false;
    }
  },
  /**
   * True if any character in the string is a digit
   */
  HAS_DIGIT {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        if (Character.isDigit(input.charAt(i))) {
          return true;
        }
      }
      return false;
    }
  },
  /**
   * True if any character in the string is a letter
   */
  HAS_LETTER {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        if (Character.isLetter(input.charAt(i))) {
          return true;
        }
      }
      return false;
    }
  },
  /**
   * True if any character in the string is a letter or digit
   */
  HAS_LETTER_OR_DIGIT {
    @Override
    public boolean test(CharSequence input) {
      if (IS_NULL_OR_BLANK.test(input)) {
        return false;
      }
      for (int i = 0; i < input.length(); i++) {
        if (Character.isLetter(input.charAt(i)) || Character.isDigit(input.charAt(i))) {
          return true;
        }
      }
      return false;
    }
  };


  /**
   * Returns a method that performs an exact match on two charsequences
   *
   * @param toMatch the sequence to match
   * @return the predicate
   */
  public static Predicate<CharSequence> MATCHES(String toMatch) {
    return MATCHES(toMatch, true);
  }

  /**
   * Returns a method that performs a match on two charsequences using {@link String#equals(Object)} or {@link
   * String#equalsIgnoreCase(String)}
   *
   * @param toMatch       the sequence to match
   * @param caseSensitive True case sensitive match, False case insensitive
   * @return the predicate
   */
  public static Predicate<CharSequence> MATCHES(String toMatch, boolean caseSensitive) {
    return new MatchPredicate(StringFunctions.NULL_TO_EMPTY.apply(toMatch), caseSensitive);
  }

  /**
   * Returns a method that checks if a text is contained an given string.
   *
   * @param text The text to check for
   * @return the predicate
   */
  public static Predicate<CharSequence> CONTAINS(String text) {
    return CONTAINS(text, true);
  }


  /**
   * Returns a method that checks if a text is contained an given string.
   *
   * @param text          The text to check for
   * @param caseSensitive True case sensitive match, False case insensitive
   * @return the predicate
   */
  public static Predicate<CharSequence> CONTAINS(String text, boolean caseSensitive) {
    return new ContainsPredicate(caseSensitive, text);
  }


  /**
   * Creates a predicate that matches a given regular expression
   *
   * @param pattern the pattern to match
   * @return the predicate
   */
  public static Predicate<CharSequence> REGEX_MATCH(String pattern) {
    return Predicates.containsPattern(pattern)::apply;
  }

  /**
   * Creates a predicate that matches the beginning of a string
   *
   * @param toMatch       the sequence to match
   * @param caseSensitive True case sensitive match, False case insensitive
   * @return the predicate
   */
  public static Predicate<CharSequence> STARTS_WITH(String toMatch, boolean caseSensitive) {
    return new StartsWithPredicate(StringFunctions.NULL_TO_EMPTY.apply(toMatch), caseSensitive);
  }

  /**
   * Creates a predicate that matches the ending of a string
   *
   * @param toMatch       the sequence to match
   * @param caseSensitive True case sensitive match, False case insensitive
   * @return the predicate
   */
  public static Predicate<CharSequence> ENDS_WITH(String toMatch, boolean caseSensitive) {
    return new EndsWithPredicate(StringFunctions.NULL_TO_EMPTY.apply(toMatch), caseSensitive);
  }


  private static class StartsWithPredicate implements Predicate<CharSequence>, Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean caseSensitive;
    private final String toMatch;

    private StartsWithPredicate(String toMatch, boolean caseSensitive) {
      this.toMatch = caseSensitive ? toMatch : toMatch.toLowerCase();
      this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean test(CharSequence input) {
      if (input == null) {
        return false;
      } else if (caseSensitive) {
        return input.toString().startsWith(toMatch);
      }
      return input.toString().toLowerCase().startsWith(toMatch);
    }
  }

  private static class EndsWithPredicate implements Predicate<CharSequence>, Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean caseSensitive;
    private final String toMatch;

    private EndsWithPredicate(String toMatch, boolean caseSensitive) {
      this.toMatch = caseSensitive ? toMatch : toMatch.toLowerCase();
      this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean test(CharSequence input) {
      if (input == null) {
        return false;
      } else if (caseSensitive) {
        return input.toString().endsWith(toMatch);
      }
      return input.toString().toLowerCase().endsWith(toMatch);
    }
  }

  private static class MatchPredicate implements Predicate<CharSequence>, Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean caseSensitive;
    private final String toMatch;

    private MatchPredicate(String toMatch, boolean caseSensitive) {
      this.toMatch = toMatch;
      this.caseSensitive = caseSensitive;
    }

    @Override
    public boolean test(CharSequence input) {
      if (input == null) {
        return false;
      } else if (caseSensitive) {
        return input.toString().equals(toMatch);
      }
      return input.toString().equalsIgnoreCase(toMatch);
    }
  }

  private static class ContainsPredicate implements Predicate<CharSequence>, Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean caseSensitive;
    private final String toMatch;

    private ContainsPredicate(boolean caseSensitive, String toMatch) {
      this.caseSensitive = caseSensitive;
      this.toMatch = caseSensitive ? toMatch : toMatch.toLowerCase();
    }

    @Override
    public boolean test(CharSequence input) {
      if (input == null) {
        return false;
      }

      if (caseSensitive) {
        return input.toString().contains(toMatch);
      }

      return input.toString().toLowerCase().contains(toMatch);
    }

  }

}//END OF StringPredicates
