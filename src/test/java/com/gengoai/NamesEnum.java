package com.gengoai;

import com.gengoai.collection.Sets;

import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * The type NamesEnum.
 */
public final class NamesEnum extends EnumValue<NamesEnum> implements Comparable<NamesEnum> {
   private static final long serialVersionUID = 1L;
   private static final Set<NamesEnum> values = Sets.newConcurrentHashSet();
   private static final Registry<NamesEnum> registry = new Registry<>(NamesEnum::new, NamesEnum.class);


   private NamesEnum(String name) {
      super(name);
   }

   @Override
   protected Registry<NamesEnum> registry() {
      return registry;
   }

   /**
    * <p>Creates a new or retrieves an existing instance of NamesEnum with the given name.</p>
    *
    * @return The instance of NamesEnum corresponding th the give name.
    */
   public static NamesEnum create(String name) {
      NamesEnum toReturn = registry.make(name);
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
   public static NamesEnum valueOf(String name) {
      return registry.valueOf(name);
   }

   @Override
   public int compareTo(NamesEnum o) {
      return this.canonicalName().compareTo(o.canonicalName());
   }

   private Object readResolve() throws ObjectStreamException {
      return create(name());
   }


}//END OF NamesEnum
