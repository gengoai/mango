#! /usr/bin/env python

import codecs
import re
import sys
from optparse import OptionParser

parser = OptionParser()
parser.add_option("-p", "--package", dest="package_name", help="The package name the class will be in")
parser.add_option("-n", "--name", dest="class_name", help="The class name")
parser.add_option("-f", "--file", dest="file", help="The path to write the java file to")
parser.add_option("--hierarchical", action="store_true", default=False, dest="hier", help="The path to write the java file to")
(options,args) = parser.parse_args()

if options.class_name is None:
    print "No Class Name Given"
    sys.exit(-1)
elif options.package_name is None:
    print "No Package Name Given"
    sys.exit(-1)
elif options.file is None:
    print "No File Path Given"
    sys.exit(-1)

flat = """package {{PACKAGE_NAME}}

import com.gengoai.collection.Sets;
import com.davidbracewell.DynamicEnum;
import com.davidbracewell.EnumValue;
import lombok.NonNull;

/**
* Auto generated using enumGen.py
* The type {{CLASS_NAME}}. 
*/
public final class {{CLASS_NAME}} extends EnumValue implements Comparable<{{CLASS_NAME}}> {
    public static final String CANONICAL_NAME = {{CLASS_NAME}}.class..getCanonicalName();
    private static final long serialVersionUID = 1L;
    private static final Set<{{CLASS_NAME}}> values = Sets.newConcurrentHashSet();

    private {{CLASS_NAME}}(String name){
        super(CANONICAL_NAME, name);
    }

    /**
    * <p>Creates a new or retrieves an existing instance of {{CLASS_NAME}} with the given name.</p>
    *
    * @return The instance of {{CLASS_NAME}} corresponding th the give name.
    */
    public static {{CLASS_NAME}} create(@NonNull String name) {
        {{CLASS_NAME}} toReturn = DynamicEnum.register(new {{CLASS_NAME}}(name));
        values.add(toReturn);
        return toReturn;
    }

    /**
    * <p>Retrieves all currently known values of {{CLASS_NAME}}.</p>
    *
    * @return An unmodifiable collection of currently known values for {{CLASS_NAME}}.
    */
    public static Collection<{{CLASS_NAME}}> values() {
        return Collections.unmodifiableSet(values);
    }

    /**
    * <p>Returns the constant of {{CLASS_NAME}} with the specified name.The normalized version of the specified name will
    * be matched allowing for case and space variations.</p>
    *
    * @return The constant of {{CLASS_NAME}} with the specified name
    * @throws IllegalArgumentException if the specified name is not a member of {{CLASS_NAME}}.
    */
    public static {{CLASS_NAME}} valueOf(@NonNull String name) {
        return DynamicEnum.valueOf({{CLASS_NAME}}.class, name);
    }

    @Override
    public int compareTo(@NonNull {{CLASS_NAME}} o) {
        return this.canonicalName().compareTo(o.canonicalName());
    }

}//END OF {{CLASS_NAME}}
"""

hierarchical = """package {{PACKAGE_NAME}};


import com.davidbracewell.DynamicEnum;
import com.davidbracewell.HierarchicalEnumValue;
import com.gengoai.collection.Sets;
import lombok.NonNull;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Auto generated using enumGen.py
 * The type {{CLASS_NAME}}.
 */
public final class {{CLASS_NAME}} extends HierarchicalEnumValue<{{CLASS_NAME}}> implements Comparable<{{CLASS_NAME}}> {
  public static final String CANONICAL_NAME = {{CLASS_NAME}}.class..getCanonicalName();
  private static final long serialVersionUID = 1L;
  private static final Set<{{CLASS_NAME}}> values = Sets.newConcurrentHashSet();

  public static final {{CLASS_NAME}} ROOT = RanksEnum.create("{{CLASS_NAME}}_ROOT");

  private {{CLASS_NAME}}(String name, {{CLASS_NAME}} parent) {
    super(CANONICAL_NAME,name, parent);
  }

   @Override
   protected {{CLASS_NAME}} getSingleRoot() {
      return ROOT;
   }

  /**
   * <p>Creates a new or retrieves an existing instance of {{CLASS_NAME}} with the given name.</p>
   *
   * @param name the specified name of the {{CLASS_NAME}}
   * @return The instance of {{CLASS_NAME}} corresponding th the give name.
   */
  public static {{CLASS_NAME}} create(@NonNull String name) {
    return create(name, null);
  }

  /**
   * <p>Creates a new or retrieves an existing instance of {{CLASS_NAME}} with the given name.</p>
   *
   * @param name   the specified name of the {{CLASS_NAME}}
   * @param parent the parent element of the enum;
   * @return The instance of {{CLASS_NAME}} corresponding th the give name.
   */
  public static {{CLASS_NAME}} create(@NonNull String name, {{CLASS_NAME}} parent) {
    {{CLASS_NAME}} toReturn = DynamicEnum.register(new {{CLASS_NAME}}(name, parent));
    toReturn.setParentIfAbsent(parent);
    values.add(toReturn);
    return toReturn;
  }

  /**
   * <p>Retrieves all currently known values of {{CLASS_NAME}}.</p>
   *
   * @return An unmodifiable collection of currently known values for {{CLASS_NAME}}.
   */
  public static Collection<{{CLASS_NAME}}> values() {
    return Collections.unmodifiableSet(values);
  }

  /**
   * <p>Returns the constant of {{CLASS_NAME}} with the specified name.The normalized version of the specified name will
   * be matched allowing for case and space variations.</p>
   *
   * @return The constant of {{CLASS_NAME}} with the specified name
   * @throws IllegalArgumentException if the specified name is not a member of {{CLASS_NAME}}.
   */
  public static {{CLASS_NAME}} valueOf(@NonNull String name) {
    return DynamicEnum.valueOf({{CLASS_NAME}}.class, name);
  }

   @Override
   public List<{{CLASS_NAME}}> getChildren() {
      return values().stream().filter(v -> this != v && v.getParent() == this).collect(Collectors.toList());
   }

  @Override
  public int compareTo(@NonNull {{CLASS_NAME}} o) {
    return canonicalName().compareTo(o.canonicalName());
  }

}// END OF {{CLASS_NAME}}

"""

if options.hier:
    template = hierarchical
else:
    template = flat


template = re.sub(r'{{PACKAGE_NAME}}', options.package_name, template)
template = re.sub(r'{{CLASS_NAME}}', options.class_name, template)

file = codecs.open(options.file + '/' + options.class_name +".java", "w", "utf-8")
file.write(unicode(template))
file.close()

