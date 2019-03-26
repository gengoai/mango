package com.gengoai;

import java.util.Collection;

/**
 * @author David B. Bracewell
 */
public final class RanksEnum extends HierarchicalEnumValue<RanksEnum> implements Comparable<RanksEnum> {
   private static final long serialVersionUID = 1L;
   private static final HierarchicalRegistry<RanksEnum> registry = new HierarchicalRegistry<>(RanksEnum::new,
                                                                                              RanksEnum.class,
                                                                                              "RanksEnum_Root");
   public static final RanksEnum ROOT = registry.ROOT;

   private RanksEnum(String name) {
      super(name);
   }

   @Override
   protected HierarchicalRegistry<RanksEnum> registry() {
      return registry;
   }

   /**
    * <p>Creates a new or retrieves an existing instance of RanksEnum with the given name.</p>
    *
    * @param name the specified name of the RanksEnum
    * @return The instance of RanksEnum corresponding th the give name.
    */
   public static RanksEnum create(String name) {
      return registry.make(name);
   }


   public static RanksEnum create(RanksEnum parent, String name) {
      return registry.make(parent, name);
   }


   /**
    * <p>Retrieves all currently known values of RanksEnum.</p>
    *
    * @return An unmodifiable collection of currently known values for RanksEnum.
    */
   public static Collection<RanksEnum> values() {
      return registry.values();
   }

   /**
    * <p>Returns the constant of RanksEnum with the specified name.The normalized version of the specified name will
    * be matched allowing for case and space variations.</p>
    *
    * @return The constant of RanksEnum with the specified name
    * @throws IllegalArgumentException if the specified name is not a member of RanksEnum.
    */
   public static RanksEnum valueOf(String name) {
      return registry.valueOf(name);
   }


}// END OF RanksEnum

