package com.davidbracewell.parsing;

import com.davidbracewell.Regex;
import com.davidbracewell.string.CharPredicate;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Lexical pattern.
 *
 * @author David B. Bracewell
 */
public abstract class LexicalPattern implements Serializable {
   /**
    * The constant NO_MATCH.
    */
   public static final int NO_MATCH = -1;
   private static final long serialVersionUID = 1L;

   /**
    * String literal lexical pattern.
    *
    * @param literal the literal
    * @return the lexical pattern
    */
   public static LexicalPattern stringLiteral(@NonNull String literal) {
      return new LiteralPattern(literal);
   }

   /**
    * Char literal lexical pattern.
    *
    * @param literal the literal
    * @return the lexical pattern
    */
   public static LexicalPattern charLiteral(@NonNull char literal) {
      return new CharLiteralPattern(CharPredicate.anyOf(Character.toString(literal)));
   }

   /**
    * Char literal lexical pattern.
    *
    * @param literal the literal
    * @return the lexical pattern
    */
   public static LexicalPattern charLiteral(@NonNull CharPredicate literal) {
      return new CharLiteralPattern(literal);
   }

   /**
    * Char predicate lexical pattern.
    *
    * @param predicate the predicate
    * @return the lexical pattern
    */
   public static LexicalPattern charPredicate(@NonNull CharPredicate predicate) {
      return new CharPredicatePattern(predicate);
   }

   /**
    * Regex lexical pattern.
    *
    * @param pattern the pattern
    * @return the lexical pattern
    */
   public static LexicalPattern regex(@NonNull Pattern pattern) {
      return new RegexPattern(pattern);
   }

   /**
    * Regex lexical pattern.
    *
    * @param pattern the pattern
    * @return the lexical pattern
    */
   public static LexicalPattern regex(@NonNull String pattern) {
      return new RegexPattern(Pattern.compile(pattern));
   }

   /**
    * Regex lexical pattern.
    *
    * @param pattern the pattern
    * @return the lexical pattern
    */
   public static LexicalPattern regex(@NonNull Regex pattern) {
      return new RegexPattern(pattern.toPattern());
   }

   /**
    * The entry point of application.
    *
    * @param args the input arguments
    */
   public static void main(String[] args) {
      PatternLexer lexer = PatternLexer.builder()
                                       .addReserved(CommonTypes.EQUALS, '=')
                                       .addReserved(CommonTypes.OPENBRACKET, '[')
                                       .addReserved(CommonTypes.CLOSEBRACKET, ']')
                                       .addReserved(CommonTypes.OPENBRACE, '{')
                                       .addReserved(CommonTypes.CLOSEBRACE, '}')
                                       .addReserved(CommonTypes.COMMA, ',')
                                       .addReserved(CommonTypes.BACKSLASH, '\\')
                                       .setQuoteCharacter('"')
                                       .add(CommonTypes.WHITESPACE, CharPredicate.WHITESPACE)
                                       .add(CommonTypes.WORD, CharPredicate.ANY)
                                       .addQuoted(CommonTypes.TILDE, regex("\"(\"\"|[^\"])+\""))
                                       .build();


      String text = "key = \"value = 1\"\nkey = {value}\nblah = [\"1\",2,3,4,5]\n";

      ParserTokenStream ts = lexer.lex(text);
      ParserToken token;
      while ((token = ts.consume()) != null) {
         System.out.println(token);
      }
   }

   /**
    * Match int.
    *
    * @param sequence the sequence
    * @param start    the start
    * @return the int
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
      public int match(@NonNull CharSequence sequence, int start) {
         Preconditions.checkPositionIndex(start, sequence.length());
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

   @Value
   private static class CharLiteralPattern extends LexicalPattern {
      private static final long serialVersionUID = 1L;
      private final CharPredicate predicate;

      @Override
      public int match(@NonNull CharSequence sequence, int start) {
         Preconditions.checkPositionIndex(start, sequence.length());
         return predicate.matches(sequence.charAt(start)) ? 1 : NO_MATCH;
      }
   }

   @Value
   private static class CharPredicatePattern extends LexicalPattern {
      private static final long serialVersionUID = 1L;
      private final CharPredicate pattern;

      private CharPredicatePattern(CharPredicate pattern) {
         this.pattern = pattern;
      }

      @Override
      public int match(@NonNull CharSequence sequence, int start) {
         Preconditions.checkPositionIndex(start, sequence.length());
         int length = start;
         while (length < sequence.length() && pattern.matches(sequence.charAt(length))) {
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
      public int match(@NonNull CharSequence sequence, int start) {
         Preconditions.checkPositionIndex(start, sequence.length());
         Matcher m = pattern.matcher(sequence);
         if (m.find(start) && m.start() == start) {
            return m.group().length();
         }
         return NO_MATCH;
      }
   }

}// END OF LexicalPattern
