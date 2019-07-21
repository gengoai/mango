package com.gengoai.parsing.handlers;

import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.validation.ExpressionValidator;

import static com.gengoai.Validation.notNull;

/**
 * A <code>PrefixHandler</code> that validates the generated expression.
 *
 * @author David B. Bracewell
 */
public class ValidatingPrefixHandler implements PrefixHandler {
   private static final long serialVersionUID = 1L;
   private ExpressionValidator validator;
   private PrefixHandler backing;

   private ValidatingPrefixHandler(PrefixHandler backing,
                                   ExpressionValidator validator
                                  ) {
      this.backing = backing;
      this.validator = validator;
   }


   /**
    * creates a new <code>PrefixHandler</code> that validates the generated expression.
    *
    * @param backing   the PrefixHandler that will generate expressions
    * @param validator the validator
    * @return the prefix handler
    */
   public static PrefixHandler prefixValidator(PrefixHandler backing,
                                               ExpressionValidator validator
                                              ) {
      return new ValidatingPrefixHandler(notNull(backing), notNull(validator));
   }

   @Override
   public Expression parse(ExpressionIterator expressionIterator, ParserToken token) throws ParseException {
      return validator.validate(backing.parse(expressionIterator, token));
   }

}//END OF ValidatingPrefixHandler
