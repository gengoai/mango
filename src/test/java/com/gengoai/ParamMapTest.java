package com.gengoai;

import com.gengoai.json.JsonEntry;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * @author David B. Bracewell
 */
public class ParamMapTest {

   static final ParameterDef<String> stringParam = ParameterDef.strParam("str");
   static final ParameterDef<Integer> intParam = ParameterDef.intParam("int");
   static final ParameterDef<Float> floatParam = ParameterDef.floatParam("float");
   static final ParameterDef<Double> doubleParam = ParameterDef.doubleParam("double");
   static final ParameterDef<Boolean> booleanParam = ParameterDef.boolParam("bool");

   private static class TestParameters extends ParamMap<TestParameters> {
      private static final long serialVersionUID = 1L;
      public final Parameter<String> strP = parameter(stringParam, "");
      public final Parameter<Integer> intP = parameter(intParam, -1);
      public final Parameter<Float> floatP = parameter(floatParam, -1f);
      public final Parameter<Double> doubleP = parameter(doubleParam, -1d);
      public final Parameter<Boolean> boolP = parameter(booleanParam, true);
   }

   @Test
   public void testConsumerUpdate() {
      TestParameters parameters = new TestParameters();
      parameters.update(p -> {
         p.strP.set("testSet");
         p.intP.set(100);
      });
      assertEquals("testSet", parameters.strP.value());
      assertEquals(100, parameters.intP.value(), 0);
      assertEquals(-1d, parameters.doubleP.value(), 0d);
      assertEquals(-1f, parameters.floatP.value(), 0f);
      assertTrue(parameters.boolP.value());

      assertEquals("testSet", parameters.get("str"));
      assertEquals("testSet", parameters.get(stringParam));
      assertEquals(100, (int) parameters.<Integer>get("int"));
      assertEquals(100, parameters.get(intParam), 0);
      assertEquals(-1d, parameters.get("double"), 0d);
      assertEquals(-1d, parameters.get(doubleParam), 0d);
      assertEquals(-1f, parameters.get("float"), 0f);
      assertEquals(-1f, parameters.get(floatParam), 0f);
      assertTrue(parameters.get(booleanParam.name));
      assertTrue(parameters.get(booleanParam));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testSetBadValue() {
      TestParameters parameters = new TestParameters();
      parameters.set("str", 123);
   }


   @Test(expected = IllegalArgumentException.class)
   public void testSetBadType() {
      TestParameters parameters = new TestParameters();
      parameters.set("str", "123");
      ParameterDef<JsonEntry> ii = ParameterDef.param("str", JsonEntry.class);
      parameters.get(ii);
   }

}