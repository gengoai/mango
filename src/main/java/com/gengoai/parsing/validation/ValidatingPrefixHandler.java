package com.gengoai.parsing.validation;

import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.handlers.PrefixHandler;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
public class ValidatingPrefixHandler extends PrefixHandler {
   private ExpressionValidator validator;
   private PrefixHandler backing;

   protected ValidatingPrefixHandler(PrefixHandler backing,
                                     ExpressionValidator validator
                                    ) {
      this.backing = backing;
      this.validator = validator;
   }


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
