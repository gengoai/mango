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

package com.davidbracewell.cli;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.function.Unchecked;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <p> A simple command line parser. </p> <p> Options (flags) are added via the <code>addOption</code> method and have
 * the following specification: <p/>
 * <pre>
 * --longOption=ARG
 * </pre>
 * <p/>
 * <pre>
 * -s ARG
 * </pre>
 * <p/> where ARG means the option requires an argument. An option can have an unlimited number of aliases. </p>
 *
 * @author David B. Bracewell
 */
public class CommandLineParser {

  private static final String KEY_VALUE_SEPARATOR = "=";
  private static final String LONG = "--";
  private static final String SHORT = "-";
  private final Map<String, NamedOption> options = new HashMap<>();
  private final Set<NamedOption> optionSet = new HashSet<>();
  private final Map<String, String> unamedOptions = new HashMap<>();
  private final Object owner;
  private final String applicationDescription;


  /**
   * Instantiates a new Command line parser.
   */
  public CommandLineParser() {
    this(null, StringUtils.EMPTY);
  }

  /**
   * Instantiates a new Command line parser.
   *
   * @param applicationDescription the application description
   */
  public CommandLineParser(String applicationDescription) {
    this(null, applicationDescription);
  }

  /**
   * Instantiates a new Command line parser.
   *
   * @param owner the owner
   */
  public CommandLineParser(Object owner) {
    this(owner, StringUtils.EMPTY);
  }

  /**
   * Instantiates a new Command line parser.
   *
   * @param owner                  the owner
   * @param applicationDescription the application description
   */
  public CommandLineParser(Object owner, String applicationDescription) {
    this.owner = owner;
    if (owner != null) {
      Reflect.onObject(owner).allowPrivilegedAccess().getFields().forEach(field -> {
        if (field.getAnnotationsByType(Option.class).length > 0) {
          addOption(new NamedOption(field));
        }
      });
    }
    this.applicationDescription = StringUtils.isNullOrBlank(applicationDescription)
                                  ? "Help"
                                  : applicationDescription.trim();
    addOption(NamedOption.HELP);
    addOption(NamedOption.CONFIG);
    addOption(NamedOption.CONFIG_EXPLAIN);
  }

  private static String peek(List<String> list, int index) {
    if (index < 0 || index >= list.size()) {
      return StringUtils.EMPTY;
    }
    return list.get(index);
  }

  private static boolean isOptionName(String string) {
    return string.startsWith(LONG) || string.startsWith(SHORT);
  }

  /**
   * Add option.
   *
   * @param namedOption the named option
   */
  public void addOption(@NonNull NamedOption namedOption) {
    options.put(namedOption.getName(), namedOption);
    optionSet.add(namedOption);
    if (namedOption.getAliases() != null) {
      for (String alias : namedOption.getAliases()) {
        options.put(alias, namedOption);
      }
    }
  }

  /**
   * Parses an array of arguments
   *
   * @param args The command line arguments.
   * @return the non-config/option parameters
   */
  public String[] parse(@NonNull String[] args) {
    List<String> filtered = new ArrayList<>();
    List<String> cleanedArgs = new ArrayList<>();
    for (String c : args) {
      if (c.endsWith(KEY_VALUE_SEPARATOR) && isOptionName(c)) {
        cleanedArgs.add(c.substring(0, c.length() - 1));
        cleanedArgs.add(KEY_VALUE_SEPARATOR);
      } else if (c.contains(KEY_VALUE_SEPARATOR) && isOptionName(c)) {
        int index = c.lastIndexOf(KEY_VALUE_SEPARATOR);
        cleanedArgs.add(c.substring(0, index));
        cleanedArgs.add(KEY_VALUE_SEPARATOR);
        cleanedArgs.add(c.substring(index + KEY_VALUE_SEPARATOR.length()));
      } else {
        cleanedArgs.add(c);
      }
    }

    for (int i = 0; i < cleanedArgs.size(); i++) {
      String current = cleanedArgs.get(i);

      if (StringUtils.isNullOrBlank(current)) {
        continue;
      } else if (LONG.equals(current) || SHORT.equals(current)) {
        throw new CommandLineParserException(current, null);
      }

      if (current.startsWith(LONG)) {
        String value = "true";
        String next = peek(cleanedArgs, i + 1);
        if (next.equalsIgnoreCase(KEY_VALUE_SEPARATOR)) {
          value = peek(cleanedArgs, i + 2);
          i += 2;
        } else if (!isOptionName(next)) {
          i++;
          value = next;
        }
        setValue(current, value, filtered);
      } else if (current.startsWith(SHORT)) {

        //Are we setting multiple "boolean" values?
        if (current.length() > 2) {
          char[] opts = current.substring(1).toCharArray();
          for (char c : opts) {
            setValue("-" + c, "true", filtered);
          }
        } else {
          String value = "true";
          String next = peek(cleanedArgs, i + 1);
          if (next.equalsIgnoreCase(KEY_VALUE_SEPARATOR)) {
            value = peek(cleanedArgs, i + 2);
            i += 2;
          } else if (!isOptionName(next)) {
            i++;
            value = next;
          }
          setValue(current, value, filtered);
        }

      } else {
        throw new CommandLineParserException(current, null);
      }

    }

    optionSet.forEach(Unchecked.consumer(option -> {
      if (option.getName().equals("h") && Cast.<Boolean>as(option.getValue())) {
        showHelp();
        System.exit(0);
      } else if (option.isRequired() && !isSet(option.getName())) {
        System.err.println("ERROR: " + option.getName() + " is required, but was not set.");
        showHelp();
        System.exit(-1);
      }

      if (owner != null && option.getField() != null) {
        Reflect.onObject(owner).allowPrivilegedAccess().set(option.getField().getName(), option.getValue());
      }
    }));

    return filtered.toArray(new String[filtered.size()]);
  }


  /**
   * Prints help to standard error
   */

  public void showHelp() {
    System.err.println(applicationDescription);
    System.err.println("===============================================");

    Map<NamedOption, String> optionNames = new HashMap<>();
    optionSet.stream()
             .forEach(option -> {
               String out = Stream.concat(
                 Stream.of(option.getAliasSpecifications()),
                 Stream.of(option.getSpecification())
                                         ).sorted((s1, s2) -> Integer.compare(s1.length(), s2.length()))
                                  .collect(Collectors.joining(", "));
               if (option.isRequired()) {
                 out += " *";
               }
               optionNames.put(option, out);
             });

    int maxArgName = optionNames.values().stream().mapToInt(String::length).max().orElse(10);

    optionNames.entrySet().stream()
               .sorted(Map.Entry.comparingByValue())
               .forEach(entry -> {
                 String arg = entry.getValue();
                 boolean insertSpace = !arg.startsWith("--");
                 if (insertSpace) {
                   System.err.print(" ");
                 }
                 System.err.printf("%1$-" + maxArgName + "s\t", arg);
                 System.err.println(entry.getKey().getDescription());
               });

    System.err.println("===============================================");
    System.err.println("* = Required");

  }


  private String setValue(String key, String value, List<String> filtered) {
    NamedOption option = options.get(key.replaceAll("^-+", ""));

    if (option == null) {
      filtered.add(key);
      if (value == null) {
        value = "true";
      } else {
        filtered.add(value);
      }
      unamedOptions.put(key.replaceAll("^-+", ""), value);
      return value;

    } else if (option.isBoolean()) {

      if (value == null || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
        option.setValue(value);
        return null;
      }

      option.setValue(null);
      return value;

    } else {

      if (StringUtils.isNullOrBlank(value)) {
        throw new CommandLineParserException(key, value);
      }

      option.setValue(value);
      return value;

    }

  }

  /**
   * Determines if an option was set or not.
   *
   * @param optionName the option name
   * @return True if it was set (boolean options must be true), False otherwise
   */
  public boolean isSet(String optionName) {
    if (!options.containsKey(optionName)) {
      return unamedOptions.containsKey(optionName);
    }
    NamedOption option = options.get(optionName);
    if (option.isBoolean()) {
      return option.getValue() != null && Cast.<Boolean>as(option.getValue());
    }
    return option.getValue() != null;
  }

  public boolean isSet(NamedOption option) {
    if (option == null) {
      return false;
    }
    return isSet(option.getName());
  }

  /**
   * Get t.
   *
   * @param <T>        the type parameter
   * @param optionName the option name
   * @return the t
   */
  public <T> T get(String optionName) {
    if (!options.containsKey(optionName)) {
      return Cast.as(unamedOptions.get(optionName));
    }
    NamedOption option = options.get(optionName);
    if (option.isBoolean()) {
      if (option.getValue() == null) {
        return Cast.as(Boolean.FALSE);
      }
      return Cast.as(option.getValue());
    }
    return options.get(optionName).getValue();
  }

  /**
   * Gets the specified options.
   *
   * @return the specified options
   */
  public Set<NamedOption> getOptions() {
    return Collections.unmodifiableSet(optionSet);
  }

  /**
   * Gets options and values for everything passed in to the command line including unamed options.
   *
   * @return An <code>Map.Entry</code> of options and values for everything passed in to the command line including
   * unamed options.
   */
  public Set<Map.Entry<String, String>> getSetEntries() {
    Set<Map.Entry<String, String>> entries = optionSet.stream()
                                                      .filter(this::isSet)
                                                      .map(no -> Tuple2.of(no.getName(),
                                                                           Convert.convert(no.getValue(), String.class)
                                                                          ))
                                                      .collect(Collectors.toSet());
    entries.addAll(unamedOptions.entrySet());
    return entries;
  }

}//END OF CLI
