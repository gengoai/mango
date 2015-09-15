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

package com.davidbracewell.string;

import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

  @Test
  public void testTrim() throws Exception {
    assertEquals("test", StringUtils.trim("   test　"));
  }

  @Test
  public void testCase() throws Exception {
    assertEquals("Title Case", StringUtils.toTitleCase("title case"));
    assertEquals("Title Case", StringUtils.toTitleCase("TITLE CASE"));
    assertEquals(" ", StringUtils.toTitleCase(" "));
    assertNull(StringUtils.toTitleCase(null));

    assertEquals("TITLE CASE", StringFunctions.UPPER_CASE.apply("title case"));
    assertEquals("TITLE CASE", StringFunctions.UPPER_CASE.apply("TITLE CASE"));
    assertEquals(" ", StringFunctions.UPPER_CASE.apply(" "));
    assertNull(StringFunctions.UPPER_CASE.apply(null));

    assertEquals("title case", StringFunctions.LOWER_CASE.apply("title case"));
    assertEquals("title case", StringFunctions.LOWER_CASE.apply("TITLE CASE"));
    assertEquals(" ", StringFunctions.LOWER_CASE.apply(" "));
    assertNull(StringFunctions.LOWER_CASE.apply(null));
  }

  @Test
  public void testAbbreviate() throws Exception {
    assertEquals("abc...", StringUtils.abbreviate("abcdefgh", 3));
    assertEquals("abc", StringUtils.abbreviate("abc", 3));
    assertEquals("", StringUtils.abbreviate("", 3));
    assertNull(StringUtils.abbreviate(null, 3));
  }

  @Test
  public void testCompare() throws Exception {
    assertEquals(0, StringUtils.compare(null, null, true));
    assertEquals(0, StringUtils.compare("ABC", "abc", true));
    assertEquals(1, StringUtils.compare("ABC", null, true));
    assertEquals(-1, StringUtils.compare(null, "ABC", true));
    assertNotEquals(0, StringUtils.compare("ABC", "abc", false));
  }

  @Test
  public void testNormalization() throws Exception {
    assertEquals("democrocia", StringUtils.removeDiacritics("democrocìa"));
    assertEquals("123", StringUtils.toCanonicalForm("１２３"));
  }

  @Test
  public void testIsMethods() throws Exception {
    assertTrue(StringUtils.isLetterOrDigit('a'));
    assertTrue(StringUtils.isLetterOrDigit('1'));
    assertTrue(StringUtils.isLetterOrDigit('あ'));
    assertFalse(StringUtils.isLetterOrDigit('"'));
    assertFalse(StringUtils.isLetterOrDigit(' '));

    assertTrue(StringUtils.isLowerCase("lower"));
    assertFalse(StringUtils.isLowerCase("あえおいう"));
    assertFalse(StringUtils.isLowerCase("UPPER"));
    assertFalse(StringUtils.isLowerCase(""));
    assertFalse(StringUtils.isLowerCase(null));

    assertFalse(StringUtils.isUpperCase("lower"));
    assertFalse(StringUtils.isUpperCase("あえおいう"));
    assertTrue(StringUtils.isUpperCase("UPPER"));
    assertFalse(StringUtils.isUpperCase(""));
    assertFalse(StringUtils.isUpperCase(null));

    assertTrue(StringUtils.isAlphaNumeric("lower"));
    assertTrue(StringUtils.isAlphaNumeric("UPPER"));
    assertTrue(StringUtils.isAlphaNumeric("lower123"));
    assertTrue(StringUtils.isAlphaNumeric("UP589PER"));
    assertTrue(StringUtils.isAlphaNumeric("lower123"));
    assertTrue(StringUtils.isAlphaNumeric("あえおいう１２３"));
    assertFalse(StringUtils.isAlphaNumeric(""));
    assertFalse(StringUtils.isAlphaNumeric(null));

    assertFalse(StringUtils.isNonAlphaNumeric("lower"));
    assertFalse(StringUtils.isNonAlphaNumeric("UPPER"));
    assertFalse(StringUtils.isNonAlphaNumeric("lower123"));
    assertFalse(StringUtils.isNonAlphaNumeric("UP589PER"));
    assertFalse(StringUtils.isNonAlphaNumeric("lower123"));
    assertFalse(StringUtils.isNonAlphaNumeric("あえおいう１２３"));
    assertTrue(StringUtils.isNonAlphaNumeric(""));
    assertTrue(StringUtils.isNonAlphaNumeric(null));

    assertTrue(StringUtils.isLetter("lower"));
    assertFalse(StringUtils.isLetter("lower123"));
    assertFalse(StringUtils.isLetter(""));
    assertFalse(StringUtils.isLetter(null));

    assertFalse(StringUtils.isDigit("lower123"));
    assertFalse(StringUtils.isDigit(""));
    assertFalse(StringUtils.isDigit(null));
    assertTrue(StringUtils.isDigit("１２３"));

    assertTrue(StringUtils.isPunctuation("、"));
    assertTrue(StringUtils.isPunctuation(","));
    assertFalse(StringUtils.isPunctuation("abc"));
    assertFalse(StringUtils.isPunctuation(""));
    assertFalse(StringUtils.isPunctuation(null));
  }


}//END OF StringUtilsTest