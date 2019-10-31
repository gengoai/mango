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

package com.gengoai.io;

import com.gengoai.SystemInfo;
import com.gengoai.Validation;
import com.gengoai.io.resource.ClasspathResource;
import com.gengoai.io.resource.FileResource;
import com.gengoai.io.resource.Resource;
import com.gengoai.io.resource.ZipResource;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <p>Utilities for reading contents of jar files.</p>
 *
 * @author David B. Bracewell
 */
public class JarUtils {


   private static final List<Resource> classpathResources = new ArrayList<>();

   /**
    * Gets classpath resources.
    *
    * @return A list of all resources on the classpath
    */
   public static List<Resource> getClasspathResources() {
      synchronized (classpathResources) {
         if (classpathResources.isEmpty()) {
            for (String jar : SystemInfo.JAVA_CLASS_PATH.split(SystemInfo.PATH_SEPARATOR)) {
               File file = new File(jar);
               if (file.isDirectory()) {
                  classpathResources.addAll(new FileResource(file).getChildren(true));
               } else {
                  classpathResources.addAll(getJarContents(Resources.fromFile(jar), v -> true));
               }
            }
         }
         return Collections.unmodifiableList(classpathResources);
      }
   }

   public static List<Resource> getClassPathJars() {
      List<Resource> jars = new ArrayList<>();
      for (String jar : SystemInfo.JAVA_CLASS_PATH.split(SystemInfo.PATH_SEPARATOR)) {
         File file = new File(jar);
         if (!file.exists()) {
            continue;
         }
         if (file.isDirectory()) {
            jars.add(new FileResource(jar));
         } else {
            jars.add(new ZipResource(jar, null));
         }

      }
      return jars;
   }

   /**
    * Gets the jar file that a class is stored in.
    *
    * @param clazz The class whose associated jar file is descried.
    * @return The Resource (jar file) for the class
    */
   public static Resource getJar(Class<?> clazz) {
      URL fileURL = clazz.getProtectionDomain().getCodeSource().getLocation();
      return new FileResource(fileURL.getFile());
   }

   private static List<Resource> getJarContents(Resource resource, Predicate<? super String> stringMatcher) {
      if (resource.isDirectory()) {
         return getResourcesFromDirectory(resource, stringMatcher);
      } else {
         return getResourcesFromJar(resource, stringMatcher);
      }

   }

   /**
    * <p> Traverse a jar file and get the package names in it </p>
    *
    * @param resource The jar file to traverse
    * @return A Set of package names
    */
   public static List<Resource> getJarContents(Resource resource) {
      return getResourcesFromJar(resource, v -> true);
   }

   private static List<Resource> getResourcesFromDirectory(Resource resource, Predicate<? super String> stringMatcher) {
      Validation.checkArgument(resource.isDirectory());
      List<Resource> children = new ArrayList<>();
      for (Resource child : resource.getChildren()) {
         children.add(child);
         children.addAll(getJarContents(child));
      }
      return children;
   }

   private static List<Resource> getResourcesFromJar(Resource resource, Predicate<? super String> stringMatcher) {
      JarFile jf = null;
      List<Resource> resources = new ArrayList<>();
      try {
         try {
            jf = new JarFile(resource.asFile().get());
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
         Enumeration<JarEntry> e = jf.entries();
         while (e.hasMoreElements()) {
            String name = e.nextElement().getName();
            if (stringMatcher.test(name)) {
               resources.add(new ClasspathResource(name));
            }
         }
      } finally {
         QuietIO.closeQuietly(jf);
      }
      return resources;
   }


}//END OF JarUtils
