package com.gengoai.reflection;

import com.gengoai.conversion.Cast;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <p>Convenience methods for creating type information</p>
 *
 * @author David B. Bracewell
 */
public final class Types {

   private Types() {
      throw new IllegalAccessError();
   }


   /**
    * Converts type information to class information
    *
    * @param <T>  the type parameter
    * @param type the type
    * @return the class
    */
   public static <T> Class<T> asClass(Type type) {
      if (type instanceof Class) {
         return Cast.as(type);
      }
      if (type instanceof ParameterizedType) {
         return asClass(Cast.<ParameterizedType>as(type).getRawType());
      }
      if (type instanceof GenericArrayType) {
         Class<?> componenet = asClass(Cast.<GenericArrayType>as(type).getGenericComponentType());
         return Cast.as(Array.newInstance(componenet, 1).getClass());
      }
      throw new IllegalArgumentException("Unable to handle type (" + type.getClass() + "): " + type.getTypeName());
   }


   /**
    * Creates parameterized type information for the given raw type and optional type arguments.
    *
    * @param rawType       the raw type
    * @param typeArguments the type arguments
    * @return the parameterized type
    */
   public static Type parameterizedType(Type rawType, Type... typeArguments) {
      return new ParameterizedTypeImpl(rawType, typeArguments, null);
   }

   public static boolean isAssignable(Type t1, Type toCheck) {
      return toClass(t1).isAssignableFrom(toClass(toCheck));
   }

   public static Class<?> toClass(Type type) {
      if (type instanceof ParameterizedType) {
         ParameterizedType pt = Cast.as(type);
         return toClass(pt.getRawType());
      }
      return Cast.as(type, Class.class);
   }

   public static Type type(Type rawType, Type... typeArguments) {
      if (typeArguments == null || typeArguments.length == 0) {
         return rawType;
      }
      return new ParameterizedTypeImpl(rawType, typeArguments, null);
   }

   private static class ParameterizedTypeImpl implements ParameterizedType {
      private final Type rawType;
      private final Type[] actualTypeArguments;
      private final Type ownerType;

      private ParameterizedTypeImpl(Type rawType, Type[] actualTypeArguments, Type ownerType) {
         this.rawType = rawType;
         this.actualTypeArguments = actualTypeArguments;
         this.ownerType = ownerType;
      }

      @Override
      public Type[] getActualTypeArguments() {
         return actualTypeArguments;
      }

      @Override
      public Type getRawType() {
         return rawType;
      }

      @Override
      public Type getOwnerType() {
         return ownerType;
      }
   }

}//END OF Types
