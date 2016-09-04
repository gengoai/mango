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
    * The constant QUOTE.
    */
   Regex QUOTE = re("\"");

   /**
    * The constant ANY.
    */
   Regex ANY = re(".");

   /**
    * The constant WHITESPACE.
    */
   Regex WHITESPACE = re("\\p{Z}\t\n\r\f");
   /**
    * The constant LETTER.
    */
   Regex LETTER = re("\\p{L}");
   /**
    * The constant LOWERCASE_LETTER.
    */
   Regex LOWERCASE_LETTER = re("\\p{Ll}");
   /**
    * The constant UPPERCASE_LETTER.
    */
   Regex UPPERCASE_LETTER = re("\\p{Lu}");
   /**
    * The constant MARK.
    */
   Regex MARK = re("\\p{m}");
   /**
    * The constant SYMBOL.
    */
   Regex SYMBOL = re("\\p{S}");
   /**
    * The constant MATH_SYMBOL.
    */
   Regex MATH_SYMBOL = re("\\p{Sm}");
   /**
    * The constant CURRENCY_SYMBOL.
    */
   Regex CURRENCY_SYMBOL = re("\\p{Sc}");
   /**
    * The constant MODIFIER_SYMBOL.
    */
   Regex MODIFIER_SYMBOL = re("\\p{Sk}");
   /**
    * The constant OTHER_SYMBOL.
    */
   Regex OTHER_SYMBOL = re("\\p{So}");
   /**
    * The constant NUMBER.
    */
   Regex NUMBER = re("\\p{N}");
   /**
    * The constant DIGIT.
    */
   Regex DIGIT = re("\\p{Nd}");
   /**
    * The constant PUNCTUATION.
    */
   Regex PUNCTUATION = re("\\p{P}");
   /**
    * The constant DASH_PUNCTUATION.
    */
   Regex DASH_PUNCTUATION = re("\\p{Pd}");
   /**
    * The constant OPEN_PUNCTUATION.
    */
   Regex OPEN_PUNCTUATION = re("\\p{Ps}");
   /**
    * The constant CLOSE_PUNCTUATION.
    */
   Regex CLOSE_PUNCTUATION = re("\\p{Pe}");
   /**
    * The constant INITIAL_PUNCTUATION.
    */
   Regex INITIAL_PUNCTUATION = re("\\p{Pi}");
   /**
    * The constant FINAL_PUNCTUATION.
    */
   Regex FINAL_PUNCTUATION = re("\\p{Pf}");
   /**
    * The constant CONNECTOR_PUNCTUATION.
    */
   Regex CONNECTOR_PUNCTUATION = re("\\p{Pc}");
   /**
    * The constant BACKSLASH.
    */
   Regex BACKSLASH = re("\\");

   /**
    * The constant DOUBLE_BACKSLASH.
    */
   Regex DOUBLE_BACKSLASH = re("\\\\");

   /**
    * The constant WORD_BOUNDRY.
    */
   Regex WORD_BOUNDRY = re("\b");

   /**
    * The constant NON_WORD_BOUNDRY.
    */
   Regex NON_WORD_BOUNDRY = re("\\B");

   /**
    * Pos look ahead regex.
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex posLookAhead(@NonNull Regex regex) {
      return re("(?=" + regex.toString() + ")");
   }

   /**
    * Neg look ahead regex.
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex negLookAhead(@NonNull Regex regex) {
      return re("(?!" + regex.toString() + ")");
   }

   /**
    * Pos look behind regex.
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex posLookBehind(@NonNull Regex regex) {
      return re("(?<=" + regex.toString() + ")");
   }

   /**
    * Neg look behind regex.
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex negLookBehind(@NonNull Regex regex) {
      return re("(?<!" + regex.toString() + ")");
   }


   /**
    * Chars regex.
    *
    * @param first  the first
    * @param others the others
    * @return the regex
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
    * Chars regex.
    *
    * @param chars the chars
    * @return the regex
    */
   static Regex chars(String chars) {
      return chars(chars, false);
   }

   /**
    * Chars regex.
    *
    * @param chars  the chars
    * @param negate the negate
    * @return the regex
    */
   static Regex chars(String chars, boolean negate) {
      if (chars != null) {
         return new Regex("[" + (negate ? "^" : "") + chars + "]", false);
      }
      return new Regex(null, false);
   }

   /**
    * Re regex.
    *
    * @param pattern the pattern
    * @return the regex
    */
   static Regex re(String pattern) {
      return new Regex(pattern, false);
   }


   /**
    * Begin line regex.
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex beginLine(Regex regex) {
      if (regex == null) {
         return re("^");
      }
      return re("^" + regex.toString());
   }


   /**
    * Quote regex.
    *
    * @param word the word
    * @return the regex
    */
   static Regex quote(String word) {
      return new Regex(word, true);
   }

   /**
    * Or regex.
    *
    * @param first   the first
    * @param regexes the regexes
    * @return the regex
    */
   static Regex or(@NonNull Regex first, Regex... regexes) {
      StringBuilder pattern = new StringBuilder(first.toString());
      if (regexes != null) {
         for (Regex rp : regexes) {
            if (rp != null) {
               pattern.append("|").append(rp.toString());
            }
         }
      }
      return nmGroup(re(pattern.toString()));
   }

   /**
    * And regex.
    *
    * @param first   the first
    * @param regexes the regexes
    * @return the regex
    */
   static Regex and(@NonNull Regex first, Regex... regexes) {
      StringBuilder pattern = new StringBuilder(first.toString());
      if (regexes != null) {
         for (Regex rp : regexes) {
            if (rp != null) {
               pattern.append("&&").append(rp.toString());
            }
         }
      }
      return re(pattern.toString());
   }

   /**
    * Group regex.
    *
    * @param first   the first
    * @param regexes the regexes
    * @return the regex
    */
   static Regex group(@NonNull Regex first, Regex... regexes) {
      return group(null, first, regexes);
   }

   /**
    * Non matching group regex.
    *
    * @param first   the first
    * @param regexes the regexes
    * @return the regex
    */
   static Regex nmGroup(@NonNull Regex first, Regex... regexes) {
      String pattern = first.toString();
      if (regexes != null) {
         for (Regex rp : regexes) {
            if (rp != null) {
               pattern += rp.toString();
            }
         }
      }
      return re("(?:" + pattern + ")");
   }


   /**
    * Group regex.
    *
    * @param name    the name
    * @param first   the first
    * @param regexes the regexes
    * @return the regex
    */
   static Regex group(String name, @NonNull Regex first, Regex... regexes) {
      String pattern = first.toString();
      if (regexes != null) {
         for (Regex rp : regexes) {
            if (rp != null) {
               pattern += rp.toString();
            }
         }
      }
      return re("(" + (StringUtils.isNotNullOrBlank(name) ? "?<" + name + ">" : StringUtils.EMPTY) + pattern + ")");
   }

   /**
    * Seq regex.
    *
    * @param first   the first
    * @param regexes the regexes
    * @return the regex
    */
   static Regex seq(@NonNull Regex first, Regex... regexes) {
      StringBuilder pattern = new StringBuilder(first.toString());
      if (regexes != null) {
         for (Regex rp : regexes) {
            if (rp != null) {
               pattern.append(rp.toString());
            }
         }
      }
      return re(pattern.toString());
   }

   /**
    * Not regex.
    *
    * @param regex the regex
    * @return the regex
    */
   static Regex not(Regex regex) {
      return regex.not();
   }


}//END OF Re
