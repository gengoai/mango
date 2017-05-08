package com.davidbracewell;

import com.davidbracewell.string.StringUtils;
import lombok.NonNull;

import static com.davidbracewell.Regex.toChars;

/**
 * <p>DSL for building regular expressions.</p>
 *
 * @author David B. Bracewell
 */
public interface Re {
   /**
    * The quote character.
    */
   Regex QUOTE = re("\"");
   /**
    * An unescaped period representing match anything.
    */
   Regex ANY = re(".");
   /**
    * matches unicode whitespace.
    */
   Regex WHITESPACE = chars("\\p{Z}\t\n\r\f");
   /**
    * Unicode letter
    */
   Regex LETTER = re("\\p{L}");
   /**
    * Unicode lowercase letter
    */
   Regex LOWERCASE_LETTER = re("\\p{Ll}");
   /**
    * Unicode uppercase letter
    */
   Regex UPPERCASE_LETTER = re("\\p{Lu}");
   /**
    * Unicode mark characters.
    */
   Regex MARK = re("\\p{m}");
   /**
    * Unicode symbol characters
    */
   Regex SYMBOL = re("\\p{S}");
   /**
    * Unicode math symbols
    */
   Regex MATH_SYMBOL = re("\\p{Sm}");
   /**
    * Unicode currency characters
    */
   Regex CURRENCY_SYMBOL = re("\\p{Sc}");
   /**
    * Unicode modifier symbols
    */
   Regex MODIFIER_SYMBOL = re("\\p{Sk}");
   /**
    * Unicode other symbols
    */
   Regex OTHER_SYMBOL = re("\\p{So}");
   /**
    * Unicode numbers
    */
   Regex NUMBER = re("\\p{N}");
   /**
    * Unicode digits
    */
   Regex DIGIT = re("\\p{Nd}");
   /**
    * Unicode punctuation
    */
   Regex PUNCTUATION = re("\\p{P}");
   /**
    * Unicode dash punctuation
    */
   Regex DASH_PUNCTUATION = re("\\p{Pd}");
   /**
    * Unicode open punctuation
    */
   Regex OPEN_PUNCTUATION = re("\\p{Ps}");
   /**
    * Unicode close punctuation
    */
   Regex CLOSE_PUNCTUATION = re("\\p{Pe}");
   /**
    * Unicode initial punctuation
    */
   Regex INITIAL_PUNCTUATION = re("\\p{Pi}");
   /**
    * Unicode final punctuation
    */
   Regex FINAL_PUNCTUATION = re("\\p{Pf}");
   /**
    * Unicode connector punctuation
    */
   Regex CONNECTOR_PUNCTUATION = re("\\p{Pc}");
   /**
    * Backlash character
    */
   Regex BACKSLASH = re("\\");
   /**
    * Escaped backslash
    */
   Regex ESC_BACKSLASH = re("\\\\");
   /**
    * Word boundary
    */
   Regex WORD_BOUNDARY = re("\b");

   /**
    * Non-consuming positive lookahead
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex posLookahead(@NonNull Regex regex) {
      return re("(?=" + regex.toString() + ")");
   }

   /**
    * Non-consuming negative lookahead
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex negLookahead(@NonNull Regex regex) {
      return re("(?!" + regex.toString() + ")");
   }

   /**
    * Non-consuming positive lookbehind
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex posLookbehind(@NonNull Regex regex) {
      return re("(?<=" + regex.toString() + ")");
   }

   /**
    * Non-consuming negative lookbehind
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex negLookbehind(@NonNull Regex regex) {
      return re("(?<!" + regex.toString() + ")");
   }


   /**
    * Creates character class out of the supplied regular expressions.
    *
    * @param first  the first (at least one is required) regex
    * @param others the other regex making up the character class
    * @return the character class regex
    */
   static Regex chars(@NonNull Regex first, Regex... others) {
      StringBuilder p = new StringBuilder(toChars(first.toString()));
      if (others != null) {
         for (Regex other : others) {
            if (other != null) {
               p.append(toChars(other.toString()));
            }
         }
      }
      return chars(p.toString());
   }


   /**
    * Creates character class out of the supplied regular expressions.
    *
    * @param chars the string of characters making up the character class
    * @return the character class regex
    */
   static Regex chars(@NonNull String chars) {
      return chars(chars, false);
   }

   /**
    * Creates character class out of the supplied regular expressions.
    *
    * @param chars  the string of characters making up the character class
    * @param negate true if the character class should be negated, false if not negated
    * @return the character class regex
    */
   static Regex chars(@NonNull String chars, boolean negate) {
      if (chars != null) {
         return new Regex("[" + (negate ? "^" : "") + chars + "]", false);
      }
      return new Regex(null, false);
   }

   /**
    * Creates a regex object for the supplied pattern
    *
    * @param pattern the regular expression pattern
    * @return the regex
    */
   static Regex re(@NonNull String pattern) {
      return new Regex(pattern, false);
   }


   /**
    * Creates a regex that must match at the beginning of the line.
    *
    * @param regex the regex to match starting at the beginning of the line
    * @return the regex
    */
   static Regex beginLine(@NonNull Regex regex) {
      return re("^" + regex.toString());
   }


   /**
    * Creates a regular expression that is a quoted version of the supplied pattern.
    *
    * @param pattern the regular expression pattern
    * @return the regex with the supplied pattern quoted
    */
   static Regex quote(@NonNull String pattern) {
      return new Regex(pattern, true);
   }

   /**
    * Creates a non-matching group of the supplied regex ored together.
    *
    * @param first  the first (at least one is required) regex
    * @param others the other regex making up the or group
    * @return the non-matching group of regex that are ored together
    */
   static Regex or(@NonNull Regex first, Regex... others) {
      StringBuilder pattern = new StringBuilder(first.toString());
      if (others != null) {
         for (Regex rp : others) {
            if (rp != null) {
               pattern.append("|").append(rp.toString());
            }
         }
      }
      return nmGroup(re(pattern.toString()));
   }

   /**
    * Ands together the supplied regular expression
    *
    * @param first  the first (at least one is required) regex
    * @param others the other regex making up the or group
    * @return the regular expression anded together
    */
   static Regex and(@NonNull Regex first, Regex... others) {
      StringBuilder pattern = new StringBuilder(first.toString());
      if (others != null) {
         for (Regex rp : others) {
            if (rp != null) {
               pattern.append("&&").append(rp.toString());
            }
         }
      }
      return re(pattern.toString());
   }

   /**
    * Creates a matching group of the supplied regular expressions
    *
    * @param first  the first (at least one is required) regex
    * @param others the other regex making up the or group
    * @return the regex
    */
   static Regex group(@NonNull Regex first, Regex... others) {
      return group(null, first, others);
   }

   /**
    * Creates a non-matching group of the supplied regular expressions
    *
    * @param first  the first (at least one is required) regex
    * @param others the other regex making up the or group
    * @return the regex
    */
   static Regex nmGroup(@NonNull Regex first, Regex... others) {
      String pattern = first.toString();
      if (others != null) {
         for (Regex rp : others) {
            if (rp != null) {
               pattern += rp.toString();
            }
         }
      }
      return re("(?:" + pattern + ")");
   }


   /**
    * Creates a matching group of the supplied regular expressions. If the supplied name is not null or blank, the group
    * will be named.
    *
    * @param first  the first (at least one is required) regex
    * @param others the other regex making up the or group
    * @return the regex
    */
   static Regex group(String name, @NonNull Regex first, Regex... others) {
      String pattern = first.toString();
      if (others != null) {
         for (Regex rp : others) {
            if (rp != null) {
               pattern += rp.toString();
            }
         }
      }
      return re("(" + (StringUtils.isNotNullOrBlank(name) ? "?<" + name + ">" : StringUtils.EMPTY) + pattern + ")");
   }

   /**
    * Creates a sequence (i.e. concatenation) of the supplied regular expressions.
    *
    * @param first  the first (at least one is required) regex
    * @param others the other regex making up the or sequence
    * @return the regex
    */
   static Regex seq(@NonNull Regex first, Regex... others) {
      StringBuilder pattern = new StringBuilder(first.toString());
      if (others != null) {
         for (Regex rp : others) {
            if (rp != null) {
               pattern.append(rp.toString());
            }
         }
      }
      return re(pattern.toString());
   }

   /**
    * Negates the supplied regular expression.
    *
    * @param regex the regular expression
    * @return the negated regex
    */
   static Regex not(@NonNull Regex regex) {
      return regex.not();
   }


}//END OF Re
