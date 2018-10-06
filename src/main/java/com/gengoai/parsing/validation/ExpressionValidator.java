package com.gengoai.parsing.validation;

import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.expressions.Expression;

import java.io.Serializable;

import static com.gengoai.Validation.checkArgument;
import static com.gengoai.Validation.notNull;

/**
 * Validates an expression to make sure it conforms to a desired spec.
 *
 * @author David B. Bracewell
 */
public interface ExpressionValidator extends Serializable {

   /**
    * Validates the expression throwing a {@link ParseException} if the expression invalid.
    *
    * @param expression the expression to validate
    * @return the validated expression
    * @throws ParseException the expression is invalid
    */
   Expression validate(Expression expression) throws ParseException;


   /**
    * Chains multiple expression validators together.
    *
    * @param validators the validators
    * @return the chained validators
    */
   static ExpressionValidator chain(ExpressionValidator... validators) {
      notNull(validators);
      checkArgument(validators.length > 0, "Must have at least one validator!");
      return (ExpressionValidator) expression -> {
         for (ExpressionValidator validator : validators) {
            validator.validate(expression);
         }
         return expression;
      };
   }

}//END OF ExpressionValidator
