package com.gengoai.conversion;

import com.gengoai.EnumValue;
import com.gengoai.Primitives;
import com.gengoai.logging.Logger;
import com.gengoai.reflection.Types;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import static com.gengoai.collection.Collect.arrayOfInt;
import static com.gengoai.reflection.Types.parameterizedType;

/**
 * @author David B. Bracewell
 */
public final class Converter {
   private static final Map<Class<?>, TypeConverter> converterMap = new ConcurrentHashMap<>();
   private static final Logger LOG = Logger.getLogger(Converter.class);

   static {
      ServiceLoader.load(TypeConverter.class)
                   .iterator()
                   .forEachRemaining(tc -> {
                      for (Class aClass : tc.getConversionType()) {
                         if (converterMap.containsKey(aClass)) {
                            throw new IllegalStateException("Attempting to define multiple converters for: " + aClass);
                         }
                         converterMap.put(aClass, tc);
                      }
                   });
   }


   public static <T> T convertSilently(Object sourceObject, Class<T> destType) {
      try {
         return convert(sourceObject, destType);
      } catch (TypeConversionException e) {
         return null;
      }
   }

   public static <T> T convertSilently(Object sourceObject, Class<?> destType, Type... parameters) {
      try {
         return convert(sourceObject, destType, parameters);
      } catch (TypeConversionException e) {
         return null;
      }
   }

   public static <T> T convertSilently(Object sourceObject, Type destType) {
      try {
         return convert(sourceObject, destType);
      } catch (TypeConversionException e) {
         return null;
      }
   }

   public static <T> T convert(Object sourceObject, Class<?> destType, Type... parameters) throws TypeConversionException {
      return Cast.as(convert(sourceObject, parameterizedType(destType, parameters)));
   }

   public static <T> T convert(Object sourceObject, Class<T> destType) throws TypeConversionException {
      return Cast.as(convert(sourceObject, Cast.<Type>as(destType)));
   }

   public static <T> T convert(Object sourceObject, Type destType) throws TypeConversionException {
      if (sourceObject == null) {
         return null;
      }

      Class<?> rawClass = Primitives.wrap(Types.asClass(destType));
      if (converterMap.containsKey(rawClass)) {
         return Cast.as(converterMap.get(rawClass).convert(sourceObject, Types.getActualTypeArguments(destType)));
      }

      if (Enum.class.isAssignableFrom(rawClass)) {
         return Cast.as(converterMap.get(Enum.class).convert(sourceObject, rawClass));
      }
      if (EnumValue.class.isAssignableFrom(rawClass)) {
         return Cast.as(converterMap.get(EnumValue.class).convert(sourceObject, rawClass));
      }
      if (rawClass.isArray()) {
         Type[] pt = Types.getActualTypeArguments(destType);
         Type componentType = rawClass.getComponentType();
         if (pt != null && pt.length > 0) {
            componentType = parameterizedType(componentType, pt);
         }
         return Cast.as(converterMap.get(Object[].class).convert(sourceObject, componentType));
      }

      throw new TypeConversionException(sourceObject.getClass(), destType);
   }

   public static void main(String[] args) throws Exception {
      int[] itr = arrayOfInt(1, 2, 3, 4, 5);
      System.out.println(Converter.<Object>convert(itr, List.class, Double.class));
   }

}//END OF Converter
