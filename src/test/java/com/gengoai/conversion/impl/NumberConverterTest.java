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

import com.gengoai.conversion.Convert;
import com.gengoai.conversion.NumberConverter;
import com.gengoai.conversion.Val;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class NumberConverterTest {

   @Test
   public void bigIntegerTests() throws Exception {
      //Test null inputs
      assertNull(NumberConverter.BIG_INTEGER.apply(null));

      //Test casts
      Assert.assertEquals(BigInteger.ONE, Convert.convert(new BigInteger("1"), BigInteger.class));

      //Test number conversion
      assertEquals(BigInteger.TEN, Convert.convert(10d, BigInteger.class));

      //Test BigDecimal Conversion
      assertEquals(BigInteger.TEN, Convert.convert(new BigDecimal(10d), BigInteger.class));

      //Test boolean conversion
      assertEquals(BigInteger.ONE, Convert.convert(true, BigInteger.class));
      assertEquals(BigInteger.ZERO, Convert.convert(false, BigInteger.class));

      //Test character conversion
      assertEquals(new BigInteger("32"), Convert.convert(' ', BigInteger.class));
      assertEquals(new BigInteger("97"), Convert.convert('a', BigInteger.class));

      //Test String conversion
      assertEquals(new BigInteger("32"), Convert.convert("32", BigInteger.class));
      assertEquals(new BigInteger("97"), Convert.convert("97", BigInteger.class));

      //Test fail
      assertNull(Convert.convert(new HashMap<>(), BigInteger.class));
   }

   @Test
   public void integerTests() throws Exception {
      //Test null inputs
      assertEquals(new Integer(0), Convert.convert(null, int.class));
      assertNull(Convert.convert(null, Integer.class));

      //Test casts
      assertEquals(new Integer(0), Convert.convert(0, int.class));
      assertEquals(new Integer(0), Convert.convert(0, Integer.class));

      //Test number conversion
      assertEquals(new Integer(100), Convert.convert(100.52, int.class));
      assertEquals(new Integer(100), Convert.convert(100.36, Integer.class));

      //Test BigDecimal Conversion
      assertEquals(new Integer(1000), Convert.convert(new BigDecimal(1000), int.class));
      assertEquals(new Integer(1000), Convert.convert(new BigDecimal(1000), Integer.class));

      //Test BigInteger Conversion
      assertEquals(new Integer(1000), Convert.convert(new BigInteger("1000"), int.class));
      assertEquals(new Integer(1000), Convert.convert(new BigInteger("1000"), Integer.class));

      //Test boolean conversion
      assertEquals(new Integer(1), Convert.convert(true, int.class));
      assertEquals(new Integer(0), Convert.convert(false, Integer.class));

      //Test character conversion
      assertEquals(new Integer(32), Convert.convert(' ', int.class));
      assertEquals(new Integer(97), Convert.convert('a', Integer.class));

      //Test String conversion
      assertEquals(new Integer(32), Convert.convert("32", int.class));
      assertEquals(new Integer(97), Convert.convert("97", Integer.class));

      //Test fail
      assertEquals(new Integer(0), Convert.convert(new ArrayList<>(), int.class));
      assertNull(Convert.convert(new HashMap<>(), Integer.class));

      //Val test
      Assert.assertEquals(new Integer(1), Val.of("1.2").asInteger());
      assertEquals(new Integer(1), Val.of(null).asInteger(1));
      assertEquals(1, Val.of("1.2").asIntegerValue());
      assertEquals(1, Val.of(null).asIntegerValue(1));

   }

   @Test
   public void doubleTests() throws Exception {
      //Test null inputs
      assertEquals(0d, Convert.convert(null, double.class), 0d);
      assertNull(Convert.convert(null, Double.class));

      //Test casts
      assertEquals(98.6, Convert.convert(98.6, double.class), 0d);
      assertEquals(new Double(97.8), Convert.convert(97.8, Double.class));

      //Test number conversion
      assertEquals(100d, Convert.convert(100, double.class), 0d);
      assertEquals(new Double(103), Convert.convert(103, Double.class));

      //Test fail
      assertEquals(0d, Convert.convert(new ArrayList<>(), double.class), 0d);
      assertNull(Convert.convert(new HashMap<>(), Double.class));

      //Val test
      assertEquals(new Double(1.2), Val.of("1.2").asDouble());
      assertEquals(new Double(1), Val.of(null).asDouble(1d));
      assertEquals(1.2, Val.of("1.2").asDoubleValue(), 0d);
      assertEquals(1d, Val.of(null).asDoubleValue(1d), 0d);
   }

   @Test
   public void floatTests() throws Exception {
      //Test null inputs
      assertEquals(0f, Convert.convert(null, float.class), 0f);
      assertNull(Convert.convert(null, Float.class));

      //Test casts
      assertEquals(98.6f, Convert.convert(98.6, float.class), 0f);
      assertEquals(new Float(97.8), Convert.convert(97.8, Float.class));

      //Test number conversion
      assertEquals(100f, Convert.convert(100, float.class), 0f);
      assertEquals(new Float(103), Convert.convert(103, Float.class));

      //Test fail
      assertEquals(0f, Convert.convert(new ArrayList<>(), float.class), 0f);
      assertNull(Convert.convert(new HashMap<>(), Float.class));

      //Val test
      assertEquals(new Float(1.2), Val.of("1.2").asFloat());
      assertEquals(new Float(1), Val.of(null).asFloat(1f));
      assertEquals(1.2f, Val.of("1.2").asFloatValue(), 0d);
      assertEquals(1f, Val.of(null).asFloatValue(1f), 0d);
   }

   @Test
   public void longTests() throws Exception {
      //Test null inputs
      assertEquals(0L, (long) Convert.convert(null, long.class));
      assertNull(Convert.convert(null, Long.class));

      //Test casts
      assertEquals(98, (long) Convert.convert(98, long.class));
      assertEquals(new Long(97), Convert.convert(97, Long.class));

      //Test number conversion
      assertEquals(100L, (long) Convert.convert(100.968, long.class));
      assertEquals(new Long(103), Convert.convert(103.0005, Long.class));

      //Test fail
      assertEquals(0L, (long) Convert.convert(new ArrayList<>(), long.class));
      assertNull(Convert.convert(new HashMap<>(), Long.class));

      //Val test
      assertEquals(new Long(1), Val.of("1.2").asLong());
      assertEquals(new Long(1), Val.of(null).asLong(1L));
      assertEquals(1L, Val.of("1.2").asLongValue());
      assertEquals(1L, Val.of(null).asLongValue(1L));
   }


   @Test
   public void shortTests() throws Exception {
      //Test null inputs
      assertEquals(0, (short) Convert.convert(null, short.class));
      assertNull(Convert.convert(null, Short.class));

      //Test casts
      assertEquals(98, (short) Convert.convert(98, short.class));
      assertEquals(new Short((short) 97), Convert.convert(97, Short.class));

      //Test number conversion
      assertEquals(100, (short) Convert.convert(100.968, short.class));
      assertEquals(new Short((short) 103), Convert.convert(103.0005, Short.class));

      //Test fail
      assertEquals(0, (short) Convert.convert(new ArrayList<>(), short.class));
      assertNull(Convert.convert(new HashMap<>(), Short.class));

      //Val test
      assertEquals(new Short((short) 1), Val.of("1.2").asShort());
      assertEquals(new Short((short) 1), Val.of(null).asShort((short) 1));
      assertEquals((short) 1, Val.of("1.2").asShortValue());
      assertEquals((short) 1, Val.of(null).asShortValue((short) 1));
   }


   @Test
   public void byteTests() throws Exception {
      //Test null inputs
      assertEquals(0, (byte) Convert.convert(null, byte.class));
      assertNull(Convert.convert(null, Byte.class));

      //Test casts
      assertEquals(98, (byte) Convert.convert(98, byte.class));
      assertEquals(new Byte((byte) 97), Convert.convert(97, Byte.class));

      //Test number conversion
      assertEquals(100, (byte) Convert.convert(100.968, byte.class));
      assertEquals(new Byte((byte) 103), Convert.convert(103.0005, Byte.class));

      //Test fail
      assertEquals(0, (byte) Convert.convert(new ArrayList<>(), byte.class));
      assertNull(Convert.convert(new HashMap<>(), Byte.class));

      //Val test
      assertEquals(new Byte((byte) 1), Val.of("1.2").asByte());
      assertEquals(new Byte((byte) 1), Val.of(null).asByte((byte) 1));
      assertEquals((byte) 1, Val.of("1.2").asByteValue());
      assertEquals((byte) 1, Val.of(null).asByteValue((byte) 1));
   }


   @Test
   public void booleanTests() throws Exception {
      //Test null inputs
      assertEquals(false, Convert.convert(null, boolean.class));
      assertNull(Convert.convert(null, Boolean.class));

      //Test casts
      assertEquals(false, Convert.convert(false, boolean.class));
      assertEquals(true, Convert.convert(Boolean.TRUE, Boolean.class));

      //Test number conversion
      assertEquals(true, Convert.convert(1, boolean.class));
      assertEquals(false, Convert.convert(1000.5, Boolean.class));

      //Test String conversion
      assertEquals(true, Convert.convert("true", boolean.class));
      assertEquals(false, Convert.convert("this is not a valid value", Boolean.class));

      //Test fail
      assertEquals(false, Convert.convert(new ArrayList<>(), boolean.class));
      assertNull(Convert.convert(new HashMap<>(), Boolean.class));

      //Val test
      assertEquals(Boolean.TRUE, Val.of(1.0).asBoolean());
      assertEquals(Boolean.FALSE, Val.of(null).asBoolean(false));
      assertEquals(true, Val.of("true").asBooleanValue());
      assertEquals(false, Val.of(null).asBooleanValue(false));
   }

}//END OF NumberConverterTest