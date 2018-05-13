package com.gengoai.string;

import com.gengoai.function.SerializablePredicate;
import lombok.NonNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The interface String matcher.
 *
 * @author David B. Bracewell
 */
@FunctionalInterface
public interface StringMatcher extends SerializablePredicate<CharSequence> {
   /**
    * The constant NotNull.
    */
   StringMatcher NotNull = Objects::nonNull;
   /**
    * The constant HasPunctuation.
    */
   StringMatcher HasPunctuation = NotNull.and(CharMatcher.Punctuation::matchesAnyOf);
   /**
    * The constant HasUpperCase.
    */
   StringMatcher HasUpperCase = NotNull.and(CharMatcher.UpperCase::matchesAnyOf);
   /**
    * The constant HasLowerCase.
    */
   StringMatcher HasLowerCase = NotNull.and(CharMatcher.LowerCase::matchesAnyOf);
   /**
    * The constant HasLetter.
    */
   StringMatcher HasLetter = NotNull.and(CharMatcher.Letter::matchesAnyOf);
   /**
    * The constant HasDigit.
    */
   StringMatcher HasDigit = NotNull.and(CharMatcher.Digit::matchesAnyOf);
   /**
    * The constant HasLetterOrDigit.
    */
   StringMatcher HasLetterOrDigit = NotNull.and(CharMatcher.LetterOrDigit::matchesAnyOf);
   /**
    * The constant LetterOrWhitespace.
    */
   StringMatcher LetterOrWhitespace = NotNull.and(CharMatcher.Letter.or(CharMatcher.WhiteSpace)::matchesAllOf);
   /**
    * The constant Null.
    */
   StringMatcher Null = Objects::isNull;
   /**
    * The constant NullOrBlank.
    */
   StringMatcher NullOrBlank = Null.or(CharMatcher.WhiteSpace::matchesAllOf);
   /**
    * The constant NotNullOrBlank.
    */
   StringMatcher NotNullOrBlank = NullOrBlank.negate();
   /**
    * The constant LowerCase.
    */
   StringMatcher LowerCase = NotNullOrBlank.and(CharMatcher.LowerCase::matchesAllOf);
   /**
    * The constant Letter.
    */
   StringMatcher Letter = NotNullOrBlank.and(CharMatcher.Letter::matchesAllOf);
   /**
    * The constant LetterOrDigit.
    */
   StringMatcher LetterOrDigit = NotNullOrBlank.and(CharMatcher.LetterOrDigit::matchesAllOf);
   /**
    * The constant Digit.
    */
   StringMatcher Digit = NotNullOrBlank.and(CharMatcher.Digit::matchesAllOf);
   /**
    * The constant Punctuation.
    */
   StringMatcher Punctuation = NotNullOrBlank.and(CharMatcher.Punctuation::matchesAllOf);
   /**
    * The constant UpperCase.
    */
   StringMatcher UpperCase = NotNullOrBlank.and(CharMatcher.UpperCase::matchesAllOf);

   /**
    * Contains string matcher.
    *
    * @param match the match
    * @return the string matcher
    */
   static StringMatcher contains(@NonNull String match) {
      return contains(match, true);
   }

   /**
    * Contains string matcher.
    *
    * @param match         the match
    * @param caseSensitive the case sensitive
    * @return the string matcher
    */
   static StringMatcher contains(@NonNull String match, boolean caseSensitive) {
      final String prefix = caseSensitive ? match : match.toLowerCase();
      return sequence -> sequence != null && (caseSensitive ? sequence.toString().contains(prefix)
                                                            : sequence.toString().toLowerCase().contains(prefix));
   }

   /**
    * Ends with string matcher.
    *
    * @param match the match
    * @return the string matcher
    */
   static StringMatcher endsWith(@NonNull String match) {
      return endsWith(match, true);
   }

   /**
    * Ends with string matcher.
    *
    * @param match         the match
    * @param caseSensitive the case sensitive
    * @return the string matcher
    */
   static StringMatcher endsWith(@NonNull String match, boolean caseSensitive) {
      final String suffix = caseSensitive ? match : match.toLowerCase();
      return sequence -> sequence != null && (caseSensitive ? sequence.toString().endsWith(suffix)
                                                            : sequence.toString().toLowerCase().endsWith(suffix));
   }

   /**
    * Matches string matcher.
    *
    * @param match the match
    * @return the string matcher
    */
   static StringMatcher matches(@NonNull String match) {
      return matches(match, true);
   }

   /**
    * Matches string matcher.
    *
    * @param match         the match
    * @param caseSensitive the case sensitive
    * @return the string matcher
    */
   static StringMatcher matches(@NonNull String match, boolean caseSensitive) {
      return sequence -> sequence != null && (caseSensitive ? match.equals(sequence.toString())
                                                            : match.equalsIgnoreCase(sequence.toString()));
   }

   /**
    * Regex string matcher.
    *
    * @param pattern the pattern
    * @return the string matcher
    */
   static StringMatcher regex(@NonNull Pattern pattern) {
      return sequence -> sequence != null && pattern.matcher(sequence).find();
   }

   /**
    * Regex string matcher.
    *
    * @param pattern the pattern
    * @return the string matcher
    */
   static StringMatcher regex(@NonNull String pattern) {
      return regex(Pattern.compile(pattern));
   }

   /**
    * Starts with string matcher.
    *
    * @param match the match
    * @return the string matcher
    */
   static StringMatcher startsWith(@NonNull String match) {
      return startsWith(match, true);
   }

   /**
    * Starts with string matcher.
    *
    * @param match         the match
    * @param caseSensitive the case sensitive
    * @return the string matcher
    */
   static StringMatcher startsWith(@NonNull String match, boolean caseSensitive) {
      final String prefix = caseSensitive ? match : match.toLowerCase();
      return sequence -> sequence != null && (caseSensitive ? sequence.toString().startsWith(prefix)
                                                            : sequence.toString().toLowerCase().startsWith(prefix));
   }

   @Override
   default StringMatcher and(@NonNull SerializablePredicate<? super CharSequence> other) {
      return sequence -> (test(sequence) && other.test(sequence));
   }

   @Override
   default StringMatcher negate() {
      return character -> !test(character);
   }

   @Override
   default StringMatcher or(@NonNull SerializablePredicate<? super CharSequence> other) {
      return sequence -> (test(sequence) || other.test(sequence));
   }

}//END OF StringMatcher
