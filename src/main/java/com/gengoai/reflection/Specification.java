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

package com.gengoai.reflection;

import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Val;
import com.gengoai.function.Unchecked;
import com.gengoai.io.CSV;
import com.gengoai.io.CSVReader;
import com.gengoai.string.StringUtils;
import lombok.SneakyThrows;

import java.io.StringReader;
import java.util.List;

/**
 * <p>Defines a methodology for converting a string representation of specification into an object. This is done using
 * bean style setters where the string form is the name after <code>set</code> with the first letter lowercased, e.g.
 * <code>setParameter</code> becomes <code>parameter</code>. A colon (<code>:</code>) is used to separate parameters and
 * their values and commas are used to separate entries. As an example the following specification string:</p>
 *
 * <pre>{@code parameter:A, isRecursive:true}</pre>
 *
 * <p>would result in the setters <code>setParameter</code> and <code>setIsRecursive</code> being invoked with the
 * values <code>A</code> and <code>true</code> respectively.</p>
 *
 * <p>In addition to setters, <code>add</code> and <code>put</code> prefixed methods can also be specified. For put
 * operations a second value can be specified using a second colon operator, e.g. <code>all:key:value</code>. For
 * methods whose names are simply <code>add</code>, <code>put</code>, or <code>set</code>, the name can specified as the
 * method name.</p>
 *
 * @author David B. Bracewell
 */
public interface Specification {

   /**
    * Initializes the object via string specification. The string specification is in the
    * form of <code>methodName=Value</code>.
    *
    * @param spec The specification
    */
   @SneakyThrows
   default Specification fromString(String spec) {
      if (spec != null) {
         Reflect rThis = Reflect.onObject(this);
         try (CSVReader reader = CSV.builder().removeEmptyCells().reader(new StringReader(spec))) {
            reader.forEach(Unchecked.consumer(row -> {
               for (String prop : row) {
                  List<String> parts = StringUtils.split(prop, ':');

                  String methodName = parts.get(0).trim();
                  String capitalize = StringUtils.toTitleCase(methodName);
                  if (!rThis.containsMethod(methodName)) {
                     if (methodName.equals("put") && rThis.containsMethod("put")) {
                        methodName = "put";
                     } else if (methodName.equals("add") && rThis.containsMethod("add")) {
                        methodName = "add";
                     } else if (methodName.equals("set") && rThis.containsMethod("set")) {
                        methodName = "set";
                     } else if (rThis.containsMethod("put" + capitalize)) {
                        methodName = "put" + capitalize;
                     } else if (rThis.containsMethod("set" + capitalize)) {
                        methodName = "set" + capitalize;
                     } else if (rThis.containsMethod("add" + capitalize)) {
                        methodName = "add" + capitalize;
                     } else {
                        throw new IllegalArgumentException(methodName + " is an invalid property");
                     }
                  }

                  switch (parts.size()) {
                     case 1:
                        rThis.invoke(methodName);
                        break;
                     case 2:
                        rThis.invoke(methodName, Val.of(parts.get(1).trim()));
                        break;
                     case 3:
                        rThis.invoke(methodName, Val.of(parts.get(1).trim()), Val.of(parts.get(2).trim()));
                        break;
                     default:
                        throw new IllegalArgumentException(prop + " is not a valid specification");
                  }
               }
            }));
         }
      }
      return Cast.as(this);
   }


}//END OF Specification
