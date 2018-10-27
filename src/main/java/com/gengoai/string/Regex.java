package com.gengoai.string;

import java.util.regex.Pattern;

/**
 * The type Regex.
 *
 * @author David B. Bracewell
 */
public final class Regex {

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

   private Regex() {
      throw new IllegalAccessError();
   }


   /**
    * Chars string.
    *
    * @param negated the negated
    * @param chars   the chars
    * @return the string
    */
   public static String chars(boolean negated, String... chars) {
      if (negated) {
         return "[^" + String.join("", chars) + "]";
      }
      return "[" + String.join("", chars) + "]";
   }

   /**
    * Chars string.
    *
    * @param negated the negated
    * @param chars   the chars
    * @return the string
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
    * Chars string.
    *
    * @param chars the chars
    * @return the string
    */
   public static String chars(String... chars) {
      return chars(false, chars);
   }

   /**
    * Chars string.
    *
    * @param chars the chars
    * @return the string
    */
   public static String chars(char... chars) {
      return chars(false, chars);
   }

   public static String namedGroup(String groupName, String... regex) {
      return String.format("(?<%s> %s)", groupName, String.join("", regex));
   }

   /**
    * Non-consuming negative lookahead
    *
    * @param regex the regex
    * @return the regex
    */
   public static String negLookahead(String... regex) {
      return String.format("(?! %s)", String.join("", regex));
   }

   /**
    * Non-consuming negative lookbehind
    *
    * @param regex the regex
    * @return the regex
    */
   public static String negLookbehind(String... regex) {
      return String.format("(?<! %s)", String.join("", regex));
   }

   /**
    * Non-consuming positive lookahead
    *
    * @param regex the regex
    * @return the regex
    */
   public static String posLookahead(String... regex) {
      return String.format("(?= %s)", String.join("", regex));
   }

   /**
    * Non-consuming positive lookbehind
    *
    * @param regex the regex
    * @return the regex
    */
   public static String posLookbehind(String... regex) {
      return String.format("(?<= %s)", String.join("", regex));
   }

   public static String q(String... regex) {
      return String.format("\\Q%s\\E", String.join("", regex));
   }

   /**
    * Re pattern.
    *
    * @param flags    the flags
    * @param patterns the patterns
    * @return the pattern
    */
   public static Pattern re(int flags, String... patterns) {
      return Pattern.compile(String.join("", patterns), flags);
   }

   /**
    * Re pattern.
    *
    * @param patterns the patterns
    * @return the pattern
    */
   public static Pattern re(String... patterns) {
      return Pattern.compile(String.join("", patterns));
   }


}//END OF Regex
