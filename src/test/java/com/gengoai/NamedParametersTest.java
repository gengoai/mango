package com.gengoai;

import com.gengoai.json.JsonEntry;
import com.gengoai.reflection.Types;
import org.junit.Test;

import java.lang.reflect.Type;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class NamedParametersTest {


   public enum P implements NamedParameters.Value {
      ARG1 {
         @Override
         public Type getValueType() {
            return int.class;
         }

         @Override
         public Object defaultValue() {
            return 1;
         }
      },
      ARG2 {
         @Override
         public Type getValueType() {
            return String.class;
         }

      }
   }


   public static String testStringArg(NamedParameters<P> parameters) {
      return parameters.getString(P.ARG2);
   }

   public static int testIntArg(NamedParameters<P> parameters) {
      return parameters.getInt(P.ARG1);
   }


   @Test
   public void test1() {
      assertNull(testStringArg(NamedParameters.params(P.ARG1, 20)));
      assertEquals("ALPHABET", testStringArg(NamedParameters.params(P.ARG2, "ALPHABET")));
      assertEquals(1, testIntArg(NamedParameters.params(P.ARG2, "ALPHABET")), 0);
      assertEquals(20, testIntArg(NamedParameters.params(P.ARG1, 20,
                                                         P.ARG2, "ALPHABET")), 0);
   }


   @Test(expected = IllegalArgumentException.class)
   public void badValue() {
      testStringArg(NamedParameters.params(P.ARG2, 20));
   }


   @Test
   public void copy() {
      NamedParameters<P> n = NamedParameters.params(P.ARG1, 20,
                                                    P.ARG2, "ALPHABET");
      NamedParameters<P> nCopy = n.copy();
      assertEquals(n, nCopy);

   }


   @Test
   public void getOrDefault() {
      NamedParameters<P> n = NamedParameters.params(P.ARG2, "ALPHABET");
      assertTrue(n.isSet(P.ARG2));
      assertFalse(n.isSet(P.ARG1));
      assertEquals(1, n.getInt(P.ARG1), 0);
      assertEquals(100, n.getOrDefault(P.ARG1, 100), 0);
      assertEquals("ALPHABET", n.get(P.ARG2, String.class));
   }

   @Test
   public void json() {
      NamedParameters<P> n = NamedParameters.params(P.ARG1, 20,
                                                    P.ARG2, "ALPHABET");
      JsonEntry e = JsonEntry.from(n);
      NamedParameters<P> nDes = e.getAs(Types.parameterizedType(NamedParameters.class, P.class));
      assertEquals(n, nDes);
   }
}