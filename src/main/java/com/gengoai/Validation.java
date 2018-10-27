package com.gengoai;

import com.gengoai.string.Strings;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <p>Convenience methods for validating method arguments.</p>
 *
 * @author David B. Bracewell
 */
public final class Validation {

   private Validation() {
      throw new IllegalAccessError();
   }


   /**
    * Checks that the evaluation is true. If evaluation is true, the given return value is returned, otherwise the
    * exception supplier is used to generate a RuntimeException to throw.
    *
    * @param <T>               the return type parameter
    * @param evaluation        the evaluation test
    * @param exceptionSupplier the supplier to use to generate exceptions (null will cause a default RuntimeException to
    *                          be thrown)
    * @param returnValue       the return value to return if the evaluation is true
    * @return the return value
    */
   public static <T> T validate(boolean evaluation, Supplier<RuntimeException> exceptionSupplier, T returnValue) {
      if (evaluation) {
         return returnValue;
      }
      throw exceptionSupplier == null ? new RuntimeException() : exceptionSupplier.get();
   }

   /**
    * Checks that the evaluation is true. If evaluation is true, the given value is returned, otherwise the exception
    * supplier is used to generate a RuntimeException to throw.
    *
    * @param <T>               the return type parameter
    * @param value             the value to test
    * @param evaluator         the predicate to use to test the value
    * @param exceptionSupplier the supplier to use to generate exceptions
    * @param nullable          True the value is able to be null, False it cannot be null
    * @return the given value
    */
   public static <T> T validate(T value, Predicate<T> evaluator, Supplier<RuntimeException> exceptionSupplier, boolean nullable) {
      if ((value == null && nullable) || evaluator.test(value)) {
         return value;
      } else if (value == null) {
         throw new NullPointerException();
      }
      throw exceptionSupplier == null ? new RuntimeException() : exceptionSupplier.get();
   }

   /**
    * Checks that the evaluation is true. If evaluation is true, the given value is returned, otherwise the exception
    * supplier is used to generate a RuntimeException to throw.
    *
    * @param <T>               the return type parameter
    * @param value             the value to test
    * @param predicate         the predicate
    * @param message           the message to use when creating the runtime exception
    * @param exceptionSupplier the supplier to use to generate exceptions
    * @param nullable          True the value is able to be null, False it cannot be null
    * @return the given value
    */
   public static <T> T validate(T value, Predicate<T> predicate, String message, Function<String, RuntimeException> exceptionSupplier, boolean nullable) {
      return validate(value, predicate, () -> exceptionSupplier.apply(message), nullable);
   }

   /**
    * Checks that the evaluation is true. If evaluation is true, the given value is returned, otherwise the exception
    * supplier is used to generate an IllegalArgumentException to throw.
    *
    * @param <T>       the return type parameter
    * @param value     the value to test
    * @param predicate the predicate
    * @param message   the message to use when creating the runtime exception
    * @param nullable  True the value is able to be null, False it cannot be null
    * @return the given value
    */
   public static <T> T validateArg(T value, Predicate<T> predicate, String message, boolean nullable) {
      return validate(value, predicate, message, IllegalArgumentException::new, nullable);
   }

   /**
    * Checks that the evaluation is true. If evaluation is true, the given value is returned, otherwise the exception
    * supplier is used to generate an IllegalArgumentException to throw.
    *
    * @param <T>       the return type parameter
    * @param value     the value to test
    * @param predicate the predicate
    * @param nullable  True the value is able to be null, False it cannot be null
    * @return the given value
    */
   public static <T> T validateArg(T value, Predicate<T> predicate, boolean nullable) {
      return validate(value, predicate, IllegalArgumentException::new, nullable);
   }

   /**
    * Throws a <code>NullPointerException</code> if the given object is null.
    *
    * @param <T>    the type of the given object
    * @param object the object to check
    * @return the object
    */
   public static <T> T notNull(T object) {
      if (object == null) {
         throw new NullPointerException();
      }
      return object;
   }

   /**
    * Throws a <code>NullPointerException</code> if the given object is null.
    *
    * @param <T>     the type of the given object
    * @param object  the object to check
    * @param message the message to use in the <code>NullPointerException</code>
    * @return the object
    */
   public static <T> T notNull(T object, String message) {
      if (object == null) {
         if (message != null) {
            throw new NullPointerException(message);
         } else {
            throw new NullPointerException();
         }
      }
      return object;
   }

   /**
    * Throws a <code>IllegalArgumentException</code> if the given string is null or blank.
    *
    * @param string the string to check
    * @return the object
    */
   public static String notNullOrBlank(String string) {
      if (Strings.isNullOrBlank(string)) {
         throw new IllegalArgumentException("String must not be null or blank.");
      }
      return string;
   }

   /**
    * Throws a <code>IllegalArgumentException</code> if the given string is null or blank.
    *
    * @param string  the string to check
    * @param message the message to use in the <code>IllegalArgumentException</code>
    * @return the object
    */
   public static String notNullOrBlank(String string, String message) {
      if (Strings.isNullOrBlank(string)) {
         if (message != null) {
            throw new IllegalArgumentException(message);
         } else {
            throw new IllegalArgumentException("String must not be null or blank.");
         }
      }
      return string;
   }

   /**
    * Throws a <code>IllegalArgumentException</code> if the given boolean evaluates to false.
    *
    * @param evaluation the object to check
    */
   public static void checkArgument(boolean evaluation) {
      if (!evaluation) {
         throw new IllegalArgumentException();
      }
   }

   /**
    * Throws a <code>IllegalArgumentException</code> if the given boolean evaluates to false.
    *
    * @param evaluation the object to check
    * @param message    the message to use in the <code>IllegalArgumentException</code>
    */
   public static void checkArgument(boolean evaluation, String message) {
      if (!evaluation) {
         if (message != null) {
            throw new IllegalArgumentException(message);
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   public static void checkArgument(boolean evaluation, Supplier<String> message) {
      if (!evaluation) {
         if (message != null) {
            throw new IllegalArgumentException(message.get());
         } else {
            throw new IllegalArgumentException();
         }
      }
   }

   /**
    * Checks that the given index is in the  range <code>[0, size)</code>
    *
    * @param index the index to validate
    * @param size  the size of the array, list, etc.
    * @return the index
    * @throws IndexOutOfBoundsException if index is negative or greater than equal to size
    * @throws IllegalArgumentException  if size is negative
    */
   public static int checkElementIndex(int index, int size) {
      return checkElementIndex(index, size, "index");
   }

   public static int checkElementIndex(int index, int size, String message) {
      return checkElementIndex(index, size, () -> message);
   }

   /**
    * Checks that the given index is in the  range <code>[0, size)</code>
    *
    * @param index   the index to validate
    * @param size    the size of the array, list, etc.
    * @param message Message to prepend to the exception
    * @return the index
    * @throws IndexOutOfBoundsException if index is negative or greater than equal to size
    * @throws IllegalArgumentException  if size is negative
    */
   public static int checkElementIndex(int index, int size, Supplier<String> message) {
      if (size < 0) {
         throw new IllegalArgumentException("Negative Size: " + size);
      }
      if (index < 0) {
         throw new IndexOutOfBoundsException(
            String.format("%s (%s) must be non negative", Strings.nullToEmpty(message.get()), index));
      }
      if (index > size) {
         throw new IndexOutOfBoundsException(String.format("%s (%s) must be less than (%s)",
                                                           Strings.nullToEmpty(message.get()), index, size));
      }
      return index;
   }

   /**
    * Checks that the given index is in the  range <code>[0, size]</code>
    *
    * @param index   the index to validate
    * @param size    the size of the array, list, etc.
    * @param message Message to prepend to the exception
    * @return the index
    * @throws IndexOutOfBoundsException if index is negative or greater than size
    * @throws IllegalArgumentException  if size is negative
    */
   public static int checkPositionIndex(int index, int size, String message) {
      if (size < 0) {
         throw new IllegalArgumentException("Negative Size: " + size);
      }
      if (index < 0) {
         throw new IndexOutOfBoundsException(
            String.format("%s (%s) must be non negative", Strings.nullToEmpty(message), index));
      }
      if (index > size) {
         throw new IndexOutOfBoundsException(String.format("%s (%s) must be less than (%s)",
                                                           Strings.nullToEmpty(message), index, size));
      }
      return index;
   }

   /**
    * Checks that the given index is in the  range <code>[0, size]</code>
    *
    * @param index the index to validate
    * @param size  the size of the array, list, etc.
    * @return the index
    * @throws IndexOutOfBoundsException if index is negative or greater than size
    * @throws IllegalArgumentException  if size is negative
    */
   public static int checkPositionIndex(int index, int size) {
      return checkPositionIndex(index, size, "index");
   }


   /**
    * Throws a <code>IllegalStateException</code> if the given boolean evaluates to false.
    *
    * @param evaluation the object to check
    */
   public static void checkState(boolean evaluation) {
      if (!evaluation) {
         throw new IllegalStateException();
      }
   }

   /**
    * Throws a <code>IllegalStateException</code> if the given boolean evaluates to false.
    *
    * @param evaluation the object to check
    * @param message    the message to use in the <code>IllegalStateException</code>
    */
   public static void checkState(boolean evaluation, String message) {
      if (!evaluation) {
         if (message != null) {
            throw new IllegalStateException(message);
         } else {
            throw new IllegalStateException();
         }
      }
   }

   public static void checkState(boolean evaluation, Supplier<String> messageSupplier) {
      if (!evaluation) {
         String message = messageSupplier == null ? null : messageSupplier.get();
         if (message != null) {
            throw new IllegalStateException(message);
         } else {
            throw new IllegalStateException();
         }
      }
   }

}//END OF Validation
