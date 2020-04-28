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
 *
 */

package com.gengoai.annotation;

import org.kohsuke.MetaInfServices;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

import static com.gengoai.annotation.AnnotationUtils.getClassName;
import static com.gengoai.annotation.AnnotationUtils.getTypeMirror;

/**
 * @author David B. Bracewell
 */
@SupportedAnnotationTypes("com.gengoai.annotation.JsonHandler")
@MetaInfServices(Processor.class)
public class JsonHandlerProcessor extends AbstractServiceLikeAnnotation<JsonHandler> {

   public JsonHandlerProcessor() {
      super("META-INF/marshallers.json", JsonHandler.class);
   }

   @Override
   protected String processElement(TypeElement e, Elements elements, JsonHandler annotation) {
      return getClassName(elements, e.asType()) +
         "\t" +
         annotation.isHierarchical() +
         "\t" +
         getClassName(elements, getTypeMirror(annotation));
   }


}//END OF JsonHandlerProcessor
