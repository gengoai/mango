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

package com.gengoai.mango.conversion.impl;

import com.gengoai.mango.conversion.Convert;
import com.gengoai.mango.conversion.Val;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnumConverterTest {

  public static enum ExampleEnum {
    ONE,
    TWO,
    THREE
  }

  @Test
  public void testEnumConverter() throws Exception {
    //Test null
    assertNull(Convert.convert(null,ExampleEnum.class));

    //Test cast
    assertEquals(ExampleEnum.THREE, Convert.convert(ExampleEnum.THREE, ExampleEnum.class));

    //Test from string
    assertEquals(ExampleEnum.ONE, Convert.convert("ONE", ExampleEnum.class));
    assertEquals(ExampleEnum.TWO, Convert.convert(ExampleEnum.class.getName()+".TWO", ExampleEnum.class));

    //Test bad string
    assertNull(Convert.convert("FOUR", ExampleEnum.class));
    assertNull(Convert.convert(ExampleEnum.class.getName() + ".FOUR", ExampleEnum.class));

    //Test other
    assertNull(Convert.convert(123, ExampleEnum.class));

    //Test the val version
    assertEquals(ExampleEnum.TWO, Val.of("TWO").as(ExampleEnum.class));
    assertEquals(ExampleEnum.ONE, Val.of("FIVE").as(ExampleEnum.class, ExampleEnum.ONE));
  }
}//END OF EnumConverterTest