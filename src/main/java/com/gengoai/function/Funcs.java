package com.gengoai.function;

import java.util.function.Consumer;

import static com.gengoai.Validation.notNull;

/**
 * The type Funcs.
 *
 * @author David B. Bracewell
 */
public final class Funcs {
   private Funcs() {
      throw new IllegalAccessError();
   }

   /**
    * When when.
    *
    * @param <I>        the type parameter
    * @param <O>        the type parameter
    * @param predicate  the predicate
    * @param trueAction the true action
    * @return the when
    */
   public static <I, O> When<I, O> when(SerializablePredicate<? super I> predicate, SerializableFunction<? super I, ? extends O> trueAction) {
      return new When<>(notNull(predicate), notNull(trueAction));
   }

   /**
    * Whenc when.
    *
    * @param <I>        the type parameter
    * @param <O>        the type parameter
    * @param predicate  the predicate
    * @param trueAction the true action
    * @return the when
    */
   public static <I, O> When<I, O> whenc(SerializablePredicate<? super I> predicate, Consumer<? super I> trueAction) {
      return new When<>(notNull(predicate), i -> {
         trueAction.accept(i);
         return null;
      });
   }

   /**
    * When when.
    *
    * @param <I>       the type parameter
    * @param <O>       the type parameter
    * @param predicate the predicate
    * @param trueValue the true value
    * @return the when
    */
   public static <I, O> When<I, O> when(SerializablePredicate<? super I> predicate, O trueValue) {
      return new When<>(notNull(predicate), i -> trueValue);
   }

   /**
    * The type When.
    *
    * @param <I> the type parameter
    * @param <O> the type parameter
    */
   public static class When<I, O> implements SerializableFunction<I, O> {
      private final SerializablePredicate<? super I> predicate;
      private final SerializableFunction<? super I, ? extends O> trueAction;
      private SerializableFunction<? super I, ? extends O> falseAction = i -> null;


      private When(SerializablePredicate<? super I> predicate, SerializableFunction<? super I, ? extends O> trueAction) {
         this.predicate = predicate;
         this.trueAction = trueAction;
      }

      @Override
      public O apply(I i) {
         return predicate.test(i) ? trueAction.apply(i) : falseAction.apply(i);
      }

      /**
       * Otherwise function.
       *
       * @param falseAction the false action
       * @return the function
       */
      public SerializableFunction<I, O> otherwise(SerializableFunction<? super I, ? extends O> falseAction) {
         this.falseAction = notNull(falseAction);
         return this;
      }

      /**
       * Otherwisec function.
       *
       * @param falseAction the false action
       * @return the function
       */
      public SerializableFunction<I, O> otherwisec(Consumer<? super I> falseAction) {
         this.falseAction = i -> {
            falseAction.accept(i);
            return null;
         };
         return this;
      }

      /**
       * Otherwise function.
       *
       * @param falseValue the false value
       * @return the function
       */
      public SerializableFunction<I, O> otherwise(O falseValue) {
         this.falseAction = i -> falseValue;
         return this;
      }
   }

}//END OF Funcs
