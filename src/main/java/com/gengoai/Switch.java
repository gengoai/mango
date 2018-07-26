/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.gengoai;

import com.gengoai.conversion.Cast;
import com.gengoai.function.CheckedConsumer;
import com.gengoai.function.CheckedFunction;
import com.gengoai.function.SerializableFunction;
import com.gengoai.function.SerializablePredicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.gengoai.Validation.notNull;

/**
 * <p>Converts a value to another based on a series of predicates. In essence allows for <code>switch</code> statements
 * in the form of complex predicate -> function pairs to be performed on any data type. If no default statement is
 * provided and no case statement evaluates to true an <code>IllegalArgumentException</code> is thrown.</p> <p>An
 * example of creating a switch using the builder class is show below. All functions are unchecked.</p>
 * <pre>
 * {@code
 *  Switch.<Car,Double>builder()
 *                    .caseStmt(c -> c.getNumWheels() == 2,  c -> 0.5 )
 *                    .caseStmt(c -> c.getNumWheels() == 4,  c -> 1.0 )
 *                    .caseStmt(c -> c.getNumWheels() == 8,  c -> 2.0 )
 *                    .caseStmt(c -> c.getNumWheels() > 8,  c -> 100.0 )
 *                    .build();
 * }*
 * </pre>
 * <p>Switches can also be constructed using double brace notation as follows: Note the <code>$default</code> statement
 * sets the default case statement.</p>
 * <pre>
 * {@code
 *   new Switch<Car, Double>() {{
 *        $case(c -> c.getNumWheels() == 2, c -> 0.5 );
 *        $case(c -> c.getNumWheels() == 4, c -> 1.0);
 *        $case(c -> c.getNumWheels() == 8, c -> 2.0);
 *        $case(c -> c.getNumWheels() > 8, c -> 100.0);
 *        $default(c -> 1000);
 *    }};
 * }*
 * </pre>
 *
 * @param <T> the type parameter being switched on
 * @param <R> the type parameter returned from the switch operation
 * @author David B. Bracewell
 */
public class Switch<T, R> implements SerializableFunction<T, R> {
   private static final long serialVersionUID = 1L;

   private final ArrayList<PredFunc<T, R>> statements = new ArrayList<>();
   private CheckedFunction<? super T, ? extends R> defaultStmt;

   protected Switch() {

   }

   private Switch(List<PredFunc<T, R>> statements, CheckedFunction<? super T, ? extends R> defaultStmt) {
      this.statements.addAll(statements);
      this.statements.trimToSize();
      this.defaultStmt = defaultStmt;
   }

   /**
    * <p>Creates a switch builder that switches on type <code>T</code> and returns type <code>R</code></p>
    *
    * @param <T> the type being switched on
    * @param <R> the type being returned
    * @return the switch builder
    */
   public static <T, R> Builder<T, R> switchBuilder() {
      return new Builder<>();
   }

   /**
    * <p>Adds a case statement using the provided predicate and function.</p>
    *
    * @param predicate the predicate used to determine if the case statement is met.
    * @param function  the function ran when the case statement evaluates to true.
    */
   protected void $case(SerializablePredicate<? super T> predicate,
                        CheckedFunction<? super T, ? extends R> function
                       ) {
      this.statements.add(new PredFunc<>(notNull(predicate), notNull(function)));
   }

   /**
    * <p>Adds a case statement using the provided predicate and function.</p>
    *
    * @param predicate the predicate used to determine if the case statement is met.
    * @param consumer  the function ran when the case statement evaluates to true.
    */
   protected void $voidCase(SerializablePredicate<? super T> predicate,
                            CheckedConsumer<? super T> consumer
                           ) {
      CheckedFunction<? super T, ? extends R> function = t -> {
         consumer.accept(t);
         return null;
      };
      this.statements.add(new PredFunc<>(notNull(predicate), function));
   }

   /**
    * <p>Adds a default statement to the switch.</p>
    *
    * @param function the function to run when no case statements evaluate to true.
    */
   protected void $default(CheckedFunction<? super T, ? extends R> function) {
      this.defaultStmt = notNull(function);
   }

   /**
    * <p>Adds a case statement using the provided predicate and function first mapping the value <code>T</code> to
    * value
    * <code>V</code>.</p>
    *
    * @param <V>       the type parameter
    * @param predicate the predicate used to determine if the case statement is met.
    * @param mapper    the mapper
    * @param function  the function ran when the case statement evaluates to true.
    */
   protected <V> void $case(SerializablePredicate<? super T> predicate,
                            CheckedFunction<? super T, V> mapper,
                            CheckedFunction<? super V, ? extends R> function
                           ) {
      this.statements.add(new PredFunc<>(notNull(predicate), notNull(mapper).andThen(notNull(function))));
   }

   /**
    * <p>Adds a case statement using the provided predicate and function first mapping the value <code>T</code> to
    * value
    * <code>V</code>.</p>
    *
    * @param <V>       the type parameter
    * @param predicate the predicate used to determine if the case statement is met.
    * @param mapper    the mapper
    * @param consumer  the consumer ran when the case statement evaluates to true.
    */
   protected <V> void $voidCase(SerializablePredicate<? super T> predicate,
                                CheckedFunction<? super T, V> mapper,
                                CheckedConsumer<? super V> consumer
                               ) {
      CheckedFunction<? super V, ? extends R> function = v -> {
         consumer.accept(v);
         return null;
      };
      $case(predicate, mapper, function);
   }

   /**
    * <p>Switches on the given object. Returns the result of a function for the case statement the evaluates to true.
    * When no case statement evaluates to true, the default statement is applied if one is defined otherwise an Illegal
    * argument exception is thrown.</p>
    *
    * @param argument the argument being switched on
    * @return the result of the switch statement
    * @throws Exception Either a case statement or default statement function had an exception or no case statement
    *                   evaluated to true and a default statement was not given.
    */
   public R switchOn(T argument) throws Exception {
      for (PredFunc<T, R> predFunc : statements) {
         if (predFunc.getPredicate().test(argument)) {
            try {
               return predFunc.getFunction().apply(argument);
            } catch (Throwable throwable) {
               throw toException(throwable);
            }
         }
      }
      if (defaultStmt != null) {
         try {
            return defaultStmt.apply(argument);
         } catch (Throwable throwable) {
            throw toException(throwable);
         }
      }
      throw new IllegalArgumentException(argument + " does not match any case.");
   }

   private Exception toException(Throwable throwable) {
      if (throwable instanceof Exception) {
         return Cast.as(throwable);
      }
      return new Exception(throwable);
   }

   @Override
   public final R apply(T t) {
      try {
         return switchOn(t);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * <p>Builder class to create switch statements.</p>
    *
    * @param <T> the type parameter
    * @param <R> the type parameter
    */
   public static class Builder<T, R> {
      private final List<PredFunc<T, R>> caseStmts = new LinkedList<>();
      private CheckedFunction<? super T, ? extends R> defaultStmt = null;

      /**
       * <p>Adds a default statement to the switch.</p>
       *
       * @param defaultStmt the function to run when no case statements evaluate to true.
       * @return The builder
       */
      public Builder<T, R> defaultStatement(CheckedFunction<? super T, ? extends R> defaultStmt) {
         this.defaultStmt = defaultStmt;
         return this;
      }

      /**
       * <p>Adds a case statement using the provided predicate and function.</p>
       *
       * @param predicate the predicate used to determine if the case statement is met.
       * @param function  the function ran when the case statement evaluates to true.
       * @return The builder
       */
      public Builder<T, R> caseStmt(SerializablePredicate<? super T> predicate, CheckedFunction<? super T, ? extends R> function) {
         this.caseStmts.add(new PredFunc<>(notNull(predicate), notNull(function)));
         return this;
      }

      /**
       * <p>Adds a case statement using the provided predicate and function first mapping the value <code>T</code> to
       * value <code>V</code>.</p>
       *
       * @param <V>       the type parameter
       * @param predicate the predicate used to determine if the case statement is met.
       * @param mapper    the mapper
       * @param function  the function ran when the case statement evaluates to true.
       * @return The builder
       */
      public <V> Builder<T, R> caseStmt(SerializablePredicate<? super T> predicate, CheckedFunction<? super T, V> mapper, CheckedFunction<? super V, ? extends R> function) {
         this.caseStmts.add(new PredFunc<>(notNull(predicate), notNull(mapper).andThen(notNull(function))));
         return this;
      }

      /**
       * <p>Creates the switch statement.</p>
       *
       * @return the switch statement
       */
      public Switch<T, R> build() {
         return new Switch<>(caseStmts, defaultStmt);
      }

   }

   private static class PredFunc<T, R> implements Serializable {
      private static final long serialVersionUID = 1L;
      /**
       * The Predicate.
       */
      SerializablePredicate<? super T> predicate;
      /**
       * The Function.
       */
      CheckedFunction<? super T, ? extends R> function;

      /**
       * Instantiates a new Pred func.
       *
       * @param predicate the predicate
       * @param function  the function
       */
      public PredFunc(SerializablePredicate<? super T> predicate, CheckedFunction<? super T, ? extends R> function) {
         this.predicate = predicate;
         this.function = function;
      }

      /**
       * Gets function.
       *
       * @return the function
       */
      public CheckedFunction<? super T, ? extends R> getFunction() {
         return this.function;
      }

      /**
       * Gets predicate.
       *
       * @return the predicate
       */
      public SerializablePredicate<? super T> getPredicate() {
         return this.predicate;
      }


      public String toString() {
         return "Switch.PredFunc(predicate=" + this.getPredicate() + ", function=" + this.getFunction() + ")";
      }

      @Override
      public int hashCode() {
         return Objects.hash(predicate, function);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {return true;}
         if (obj == null || getClass() != obj.getClass()) {return false;}
         final PredFunc other = (PredFunc) obj;
         return Objects.equals(this.predicate, other.predicate)
                   && Objects.equals(this.function, other.function);
      }
   }


}//END OF Switch
