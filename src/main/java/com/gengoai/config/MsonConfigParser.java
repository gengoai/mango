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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.gengoai.config.ConfigScanner.ConfigTokenType;
import static com.gengoai.function.CheckedConsumer.asFunction;
import static com.gengoai.parsing.CommonTypes.OPENBRACE;
import static com.gengoai.parsing.handlers.ValidatingInfixHandler.infixValidator;
import static com.gengoai.parsing.handlers.ValidatingPrefixHandler.prefixValidator;
import static com.gengoai.parsing.validation.BinaryOperatorValidator.newBinaryOpValidator;
import static com.gengoai.parsing.validation.OperatorValidator.newOperatorValidator;
import static com.gengoai.parsing.validation.PrefixOperatorValidator.newPrefixOpValidator;

/**
 * The type Mson config parser.
 *
 * @author David B. Bracewell
 */
public class MsonConfigParser extends Parser {
   public static final String MSON_EXTENSION = ".conf";
   private static final Logger LOG = Logger.getLogger(MsonConfigParser.class);
   private static final Grammar MSON_GRAMMAR = new Grammar() {{
      registerSkip(ConfigTokenType.COMMENT);

      //Value Handlers
      register(ConfigTokenType.STRING,
               new ValueHandler(t -> new StringValueExpression(t.text.replaceAll("\\\\(.)", "$1"), t.type)));
      register(ConfigTokenType.BOOLEAN, new ValueHandler(BooleanValueExpression::new));
      register(ConfigTokenType.NULL, new ValueHandler(NullValueExpression::new));
      register(ConfigTokenType.BEAN, new ValueHandler(t -> new StringValueExpression(t.text, t.type)));
      register(ConfigTokenType.KEY, new ValueHandler(KeyValueExpression::new));
      register(ConfigTokenType.VALUE_SEPARATOR, new ValueHandler());


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

      //Imports can take a string or key on the right
      register(ConfigTokenType.IMPORT, prefixValidator(new PrefixOperatorHandler(),
                                                       newPrefixOpValidator(
                                                          e -> e.isInstance(StringValueExpression.class,
                                                                            KeyValueExpression.class))));

      //Append Properties can take keys or strings on the left and anything on the right
      register(ConfigTokenType.APPEND_PROPERTY, infixValidator(new BinaryOperatorHandler(10, false),
                                                               newBinaryOpValidator(
                                                                  e -> e.isInstance(StringValueExpression.class,
                                                                                    KeyValueExpression.class),
                                                                  e -> true)));


      //Only time we have begin object as a prefix is when we are defining a map value
      register(ConfigTokenType.BEGIN_OBJECT, new MapPrefixHandler(ConfigTokenType.END_OBJECT,
                                                                  ConfigTokenType.VALUE_SEPARATOR,
                                                                  newOperatorValidator(
                                                                     token -> token.isInstance(
                                                                        ConfigTokenType.KEY_VALUE_SEPARATOR))));


      register(ConfigTokenType.BEGIN_ARRAY, new ArrayHandler(ConfigTokenType.END_ARRAY,
                                                             ConfigTokenType.VALUE_SEPARATOR));

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

   /**
    * Convert expression json entry.
    *
    * @param exp the exp
    * @return the json entry
    * @throws ParseException the parse exception
    */
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

   private JsonEntry createArray(ArrayExpression exp) throws ParseException {
      JsonEntry array = JsonEntry.array();
      for (Expression e : exp.expressions) {
         array.addValue(convertExpression(e));
      }
      return array;
   }

   private JsonEntry createJsonMap(MultivalueExpression mve) throws ParseException {
      JsonEntry map = JsonEntry.object();
      for (Expression e : mve.expressions) {
         BinaryOperatorExpression boe = e.as(BinaryOperatorExpression.class);
         String key = boe.left.toString();
         map.addProperty(key, convertExpression(boe.right));
      }
      return map;
   }

   private String effectiveKey(String scope, String key) {
      return StringUtils.isNullOrBlank(scope)
             ? key
             : scope + "." + key;
   }

   private void handleAppendProperty(String prepend, BinaryOperatorExpression exp) throws ParseException {
      String key = effectiveKey(prepend, exp.left.toString());
      JsonEntry value = convertExpression(exp.right);
      List<Object> list = new ArrayList<>(Config.get(key).asList(Object.class));
      processJson(list, value);
      Config.getInstance().setterFunction.setProperty(key, JsonEntry.array(list).toString(), resourceName);
   }

   private void handleImport(String importString) {
      String path;
      if (importString.contains("/")) {
         path = importString;
         if (!path.endsWith(MSON_EXTENSION)) {
            path += MSON_EXTENSION;
         }
      } else if (importString.endsWith(MSON_EXTENSION)) {
         int index = importString.lastIndexOf('.');
         path = importString.substring(0, index).replaceAll("\\.", "/") + MSON_EXTENSION;
      } else {
         path = importString.replace(".", "/") + MSON_EXTENSION;
      }

      path = Config.resolveVariables(path).trim();
      LOG.fine("Importing Conf: {0}", path);
      if (path.startsWith("file:")) {
         Config.loadConfig(Resources.from(path));
      } else {
         Config.loadConfig(new ClasspathResource(path));
      }
   }

   private void handleProperty(String prepend, BinaryOperatorExpression exp) throws ParseException {
      String key = effectiveKey(prepend, exp.left.toString());
      JsonEntry value = convertExpression(exp.right);
      String strvalue = value.isPrimitive() ? value.get().toString() : value.toString();
      Config.getInstance().setterFunction.setProperty(key, strvalue, resourceName);
   }

   private void handleSection(String prepend, BinaryOperatorExpression exp) throws ParseException {
      String section = effectiveKey(prepend, exp.left.toString());
      MultivalueExpression mve = exp.right.as(MultivalueExpression.class);
      for (Expression expression : mve.expressions) {
         BinaryOperatorExpression boe = expression.as(BinaryOperatorExpression.class);
         if (boe.getTokenType().equals(OPENBRACE)) {
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

   private void processArray(List<Object> list, JsonEntry value) {
      for (JsonEntry entry : Iterables.asIterable(value.elementIterator())) {
         processJson(list, entry);
      }
   }

   private void processJson(List<Object> list, JsonEntry entry) {
      if (entry.isNull()) {
         list.add(null);
      } else if (entry.isPrimitive()) {
         list.add(entry.get());
      } else if (entry.isObject()) {
         list.add(entry.getAsMap());
      } else if (entry.isArray()) {
         processArray(list, entry);
      }
   }

}//END OF MsonConfigParser
