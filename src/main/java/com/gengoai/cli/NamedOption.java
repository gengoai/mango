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

package com.gengoai.cli;

import com.gengoai.Validation;
import com.gengoai.config.Config;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Convert;
import com.gengoai.io.resource.Resource;
import com.gengoai.string.CharMatcher;
import com.gengoai.string.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;


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
   public NamedOption(Field field) {
      Option option = Validation.notNull(field.getAnnotationsByType(Option.class))[0];
      this.field = field;

      this.name = StringUtils.isNullOrBlank(option.name()) ? field.getName() : option.name();

      Validation.checkArgument(!StringUtils.isNullOrBlank(this.name) && !CharMatcher.WhiteSpace.matchesAnyOf(this.name),
                               "Option name must have at least one character and must not have a space");

      this.type = field.getType();

      Validation.notNullOrBlank(option.description(), "Description must not be blank");

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
   protected NamedOption(String name, Class<?> type, String description, Object defaultValue, Collection<String> aliases, boolean required) {
      Validation.checkArgument(!StringUtils.isNullOrBlank(name) && !CharMatcher.WhiteSpace.matchesAnyOf(name),
                               "Option name must have at least one character and must not have a space");
      Validation.notNullOrBlank(description, "Description must not be blank");

      this.name = name;
      this.type = type;
      this.description = description;
      this.aliases = aliases == null ? new String[0] : aliases.toArray(new String[0]);
      if (defaultValue != null) {
         Config.setProperty(this.name, Convert.convert(defaultValue, String.class));
         this.value = Convert.convert(defaultValue, type);
      } else {
         this.value = null;
      }
      this.required = required;
      this.field = null;
   }

   /**
    * Builder named option builder.
    *
    * @return the named option builder
    */
   public static NamedOptionBuilder builder() {
      return new NamedOptionBuilder();
   }


   /**
    * Get alias specifications string [ ].
    *
    * @return the string [ ]
    */
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

   /**
    * Get aliases string [ ].
    *
    * @return the string [ ]
    */
   public String[] getAliases() {
      return this.aliases;
   }

   /**
    * Gets description.
    *
    * @return the description
    */
   public String getDescription() {
      return this.description;
   }

   /**
    * Gets field.
    *
    * @return the field
    */
   public Field getField() {
      return this.field;
   }

   /**
    * Gets name.
    *
    * @return the name
    */
   public String getName() {
      return this.name;
   }

   /**
    * Gets type.
    *
    * @return the type
    */
   public Class<?> getType() {
      return this.type;
   }

   /**
    * Is required boolean.
    *
    * @return the boolean
    */
   public boolean isRequired() {
      return this.required;
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, type, description, aliases, required, field, value);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final NamedOption other = (NamedOption) obj;
      return Objects.equals(this.name, other.name)
                && Objects.equals(this.type, other.type)
                && Objects.equals(this.description, other.description)
                && Objects.deepEquals(this.aliases, other.aliases)
                && Objects.equals(this.required, other.required)
                && Objects.equals(this.field, other.field)
                && Objects.equals(this.value, other.value);
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
    * @param <T> the type parameter
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

            Class<?> genericType = field == null
                                   ? String.class
                                   : (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            this.value = Convert.convert(optionValue, type, genericType);

         } else if (Map.class.isAssignableFrom(type)) {

            Class<?> keyType = field == null
                               ? String.class
                               : (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            Class<?> valueType = field == null
                                 ? String.class
                                 : (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
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
                                                       .description(
                                                          "Configuration file that can be specified on the command line.")
                                                       .build();

   /**
    * Shows how the current configuration was created with the lineage of each property when
    * <code>--config-explain</code> is given.
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

   public String toString() {
      return "NamedOption(name=" + this.getName() + ", type=" + this.getType() + ", description=" + this.getDescription() + ", aliases=" + java.util.Arrays
                                                                                                                                              .deepToString(
                                                                                                                                                 this
                                                                                                                                                    .getAliases()) + ", required=" + this
                                                                                                                                                                                        .isRequired() + ", field=" + this
                                                                                                                                                                                                                        .getField() + ", value=" + this
                                                                                                                                                                                                                                                      .getValue() + ")";
   }

   /**
    * The type Named option builder.
    */
   public static class NamedOptionBuilder {
      private String name;
      private Class<?> type;
      private String description;
      private Object defaultValue;
      private ArrayList<String> aliases;
      private boolean required;

      /**
       * Instantiates a new Named option builder.
       */
      NamedOptionBuilder() {
      }

      /**
       * Alias named option . named option builder.
       *
       * @param alias the alias
       * @return the named option . named option builder
       */
      public NamedOption.NamedOptionBuilder alias(String alias) {
         if (this.aliases == null) this.aliases = new ArrayList<String>();
         this.aliases.add(alias);
         return this;
      }

      /**
       * Aliases named option . named option builder.
       *
       * @param aliases the aliases
       * @return the named option . named option builder
       */
      public NamedOption.NamedOptionBuilder aliases(Collection<? extends String> aliases) {
         if (this.aliases == null) this.aliases = new ArrayList<String>();
         this.aliases.addAll(aliases);
         return this;
      }

      /**
       * Build named option.
       *
       * @return the named option
       */
      public NamedOption build() {
         Collection<String> aliases;
         switch (this.aliases == null ? 0 : this.aliases.size()) {
            case 0:
               aliases = java.util.Collections.emptyList();
               break;
            case 1:
               aliases = java.util.Collections.singletonList(this.aliases.get(0));
               break;
            default:
               aliases = java.util.Collections.unmodifiableList(new ArrayList<String>(this.aliases));
         }

         return new NamedOption(name, type, description, defaultValue, aliases, required);
      }

      /**
       * Clear aliases named option . named option builder.
       *
       * @return the named option . named option builder
       */
      public NamedOption.NamedOptionBuilder clearAliases() {
         if (this.aliases != null)
            this.aliases.clear();

         return this;
      }

      /**
       * Default value named option . named option builder.
       *
       * @param defaultValue the default value
       * @return the named option . named option builder
       */
      public NamedOption.NamedOptionBuilder defaultValue(Object defaultValue) {
         this.defaultValue = defaultValue;
         return this;
      }

      /**
       * Description named option . named option builder.
       *
       * @param description the description
       * @return the named option . named option builder
       */
      public NamedOption.NamedOptionBuilder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * Name named option . named option builder.
       *
       * @param name the name
       * @return the named option . named option builder
       */
      public NamedOption.NamedOptionBuilder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * Required named option . named option builder.
       *
       * @param required the required
       * @return the named option . named option builder
       */
      public NamedOption.NamedOptionBuilder required(boolean required) {
         this.required = required;
         return this;
      }

      public String toString() {
         return "NamedOption.NamedOptionBuilder(name=" + this.name + ", type=" + this.type + ", description=" + this.description + ", defaultValue=" + this.defaultValue + ", aliases=" + this.aliases + ", required=" + this.required + ")";
      }

      /**
       * Type named option . named option builder.
       *
       * @param type the type
       * @return the named option . named option builder
       */
      public NamedOption.NamedOptionBuilder type(Class<?> type) {
         this.type = type;
         return this;
      }
   }
}//END OF NamedOption
