package com.davidbracewell;

import com.google.common.collect.Sets;
import lombok.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author David B. Bracewell
 */
public final class RanksEnum extends HierarchicalEnumValue<RanksEnum> implements Comparable<RanksEnum> {
   private static final long serialVersionUID = 1L;
   private static final Set<RanksEnum> values = Sets.newConcurrentHashSet();

   public static final RanksEnum ROOT = RanksEnum.create("RanksEnum_Root");

   @Override
   protected RanksEnum getSingleRoot() {
      return ROOT;
   }

   private RanksEnum(String name, RanksEnum parent) {
      super(name, parent);
   }

   /**
    * <p>Creates a new or retrieves an existing instance of RanksEnum with the given name.</p>
    *
    * @param name the specified name of the RanksEnum
    * @return The instance of RanksEnum corresponding th the give name.
    */
   public static RanksEnum create(@NonNull String name) {
      return create(name, null);
   }

   /**
    * <p>Creates a new or retrieves an existing instance of RanksEnum with the given name.</p>
    *
    * @param name   the specified name of the RanksEnum
    * @param parent the parent element of the enum;
    * @return The instance of RanksEnum corresponding th the give name.
    */
   public static RanksEnum create(@NonNull String name, RanksEnum parent) {
      RanksEnum toReturn = DynamicEnum.register(new RanksEnum(name, parent));
      toReturn.setParentIfAbsent(parent);
      values.add(toReturn);
      return toReturn;
   }

   /**
    * <p>Retrieves all currently known values of RanksEnum.</p>
    *
    * @return An unmodifiable collection of currently known values for RanksEnum.
    */
   public static Collection<RanksEnum> values() {
      return Collections.unmodifiableSet(values);
   }

   /**
    * <p>Returns the constant of RanksEnum with the specified name.The normalized version of the specified name will
    * be matched allowing for case and space variations.</p>
    *
    * @return The constant of RanksEnum with the specified name
    * @throws IllegalArgumentException if the specified name is not a member of RanksEnum.
    */
   public static RanksEnum valueOf(@NonNull String name) {
      return DynamicEnum.valueOf(RanksEnum.class, name);
   }

   @Override
   public List<RanksEnum> getChildren() {
      return values().stream().filter(v -> this != v && v.getParent() == this).collect(Collectors.toList());
   }

   @Override
   public int compareTo(@NonNull RanksEnum o) {
      return canonicalName().compareTo(o.canonicalName());
   }

}// END OF RanksEnum

