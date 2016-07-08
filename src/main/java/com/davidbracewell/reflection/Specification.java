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

package com.davidbracewell.reflection;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.function.Unchecked;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.structured.csv.CSVReader;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Throwables;

import java.io.StringReader;
import java.util.List;

/**
 * @author David B. Bracewell
 */
public interface Specification {

  /**
   * Initializes the object via string specification. The string specification is in the
   * form of <code>methodName=Value</code>.
   *
   * @param spec The specification
   */
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
              if (rThis.containsMethod("put" + capitalize)) {
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
      } catch (Exception e) {
        e.printStackTrace();
        throw Throwables.propagate(e);
      }
    }
    return Cast.as(this);
  }


}//END OF Specification
