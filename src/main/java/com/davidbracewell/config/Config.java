/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell.config;

import com.davidbracewell.Language;
import com.davidbracewell.SystemInfo;
import com.davidbracewell.application.Application;
import com.davidbracewell.application.CommandLineApplication;
import com.davidbracewell.cli.CommandLineParser;
import com.davidbracewell.cli.NamedOption;
import com.davidbracewell.collection.Sorting;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.ClasspathResource;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.logging.LogManager;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.parsing.ParseException;
import com.davidbracewell.reflection.BeanUtils;
import com.davidbracewell.scripting.ScriptEnvironment;
import com.davidbracewell.scripting.ScriptEnvironmentManager;
import com.davidbracewell.string.StringPredicates;
import com.davidbracewell.string.StringUtils;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p> A complete configuration class that allows for inheritance, multiline inputs, variable interpolation, and
 * scripting. </p> <p/> <p> <ul> <li>${var} will substitute the value of "var"</li> <p/> </ul> </p>
 *
 * @author David B. Bracewell
 */
public final class Config implements Serializable {

   private static final String BEAN_PROPERTY = "@{";
   private static final Pattern BEAN_SUBSTITUTION = Pattern.compile(Pattern.quote(BEAN_PROPERTY) + "(.+?)\\}");
   private static final String DEFAULT_CONFIG_FILE_NAME = "default.conf";
   private static final String SCRIPT_PROPERTY = "script[";
   private static final Pattern STRING_SUBSTITUTION = Pattern.compile("\\$\\{(.+?)\\}");
   private static final String SYSTEM_PROPERTY = "system.";
   private static final long serialVersionUID = 6875819132224789761L;
   private static final Logger log = Logger.getLogger(Config.class);
   private static Resource localConfigDirectory = Resources.fromFile(SystemInfo.USER_HOME + "/config/");
   private static ClassLoader defaultClassLoader = Config.class.getClassLoader();
   private volatile static Config INSTANCE;
   private final Map<String, String> properties = new ConcurrentHashMap<>();
   private final Set<String> loaded = new ConcurrentSkipListSet<>();
   /**
    * The Setter function.
    */
   ConfigPropertySetter setterFunction = ConfigSettingError.INSTANCE;

   /**
    * Gets the singleton instance of the config. This should not normally be used by api consumers
    *
    * @return the singleton instance of the config.
    */
   public static Config getInstance() {
      if (INSTANCE == null) {
         synchronized (Config.class) {
            INSTANCE = new Config();
         }
      }
      return INSTANCE;
   }

   /**
    * Sets the singleton instance of the config. This is mainly useful in distributed environments where we are passing
    * configurations around.
    *
    * @param config the config that will become the single instance
    * @return the singleton instance of the config
    */
   public static Config setInstance(Config config) {
      synchronized (Config.class) {
         INSTANCE = config;
      }
      return INSTANCE;
   }

   /**
    * Determines if a given configuration resource has been loaded
    *
    * @param configResource the config resource to check.
    * @return True if the config has been loaded, False if not
    */
   public static boolean isConfigLoaded(@NonNull Resource configResource) {
      return getInstance().loaded.contains(configResource.path());
   }

   @SneakyThrows
   private static Val getBean(String value) {
      List<String> parts = StringUtils.split(value, ',');
      List<Object> beans = new ArrayList<>();
      for (String beanName : parts) {
         Matcher m = BEAN_SUBSTITUTION.matcher(beanName);
         if (m.find()) {
            beans.add(BeanUtils.getNamedBean(m.group(1), Object.class));
         } else {
            beans.add(beanName);
         }
      }
      return beans.size() == 1 ? Val.of(beans.get(0)) : Val.of(beans);
   }

   public static boolean isBean(String property) {
      if( StringUtils.isNullOrBlank(Config.getRaw(property))){
         return false;
      }
      Matcher m = BEAN_SUBSTITUTION.matcher(Config.getRaw(property));
      return m.find();
   }

   /**
    * Clears all set properties
    */
   public static void clear() {
      getInstance().loaded.clear();
      getInstance().properties.clear();
   }


   /**
    * <p>Creates a map from a number of config properties. For example, given the following config excerpt:</p>
    * <pre>
    * {@code
    *    mymap {
    *       key1 = 100
    *       key2 = 200
    *       key3 = 300
    *    }
    * }*
    * </pre>
    * <p>We can create map containing <code>{key1=100,key2=200,key3=300}</code>, but calling the method using:
    * <code>getMap("mymap", String.class, Integer.class);</code></p>
    *
    * @param <K>        the key type parameter
    * @param <V>        the value type parameter
    * @param mapName    the name of the map that is used to find map entries.
    * @param keyClass   the key class
    * @param valueClass the value class
    * @return A map containing keys and values whose properties are <code>mapName.*</code> where "*" is a wildcard for
    * key names
    */
   public static <K, V> Map<K, V> getMap(String mapName, Class<K> keyClass, Class<V> valueClass) {
      Map<K, V> map = new HashMap<>();
      getPropertiesMatching(StringPredicates.STARTS_WITH(mapName, true)).forEach(property -> {
         if (property.length() > mapName.length()) {
            String k = property.substring(mapName.length() + 1);
            map.put(Convert.convert(k, keyClass), get(property).as(valueClass));
         }
      });
      return map;
   }

   /**
    * Checks if a property is in the config or or set on the system. The property name is constructed as
    * <code>propertyPrefix + . + propertyComponent[0] + . + propertyComponent[1] + ...</code>
    *
    * @param propertyPrefix     The prefix
    * @param propertyComponents The components.
    * @return True if the property is known, false if not.
    */
   public static boolean hasProperty(String propertyPrefix, String... propertyComponents) {
      String propertyName = propertyPrefix;
      if (propertyComponents != null && propertyComponents.length > 0) {
         propertyName += "." + StringUtils.join(propertyComponents, ".");
      }
      return getInstance().properties.containsKey(propertyName) || System.getProperties().contains(propertyName);
   }

   /**
    * Checks if a property is in the config or or set on the system. The property name is constructed as
    * <code>clazz.getName() + . + propertyComponent[0] + . + propertyComponent[1] + ...</code>
    *
    * @param clazz              The class with which the property is associated.
    * @param propertyComponents The components.
    * @return True if the property is known, false if not.
    */
   public static boolean hasProperty(Class<?> clazz, String... propertyComponents) {
      return hasProperty(clazz.getName(), propertyComponents);
   }

   /**
    * <p>Checks if a property is in the config or or set on the system. The property name is constructed as
    * <code>propertyPrefix+ . + propertyComponent[0] + . + propertyComponent[1] + ... +
    * (language.toString()|language.getCode().toLowerCase())</code> This will return true if the language specific
    * config option is set or a default (i.e. no-language specified) version is set. </p>
    *
    * @param propertyPrefix     The prefix
    * @param language           The language we would like the config for
    * @param propertyComponents The components.
    * @return True if the property is known, false if not.
    */
   public static boolean hasProperty(String propertyPrefix, Language language, String... propertyComponents) {
      return findKey(propertyPrefix, language, propertyComponents) != null;
   }

   /**
    * Finds the closest key to the given components
    *
    * @param propertyPrefix     the property prefix
    * @param language           the language
    * @param propertyComponents the property components
    * @return the string
    */
   public static String closestKey(String propertyPrefix, Language language, String... propertyComponents) {
      return findKey(propertyPrefix, language, propertyComponents);
   }

   private static String findKey(String propertyPrefix, Language language, String... propertyComponents) {

      if (propertyComponents == null || propertyComponents.length == 0) {
         for (String key :
            new String[]{
               propertyPrefix + "." + language,
               propertyPrefix + "." + language.toString().toLowerCase(),
               propertyPrefix + "." + language.getCode(),
               propertyPrefix + "." + language.getCode().toLowerCase(),
               propertyPrefix
            }
            ) {
            if (hasProperty(key)) {
               return key;
            }
         }

         return null;
      }


      String components = StringUtils.join(propertyComponents, ".");

      for (String key :
         new String[]{
            propertyPrefix + "." + language + "." + components,
            propertyPrefix + "." + language.toString().toLowerCase() + "." + components,
            propertyPrefix + "." + language.getCode() + "." + components,
            propertyPrefix + "." + language.getCode().toLowerCase() + "." + components,
            propertyPrefix + "." + components + "." + language,
            propertyPrefix + "." + components + "." + language.toString().toLowerCase(),
            propertyPrefix + "." + components + "." + language.getCode(),
            propertyPrefix + "." + components + "." + language.getCode().toLowerCase(),
            propertyPrefix + "." + components
         }
         ) {
         if (hasProperty(key)) {
            return key;
         }
      }

      return null;
   }

   /**
    * <p>Checks if a property is in the config or or set on the system. The property name is constructed as
    * <code>clazz.getName() + . + propertyComponent[0] + . + propertyComponent[1] + ... +
    * (language.toString()|language.getCode().toLowerCase())</code> This will return true if the language specific
    * config option is set or a default (i.e. no-language specified) version is set. </p>
    *
    * @param clazz              The class with which the property is associated.
    * @param language           The language we would like the config for
    * @param propertyComponents The components.
    * @return True if the property is known, false if not.
    */
   public static boolean hasProperty(Class<?> clazz, Language language, String... propertyComponents) {
      return hasProperty(clazz.getName(), language, propertyComponents);
   }


   /**
    * Gets the value of a property for a given class and language (the language is optional)
    *
    * @param clazz              The class
    * @param language           The language
    * @param propertyComponents The components.
    * @return The value associated with clazz.propertyName.language exists or clazz.propertyName
    */
   public static Val get(Class<?> clazz, Language language, String... propertyComponents) {
      return get(clazz.getName(), language, propertyComponents);
   }

   /**
    * Gets the value of a property for a given class
    *
    * @param clazz              The class
    * @param propertyComponents The components
    * @return The value associated with clazz.propertyName
    */
   public static Val get(Class<?> clazz, String... propertyComponents) {
      return get(clazz.getName(), propertyComponents);
   }


   /**
    * Gets the value of a property for a given class and language (the language is optional)
    *
    * @param propertyPrefix     The prefix
    * @param language           The language
    * @param propertyComponents The components.
    * @return The value associated with clazz.propertyName.language exists or clazz.propertyName
    */
   public static Val get(String propertyPrefix, Language language, String... propertyComponents) {
      String key = findKey(propertyPrefix, language, propertyComponents);
      return key == null ? Val.of(null) : get(key);
   }


   static String getRaw(String property) {
      return getInstance().properties.get(property);
   }

   /**
    * <p>Gets the value associated with a property. The property name is constructed as <code>propertyPrefix + . +
    * propertyComponent[0] + . + propertyComponent[1] + ...</code></p>
    *
    * @param propertyPrefix     The prefix
    * @param propertyComponents The components
    * @return The value for the property
    */
   public static Val get(String propertyPrefix, String... propertyComponents) {
      String propertyName = propertyPrefix;
      if (propertyComponents != null && propertyComponents.length > 0) {
         propertyName += "." + StringUtils.join(propertyComponents, ".");
      }

      if (StringUtils.isNullOrBlank(propertyName)) {
         return new Val(null);
      }

      String value;
      if (getInstance().properties.containsKey(propertyName)) {
         value = getInstance().properties.get(propertyName);
      } else if (System.getProperty(propertyName) != null) {
         value = System.getProperty(propertyName);
      } else {
         value = System.getenv(propertyName);
      }

      if (value == null) {
         return new Val(null);
      }

      //resolve variables
      if (STRING_SUBSTITUTION.matcher(value).find()) {
         value = resolveVariables(getInstance().properties.get(propertyName));
      }

      if (value == null) {
         return new Val(null);
      }

      //If the value is a script then process it
      if (value.startsWith(SCRIPT_PROPERTY)) {
         return Val.of(processScript(value));
      }

      if (value.contains(BEAN_PROPERTY)) {
         return getBean(value);
      }

      return Val.of(value);
   }


   /**
    * <p> Gets a <code>list</code> of properties whose name is matched using the supplied <code>StringMatcher</code>.
    * </p>
    *
    * @param matcher The <code>StringMatcher</code> to use for matching property names.
    * @return A <code>List</code> of properties matched using the <code>StringMatcher</code>.
    */
   public static List<String> getPropertiesMatching(Predicate<? super String> matcher) {
      if (matcher != null) {
         return getInstance().properties.keySet().parallelStream().filter(matcher).collect(Collectors.toList());
      }
      return Collections.emptyList();
   }


   /**
    * Load package config.
    *
    * @param packageName the package name
    */
   public static void loadPackageConfig(@NonNull String packageName) {
      loadConfig(Resources.fromClasspath(packageName.replace('.', '/') + "/default.conf"));
   }

   /**
    * Loads a config file
    *
    * @param resource The config file
    */
   @SneakyThrows
   public static void loadConfig(Resource resource) {
      if (resource == null || !resource.exists()) {
         return;
      }
      if (resource.path() != null && getInstance().loaded.contains(resource.path())) {
         return; //Only load once!
      }
      new ConfigParser(resource).parse();
      if (resource.path() != null) {
         getInstance().loaded.add(resource.path());
      }
   }

   private static String getCallingClass() {
      // Auto-discover the package of the calling class.
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      for (int i = 1; i < stackTrace.length; i++) {
         StackTraceElement ste = stackTrace[i];
         // ignore the config class
         if (!ste.getClassName().equals(Config.class.getName()) &&
                !ste.getClassName().equals(CommandLineApplication.class.getName())
                && !ste.getClassName().equals(Application.class.getName())
            ) {
            return ste.getClassName();
         }
      }
      return null;
   }

   /**
    * Sets all properties from the given array of arguments
    *
    * @param args the args
    */
   public static void setAllCommandLine(String[] args) {
      if (args != null) {
         CommandLineParser parser = new CommandLineParser();
         parser.parse(args);
         setAllCommandLine(parser);
      }
   }

   /**
    * Sets all properties from the given command line parser.
    *
    * @param parser the parser
    */
   public static void setAllCommandLine(CommandLineParser parser) {
      if (parser != null) {
         parser.getSetEntries()
               .forEach(entry -> getInstance().setterFunction.setProperty(entry.getKey(),
                                                                          entry.getValue(),
                                                                          "CommandLine"
                                                                         )
                       );
      }
   }

   /**
    * <p> Initializes the configuration. </p> <p> Looks for a properties (programName.conf) in the classpath, the user
    * home directory, and in the run directory to load </p> <p> The command line arguments are parsed and added to the
    * configuration. </p>
    *
    * @param programName   the program name
    * @param args          the command line arguments
    * @param parser        the                    to use for parsing the arguments
    * @param otherPackages Other packages whose configs we should load
    * @return Non config/option parameters from command line
    */
   @SneakyThrows
   public static String[] initialize(String programName, String[] args, CommandLineParser parser, String... otherPackages) {
      Preloader.preload();
      String rval[];
      if (args != null) {
         rval = parser.parse(args);
      } else {
         rval = new String[0];
      }


      //Check if we should only explain the config
      if (NamedOption.CONFIG_EXPLAIN.<Boolean>getValue()) {
         getInstance().setterFunction = ConfigExplainSettingFunction.INSTANCE;
      } else {
         getInstance().setterFunction = ConfigSettingFunction.INSTANCE;
      }

      if (otherPackages != null) {
         for (String otherPackage : otherPackages) {
            loadPackageConfig(otherPackage);
         }
      }

      // Auto-discover the package of the calling class.
      String className = getCallingClass();

      if (className != null) {
         loadDefaultConf(className);
      }


      //Look for application specific properties
      Stream.of(
         new ClasspathResource(programName.replace(".", "/") + ".conf", defaultClassLoader),
         Resources.fromFile(new File(SystemInfo.USER_HOME, programName + ".conf")),
         localConfigDirectory.getChild(programName + ".conf"),
         Resources.fromFile(new File(programName + ".conf"))
               )
            .filter(Resource::exists)
            .forEach(resource -> {
               log.finest("Loading {0}.conf from :", programName);
               loadConfig(resource);
            });


      // Store the command line arguments as a config settings.
      if (args != null) {
         parser.getSetEntries().forEach(entry -> {
            ConfigSettingFunction.INSTANCE.setProperty(entry.getKey(), entry.getValue(), "CommandLine");
         });
      }

      if (parser.isSet(NamedOption.CONFIG)) {
         loadConfig(NamedOption.CONFIG.getValue());
      }


      setAllCommandLine(parser);

      // If config-explain was set then output the config recording and then quit
      if (parser.isSet(NamedOption.CONFIG_EXPLAIN)) {
         ConfigExplainSettingFunction settings = (ConfigExplainSettingFunction) getInstance().setterFunction;
         for (String key : new TreeSet<>(settings.properties.keySet())) {
            System.err.println(key);
            int max = settings.properties.get(key).size();
            int i = 1;
            for (String prop : settings.properties.get(key)) {
               System.err.println("\t" + (i == max ? "*" : "") + prop.replaceAll("\r?\n", "  "));
               i++;
            }
            System.err.println("--------------------------------------------------");
         }
         System.exit(0);
      }


      return rval;
   }

   /**
    * Load default conf.
    *
    * @param packageName the package name
    * @return the boolean
    * @throws ParseException the parse exception
    */
   protected static boolean loadDefaultConf(String packageName) throws ParseException {

      if (packageName.endsWith(".conf")) {
         return false;
      }

      packageName = packageName.replaceAll("\\$[0-9]+$", "");


      Resource defaultConf = new ClasspathResource((packageName.replaceAll("\\.",
                                                                           "/"
                                                                          ) + "/" + DEFAULT_CONFIG_FILE_NAME).trim(),
                                                   Config.getDefaultClassLoader()
      );

      // Go through each level of the package until we find one that
      // has a default properties file or we cannot go any further.
      while (!defaultConf.exists()) {
         int idx = packageName.lastIndexOf('.');
         if (idx == -1) {
            defaultConf = new ClasspathResource(packageName + "/" + DEFAULT_CONFIG_FILE_NAME,
                                                Config.getDefaultClassLoader()
            );
            break;
         }
         packageName = packageName.substring(0, idx);
         defaultConf = new ClasspathResource(packageName.replaceAll("\\.", "/") + "/" + DEFAULT_CONFIG_FILE_NAME,
                                             Config.getDefaultClassLoader()
         );
      }

      if (defaultConf.exists()) {
         loadConfig(defaultConf);
         return true;
      }


      return false;
   }

   /**
    * Initializes the config file without specifying any command line arguments
    *
    * @param programName The program name
    * @return An empty array
    */
   public static String[] initialize(String programName) {
      return initialize(programName, new String[0], new CommandLineParser());
   }

   /**
    * Initialize test.
    */
   public static void initializeTest() {
      clear();
      initialize("Test");
   }

   /**
    * <p> Initializes the configuration. </p> <p> Looks for a properties (programName.conf) in the classpath, the user
    * home directory, and in the run directory to load </p> <p> The command line arguments are parsed and added to the
    * configuration. </p>
    *
    * @param programName   the program name
    * @param args          the command line arguments
    * @param otherPackages Other packages whose configs we should load
    * @return Non config/option parameters from command line
    */
   public static String[] initialize(String programName, String[] args, String... otherPackages) {
      return initialize(programName, args, new CommandLineParser(), otherPackages);
   }

   @SneakyThrows
   private static Object processScript(String scriptStatement) {
      int idx = scriptStatement.indexOf(']');

      if (idx == -1) {
         throw new RuntimeException(scriptStatement + " is malformed (missing closing ']'')");
      }

      String extension = scriptStatement.substring(SCRIPT_PROPERTY.length(), idx).trim();

      String objectName = null;
      if (extension.contains(",")) {
         String[] splits = extension.split(",");
         extension = splits[0].trim();
         objectName = splits[1].trim();
      }

      ScriptEnvironment env = ScriptEnvironmentManager.getInstance().getEnvironment(extension);
      if (!StringUtils.isNullOrBlank(objectName)) {
         return env.getObject(objectName);
      }

      idx = scriptStatement.indexOf(':');
      if (idx == -1) {
         throw new RuntimeException(scriptStatement + " is malformed (missing :)");
      }

      String script = scriptStatement.substring(idx + 1).trim();
      return env.eval(script);
   }

   /**
    * Resolve variables string.
    *
    * @param string the string
    * @return the string
    */
   static String resolveVariables(String string) {
      if (string == null) {
         return null;
      }

      String rval = string;
      Matcher m = STRING_SUBSTITUTION.matcher(string);
      while (m.find()) {
         if (getInstance().properties.containsKey(m.group(1))) {
            rval = rval.replaceAll(Pattern.quote(m.group(0)), get(m.group(1)).asString());
         } else if (System.getProperties().contains(m.group(1))) {
            rval = rval.replaceAll(Pattern.quote(m.group(0)), System.getProperties().get(m.group(1)).toString());
         } else if (System.getenv().containsKey(m.group(1))) {
            rval = rval.replaceAll(Pattern.quote(m.group(0)), System.getenv().get(m.group(1)));
         }
      }
      return rval;
   }

   /**
    * Sets the value of a property.
    *
    * @param name  the name of the property
    * @param value the value of the property
    */
   @SneakyThrows
   public static void setProperty(String name, String value) {
      getInstance().properties.put(name, value);
      if (name.toLowerCase().endsWith(".level")) {
         String className = name.substring(0, name.length() - ".level".length());
         LogManager.getLogManager().setLevel(className, Level.parse(value.trim().toUpperCase()));
      }
      if (name.equals("com.davidbracewell.logging.logfile")) {
         LogManager.addFileHandler(value);
      }
      if (name.toLowerCase().startsWith(SYSTEM_PROPERTY)) {
         String systemSetting = name.substring(SYSTEM_PROPERTY.length());
         System.setProperty(systemSetting, value);
      }
      log.finest("Setting property {0} to value of {1}", name, value);
   }

   /**
    * Determines if the value of the given property is a script or not
    *
    * @param propertyName The name of the property to check
    * @return True if the property exists and its value is a script
    */
   public static boolean valueIsScript(String propertyName) {
      return (getInstance().properties.containsKey(propertyName) && getInstance().properties.get(propertyName)
                                                                                            .startsWith(
                                                                                               SCRIPT_PROPERTY));
   }

   /**
    * Gets default class loader.
    *
    * @return the default class loader
    */
   public static ClassLoader getDefaultClassLoader() {
      return defaultClassLoader;
   }

   /**
    * Sets the default <code>ClassLoader</code> to use with Classpath resources
    *
    * @param newDefaultClassLoader The new ClassLoader
    */
   public static void setDefaultClassLoader(@NonNull ClassLoader newDefaultClassLoader) {
      defaultClassLoader = newDefaultClassLoader;
   }

   /**
    * Converts the config into an ini format easily consumed by Python with the section "default"
    *
    * @param output the resource to output the ini file to
    * @return the resource writen to
    * @throws IOException Something went wrong writing the config file
    */
   public static Resource toPythonConfigParser(@NonNull Resource output) throws IOException {
      return toPythonConfigParser("default", output);
   }

   /**
    * Converts the config into an ini format easily consumed by Python with the given section name
    *
    * @param sectionName The section name to write properties under
    * @param output      the resource to output the ini file to
    * @return the resource writen to
    * @throws IOException Something went wrong writing the config file
    */
   public static Resource toPythonConfigParser(@NonNull String sectionName, @NonNull Resource output) throws IOException {
      try (BufferedWriter writer = new BufferedWriter(output.writer())) {
         writer.write("[");
         writer.write(sectionName);
         writer.write("]\n");
         for (Map.Entry<String, String> e : Sorting.sortMapEntries(getInstance().properties,
                                                                   Map.Entry.comparingByKey())) {
            writer.write(e.getKey());
            writer.write(" : ");
            writer.write(e.getValue().replaceAll("\n", "\n\t\t\t"));
            writer.write("\n");
         }
      }
      return output;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      out.writeObject(properties);
      out.writeObject(loaded);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      getInstance().properties.putAll(Cast.<Map<String, String>>as(in.readObject()));
      getInstance().loaded.addAll(Cast.<Set<String>>as(in.readObject()));
   }

   /**
    * A ConfigPropertySetter implementation that records which config file set a property. This class is used with the
    * <code>config-explain</code> command line option.
    */
   enum ConfigExplainSettingFunction implements ConfigPropertySetter {
      /**
       * The INSTANCE.
       */
      INSTANCE;
      /**
       * The Properties.
       */
      final Map<String, Set<String>> properties = new HashMap<>();

      @Override
      public void setProperty(String name, String value, String resourceName) {
         Config.setProperty(name, value);
         if (!properties.containsKey(name)) {
            properties.put(name, new LinkedHashSet<>());
         }
         properties.get(name).add(resourceName + "::" + value);
      }

   }

   /**
    * Standard implementation of ConfigPropertySetter
    */
   enum ConfigSettingFunction implements ConfigPropertySetter {
      /**
       * The INSTANCE.
       */
      INSTANCE;

      @Override
      public void setProperty(String name, String value, String resourceName) {
         Config.setProperty(name, value);
      }

   }

   /**
    * The enum Config setting error.
    */
   enum ConfigSettingError implements ConfigPropertySetter {
      /**
       * The INSTANCE.
       */
      INSTANCE;

      @Override
      public void setProperty(String name, String value, String resourceName) {
         throw new IllegalStateException("Config not initialized");
      }

   }

   /**
    * A <code>ConfigPropertySetter</code> takes care of setting properties and their values are they are parsed by the
    * {@link ConfigParser}.
    *
    * @author David B. Bracewell
    */
   interface ConfigPropertySetter {

      /**
       * Sets a property
       *
       * @param name         The name of the property
       * @param value        The value of the property
       * @param resourceName The resource that is responsible for this property
       */
      void setProperty(String name, String value, String resourceName);

   }// END OF ConfigPropertySetter

}// END OF Config
