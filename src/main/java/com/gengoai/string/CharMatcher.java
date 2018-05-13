package com.gengoai.string;

import com.gengoai.Validation;
import com.gengoai.function.SerializablePredicate;
import lombok.NonNull;

import java.util.BitSet;

/**
 * The interface Char matcher.
 *
 * @author David B. Bracewell
 */
@FunctionalInterface
public interface CharMatcher extends SerializablePredicate<Character> {

   /**
    * The constant Any.
    */
   CharMatcher Any = character -> true;
   /**
    * The constant Ascii.
    */
   CharMatcher Ascii = character -> character <= 127;
   /**
    * The constant IsDigit.
    */
   CharMatcher Digit = Character::isDigit;
   /**
    * The constant isIdeographic.
    */
   CharMatcher Ideographic = Character::isIdeographic;
   /**
    * The constant isLetter.
    */
   CharMatcher Letter = Character::isLetter;
   /**
    * The constant isLetterOrDigit.
    */
   CharMatcher LetterOrDigit = Character::isLetterOrDigit;
   /**
    * The constant isLowerCase.
    */
   CharMatcher LowerCase = Character::isLowerCase;
   /**
    * The constant None.
    */
   CharMatcher None = character -> false;
   /**
    * The constant isPunctuation.
    */
   CharMatcher Punctuation = StringUtils::isPunctuation;
   /**
    * The constant isUpperCase.
    */
   CharMatcher UpperCase = Character::isUpperCase;
   /**
    * The constant WhiteSpace.
    */
   CharMatcher WhiteSpace = Character::isWhitespace;

   CharMatcher BreakingWhiteSpace = character -> {
      //Taken from Guava
      switch (character) {
         case '\t':
         case '\n':
         case '\013':
         case '\f':
         case '\r':
         case ' ':
         case '\u0085':
         case '\u1680':
         case '\u2028':
         case '\u2029':
         case '\u205f':
         case '\u3000':
            return true;
         case '\u2007':
            return false;
         default:
            return character >= '\u2000' && character <= '\u200a';
      }
   };


   static CharMatcher anyOf(@NonNull CharSequence characters) {
      final BitSet bitSet = characters.chars().collect(BitSet::new, BitSet::set, BitSet::or);
      return bitSet::get;
   }

   @Override
   default CharMatcher and(@NonNull SerializablePredicate<? super Character> other) {
      return character -> (test(character) && other.test(character));
   }

   /**
    * Find in int.
    *
    * @param sequence the sequence
    * @return the int
    */
   default int findIn(@NonNull CharSequence sequence) {
      return findIn(sequence, 0);
   }

   /**
    * Find in int.
    *
    * @param sequence the sequence
    * @param offset   the offset
    * @return the int
    */
   default int findIn(@NonNull CharSequence sequence, int offset) {
      Validation.checkElementIndex(offset, sequence.length());
      for (int i = offset; i < sequence.length(); i++) {
         if (test(sequence.charAt(i))) {
            return i;
         }
      }
      return -1;
   }

   /**
    * Matches all of boolean.
    *
    * @param sequence the sequence
    * @return the boolean
    */
   default boolean matchesAllOf(@NonNull CharSequence sequence) {
      for (int i = 0; i < sequence.length(); i++) {
         if (!test(sequence.charAt(i))) {
            return false;
         }
      }
      return true;
   }

   /**
    * Matches any of boolean.
    *
    * @param sequence the sequence
    * @return the boolean
    */
   default boolean matchesAnyOf(@NonNull CharSequence sequence) {
      for (int i = 0; i < sequence.length(); i++) {
         if (test(sequence.charAt(i))) {
            return true;
         }
      }
      return false;
   }

   /**
    * Matches none of boolean.
    *
    * @param sequence the sequence
    * @return the boolean
    */
   default boolean matchesNoneOf(@NonNull CharSequence sequence) {
      for (int i = 0; i < sequence.length(); i++) {
         if (test(sequence.charAt(i))) {
            return false;
         }
      }
      return true;
   }

   @Override
   default CharMatcher negate() {
      return character -> !test(character);
   }

   @Override
   default CharMatcher or(@NonNull SerializablePredicate<? super Character> other) {
      return character -> (test(character) || other.test(character));
   }

   /**
    * Trim from string.
    *
    * @param sequence the sequence
    * @return the string
    */
   default String trimFrom(@NonNull CharSequence sequence) {
      return trimTrailingFrom(trimLeadingFrom(sequence));
   }

   /**
    * Trim leading from string.
    *
    * @param sequence the sequence
    * @return the string
    */
   default String trimLeadingFrom(@NonNull CharSequence sequence) {
      for (int first = 0; first < sequence.length(); first++) {
         if (!test(sequence.charAt(first))) {
            return sequence.subSequence(first, sequence.length()).toString();
         }
      }
      return StringUtils.EMPTY;
   }

   /**
    * Trim trailing from string.
    *
    * @param sequence the sequence
    * @return the string
    */
   default String trimTrailingFrom(@NonNull CharSequence sequence) {
      for (int last = sequence.length() - 1; last >= 0; last--) {
         if (!test(sequence.charAt(last))) {
            return sequence.subSequence(0, last + 1).toString();
         }
      }
      return StringUtils.EMPTY;
   }


}//END OF CharMatcher