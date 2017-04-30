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

package com.davidbracewell.json;

/**
 * The types of elements that are encountered in an structured document.
 *
 * @author David B. Bracewell
 */
public enum JsonTokenType {
   /**
    * Marks the beginning of an object
    */
   BEGIN_OBJECT,
   /**
    * Marks the end of an object
    */
   END_OBJECT,
   /**
    * Marks a value in an array
    */
   VALUE,
   /**
    * Marks the beginning of an array
    */
   BEGIN_ARRAY,
   /**
    * Marks the end of an array
    */
   END_ARRAY,
   /**
    * Marks the beginning of a document
    */
   BEGIN_DOCUMENT,
   /**
    * Marks the end of a document
    */
   END_DOCUMENT,
   /**
    * Marks the beginning of a key-value pair
    */
   NAME,
   /**
    * Something unknown
    */
   OTHER,
   /**
    * Marks the end of a key value pair
    */
   END_KEY_VALUE
} //END OF StructuredElement
