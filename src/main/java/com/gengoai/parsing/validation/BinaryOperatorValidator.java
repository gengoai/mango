package com.gengoai.parsing.validation;

import com.gengoai.function.SerializablePredicate;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.expressions.Expression;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
public class BinaryOperatorValidator implements ExpressionValidator {
   private static final long serialVersionUID = 1L;
   private final SerializablePredicate<Expression> leftValidator;
   private final SerializablePredicate<Expression> rightValidator;

   protected BinaryOperatorValidator(SerializablePredicate<Expression> leftValidator, SerializablePredicate<Expression> rightValidator) {
      this.leftValidator = leftValidator;
      this.rightValidator = rightValidator;
   }


   public static BinaryOperatorValidator newBinaryOpValidator(SerializablePredicate<Expression> leftValidator,
                                                              SerializablePredicate<Expression> rightValidator
                                                             ) {
      return new BinaryOperatorValidator(notNull(leftValidator), notNull(rightValidator));
   }

   @Override
   public Expression validate(Expression expression) throws ParseException {
      BinaryOperatorExpression boe = expression.as(BinaryOperatorExpression.class);
      Expression left = boe.left;
      Expression right = boe.right;
      if (!leftValidator.test(left)) {
         throw new ParseException(
            String.format(
               "Invalid Left Hand Expression (type=%s, expression=%s)",
               left.getClass(),
               left));
      }

      if (!rightValidator.test(right)) {
         throw new ParseException(
            String.format(
               "Invalid Right Hand Expression (type=%s, expression=%s)",
               right.getClass(),
               right));
      }
      return expression;
   }
}//END OF BinaryOperatorValidator
