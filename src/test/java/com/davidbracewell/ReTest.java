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

package com.davidbracewell;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.davidbracewell.Re.*;
import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class ReTest {

   @Test
   public void simplePattern() throws Exception {
      Regex r = or(re("A"), re("B"), re("C"));
      Pattern p = r.toPattern();
      assertTrue(p.matcher("ZDEFA").find());
   }

   @Test
   public void lookBehind() throws Exception {
      Regex r = seq(negLookBehind(quote("$")),
                    LETTER.plus()
                   ).endLine();
      Pattern p = r.toPattern();

      Matcher m = p.matcher("ZDEFA");
      assertTrue(m.find());
      assertEquals("ZDEFA", m.group());

      m = p.matcher("$ABBA");
      assertTrue(m.find());
      assertEquals("BBA", m.group());


      r = seq(posLookBehind(quote("$")),
              LETTER.plus()
             ).endLine();

      p = r.toPattern();
      assertFalse(p.matcher("ZDEFA").find());

      m = p.matcher("$ABBA");
      assertTrue(m.find());
      assertEquals("ABBA", m.group());
   }

   @Test
   public void lookAhead() throws Exception {
      Regex r = LETTER.plus().then(negLookAhead(DIGIT));
      Pattern p = r.toPattern();

      Matcher m = p.matcher("A1");
      assertFalse(m.find());
      m = p.matcher("AB1");
      assertTrue(m.find());
      assertEquals("A", m.group());


      r = LETTER.plus().then(posLookAhead(DIGIT));
      p = r.toPattern();

      m = p.matcher("AB1");
      assertTrue(m.find());
      assertEquals("AB", m.group());
      m = p.matcher("AB");
      assertFalse(m.find());
   }

   @Test
   public void namedGroup() throws Exception {
      Regex r = beginLine(nmGroup(group("AREA", DIGIT.range(3, 3)),
                                  re("-"),
                                  group("CO", DIGIT.range(3, 3)),
                                  re("-"),
                                  group("ASSIGNED", DIGIT.range(4, 4)))
                         ).endLine();

      Pattern p = r.toPattern();
      Matcher m = p.matcher("555-555-5509");
      assertTrue(m.find());

      assertEquals("555-555-5509", m.group());
      assertEquals("555", m.group("AREA"));
      assertEquals("555", m.group("CO"));
      assertEquals("5509", m.group("ASSIGNED"));

   }


   @Test
   public void chars() throws Exception {
      Regex[] matchLetter = {
         Re.chars("\\s", true),
         Re.chars("\\w"),
         Re.chars(LETTER),
         Re.chars(LETTER, DIGIT),
         Re.chars("\\s").not()
      };

      for (Regex r : matchLetter) {
         Matcher m1 = r.toPattern().matcher(" A ");
         assertTrue(r.toPattern() + " failed", m1.find());
         assertEquals(r.toPattern() + " failed", "A", m1.group());
      }
   }
}