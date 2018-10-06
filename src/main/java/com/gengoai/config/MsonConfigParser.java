package com.gengoai.config;

import com.gengoai.io.resource.Resource;
import com.gengoai.json.JsonEntry;
import com.gengoai.parsing.*;
import com.gengoai.parsing.expressions.*;
import com.gengoai.parsing.handlers.*;
import com.gengoai.string.StringUtils;

import java.io.IOException;
import java.util.Iterator;

import static com.gengoai.config.Scanner.ConfigTokenType.*;
import static com.gengoai.function.CheckedConsumer.asFunction;
import static com.gengoai.parsing.CommonTypes.*;
import static com.gengoai.parsing.validation.BinaryOperatorValidator.newBinaryOpValidator;
import static com.gengoai.parsing.validation.OperatorValidator.newOperatorValidator;
import static com.gengoai.parsing.validation.PrefixOperatorValidator.newPrefixOpValidator;
import static com.gengoai.parsing.validation.ValidatingInfixHandler.infixValidator;
import static com.gengoai.parsing.validation.ValidatingPrefixHandler.prefixValidator;

/**
 * @author David B. Bracewell
 */
public class MsonConfigParser extends Parser {
   static final Grammar G = new Grammar() {{
      registerSkip(Scanner.ConfigTokenType.COMMENT);
      register(WORD, new ValueHandler(t -> new StringValueExpression(t.text.replaceAll("\\\\(.)", "$1"), t.type)));
      register(BOOLEAN, new ValueHandler(BooleanValueExpression::new));
      register(NULL, new ValueHandler(NullValueExpression::new));
      register(BEAN, new ValueHandler(t -> new StringValueExpression(t.text, t.type)));
      register(KEY, new ValueHandler(KeyValueExpression::new));
      register(COMMA, new ValueHandler());
      register(COLON, infixValidator(new BinaryOperatorHandler(10, false),
                                     newBinaryOpValidator(e -> e.isInstance(StringValueExpression.class),
                                                          e -> true)));
      register(EQUALS, infixValidator(new BinaryOperatorHandler(10, false),
                                      newBinaryOpValidator(e -> e.isInstance(KeyValueExpression.class)
                                                                   || e.isInstance(StringValueExpression.class),
                                                           e -> true)));
      register(Scanner.ConfigTokenType.IMPORT, prefixValidator(new PrefixOperatorHandler(),
                                                               newPrefixOpValidator(e ->
                                                                                       e.isInstance(
                                                                                          StringValueExpression.class)
                                                                                          || e.isInstance(
                                                                                          KeyValueExpression.class))));
      register(Scanner.ConfigTokenType.APPEND, infixValidator(new BinaryOperatorHandler(10, false),
                                                              newBinaryOpValidator(
                                                                 e -> e.isInstance(KeyValueExpression.class),
                                                                 e -> true)));
      register(CommonTypes.OPENBRACE, new MapPrefixHandler(CommonTypes.CLOSEBRACE,
                                                           COMMA,
                                                           newOperatorValidator(
                                                              token -> token.getType().isInstance(COLON))));
      register(CommonTypes.OPENBRACKET, new ArrayHandler(CommonTypes.CLOSEBRACKET,
                                                         COMMA));
      register(CommonTypes.OPENBRACE, new MapInfixHandler(10,
                                                          CommonTypes.CLOSEBRACE,
                                                          null,
                                                          newOperatorValidator(
                                                             token -> token.getType().isInstance(EQUALS, OPENBRACE))));
   }};
   private Resource resource;
   private String resourceName;
   private Evaluator<Expression> evaluator = new Evaluator<Expression>() {
      {
         $(PrefixOperatorExpression.class,
           Scanner.ConfigTokenType.IMPORT,
           asFunction(exp -> handleImport(exp.right.toString())));

         $(BinaryOperatorExpression.class,
           Scanner.ConfigTokenType.APPEND,
           asFunction(exp -> System.out.println("APPENDING: " + exp.left + " => " + exp.right.toString())));

         $(BinaryOperatorExpression.class,
           EQUALS,
           asFunction(exp -> handleProperty(null, exp)));

         $(BinaryOperatorExpression.class,
           CommonTypes.OPENBRACE,
           asFunction(exp -> handleSection("", exp)));

      }
   };

   public MsonConfigParser(Resource config) {
      super(G, new ScannerLexer());
      this.resource = config;
      this.resourceName = config.descriptor();
   }

   public static void parseResource(Resource resource) throws ParseException {
      new MsonConfigParser(resource).parse();
   }

   public JsonEntry convertExpression(Expression exp) throws ParseException {
      if (exp.isInstance(ValueExpression.class)) {
         return JsonEntry.from(exp.as(ValueExpression.class).getValue());
      } else if (exp.isInstance(ArrayExpression.class)) {
         return createArray(exp.as(ArrayExpression.class));
      } else if (exp.isInstance(MultivalueExpression.class)) {
         return createJsonMap(exp.as(MultivalueExpression.class));
      }
      throw new ParseException("Unexpected Expression: " + exp);
   }

   public JsonEntry createArray(ArrayExpression exp) throws ParseException {
      JsonEntry array = JsonEntry.array();
      for (Expression e : exp.expressions) {
         array.addValue(convertExpression(e));
      }
      return array;
   }

   public JsonEntry createJsonMap(MultivalueExpression mve) throws ParseException {
      JsonEntry map = JsonEntry.object();
      for (Expression e : mve.expressions) {
         BinaryOperatorExpression boe = e.as(BinaryOperatorExpression.class);
         String key = boe.left.toString();
         map.addProperty(key, convertExpression(boe.right));
      }
      return map;
   }

   public String effectiveKey(String scope, String key) {
      if (StringUtils.isNotNullOrBlank(scope)) {
         key = scope + "." + key;
      }
      return key;
   }

   private void handleImport(String importString) {
      System.out.println("IMPORTING: " + importString);
   }

   public void handleProperty(String prepend, BinaryOperatorExpression exp) throws ParseException {
      String key = effectiveKey(prepend, exp.left.toString());
      JsonEntry value = convertExpression(exp.right);
      System.out.println(String.format("key=(%s), value=(%s)", key, value));
   }

   private void handleSection(String prepend, BinaryOperatorExpression exp) throws ParseException {
      String section = effectiveKey(prepend, exp.left.toString());
      MultivalueExpression mve = exp.right.as(MultivalueExpression.class);
      for (Expression expression : mve.expressions) {
         BinaryOperatorExpression boe = expression.as(BinaryOperatorExpression.class);
         if (boe.getTokenType().equals(OPENBRACE)) {
            handleSection(section, boe);
         } else {
            handleProperty(section, boe);
         }
      }
   }

   public void parse() throws ParseException {
      super.evaluateAll(resource, evaluator);
   }

   private static class ScannerLexer implements Lexer {

      @Override
      public ParserTokenStream lex(Resource input) throws IOException {
         return new ParserTokenStream(new Iterator<ParserToken>() {
            Scanner scanner = new Scanner(input.reader());
            ParserToken token = null;
            boolean hasNext = true;

            protected boolean advance() {
               if (token != null) {
                  return true;
               }
               if (!hasNext) {
                  return false;
               }
               try {
                  token = scanner.next();
                  if (token == null) {
                     hasNext = false;
                  }
                  return hasNext;
               } catch (Exception e) {
                  throw new RuntimeException(e);
               }
            }

            @Override
            public boolean hasNext() {
               return advance();
            }

            @Override
            public ParserToken next() {
               advance();
               ParserToken toReturn = token;
               token = null;
               return toReturn;
            }
         });
      }
   }

   public static class KeyValueExpression extends ValueExpression<String> {
      public final String key;

      public KeyValueExpression(ParserToken token) {
         super(token.type);
         this.key = token.text;
      }


      @Override
      public String getValue() {
         return key;
      }

   }

}//END OF MsonConfigParser
