package com.gengoai.string;

import java.util.regex.Pattern;

/**
 * <p>Commonly used Regex classes and constructs for building Patterns.</p>
 *
 * @author David B. Bracewell
 */
public final class Re {
   /**
    * An unescaped period representing match anything.
    */
   public static final String ANY = ".";
   /**
    * Backlash character
    */
   public static final String BACKSLASH = "\\";
   /**
    * The constant CARRIAGE_RETURN.
    */
   public static final String CARRIAGE_RETURN = "\f";
   /**
    * Unicode close punctuation
    */
   public static final String CLOSE_PUNCTUATION = "\\p{Pe}";
   /**
    * Unicode connector punctuation
    */
   public static final String CONNECTOR_PUNCTUATION = "\\p{Pc}";
   /**
    * Unicode currency characters
    */
   public static final String CURRENCY_SYMBOL = "\\p{Sc}";
   /**
    * Unicode dash punctuation
    */
   public static final String DASH_PUNCTUATION = "\\p{Pd}";
   /**
    * Unicode digits
    */
   public static final String DIGIT = "\\p{Nd}";
   /**
    * Escaped backslash
    */
   public static final String ESC_BACKSLASH = "\\\\";
   /**
    * Unicode final punctuation
    */
   public static final String FINAL_PUNCTUATION = "\\p{Pf}";
   /**
    * The constant FORM_FEED.
    */
   public static final String FORM_FEED = "\f";
   /**
    * Unicode initial punctuation
    */
   public static final String INITIAL_PUNCTUATION = "\\p{Pi}";
   /**
    * Unicode letter
    */
   public static final String LETTER = "\\p{L}";
   /**
    * The constant LINE_FEED.
    */
   public static final String LINE_FEED = "\n";
   /**
    * Unicode lowercase letter
    */
   public static final String LOWERCASE_LETTER = "\\p{Ll}";
   /**
    * Unicode mark characters.
    */
   public static final String MARK = "\\p{m}";
   /**
    * Unicode math symbols
    */
   public static final String MATH_SYMBOL = "\\p{Sm}";
   /**
    * Unicode modifier symbols
    */
   public static final String MODIFIER_SYMBOL = "\\p{Sk}";
   /**
    * Unicode numbers
    */
   public static final String NUMBER = "\\p{N}";
   /**
    * Unicode open punctuation
    */
   public static final String OPEN_PUNCTUATION = "\\p{Ps}";
   /**
    * Unicode other symbols
    */
   public static final String OTHER_SYMBOL = "\\p{So}";
   /**
    * Unicode punctuation
    */
   public static final String PUNCTUATION = "\\p{P}";
   /**
    * The quote character.
    */
   public static final String QUOTE = "\"";
   /**
    * Unicode symbol characters
    */
   public static final String SYMBOL = "\\p{S}";
   /**
    * The constant TAB.
    */
   public static final String TAB = "\t";
   /**
    * The constant UNICODE_WHITESPACE.
    */
   public static final String UNICODE_WHITESPACE = "\\p{Z}";
   /**
    * Unicode uppercase letter
    */
   public static final String UPPERCASE_LETTER = "\\p{Lu}";
   /**
    * matches unicode whitespace.
    */
   public static final String WHITESPACE = chars(UNICODE_WHITESPACE,
                                                 LINE_FEED,
                                                 FORM_FEED,
                                                 CARRIAGE_RETURN,
                                                 TAB);
   /**
    * The constant MULTIPLE_WHITESPACE.
    */
   public static final String MULTIPLE_WHITESPACE = WHITESPACE + "+";
   /**
    * Word boundary
    */
   public static final String WORD_BOUNDARY = "\b";
   /**
    * The constant ZERO_OR_MORE_WHITESPACE.
    */
   public static final String ZERO_OR_MORE_WHITESPACE = WHITESPACE + "*";

   private Re() {
      throw new IllegalAccessError();
   }


   /**
    * Converts the given array of strings into a regex character class.
    *
    * @param negated True if the class should be negated.
    * @param chars   the components of the character class
    * @return the character class
    */
   public static String chars(boolean negated, String... chars) {
      StringBuilder builder = new StringBuilder("[");
      if (negated) {
         builder.append("^");
      }
      builder.append(String.join("", chars));
      builder.append("]");
      return builder.toString();
   }

   /**
    * Converts the given array of characters into a regex character class.
    *
    * @param negated True if the class should be negated.
    * @param chars   the components of the character class
    * @return the character class
    */
   public static String chars(boolean negated, char... chars) {
      StringBuilder out = new StringBuilder("[");
      if (negated) {
         out.append("^");
      }
      for (char c : chars) {
         out.append(c);
      }
      return out.append("]").toString();
   }

   /**
    * Converts the given array of strings into a regex character class.
    *
    * @param chars the components of the character class
    * @return the character class
    */
   public static String chars(String... chars) {
      return chars(false, chars);
   }

   /**
    * Converts the given array of chars into a regex character class.
    *
    * @param chars the components of the character class
    * @return the character class
    */
   public static String chars(char... chars) {
      return chars(false, chars);
   }

   /**
    * Defines the given regex as a named match group.
    *
    * @param groupName the group name
    * @param regex     the regex
    * @return the named match group
    */
   public static String namedGroup(String groupName, String regex) {
      return String.format("(?<%s> %s)", groupName, regex);
   }

   /**
    * Defines the given regex as a non-matching group
    *
    * @param regex the regex
    * @return the non-matching group
    */
   public static String nonMatchingGroup(String regex) {
      return String.format("(?:%s)", regex);
   }

   /**
    * Defines a negative lookahead for the given regex.
    *
    * @param regex the regex
    * @return the regex
    */
   public static String negLookahead(String regex) {
      return String.format("(?! %s)", regex);
   }

   /**
    * Defines a negative non-consuming lookahead for the given regex.
    *
    * @param regex the regex
    * @return the regex
    */
   public static String negLookbehind(String... regex) {
      return String.format("(?<! %s)", String.join("", regex));
   }

   /**
    * Defines a positive lookahead for the given regex.
    *
    * @param regex the regex
    * @return the regex
    */
   public static String posLookahead(String... regex) {
      return String.format("(?= %s)", String.join("", regex));
   }

   /**
    * Defines a non-consuming positive lookahead for the given regex.
    *
    * @param regex the regex
    * @return the regex
    */
   public static String posLookbehind(String... regex) {
      return String.format("(?<= %s)", String.join("", regex));
   }


   /**
    * Combines the given regex patterns into a sequence.
    *
    * @param regex the regex
    * @return the string
    */
   public static String seq(String... regex) {
      return String.join("", regex);
   }

   /**
    * Combines the given regex patterns as alternations. Should be wrapped as a group.
    *
    * @param regex the regex
    * @return the alternation
    */
   public static String alt(String... regex) {
      return String.join("|", regex);
   }


   /**
    * Compiles the given patterns, treating them as a sequence, with the given flags.
    *
    * @param flags    the flags
    * @param patterns the patterns
    * @return the pattern
    */
   public static Pattern compile(int flags, String... patterns) {
      return Pattern.compile(String.join("", patterns), flags);
   }

   /**
    * Compiles the given patterns, treating them as a sequence.
    *
    * @param patterns the patterns
    * @return the pattern
    */
   public static Pattern compile(String... patterns) {
      return Pattern.compile(String.join("", patterns));
   }

}//END OF Regex