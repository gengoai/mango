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

package com.gengoai.enums;

import com.gengoai.json.Json;

import java.io.ObjectStreamException;
import java.util.Collection;

/**
 * @author David B. Bracewell
 */
public class Color extends EnumValue {
   private static final long serialVersionUID = 1L;
   private static final Registry<Color> registry = new Registry<>(Color.class);

   public static Color make(String name) {
      return registry.make(name, Color::new);
   }

   public static Collection<Color> values() {
      return registry.values();
   }

   private Color(String name) {
      super(name);
   }

   protected final Object readResolve() throws ObjectStreamException {
      return make(name());
   }


   public static void main(String[] args) throws Exception {
      Color red = Color.make("red");
      Color blue = Color.make("blue");

      String redJson = Json.dumps(red);
      System.out.println(redJson);

      Color r2 = Json.parseObject(redJson, EnumValue.class);
      System.out.println(red == blue);
      System.out.println(red == r2);
      System.out.println(red == red.clone());
   }

}//END OF Color
