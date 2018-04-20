package com.gengoai;

import com.google.common.collect.Sets;
import lombok.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * The type NamesEnum.
 */
public final class NamesEnum extends EnumValue implements Comparable<NamesEnum> {
   private static final long serialVersionUID = 1L;
   private static final Set<NamesEnum> values = Sets.newConcurrentHashSet();




   private NamesEnum(String name) {
      super(name);
   }

   /**
    * <p>Creates a new or retrieves an existing instance of NamesEnum with the given name.</p>
    *
    * @return The instance of NamesEnum corresponding th the give name.
    */
   public static NamesEnum create(@NonNull String name) {
      NamesEnum toReturn = DynamicEnum.register(new NamesEnum(name));
      values.add(toReturn);
      return toReturn;
   }


   /**
    * <p>Retrieves all currently known values of NamesEnum.</p>
    *
    * @return An unmodifiable collection of currently known values for NamesEnum.
    */
   public static Collection<NamesEnum> values() {
      return Collections.unmodifiableSet(values);
   }

   /**
    * <p>Returns the constant of NamesEnum with the specified name.The normalized version of the specified name will
    * be matched allowing for case and space variations.</p>
    *
    * @return The constant of NamesEnum with the specified name
    * @throws IllegalArgumentException if the specified name is not a member of NamesEnum.
    */
   public static NamesEnum valueOf(@NonNull String name) {
      return DynamicEnum.valueOf(NamesEnum.class, name);
   }

   @Override
   public int compareTo(@NonNull NamesEnum o) {
      return this.canonicalName().compareTo(o.canonicalName());
   }

}//END OF NamesEnum
