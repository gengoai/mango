package com.gengoai;

import com.gengoai.string.StringPredicates;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class SwitchTest {


   @Test
   public void builderTest() {
      Switch<String, Integer> stringToNumber = Switch.<String, Integer>switchBuilder()
            .caseStmt(StringPredicates.MATCHES("one", false), s -> 1)
            .caseStmt(StringPredicates.MATCHES("two", false), s -> 2)
            .caseStmt(StringPredicates.MATCHES("three", false), s -> 3)
            .defaultStatement(s -> 0)
            .build();

      assertEquals(1, stringToNumber.apply("one"), 0);
      assertEquals(1, stringToNumber.apply("ONE"), 0);
      assertEquals(2, stringToNumber.apply("two"), 0);
      assertEquals(3, stringToNumber.apply("three"), 0);
      assertEquals(0, stringToNumber.apply("four"), 0);
      assertEquals(0, stringToNumber.apply(null), 0);
   }

   @Test
   public void doubleBracketTest() {
      Switch<String, Integer> stringToNumber = new Switch<String, Integer>() {
         private static final long serialVersionUID = 1L;

         {
            $case(StringPredicates.MATCHES("one", false), s -> 1);
            $case(StringPredicates.MATCHES("two", false), s -> 2);
            $case(StringPredicates.MATCHES("three", false), s -> 3);
            $default(s -> 0);
         }
      };

      assertEquals(1, stringToNumber.apply("one"), 0);
      assertEquals(1, stringToNumber.apply("ONE"), 0);
      assertEquals(2, stringToNumber.apply("two"), 0);
      assertEquals(3, stringToNumber.apply("three"), 0);
      assertEquals(0, stringToNumber.apply("four"), 0);
      assertEquals(0, stringToNumber.apply(null), 0);
   }

   @Test(expected = RuntimeException.class)
   public void noDefault() {
      Switch<String, Integer> stringToNumber = Switch.<String, Integer>switchBuilder()
            .caseStmt(StringPredicates.MATCHES("one", false), s -> 1)
            .caseStmt(StringPredicates.MATCHES("two", false), s -> 2)
            .caseStmt(StringPredicates.MATCHES("three", false), s -> 3)
            .build();

      assertEquals(1, stringToNumber.apply("one"), 0);
      assertEquals(1, stringToNumber.apply("ONE"), 0);
      assertEquals(2, stringToNumber.apply("two"), 0);
      assertEquals(3, stringToNumber.apply("three"), 0);
      assertEquals(0, stringToNumber.apply("four"), 0);
      assertEquals(0, stringToNumber.apply(null), 0);
   }


   @Test
   public void mappingTest() {
      Switch<String, Integer> stringToNumber = new Switch<String, Integer>() {
         private static final long serialVersionUID = 1L;

         {
            $case(StringPredicates.MATCHES("one", false), s -> 1, s -> 1);
            $case(StringPredicates.MATCHES("two", false), s -> 1, s -> 2);
            $case(StringPredicates.MATCHES("three", false), s -> 1, s -> 3);
            $default(s -> 0);
         }
      };

      assertEquals(1, stringToNumber.apply("one"), 0);
      assertEquals(1, stringToNumber.apply("ONE"), 0);
      assertEquals(2, stringToNumber.apply("two"), 0);
      assertEquals(3, stringToNumber.apply("three"), 0);
      assertEquals(0, stringToNumber.apply("four"), 0);
      assertEquals(0, stringToNumber.apply(null), 0);
   }

   @Test
   public void builderMappingTest() {
      Switch<String, Integer> stringToNumber = Switch.<String, Integer>switchBuilder()
            .caseStmt(StringPredicates.MATCHES("one", false), s -> 1, s -> 1)
            .caseStmt(StringPredicates.MATCHES("two", false), s -> 1, s -> 2)
            .caseStmt(StringPredicates.MATCHES("three", false), s -> 1, s -> 3)
            .defaultStatement(s -> 0)
            .build();

      assertEquals(1, stringToNumber.apply("one"), 0);
      assertEquals(1, stringToNumber.apply("ONE"), 0);
      assertEquals(2, stringToNumber.apply("two"), 0);
      assertEquals(3, stringToNumber.apply("three"), 0);
      assertEquals(0, stringToNumber.apply("four"), 0);
      assertEquals(0, stringToNumber.apply(null), 0);
   }


}
