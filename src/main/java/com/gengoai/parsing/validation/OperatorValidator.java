package com.gengoai.parsing.validation;

import com.gengoai.function.SerializablePredicate;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.expressions.PostfixOperatorExpression;
import com.gengoai.parsing.expressions.PrefixOperatorExpression;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
public class OperatorValidator implements ExpressionValidator {
   private static final long serialVersionUID = 1L;
   private final SerializablePredicate<ParserToken> typePredicate;

   protected OperatorValidator(SerializablePredicate<ParserToken> typePredicate) {
      this.typePredicate = typePredicate;
   }

   public static OperatorValidator newOperatorValidator(SerializablePredicate<ParserToken> typePredicate) {
      return new OperatorValidator(notNull(typePredicate));
   }

   @Override
   public Expression validate(Expression expression) throws ParseException {
      ParserToken operator = null;
      if (expression.isInstance(BinaryOperatorExpression.class)) {
         operator = expression.as(BinaryOperatorExpression.class).operator;
      } else if (expression.isInstance(PrefixOperatorExpression.class)) {
         operator = expression.as(PrefixOperatorExpression.class).operator;
      } else if (expression.isInstance(PostfixOperatorExpression.class)) {
         operator = expression.as(PostfixOperatorExpression.class).operator;
      }
      if (operator != null && typePredicate.test(operator)) {
         return expression;
      }
      throw new ParseException(String.format("Invalid operator (operator=%s, expression=%s)", operator, expression));
   }
}//END OF OperatorValidator
