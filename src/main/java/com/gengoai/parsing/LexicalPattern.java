package com.gengoai.parsing;

import com.gengoai.Regex;
import com.gengoai.Validation;
import com.gengoai.string.CharMatcher;
import lombok.Value;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gengoai.Validation.notNull;

/**
 * <p>Methodology to find matches for patterns in CharSequences</p>
 *
 * @author David B. Bracewell
 */
public abstract class LexicalPattern implements Serializable {
   /**
    * The constant returned when no match is found.
    */
   public static final int NO_MATCH = -1;
   private static final long serialVersionUID = 1L;

   /**
    * Creates a literal pattern from the given character where a match exists when the character is found
    *
    * @param literal the literal
    * @return the lexical pattern
    */
   public static LexicalPattern charLiteral(char literal) {
      return new CharLiteralPattern(CharMatcher.anyOf(Character.toString(literal)));
   }

   /**
    * Creates a literal pattern from the given character predicate where a match exists when the character predicate
    * evaluates to true. Maximum match is one character.
    *
    * @param literal the literal pattern
    * @return the lexical pattern
    */
   public static LexicalPattern charLiteral(CharMatcher literal) {
      return new CharLiteralPattern(notNull(literal));
   }

   /**
    * Creates a pattern from the given character predicate where a matches are contiguous spans of characters that
    * evaluate to true for the given predicate.
    *
    * @param predicate the predicate to match
    * @return the lexical pattern
    */
   public static LexicalPattern charPredicate(CharMatcher predicate) {
      return new CharPredicatePattern(notNull(predicate));
   }

   /**
    * Creates a pattern that checks for a regular expression match
    *
    * @param pattern the regular expression pattern
    * @return the lexical pattern
    */
   public static LexicalPattern regex(Pattern pattern) {
      return new RegexPattern(notNull(pattern));
   }

   /**
    * Creates a pattern that checks for a regular expression match
    *
    * @param pattern the regular expression pattern
    * @return the lexical pattern
    */
   public static LexicalPattern regex(String pattern) {
      return new RegexPattern(Pattern.compile(pattern));
   }

   /**
    * Creates a pattern that checks for a regular expression match
    *
    * @param pattern the regular expression pattern
    * @return the lexical pattern
    */
   public static LexicalPattern regex(Regex pattern) {
      return new RegexPattern(notNull(pattern).toPattern());
   }

   /**
    * Creates a literal pattern from the given string where a match exists when the entire literal string is found.
    *
    * @param literal the literal pattern
    * @return the lexical pattern
    */
   public static LexicalPattern stringLiteral(String literal) {
      return new LiteralPattern(notNull(literal));
   }

   /**
    * Determines the length of a potential match for this pattern in the given <code>CharSequence</code> starting at the
    * given start position. Returns <code>NO_MATCH</code> when no match is found.
    *
    * @param sequence the character sequence to match against
    * @param start    the starting position in the character sequence to begin matching
    * @return the length of the match or <code>NO_MATCH</code>
    */
   public abstract int match(CharSequence sequence, int start);

   @Value
   private static class LiteralPattern extends LexicalPattern {
      private static final long serialVersionUID = 1L;
      private final String literal;

      private LiteralPattern(String literal) {
         this.literal = literal;
      }

      @Override
      public int match(CharSequence sequence, int start) {
         Validation.checkElementIndex(start, sequence.length());
         if (literal.length() > (sequence.length() - start)) {
            return NO_MATCH;
         }
         for (int i = 0; i < literal.length(); i++) {
            if (sequence.charAt(i + start) != literal.charAt(i)) {
               return NO_MATCH;
            }
         }
         return literal.length();
      }
   }

   private static class CharLiteralPattern extends LexicalPattern {
      private static final long serialVersionUID = 1L;
      private final CharMatcher predicate;

      public CharLiteralPattern(CharMatcher predicate) {
         this.predicate = predicate;
      }

      protected boolean canEqual(Object other) {
         return other instanceof CharLiteralPattern;
      }

      public boolean equals(Object o) {
         if (o == this) return true;
         if (!(o instanceof CharLiteralPattern)) return false;
         final CharLiteralPattern other = (CharLiteralPattern) o;
         if (!other.canEqual((Object) this)) return false;
         final Object this$predicate = this.getPredicate();
         final Object other$predicate = other.getPredicate();
         if (this$predicate == null ? other$predicate != null : !this$predicate.equals(other$predicate)) return false;
         return true;
      }

      public CharMatcher getPredicate() {
         return this.predicate;
      }

      public int hashCode() {
         final int PRIME = 59;
         int result = 1;
         final Object $predicate = this.getPredicate();
         result = result * PRIME + ($predicate == null ? 43 : $predicate.hashCode());
         return result;
      }

      @Override
      public int match(CharSequence sequence, int start) {
         Validation.checkElementIndex(start, sequence.length());
         return predicate.test(sequence.charAt(start)) ? 1 : NO_MATCH;
      }

      public String toString() {
         return "LexicalPattern.CharLiteralPattern(predicate=" + this.getPredicate() + ")";
      }
   }

   @Value
   private static class CharPredicatePattern extends LexicalPattern {
      private static final long serialVersionUID = 1L;
      private final CharMatcher pattern;

      private CharPredicatePattern(CharMatcher pattern) {
         this.pattern = pattern;
      }

      @Override
      public int match(CharSequence sequence, int start) {
         Validation.checkElementIndex(start, sequence.length());
         int length = start;
         while (length < sequence.length() && pattern.test(sequence.charAt(length))) {
            length++;
         }
         return length == start ? NO_MATCH : (length - start);
      }
   }

   @Value
   private static class RegexPattern extends LexicalPattern {
      private static final long serialVersionUID = 1L;
      private final Pattern pattern;

      @Override
      public int match(CharSequence sequence, int start) {
         Validation.checkElementIndex(start, sequence.length());
         Matcher m = pattern.matcher(sequence);
         if (m.find(start) && m.start() == start) {
            return m.group().length();
         }
         return NO_MATCH;
      }
   }

}// END OF LexicalPattern
