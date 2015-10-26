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

package com.davidbracewell.annotation;

import com.davidbracewell.conversion.Cast;
import com.google.common.base.Throwables;
import org.kohsuke.MetaInfServices;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
@SupportedAnnotationTypes("com.davidbracewell.annotation.DynamicEnumeration")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@MetaInfServices(Processor.class)
public class DynamicEnumProcessor extends AbstractProcessor {

  public DynamicEnumProcessor() {
    super();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element e : roundEnv.getElementsAnnotatedWith(DynamicEnumeration.class)) {
      if (e.getKind() == ElementKind.CLASS) {
        TypeElement classElement = Cast.as(e);


        Element parent = classElement.getEnclosingElement();
        while (parent != null && !(parent instanceof PackageElement)) {
          parent = parent.getEnclosingElement();
        }
        if (parent == null) {
          continue;
        }

        PackageElement packageElement = Cast.as(parent);

        try {

          String cName = classElement.getSimpleName() + "Enum";
          JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(packageElement.getQualifiedName() + "." + cName);
          BufferedWriter bw = new BufferedWriter(fileObject.openWriter());

          String classFile = "import com.davidbracewell.DynamicEnum;\n" +
            "import com.davidbracewell.EnumValue;\n" +
            "import com.davidbracewell.Language;\n" +
            "import com.davidbracewell.config.Config;\n" +
            "import com.davidbracewell.hermes.annotator.Annotator;\n" +
            "import com.davidbracewell.reflection.BeanUtils;\n" +
            "import com.davidbracewell.string.StringUtils;\n" +
            "import com.google.common.base.Preconditions;\n" +
            "\n" +
            "import java.io.ObjectStreamException;\n" +
            "import java.util.Collection;\n" +
            "import java.util.HashSet;\n" +
            "import java.util.Set;\n" +
            "\n" +
            "public final class $TYPE_NAME extends EnumValue {\n" +
            "\n" +
            "  private static final DynamicEnum<$TYPE_NAME> index = new DynamicEnum<>();\n" +
            "  private static final long serialVersionUID = 1L;\n" +
            "\n" +
            "  private $TYPE_NAME(String name) {\n" +
            "    super(name);\n" +
            "  }\n" +
            "\n" +
            "  /**\n" +
            "   * Creates a new $TYPE_NAME Type or retrieves an already existing one for a given name\n" +
            "   *\n" +
            "   * @param name the name\n" +
            "   * @return the $TYPE_NAME type\n" +
            "   */\n" +
            "  public static $TYPE_NAME create(String name) {\n" +
            "    if (StringUtils.isNullOrBlank(name)) {\n" +
            "      throw new IllegalArgumentException(name + \" is invalid\");\n" +
            "    }\n" +
            "    return index.register(new $TYPE_NAME(name));\n" +
            "  }\n" +
            "\n" +
            "  /**\n" +
            "   * Determine if an $TYPE_NAME exists for the given name\n" +
            "   *\n" +
            "   * @param name the name\n" +
            "   * @return True if it exists, otherwise False\n" +
            "   */\n" +
            "  public static boolean isDefined(String name) {\n" +
            "    return index.isDefined(name);\n" +
            "  }\n" +
            "\n" +
            "  /**\n" +
            "   * Gets the $TYPE_NAME from its name. Throws an <code>IllegalArgumentException</code> if the name is not valid.\n" +
            "   *\n" +
            "   * @param name the name as a string\n" +
            "   * @return the $TYPE_NAME for the string\n" +
            "   */\n" +
            "  public static $TYPE_NAME valueOf(String name) {\n" +
            "    return index.valueOf(name);\n" +
            "  }\n" +
            "\n" +
            "  /**\n" +
            "   * Returns the values for this dynamic enum\n" +
            "   *\n" +
            "   * @return All known $TYPE_NAME\n" +
            "   */\n" +
            "  public static Collection<$TYPE_NAME> values() {\n" +
            "    return index.values();\n" +
            "  }\n" +
            "\n" +
            "  private Object readResolve() throws ObjectStreamException {\n" +
            "    if (isDefined(name())) {\n" +
            "      return index.valueOf(name());\n" +
            "    }\n" +
            "    Object o = index.register(this);\n" +
            "    return o;\n" +
            "  }\n" +
            "\n" +
            "\n" +
            "}//END OF $TYPE_NAME";
          bw.append("package ");
          bw.append(packageElement.getQualifiedName());
          bw.append(";");
          bw.newLine();
          bw.newLine();
          bw.append(classFile.replace("$TYPE_NAME", cName));
          bw.newLine();
          bw.close();

        } catch (IOException ioe) {
          throw Throwables.propagate(ioe);
        }
      }
    }
    return true;
  }

}//END OF DynamicEnumProcessor
