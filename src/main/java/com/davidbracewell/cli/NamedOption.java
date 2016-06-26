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
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.Singular;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Represents an named command line option. The command line specification is determined by the name and type of the
 * option.
 * <ul>
 * <li>Name is a single character and is boolean: Short form <code>-n</code></li>
 * <li>Name is a multiple characters: long form <code>--n</code></li>
 * </ul>
 * <code>NamedOption</code>s can be built either by passing a <code>Field</code> containing an {@link Option}
 * annotation or by using the builder.
 * </p>
 * <p>
 * Three options are predefined and are automatically added to every command line parser:
 * <ul>
 * <li>{@link NamedOption#HELP}: Shows help if <code>-h</code> or <code>--help</code> is given.</li>
 * <li>{@link NamedOption#CONFIG}: Specifies a configuration resource to load when <code>--config</code> is given.
 * </li>
 * <li>{@link NamedOption#CONFIG_EXPLAIN}: Shows how the current configuration was created with the lineage of each
 * property when <code>--config-explain</code> is given.</li>
 * </ul>
 * </p>
 *
 * @author David B. Bracewell
 */
@Data
public final class NamedOption {


  private final String name;
  private final Class<?> type;
  private final String description;
  private final String[] aliases;
  private final boolean required;
  private final Field field;
  private Object value = null;

  /**
   * Instantiates a new Named option.
   *
   * @param field the field which contains an {@link Option} annotation
   */
  public NamedOption(@NonNull Field field) {
    Option option = Preconditions.checkNotNull(field.getAnnotationsByType(Option.class))[0];
    this.field = field;

    this.name = StringUtils.isNullOrBlank(option.name()) ? field.getName() : option.name();
    Preconditions.checkArgument(
      !StringUtils.isNullOrBlank(this.name) && !CharMatcher.WHITESPACE.matchesAnyOf(this.name),
      "Option name must have at least one character and must not have a space"
    );

    this.type = field.getType();

    Preconditions.checkArgument(
      !StringUtils.isNullOrBlank(option.description()),
      "Description must not be blank"
    );
    this.description = option.description();

    if (!StringUtils.isNullOrBlank(option.defaultValue())) {
      Config.setProperty(this.name, option.defaultValue());
      this.value = Convert.convert(option.defaultValue(), type);
    } else {
      this.value = null;
    }
    this.aliases = option.aliases();
    this.required = option.required();
  }

  /**
   * Instantiates a new Named option.
   *
   * @param name         the name
   * @param type         the type
   * @param description  the description
   * @param defaultValue the default value
   * @param aliases      the aliases
   * @param required     the required
   */
  @Builder
  protected NamedOption(@NonNull String name, @NonNull Class<?> type, @NonNull String description, Object defaultValue, @Singular Collection<String> aliases, boolean required) {
    Preconditions.checkArgument(
      !StringUtils.isNullOrBlank(name) && !CharMatcher.WHITESPACE.matchesAnyOf(name),
      "Option name must have at least one character and must not have a space"
    );
    Preconditions.checkArgument(
      !StringUtils.isNullOrBlank(description),
      "Description must not be blank"
    );

    this.name = name;
    this.type = type;
    this.description = description;
    this.aliases = aliases == null ? new String[0] : aliases.toArray(new String[aliases.size()]);
    if (defaultValue != null) {
      Config.setProperty(this.name, Convert.convert(defaultValue, String.class));
      this.value = Convert.convert(defaultValue, type);
    } else {
      this.value = null;
    }
    this.required = required;
    this.field = null;
  }


  public String[] getAliasSpecifications() {
    if (aliases == null) {
      return new String[0];
    }
    String[] a = new String[aliases.length];
    for (int i = 0; i < aliases.length; i++) {
      a[i] = toSpecificationForm(aliases[i]);
    }
    return a;
  }


  private String toSpecificationForm(String optionName) {
    if (isBoolean() && optionName.length() == 1) {
      return "-" + optionName;
    }
    return "--" + optionName;
  }

  /**
   * Gets specification form for the command line.
   *
   * @return the specification form for the command line
   */
  public String getSpecification() {
    return toSpecificationForm(name);
  }

  /**
   * Gets the value of the option.
   *
   * @return the value op the option
   */
  public <T> T getValue() {
    return Cast.as(value);
  }

  /**
   * Sets the value of the option.
   *
   * @param optionValue the option value
   */
  void setValue(String optionValue) {
    if (StringUtils.isNullOrBlank(optionValue) && isBoolean()) {

      this.value = true;

    } else if (!StringUtils.isNullOrBlank(optionValue)) {

      if (Collection.class.isAssignableFrom(type)) {

        Class<?> genericType = field == null ? String.class : (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        this.value = Convert.convert(optionValue, type, genericType);

      } else if (Map.class.isAssignableFrom(type)) {

        Class<?> keyType = field == null ? String.class : (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        Class<?> valueType = field == null ? String.class : (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
        this.value = Convert.convert(optionValue, type, keyType, valueType);

      } else {

        this.value = Convert.convert(optionValue, type);

      }

    }


  }

  /**
   * Determines if the option is a boolean type or not
   *
   * @return True if the option is a boolean type, False otherwise
   */
  public boolean isBoolean() {
    return boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type);
  }


  /**
   * Shows help if <code>-h</code> or <code>--help</code> is given.</li>
   */
  public static final NamedOption HELP = NamedOption.builder()
    .name("h")
    .type(Boolean.class)
    .description("Shows this help")
    .alias("help")
    .defaultValue(false)
    .build();

  /**
   * Specifies a configuration resource to load when <code>--config</code> is given.
   */
  public static final NamedOption CONFIG = NamedOption.builder()
    .name("config")
    .type(Resource.class)
    .description("Configuration file that can be specified on the command line.")
    .build();

  /**
   * Shows how the current configuration was created with the lineage of each
   * property when <code>--config-explain</code> is given.
   */
  public static final NamedOption CONFIG_EXPLAIN = NamedOption.builder()
    .name("config-explain")
    .type(Boolean.class)
    .description("Explains how the config values were set.")
    .defaultValue(false)
    .alias("config_explain")
    .alias("config_dump")
    .alias("config-dump")
    .alias("dump_config")
    .alias("dump-config")
    .build();

}//END OF NamedOption
