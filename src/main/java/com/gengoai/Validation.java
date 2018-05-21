package com.gengoai;

import com.gengoai.string.StringUtils;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The type Validation.
 *
 * @author David B. Bracewell
 */
public final class Validation {

   private Validation() {
      throw new IllegalAccessError();
   }


   public static <T> T validate(boolean evaluation, Supplier<RuntimeException> exceptionSupplier, T returnValue) {
      if (evaluation) {
         return returnValue;
      }
      throw exceptionSupplier == null ? new RuntimeException() : exceptionSupplier.get();
   }

   public static <T> T validate(T value, Predicate<T> predicate, Supplier<RuntimeException> exceptionSupplier, boolean nullable) {
      if ((value == null && nullable) || predicate.test(value)) {
         return value;
      } else if (value == null) {
         throw new NullPointerException();
      }
      throw exceptionSupplier == null ? new RuntimeException() : exceptionSupplier.get();
   }

   public static <T> T validate(T value, Predicate<T> predicate, String message, Function<String, RuntimeException> exceptionSupplier, boolean nullable) {
      if ((value == null && nullable) || predicate.test(value)) {
         return value;
      } else if (value == null) {
         throw new NullPointerException();
      }
      throw exceptionSupplier.apply(message);
   }

   public static <T> T validateArg(T value, Predicate<T> predicate, String message, boolean nullable) {
      return validate(value, predicate, message, IllegalArgumentException::new, nullable);
   }

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
      if (StringUtils.isNullOrBlank(string)) {
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
      if (StringUtils.isNullOrBlank(string)) {
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

   private static String createIndexErrorMessage(int index, int size, String message) {
      message = message == null ? StringUtils.EMPTY : message + " ";
      if (index < 0) {
         return String.format("%s(%s) must be non-negative.", message, index);
      } else if (size < 0) {
         throw new IllegalArgumentException("negative size: " + size);
      } else {
         return String.format("%s(%s) must be less than the size (%s).", message, index, size);
      }
   }

   public static int checkElementIndex(int index, int size) {
      if (index < 0 || index >= size) {
         throw new IndexOutOfBoundsException(createIndexErrorMessage(index, size, StringUtils.EMPTY));
      }
      return index;
   }

   public static int checkElementIndex(int index, int size, String message) {
      if (index < 0 || index >= size) {
         throw new IndexOutOfBoundsException(createIndexErrorMessage(index, size, message));
      }
      return index;
   }

   public static void checkElementIndexAndRange(int relativeStart, int relativeEnd, int length) {
      checkElementIndexAndRange(relativeStart, relativeEnd, length, StringUtils.EMPTY);
   }

   public static void checkElementIndexAndRange(int relativeStart, int relativeEnd, int length, String message) {
      if (relativeStart < 0 || relativeStart >= length) {
         throw new IndexOutOfBoundsException(createIndexErrorMessage(relativeStart, length, message));
      }
      if (relativeEnd < 0 || relativeEnd >= length) {
         throw new IndexOutOfBoundsException(createIndexErrorMessage(relativeEnd, length, message));
      }
      if (relativeStart > relativeEnd) {
         throw new IllegalArgumentException("starting index must be less than ending index");
      }
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

}//END OF Validation
