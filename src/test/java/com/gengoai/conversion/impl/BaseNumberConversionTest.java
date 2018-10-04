package com.gengoai.conversion.impl;

import com.gengoai.conversion.Converter;
import com.gengoai.conversion.TypeConversionException;
import com.gengoai.json.JsonEntry;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.gengoai.collection.Collect.arrayOf;
import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public abstract class BaseNumberConversionTest {
   private final Class<?> aClass;


   protected abstract Number convert(Number in);

   protected BaseNumberConversionTest(Class<?> aClass) {
      this.aClass = aClass;
   }

   @Test
   public void nullValue() throws TypeConversionException {
      assertNull(Converter.convert(null, aClass));
   }

   @Test
   public void test() {
      assertEquals(convert(1.2), Converter.convertSilently("1.2", aClass));
      assertEquals(convert(-1.2), Converter.convertSilently("-1.2", aClass));
      assertEquals(convert(1e4), Converter.convertSilently("1e4", aClass));
      assertEquals(convert(1.2), Converter.convertSilently(JsonEntry.from(1.2), aClass));
      assertEquals(convert(1.0), Converter.convertSilently(JsonEntry.from(true), aClass));
      assertEquals(convert(1.2), Converter.convertSilently(JsonEntry.from("1.2"), aClass));
      assertEquals(convert(1.0), Converter.convertSilently(true, aClass));
      assertEquals(convert(0.0), Converter.convertSilently(false, aClass));
      assertEquals(convert(0.5), Converter.convertSilently(Double.valueOf(0.5), aClass));
      assertEquals(convert(42.0), Converter.convertSilently(BigDecimal.valueOf(42), aClass));
      assertEquals(convert(42.0), Converter.convertSilently(BigInteger.valueOf(42), aClass));

      assertNull(Converter.convertSilently("This is not a number", aClass));
   }


   @Test(expected = TypeConversionException.class)
   public void badParse() throws TypeConversionException {
      Converter.convert("This is not a number", aClass);
   }

   @Test(expected = TypeConversionException.class)
   public void badJson() throws TypeConversionException {
      Converter.convert(JsonEntry.array("1", "2"), aClass);
   }

   @Test(expected = TypeConversionException.class)
   public void notSupported() throws TypeConversionException {
      Converter.convert(arrayOf(1, 2, 3, 4), aClass);
   }

}//END OF BaseNumberConversionTest
