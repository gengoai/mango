package com.gengoai.conversion;

import com.gengoai.EnumValue;
import com.gengoai.Primitives;
import com.gengoai.json.Json;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonSerializable;
import com.gengoai.reflection.BeanUtils;
import com.gengoai.reflection.ReflectionUtils;
import com.gengoai.reflection.Types;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import static com.gengoai.reflection.Types.*;

/**
 * The type Converter.
 *
 * @author David B. Bracewell
 */
public final class Converter {
   private static final Map<Class<?>, TypeConverter> converterMap = new ConcurrentHashMap<>();

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

   /**
    * Convert silently t.
    *
    * @param <T>          the type parameter
    * @param sourceObject the source object
    * @param destType     the dest type
    * @return the t
    */
   public static <T> T convertSilently(Object sourceObject, Class<T> destType) {
      try {
         return convert(sourceObject, destType);
      } catch (TypeConversionException e) {
         return null;
      }
   }

   /**
    * Convert silently t.
    *
    * @param <T>          the type parameter
    * @param sourceObject the source object
    * @param destType     the dest type
    * @param parameters   the parameters
    * @return the t
    */
   public static <T> T convertSilently(Object sourceObject, Class<?> destType, Type... parameters) {
      try {
         return convert(sourceObject, destType, parameters);
      } catch (TypeConversionException e) {
         return null;
      }
   }

   /**
    * Convert silently t.
    *
    * @param <T>          the type parameter
    * @param sourceObject the source object
    * @param destType     the dest type
    * @return the t
    */
   public static <T> T convertSilently(Object sourceObject, Type destType) {
      try {
         return convert(sourceObject, destType);
      } catch (TypeConversionException e) {
         return null;
      }
   }

   /**
    * Convert t.
    *
    * @param <T>          the type parameter
    * @param sourceObject the source object
    * @param destType     the dest type
    * @param parameters   the parameters
    * @return the t
    * @throws TypeConversionException the type conversion exception
    */
   public static <T> T convert(Object sourceObject, Class<?> destType, Type... parameters) throws TypeConversionException {
      return Cast.as(convert(sourceObject, parameterizedType(destType, parameters)));
   }

   /**
    * Convert t.
    *
    * @param <T>          the type parameter
    * @param sourceObject the source object
    * @param destType     the dest type
    * @return the t
    * @throws TypeConversionException the type conversion exception
    */
   public static <T> T convert(Object sourceObject, Class<T> destType) throws TypeConversionException {
      return Cast.as(convert(sourceObject, Cast.<Type>as(destType)));
   }

   /**
    * Convert t.
    *
    * @param <T>          the type parameter
    * @param sourceObject the source object
    * @param destType     the dest type
    * @return the t
    * @throws TypeConversionException the type conversion exception
    */
   public static <T> T convert(Object sourceObject, Type destType) throws TypeConversionException {

      //If the source is null, return null or default value if the destination type is a primitive
      if (sourceObject == null) {
         if (isPrimitive(destType)) {
            return Primitives.defaultValue(Types.asClass(destType));
         }
         return null;
      }

      Class<?> rawClass = Primitives.wrap(Types.asClass(destType));

      //First check if we have a converter defined
      if (converterMap.containsKey(rawClass)) {
         return Cast.as(converterMap.get(rawClass).convert(sourceObject, Types.getActualTypeArguments(destType)));
      }

      //General Enum processing
      if (Enum.class.isAssignableFrom(rawClass)) {
         return Cast.as(converterMap.get(Enum.class).convert(sourceObject, rawClass));
      }

      //General EnumValue processing
      if (EnumValue.class.isAssignableFrom(rawClass)) {
         return Cast.as(converterMap.get(EnumValue.class).convert(sourceObject, rawClass));
      }

      //General Array processing
      if (rawClass.isArray()) {
         Type[] pt = Types.getActualTypeArguments(destType);
         Type componentType = rawClass.getComponentType();
         if (pt != null && pt.length > 0) {
            componentType = parameterizedType(componentType, pt);
         }
         return Cast.as(converterMap.get(Object[].class).convert(sourceObject, componentType));
      }

      //Just in case the we get this far and the source object is an instance of the destination type return it.
      if (isAssignable(destType, sourceObject.getClass())) {
         return Cast.as(sourceObject);
      }

      if (isAssignable(JsonSerializable.class, rawClass)) {
         try {
            if (sourceObject instanceof CharSequence) {
               return Json.parse(sourceObject.toString()).getAs(destType);
            } else if (sourceObject instanceof JsonEntry) {
               Cast.<JsonEntry>as(sourceObject).getAs(destType);
            }
         } catch (Exception e) {
            throw new TypeConversionException(sourceObject, destType, e);
         }
      }

      //Last chance
      try {
         return Cast.as(
            BeanUtils.parameterizeObject(ReflectionUtils.createObject(convert(sourceObject, String.class))));
      } catch (Exception e) {
         e.printStackTrace();
         //ignore
      }

      throw new TypeConversionException(sourceObject, destType);
   }

}//END OF Converter
