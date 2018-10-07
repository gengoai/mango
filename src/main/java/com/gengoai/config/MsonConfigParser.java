package com.gengoai.config;

import com.gengoai.collection.Iterables;
import com.gengoai.io.Resources;
import com.gengoai.io.resource.ClasspathResource;
import com.gengoai.io.resource.Resource;
import com.gengoai.json.JsonEntry;
import com.gengoai.logging.Logger;
import com.gengoai.parsing.*;
import com.gengoai.parsing.expressions.*;
import com.gengoai.parsing.handlers.*;
import com.gengoai.string.StringUtils;

import java.util.Iterator;
import java.util.List;

import static com.gengoai.config.ConfigScanner.ConfigTokenType;
import static com.gengoai.function.CheckedConsumer.asFunction;
import static com.gengoai.parsing.handlers.ValidatingInfixHandler.infixValidator;
import static com.gengoai.parsing.handlers.ValidatingPrefixHandler.prefixValidator;
import static com.gengoai.parsing.validation.BinaryOperatorValidator.newBinaryOpValidator;
import static com.gengoai.parsing.validation.OperatorValidator.newOperatorValidator;
import static com.gengoai.parsing.validation.PrefixOperatorValidator.newPrefixOpValidator;

/**
 * Config parser for files in MSON (Mangofied JSON) format
 *
 * @author David B. Bracewell
 */
public class MsonConfigParser extends Parser {
   private static final Logger LOG = Logger.getLogger(MsonConfigParser.class);
   private static final Grammar MSON_GRAMMAR = new Grammar() {{
      registerSkip(ConfigTokenType.COMMENT);

      //--------------------------------------------------------------------------------------------
      //Value Handlers
      //--------------------------------------------------------------------------------------------

      //Quoted strings, need to replace escape characters
      register(ConfigTokenType.STRING, new ValueHandler(
         t -> new StringValueExpression(t.text.replaceAll("\\\\(.)", "$1"), t.type)));

      register(ConfigTokenType.BOOLEAN, new ValueHandler(BooleanValueExpression::new));
      register(ConfigTokenType.NULL, new ValueHandler(NullValueExpression::new));
      register(ConfigTokenType.BEAN, new ValueHandler(StringValueExpression::new));
      register(ConfigTokenType.KEY, new ValueHandler(KeyValueExpression::new));
      register(ConfigTokenType.VALUE_SEPARATOR, new ValueHandler());

      //--------------------------------------------------------------------------------------------
      // Key-Value Operators
      //--------------------------------------------------------------------------------------------

      //Key Value Properties can take strings on the left and anything on the right
      register(ConfigTokenType.KEY_VALUE_SEPARATOR, infixValidator(new BinaryOperatorHandler(10, false),
                                                                   newBinaryOpValidator(
                                                                      e -> e.isInstance(StringValueExpression.class),
                                                                      e -> true)));

      //Equal Properties can take keys or strings on the left and anything on the right
      register(ConfigTokenType.EQUAL_PROPERTY, infixValidator(new BinaryOperatorHandler(10, false),
                                                              newBinaryOpValidator(
                                                                 e -> e.isInstance(KeyValueExpression.class,
                                                                                   StringValueExpression.class),
                                                                 e -> true)));

      //Append Properties can take keys or strings on the left and anything on the right
      register(ConfigTokenType.APPEND_PROPERTY, infixValidator(new BinaryOperatorHandler(10, false),
                                                               newBinaryOpValidator(
                                                                  e -> e.isInstance(StringValueExpression.class,
                                                                                    KeyValueExpression.class),
                                                                  e -> true)));


      //--------------------------------------------------------------------------------------------
      // Config Actions (e.g. import)
      //--------------------------------------------------------------------------------------------

      //Imports can take a string or key on the right
      register(ConfigTokenType.IMPORT, prefixValidator(new PrefixOperatorHandler(),
                                                       newPrefixOpValidator(
                                                          e -> e.isInstance(StringValueExpression.class,
                                                                            KeyValueExpression.class))));


      //--------------------------------------------------------------------------------------------
      // Arrays
      //--------------------------------------------------------------------------------------------

      register(ConfigTokenType.BEGIN_ARRAY, new ArrayHandler(ConfigTokenType.END_ARRAY,
                                                             ConfigTokenType.VALUE_SEPARATOR));


      //--------------------------------------------------------------------------------------------
      // Objects
      //--------------------------------------------------------------------------------------------

      //Only time we have begin object as a prefix is when we are defining a map value
      register(ConfigTokenType.BEGIN_OBJECT, new MapPrefixHandler(ConfigTokenType.END_OBJECT,
                                                                  ConfigTokenType.VALUE_SEPARATOR,
                                                                  newOperatorValidator(
                                                                     token -> token.isInstance(
                                                                        ConfigTokenType.KEY_VALUE_SEPARATOR))));

      //Begin object as an infix operator is used for sections
      register(ConfigTokenType.BEGIN_OBJECT, new MapInfixHandler(10,
                                                                 ConfigTokenType.END_OBJECT,
                                                                 null,
                                                                 newOperatorValidator(
                                                                    token -> token.isInstance(
                                                                       ConfigTokenType.EQUAL_PROPERTY,
                                                                       ConfigTokenType.BEGIN_OBJECT))));
   }};
   private static final Lexer MSON_LEXER = input -> new ParserTokenStream(new Iterator<ParserToken>() {
      ConfigScanner scanner = new ConfigScanner(input.reader());
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
   private Resource resource;
   private String resourceName;
   private final Evaluator<Expression> MSON_EVALUATOR = new Evaluator<Expression>() {
      {
         $(PrefixOperatorExpression.class,
           ConfigTokenType.IMPORT,
           asFunction(exp -> handleImport(exp.right.toString())));

         $(BinaryOperatorExpression.class,
           ConfigTokenType.APPEND_PROPERTY,
           asFunction(exp -> handleAppendProperty(null, exp)));

         $(BinaryOperatorExpression.class,
           ConfigTokenType.EQUAL_PROPERTY,
           asFunction(exp -> handleProperty(null, exp)));

         $(BinaryOperatorExpression.class,
           ConfigTokenType.BEGIN_OBJECT,
           asFunction(exp -> handleSection("", exp)));
      }
   };

   /**
    * Instantiates a new Mson config parser.
    *
    * @param config the config
    */
   public MsonConfigParser(Resource config) {
      super(MSON_GRAMMAR, MSON_LEXER);
      this.resource = config;
      this.resourceName = config.descriptor();
   }

   /**
    * Parse resource.
    *
    * @param resource the resource
    * @throws ParseException the parse exception
    */
   public static void parseResource(Resource resource) throws ParseException {
      new MsonConfigParser(resource).parse();
   }

   private JsonEntry convertExpression(Expression exp) throws ParseException {

      if (exp.isInstance(ValueExpression.class)) {
         return JsonEntry.from(exp.as(ValueExpression.class).getValue());
      }

      if (exp.isInstance(ArrayExpression.class)) {
         ArrayExpression arrayExpression = exp.as(ArrayExpression.class);
         JsonEntry array = JsonEntry.array();
         for (Expression e : arrayExpression.expressions) {
            array.addValue(convertExpression(e));
         }
         return array;
      }

      if (exp.isInstance(MultivalueExpression.class)) {
         MultivalueExpression mve = exp.as(MultivalueExpression.class);
         JsonEntry map = JsonEntry.object();
         for (Expression e : mve.expressions) {
            BinaryOperatorExpression boe = e.as(BinaryOperatorExpression.class);
            String key = boe.left.toString();
            map.addProperty(key, convertExpression(boe.right));
         }
         return map;
      }

      throw new ParseException("Unexpected Expression: " + exp);
   }

   private String effectiveKey(String scope, String key) {
      String effectiveKey = StringUtils.isNullOrBlank(scope)
                            ? key
                            : scope + "." + key;
      if (effectiveKey.endsWith("._") && effectiveKey.length() > 2) {
         effectiveKey = effectiveKey.substring(0, effectiveKey.length() - 2);
      }
      return effectiveKey;
   }

   private void handleAppendProperty(String prepend, BinaryOperatorExpression exp) throws ParseException {
      String key = effectiveKey(prepend, exp.left.toString());
      List<Object> list = Config.get(key).asList(Object.class);
      processJson(list, convertExpression(exp.right));
      Config.getInstance()
         .setterFunction
         .setProperty(key, JsonEntry.array(list).toString(), resourceName);
   }

   private void handleImport(String importString) throws ParseException {
      if (!importString.endsWith(Config.CONF_EXTENSION) && importString.contains("/")) {
         //We don't have a MSON extension at the end and the import string is a path
         throw new ParseException(String.format("Invalid Import Statement (%s)", importString));
      }
      String path = Config.resolveVariables(importString).trim();
      if (path.contains("/")) {
         if (path.startsWith("file:")) {
            Config.loadConfig(Resources.from(path));
         } else {
            Config.loadConfig(new ClasspathResource(path));
         }
      } else {
         Config.loadPackageConfig(path);
      }
   }

   private void handleProperty(String prepend, BinaryOperatorExpression exp) throws ParseException {
      String key = effectiveKey(prepend, exp.left.toString());
      JsonEntry value = convertExpression(exp.right);
      String stringValue = value.isPrimitive() ? value.get().toString() : value.toString();
      Config.getInstance().setterFunction.setProperty(key, stringValue, resourceName);
   }

   private void handleSection(String prepend, BinaryOperatorExpression exp) throws ParseException {
      String section = effectiveKey(prepend, exp.left.toString());
      MultivalueExpression mve = exp.right.as(MultivalueExpression.class);
      for (Expression expression : mve.expressions) {
         BinaryOperatorExpression boe = expression.as(BinaryOperatorExpression.class);
         if (boe.getTokenType().equals(ConfigTokenType.BEGIN_OBJECT)) {
            handleSection(section, boe);
         } else if (boe.getTokenType().equals(ConfigTokenType.APPEND_PROPERTY)) {
            handleAppendProperty(section, boe);
         } else {
            handleProperty(section, boe);
         }
      }
   }

   /**
    * Parses the config in MSON format.
    *
    * @throws ParseException the parse exception
    */
   public void parse() throws ParseException {
      super.evaluateAll(resource, MSON_EVALUATOR);
   }

   private void processJson(List<Object> list, JsonEntry entry) {
      if (entry.isNull()) {
         list.add(null);
      } else if (entry.isPrimitive()) {
         list.add(entry.get());
      } else if (entry.isObject()) {
         list.add(entry.getAsMap());
      } else if (entry.isArray()) {
         for (JsonEntry e : Iterables.asIterable(entry.elementIterator())) {
            processJson(list, e);
         }
      }
   }

}//END OF MsonConfigParser
