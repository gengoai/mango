package com.gengoai.parsing.handlers;

import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.validation.ExpressionValidator;

import static com.gengoai.Validation.notNull;

/**
 * An <code>InfixHandler</code> that validates the generated expression.
 *
 * @author David B. Bracewell
 */
public class ValidatingInfixHandler extends InfixHandler {
   private ExpressionValidator validator;
   private InfixHandler backing;

   private ValidatingInfixHandler(InfixHandler backing,
                                  ExpressionValidator validator
                                 ) {
      super(backing.precedence());
      this.backing = backing;
      this.validator = validator;
   }


   /**
    * Creates a new <code>InfixHandler</code> that validates the generated expression using the given {@link
    * ExpressionValidator}
    *
    * @param backing   the InfixHandler that will generate expressions
    * @param validator the validator
    * @return the infix handler
    */
   public static InfixHandler infixValidator(InfixHandler backing,
                                             ExpressionValidator validator
                                            ) {
      return new ValidatingInfixHandler(notNull(backing), notNull(validator));
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, Expression left, ParserToken token) throws ParseException {
      return validator.validate(backing.parse(expressionIterator, left, token));
   }

}//END OF ValidatingInfixHandler
