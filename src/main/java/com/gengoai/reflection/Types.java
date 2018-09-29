package com.gengoai.reflection;

import com.gengoai.Primitives;
import com.gengoai.conversion.Cast;
import com.gengoai.json.Json;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

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

   final static Pattern TYPE_PATTERN = Pattern.compile("([^<>]+)(?:<([^>])+?>)");

   public static void main(String[] args) throws Exception {
      String s = "Map<String,List<String>>";
      System.out.println(fromString(s));
      System.out.println(fromString("List<com.gengoai.Language>"));
   }

   public static Type fromString(String s) {
      int tStart = s.indexOf('<');
      int tEnd = s.lastIndexOf('>');
      int rawEnd = tStart > 0 ? tStart : s.length();

      if ((tStart == -1 && tEnd != -1) || (tStart != -1 && tEnd != s.length() - 1)) {
         throw new RuntimeException("Invalid Parameterized Type Declaration: " + s);
      }

      Type rawType = null;
      try {
         rawType = ReflectionUtils.getClassForName(s.substring(0, rawEnd));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
      Type[] pTypes = null;
      if (tStart > 0) {
         pTypes = java.util.Arrays.stream(s.substring(tStart + 1, tEnd).split("[, ]+"))
                                  .map(Types::fromString)
                                  .toArray(Type[]::new);
      }
      return pTypes == null ? rawType : parameterizedType(rawType, pTypes);
   }

   public static boolean isAssignable(Type t1, Type toCheck) {
      Class<?> c1 = Primitives.wrap(toClass(t1));
      Class<?> c2 = Primitives.wrap(toClass(toCheck));
      return c1.isAssignableFrom(c2);
   }


   public static boolean isContainer(Type type) {
      if (type == null) {
         return false;
      }
      Class<?> clazz = toClass(type);
      return Iterable.class.isAssignableFrom(clazz) ||
                Iterator.class.isAssignableFrom(clazz) ||
                clazz.isArray();
   }

   public static boolean isCollection(Type type) {
      return Collection.class.isAssignableFrom(toClass(type));
   }

   public static boolean isIterable(Type type) {
      return Iterable.class.isAssignableFrom(toClass(type));
   }

   public static boolean isIterator(Type type) {
      return Iterator.class.isAssignableFrom(toClass(type));
   }

   public static boolean isArray(Type type) {
      return toClass(type).isArray();
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

      @Override
      public String toString() {
         return Json.dumps(this);
      }
   }

}//END OF Types
