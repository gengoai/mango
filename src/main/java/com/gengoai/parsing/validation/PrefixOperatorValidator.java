package com.gengoai.parsing.validation;

import com.gengoai.function.SerializablePredicate;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.expressions.PrefixOperatorExpression;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
public class PrefixOperatorValidator implements ExpressionValidator {
   private static final long serialVersionUID = 1L;
   private final SerializablePredicate<Expression> rightValidator;

   protected PrefixOperatorValidator(SerializablePredicate<Expression> rightValidator) {
      this.rightValidator = rightValidator;
   }

   public static PrefixOperatorValidator newPrefixOpValidator(SerializablePredicate<Expression> rightValidator) {
      return new PrefixOperatorValidator(notNull(rightValidator));
   }

   @Override
   public Expression validate(Expression expression) throws ParseException {
      Expression right = expression.as(PrefixOperatorExpression.class).right;
      if (!rightValidator.test(right)) {
         throw new ParseException(
            String.format(
               "Invalid Right Hand Expression (type=%s, expression=%s)",
               right.getClass(),
               right));
      }
      return expression;
   }
}//END OF PrefixOperatorValidator
