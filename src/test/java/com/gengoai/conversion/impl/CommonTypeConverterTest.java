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

package com.gengoai.conversion.impl;

import com.gengoai.collection.map.Maps;
import com.gengoai.conversion.Convert;
import com.gengoai.conversion.Val;
import com.google.common.base.Defaults;
import org.junit.Assert;
import org.junit.Test;

import java.io.StringReader;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;

public class CommonTypeConverterTest {
   @Test
   public void testCharacter() throws Exception {
      //Convert nulls
      Assert.assertEquals(Defaults.defaultValue(char.class), Convert.convert(null, char.class));
      assertNull(Convert.convert(null, Character.class));

      //casts
      assertEquals(Character.valueOf('a'), Convert.convert('a', char.class));
      assertEquals(Character.valueOf('a'), Convert.convert('a', Character.class));

      //valid string conversion
      assertEquals(Character.valueOf('a'), Convert.convert("a", char.class));
      assertEquals(Character.valueOf('a'), Convert.convert("a", Character.class));

      //invalid string conversion
      assertEquals(Defaults.defaultValue(char.class), Convert.convert("a longer string", char.class));
      assertNull(Convert.convert("a longer string", Character.class));

      //number conversion
      assertEquals(Character.valueOf('\u32e7'), Convert.convert(Integer.parseInt("32e7", 16), char.class));
      assertEquals(Character.valueOf('a'), Convert.convert(97.6, Character.class));


      //All other things
      assertEquals(Defaults.defaultValue(char.class), Convert.convert(new Object[]{}, char.class));
      assertNull(Convert.convert(new Object[]{}, Character.class));

      //Test val version
      Assert.assertEquals(Character.valueOf('a'), Val.NULL.asCharacter('a'));
      assertEquals(Character.valueOf('a'), Character.valueOf(Val.NULL.asCharacterValue('a')));
      assertEquals(Defaults.defaultValue(char.class), Character.valueOf(Val.NULL.asCharacterValue()));
   }


   @Test
   public void testString() throws Exception {
      //Convert nulls
      assertNull(Convert.convert(null, String.class));

      //casts
      assertEquals("STRING", Convert.convert("STRING", String.class));

      //char array
      assertEquals("STRING", Convert.convert(new char[]{'S', 'T', 'R', 'I', 'N', 'G'}, String.class));
      assertEquals("STRING", Convert.convert(new Character[]{'S', 'T', 'R', 'I', 'N', 'G'}, String.class));

      //byte array
      assertEquals("STRING", Convert.convert(new byte[]{'S', 'T', 'R', 'I', 'N', 'G'}, String.class));
      assertEquals("STRING", Convert.convert(new Byte[]{'S', 'T', 'R', 'I', 'N', 'G'}, String.class));

      //Reader
      assertEquals("STRING", Convert.convert(new StringReader("STRING"), String.class));

      //Other
      assertEquals("{1=3}", Convert.convert(Maps.map(1, 3), String.class));

      assertEquals("[S, TRING]", Convert.convert(new String[]{"S", "TRING"}, String.class));

      //Test val version
      assertEquals("STRING", Val.of(new char[]{'S', 'T', 'R', 'I', 'N', 'G'}).asString());
      assertEquals("STRING", Val.NULL.asString("STRING"));
   }


   @Test
   public void testStringBuilder() throws Exception {
      //Convert nulls
      assertNull(Convert.convert(null, StringBuilder.class));

      //casts
      assertEquals("STRING", Convert.convert("STRING", StringBuilder.class).toString());

      //Other
      assertEquals("{1=3}", Convert.convert(Maps.map(1, 3), StringBuilder.class).toString());

      //Test val version
      assertEquals("STRING", Val.of(new char[]{'S', 'T', 'R', 'I', 'N', 'G'}).as(StringBuilder.class).toString());
      assertEquals("STRING", Val.NULL.as(StringBuilder.class, new StringBuilder("STRING")).toString());

   }


   @Test
   public void testDate() throws Exception {
      Date Jan11985 = new GregorianCalendar(1985, 0, 1).getTime();

      //This probably highly locale dependent
      assertEquals(Jan11985, Val.of(Jan11985.getTime()).as(java.sql.Date.class));
      assertEquals(Jan11985, Val.of("01/01/1985").as(java.sql.Date.class));
      assertEquals(Jan11985, Val.of("January    1, 1985").as(java.sql.Date.class));
   }

   @Test
   public void testClass() throws Exception {
      //Convert nulls
      assertNull(Convert.convert(null, Class.class));

      //casts
      assertEquals(String.class, Convert.convert(String.class, Class.class));

      //String conversion
      assertEquals(String.class, Convert.convert("String", Class.class));

      //No conversion
      assertEquals(String.class, Convert.convert("This is not a class", Class.class));
   }

}//END OF CommonTypeConverterTest