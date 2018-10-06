package com.gengoai.parsing.validation;

import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.expressions.Expression;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
public interface ExpressionValidator extends Serializable {

   Expression validate(Expression expression) throws ParseException;

}//END OF ExpressionValidator
