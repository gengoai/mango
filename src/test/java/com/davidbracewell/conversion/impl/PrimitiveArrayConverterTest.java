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

package com.davidbracewell.conversion.impl;

import com.davidbracewell.conversion.PrimitiveArrayConverter;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.string.StringUtils;
import org.junit.Test;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class PrimitiveArrayConverterTest {

  @Test
  public void testBooleanArray() throws Exception {
    assertNull(PrimitiveArrayConverter.BOOLEAN.apply(null));
    assertEquals(
        Arrays.toString(new boolean[]{true, false, true}),
        Arrays.toString(Val.of(new Object[]{1, "abc", true}).asBooleanValueArray())
    );
    assertEquals(
        Arrays.toString(new boolean[]{}),
        Arrays.toString(Val.of(new ArrayList<>()).asBooleanValueArray())
    );
  }


  @Test
  public void testByteArray() throws Exception {
    assertNull(PrimitiveArrayConverter.BYTE.apply(null));

    //cast
    assertArrayEquals(new byte[]{'a', 'b', 'c'}, PrimitiveArrayConverter.BYTE.apply(new byte[]{'a', 'b', 'c'}));

    //Byte
    assertArrayEquals(new byte[]{'a', 'b', 'c'}, PrimitiveArrayConverter.BYTE.apply(new Byte[]{'a', 'b', 'c'}));

    //int conversion
    int a = 'a';
    int b = 'b';
    int c = 'c';
    assertArrayEquals(new byte[]{'a', 'b', 'c'}, PrimitiveArrayConverter.BYTE.apply(new Object[]{a, b, c}));

    //char conversion
    assertArrayEquals(new byte[]{'a', 'b', 'c'}, PrimitiveArrayConverter.BYTE.apply(new Character[]{'a', 'b', 'c'}));

    //String converstion
    assertArrayEquals(new byte[]{'a', 'b', 'c'}, PrimitiveArrayConverter.BYTE.apply(new Object[]{"a", "b", 'c'}));


    //Val test
    assertArrayEquals(new byte[]{'a', 'b', 'c'}, Val.of(new Object[]{a, b, c}).asByteValueArray());

    //Other
    assertNull(Val.of(new ArrayList<>()).asArray(byte.class));


    Path path = Files.createTempFile("espresso-test", StringUtils.randomHexString(10));
    String output = "This is a test";
    byte[] bytes = (output).getBytes();
    Files.write(path, output.getBytes());

    //Path
    assertArrayEquals(bytes, Val.of(path).asByteValueArray());

    //File
    assertArrayEquals(bytes, Val.of(path.toFile()).asByteValueArray());

    //URI
    assertArrayEquals(bytes, Val.of(path.toUri()).asByteValueArray());

    //URL
    assertArrayEquals(bytes, Val.of(path.toUri().toURL()).asByteValueArray());

    //Reader
    try( FileReader reader = new FileReader(path.toFile())){
      assertArrayEquals(bytes, Val.of(reader).asByteValueArray());
    }


  }


  @Test
  public void testCharArray() throws Exception {
    assertNull(PrimitiveArrayConverter.CHAR.apply(null));

    //cast
    assertArrayEquals(new char[]{'a', 'b', 'c'}, PrimitiveArrayConverter.CHAR.apply(new char[]{'a', 'b', 'c'}));

    //Byte
    assertArrayEquals(new char[]{'a', 'b', 'c'}, PrimitiveArrayConverter.CHAR.apply(new Character[]{'a', 'b', 'c'}));

    //int conversion
    int a = 'a';
    int b = 'b';
    int c = 'c';
    assertArrayEquals(new char[]{'a', 'b', 'c'}, PrimitiveArrayConverter.CHAR.apply(new Object[]{a, b, c}));

    //char conversion
    assertArrayEquals(new char[]{'a', 'b', 'c'}, PrimitiveArrayConverter.CHAR.apply(new Character[]{'a', 'b', 'c'}));

    //String converstion
    assertArrayEquals(new char[]{'a', 'b', 'c'}, PrimitiveArrayConverter.CHAR.apply(new Object[]{"a", "b", 'c'}));


    //Val test
    assertArrayEquals(new char[]{'a', 'b', 'c'}, Val.of(new Object[]{a, b, c}).asCharacterValueArray());

    //Other
    assertNull(Val.of(new ArrayList<>()).asArray(char.class));


    Path path = Files.createTempFile("espresso-test", StringUtils.randomHexString(10));
    String output = "This is a test";
    char[] chars = (output).toCharArray();
    Files.write(path, output.getBytes());

    //Path
    assertArrayEquals(chars, Val.of(path).asCharacterValueArray());

    //File
    assertArrayEquals(chars, Val.of(path.toFile()).asCharacterValueArray());

    //URI
    assertArrayEquals(chars, Val.of(path.toUri()).asCharacterValueArray());

    //URL
    assertArrayEquals(chars, Val.of(path.toUri().toURL()).asCharacterValueArray());

    //Reader
    try (FileReader reader = new FileReader(path.toFile())) {
      assertArrayEquals(chars, Val.of(reader).asCharacterValueArray());
    }
  }

  @Test
  public void testIntegerArray() throws Exception {
    assertNull(PrimitiveArrayConverter.INT.apply(null));
    assertArrayEquals(new int[]{3, 2, 1}, Val.of("3,2,1").asIntegerValueArray());
    assertArrayEquals(new int[]{0}, Val.of("abcdef").asIntegerValueArray());
  }


  @Test
  public void testDoubleArray() throws Exception {
    assertNull(PrimitiveArrayConverter.DOUBLE.apply(null));
    assertArrayEquals(new double[]{3, 2, 1}, Val.of("3,2,1").asDoubleValueArray(),0d);
    assertArrayEquals(new double[]{0}, Val.of("abcdef").asDoubleValueArray(), 0d);
  }

  @Test
  public void testFloatArray() throws Exception {
    assertNull(PrimitiveArrayConverter.FLOAT.apply(null));
    assertArrayEquals(new float[]{3, 2, 1}, Val.of("3,2,1").asFloatValueArray(), 0f);
    assertArrayEquals(new float[]{0}, Val.of("abcdef").asFloatValueArray(), 0f);
  }


  @Test
  public void testShortArray() throws Exception {
    assertNull(PrimitiveArrayConverter.SHORT.apply(null));
    assertArrayEquals(new short[]{3, 2, 1}, Val.of("3,2,1").asShortValueArray());
    assertArrayEquals(new short[]{0}, Val.of("abcdef").asShortValueArray());
  }


  @Test
  public void testLongArray() throws Exception {
    assertNull(PrimitiveArrayConverter.LONG.apply(null));
    assertArrayEquals(new long[]{3, 2, 1}, Val.of("3,2,1").asLongValueArray());
    assertArrayEquals(new long[]{0}, Val.of("abcdef").asLongValueArray());
  }


}//END OF PrimitiveArrayConverterTest