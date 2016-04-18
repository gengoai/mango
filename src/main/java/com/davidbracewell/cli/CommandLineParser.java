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

import com.davidbracewell.config.Config;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Throwables;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;


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
    this.applicationDescription = StringUtils.isNullOrBlank(applicationDescription) ? "Help" : applicationDescription.trim();
    addOption(NamedOption.HELP);
    addOption(NamedOption.CONFIG);
    addOption(NamedOption.CONFIG_EXPLAIN);
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

    String key = null;

    for (String current : args) {
      if (current == null) {
        continue;
      }

      if (LONG.equals(current) || SHORT.equals(current)) {
        throw new CommandLineParserException(current, null);
      }

      if (current.startsWith(LONG)) {
        if (key != null) {
          setValue(key, "true");
        } else {
          if (current.endsWith(KEY_VALUE_SEPARATOR)) {
            key = current.substring(LONG.length(),current.length()-1);
          } else if (current.contains(KEY_VALUE_SEPARATOR)) {
            int pos = current.indexOf(KEY_VALUE_SEPARATOR);
            setValue(current.substring(LONG.length(), pos), current.substring(pos + KEY_VALUE_SEPARATOR.length()));
          } else {
            key = current.substring(LONG.length());
          }
        }
      } else if (current.startsWith(SHORT)) {
        if (key != null) {
          setValue(key, "true");
        } else {
          if (current.contains(KEY_VALUE_SEPARATOR)) {
            int pos = current.indexOf(KEY_VALUE_SEPARATOR);
            setValue(current.substring(SHORT.length(), pos), current.substring(pos + KEY_VALUE_SEPARATOR.length()));
          } else {
            key = current.substring(SHORT.length());
          }
        }
      } else if (current.equals(KEY_VALUE_SEPARATOR)) {
        if (key == null) {
          throw new CommandLineParserException(current, null);
        }
      } else if (key != null) {
        setValue(key, current);
        key = null;
      } else {
        filtered.add(current);
      }
    }

    if (key != null) {
      setValue(key, "true");
    }

    optionSet.forEach(option -> {
      if (option.getName().equals("h") && Cast.<Boolean>as(option.getValue())) {
        showHelp();
        System.exit(0);
      } else if (option.isRequired() && !isSet(option.getName())) {
        System.err.println("ERROR: " + option.getName() + " is required, but was not set.");
        showHelp();
        System.exit(-1);
      }

      if (owner != null && option.getField() != null) {
        try {
          Reflect.onObject(owner).allowPrivilegedAccess().set(option.getField().getName(), option.getValue());
        } catch (ReflectionException e) {
          throw Throwables.propagate(e);
        }
      }
    });


    return filtered.toArray(new String[filtered.size()]);
//    List<String> filtered = new ArrayList<>();
//
//    for (ListIterator<String> iterator = Arrays.asList(args).listIterator(); iterator.hasNext(); ) {
//
//      String current = iterator.next();
//      if (current == null) {
//        continue;
//      }
//      if (LONG.equals(current) || SHORT.equals(current)) {
//        throw new CommandLineParserException(current, null);
//      }
//
//      if (current.startsWith(LONG)) {
//
//        String key;
//        String value;
//
//        if (current.endsWith(KEY_VALUE_SEPARATOR)) {
//
//          key = current.substring(0, current.length() - 1);
//          value = iterator.hasNext() ? iterator.next() : null;
//
//        } else if (current.contains(KEY_VALUE_SEPARATOR)) {
//
//          int index = current.indexOf(KEY_VALUE_SEPARATOR);
//          key = current.substring(0, index);
//          value = current.substring(index + 1);
//
//        } else {
//
//          key = current;
//          value = iterator.hasNext() ? iterator.next() : null;
//          if (KEY_VALUE_SEPARATOR.equals(value)) {
//            value = iterator.hasNext() ? iterator.next() : null;
//          }
//
//        }
//
//        value = setValue(key, value);
//        if (value == null) {
//          iterator.previous();
//        }
//
//      } else if (current.startsWith(SHORT)) {
//
//        for (int i = 1; i < current.length(); i++) {
//          if (CharMatcher.JAVA_LETTER_OR_DIGIT.negate().matches(current.charAt(i))) {
//            throw new CommandLineParserException(Character.toString(current.charAt(i)), null);
//          }
//          setValue(Character.toString(current.charAt(i)), null);
//        }
//
//      } else {
//
//        filtered.add(current);
//
//      }
//
//
//    }
//
//    optionSet.forEach(option -> {
//      if (option.getName().equals("h") && Cast.<Boolean>as(option.getValue())) {
//        showHelp();
//        System.exit(0);
//      } else if (option.isRequired() && !isSet(option.getName())) {
//        System.err.println("ERROR: " + option.getName() + " is required, but was not set.");
//        showHelp();
//        System.exit(-1);
//      }
//
//      if (owner != null && option.getField() != null) {
//        try {
//          Reflect.onObject(owner).allowPrivilegedAccess().set(option.getField().getName(), option.getValue());
//        } catch (ReflectionException e) {
//          throw Throwables.propagate(e);
//        }
//      }
//    });
//
//
//    return filtered.toArray(new String[filtered.size()]);
  }


  /**
   * Prints help to standard error
   */

  public void showHelp() {
    System.err.println(applicationDescription);
    int maxArgName = optionSet.stream().map(no -> no.getName().length()).max((x, y) -> -x.compareTo(y)).orElse(10);
    optionSet.stream().map(NamedOption::getName)
      .sorted()
      .forEach(name -> {
          NamedOption no = options.get(name);
          String arg = no.getSpecification();
          boolean insertSpace = !arg.startsWith("--");
          System.err.printf(String.format((insertSpace ? " " : "") + "%1$-" + maxArgName + "s", arg));
          System.err.print(" " + no.getDescription());
          if (no.isRequired()) {
            System.err.print("\t[REQUIRED]");
          }
          System.err.println();
          for (String alias : no.getAliases()) {
            if (!alias.equals(arg)) {
              insertSpace = !alias.startsWith("--");
              System.err.println(String.format((insertSpace ? " " : "") + "   %1$-" + maxArgName + "s   ", alias));
            }
          }
        }
      );

  }


  private String setValue(String key, String value) {
    NamedOption option = options.get(key.replaceAll("^-+", ""));

//    System.out.println(key + " := " + option);

    if (option == null) {
      if (key.startsWith(LONG)) {
        unamedOptions.put(key.substring(2), value);
        return value;
      } else if (value == null || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
        unamedOptions.put(key.substring(1), value);
        return value;
      }

      unamedOptions.put(key.substring(1), "true");
      return null;

    } else if (option.isBoolean()) {

      if (value == null || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
        option.setValue(value);
        Config.setProperty(option.getName(), value == null ? "true" : value);
        return null;
      }

      Config.setProperty(key, "true");
      option.setValue(null);
      return value;

    } else {

      if (StringUtils.isNullOrBlank(value)) {
        throw new CommandLineParserException(key, value);
      }

      Config.setProperty(option.getName(), value);
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
      .map(no -> Tuple2.of(no.getName(), Convert.convert(no.getValue(), String.class)))
      .collect(Collectors.toSet());
    entries.addAll(unamedOptions.entrySet());
    return entries;
  }

}//END OF CLI
