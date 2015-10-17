import re

functionals = {
    "BiConsumer<T,U>": ["void", "accept", "(T t, U u)", "(t,u)"],
    "BiFunction<T,U,R>": ["R", "apply", "(T t, U u)", "(t, u)"],
    "BinaryOperator<T>": ["T", "apply", "(T t, T u)", "(t, u)"],
    "BiPredicate<T,U>": ["boolean", "test", "(T t, U u)", "(t, U)"],
    "BooleanSupplier": ["boolean", "getAsBoolean", "()", "()"],
    "Consumer<T>": ["void", "accept", "(T t)", "(t)"],
    "DoubleBinaryOperator": ["double", "applyAsDouble", "(double t, double u)", "(t, u)"],
    "DoubleConsumer": ["void", "accept", "(double t)", "(t)"],
    "DoubleFunction<R>": ["R", "apply", "(double t)", "(t)"],
    "DoublePredicate": ["boolean", "test", "(double t)", "(t)"],
    "DoubleSupplier": ["double", "getAsDouble", "()", "()"],
    "DoubleToIntFunction": ["int", "applyAsInt", "(double t)", "(t)"],
    "DoubleToLongFunction": ["long", "applyAsLong", "(double t)", "(t)"],
    "DoubleUnaryOperator": ["double", "applyAsDouble", "(double t)", "(t)"],
    "Function<T,R>": ["R", "apply", "(T t)", "(t)"],
    "IntBinaryOperator": ["int", "applyAsInt", "(int t, int u)", "(t, u)"],
    "IntConsumer": ["void", "accept", "(int t)", "(t)"],
    "IntFunction<R>": ["R", "apply", "(int t)", "(t)"],
    "IntPredicate": ["boolean", "test", "(int t)", "(t)"],
    "IntSupplier": ["int", "getAsInt", "()", "()"],
    "IntToDoubleFunction": ["double", "applyAsDouble", "(int t)", "(t)"],
    "IntToLongFunction": ["long", "applyAsLong", "(int t)", "(t)"],
    "IntUnaryOperator": ["int", "applyAsInt", "(int t)", "(t)"],
    "LongBinaryOperator": ["long", "applyAsLong", "(long t, long u)", "(t, u)"],
    "LongConsumer": ["void", "accept", "(long t)", "(t)"],
    "LongFunction<R>": ["R", "apply", "(long t)", "(t)"],
    "LongPredicate": ["boolean", "test", "(long t)", "(t)"],
    "LongSupplier": ["long", "getAsLong", "()", "()"],
    "LongToDoubleFunction": ["double", "applyAsDouble", "(long t)", "(t)"],
    "LongToIntFunction": ["int", "applyAsInt", "(long t)", "(t)"],
    "LongUnaryOperator": ["long", "applyAsLong", "(long t)", "(t)"],
    "ObjDoubleConsumer<T>": ["void", "accept", "(T t, double value)", "(t, value)"],
    "ObjIntConsumer<T>": ["void", "accept", "(T t, int value)", "(t, value)"],
    "ObjLongConsumer<T>": ["void", "accept", "(T t, long value)", "(t, value)"],
    "Predicate<T>": ["boolean", "test", "(T t)", "(t)"],
    "Supplier<T>": ["T", "get", "()", "()"],
    "ToDoubleBiFunction<T,U>": ["double", "applyAsDouble", "(T t, U u)", "(t, u)"],
    "ToDoubleFunction<T>": ["double", "applyAsDouble", "(T t)", "(t)"],
    "ToIntBiFunction<T,U>": ["int", "applyAsInt", "(T t, U u)", "(t, u)"],
    "ToIntFunction<T>": ["int", "applyAsInt", "(T t)", "(t)"],
    "ToLongBiFunction<T,U>": ["Long", "applyAsLong", "(T t, U u)", "(t, u)"],
    "ToLongFunction<T>": ["long", "applyAsLong", "(T t)", "(t)"],
    "UnaryOperator<T>": ["T", "apply", "(T t)", "(t)"]
}

for (classGeneric, methodInformation) in functionals.iteritems():
    className = re.sub(r"<\w(,\w)*>", "", classGeneric)
    generics =  classGeneric.replace(className,"").replace("<","").replace(">", "").strip().split(",")
    paramJavaDoc = ""
    for g in generics:
        if g != "":
            paramJavaDoc += "* @param <%s> Functional parameter\n" % g
    paramJavaDoc = paramJavaDoc.strip()
    if paramJavaDoc == "":
        paramJavaDoc = "*"
    code = """
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.%s;

/**
 * Version of %s that is serializable
 %s
 */
@FunctionalInterface
public interface Serializable%s extends %s, Serializable {

}//END OF Serializable%s
""" % (className, className, paramJavaDoc, classGeneric, classGeneric, className)
    file = "src/main/java/com/davidbracewell/function/Serializable%s.java" % className
    out = open(file, "w")
    out.write(code)
    out.close()

for (classGeneric, methodInformation) in functionals.iteritems():
    className = re.sub(r"<\w(,\w)*>", "", classGeneric)
    generics = classGeneric.replace(className, "").replace("<", "").replace(">", "").strip().split(",")
    paramJavaDoc = ""
    for g in generics:
        if g != "":
            paramJavaDoc += "* @param <%s> Functional parameter\n" % g
    paramJavaDoc = paramJavaDoc.strip()
    if paramJavaDoc == "":
        paramJavaDoc = "*"
    code = """
package com.davidbracewell.function;
import java.io.Serializable;
import java.util.function.%s;

/**
 * Version of %s that is serializable
 %s
 */
@FunctionalInterface
public interface Checked%s extends Serializable {

    %s %s%s throws Throwable;

}//END OF Checked%s
""" % (className, className, paramJavaDoc, classGeneric, methodInformation[0], methodInformation[1], methodInformation[2],  className)
    file = "src/main/java/com/davidbracewell/function/Checked%s.java" % className
    out = open(file, "w")
    out.write(code)
    out.close()


uncheckedClass = """
package com.davidbracewell.function;

import java.io.Serializable;
import com.google.common.base.Throwables;
import java.util.function.*;

public interface Unchecked {
"""

for (classGeneric, methodInformation) in functionals.iteritems():
    className = re.sub(r"<\w(,\w)*>", "", classGeneric)
    generics = classGeneric.replace(className, "")
    mName =  className[:1].lower() + className[1:]
    call = ""
    if methodInformation[0] != "void":
        call += "return "
    call += "checked." + methodInformation[1] + methodInformation[3]
    paramJavaDoc = ""
    for g in classGeneric.replace(className, "").replace("<", "").replace(">", "").strip().split(","):
        if g != "":
            paramJavaDoc += "* @param <%s> Functional parameter\n" % g
    paramJavaDoc = paramJavaDoc.strip()
    if paramJavaDoc == "":
        paramJavaDoc = "*"
    code = """
       /**
   * Generates a version of %s that will capture exceptions and rethrow them as runtime exceptions
   * @param checked The checked functional
     %s
   * @return The checked functional.
   */
    static %s  %s %s(Checked%s checked){
       return (Serializable & %s) %s -> {
        try {
           %s;
        } catch(Throwable e){
            throw Throwables.propagate(e);
        }
       };
    }

    """ % (className, paramJavaDoc, generics, classGeneric, mName, classGeneric, classGeneric, methodInformation[3], call)
    uncheckedClass += code

uncheckedClass += "\n}//END OF Unchecked"
out = open("src/main/java/com/davidbracewell/function/Unchecked.java", "w")
out.write(uncheckedClass)
out.close()

serializedClass = """
package com.davidbracewell.function;

import java.util.function.*;

public interface Serialized {
"""
for (classGeneric, methodInformation) in functionals.iteritems():
    className = re.sub(r"<\w(,\w)*>", "", classGeneric)
    generics = classGeneric.replace(className, "")
    mName = className[:1].lower() + className[1:]
    paramJavaDoc = ""
    for g in classGeneric.replace(className, "").replace("<", "").replace(">", "").strip().split(","):
        if g != "":
            paramJavaDoc += "* @param <%s> Functional parameter\n" % g
    paramJavaDoc = paramJavaDoc.strip()
    if paramJavaDoc == "":
        paramJavaDoc = "*"
    code = """
   /**
   * Generates a serialized version of %s
   * @param serialized The serialized functional
     %s
   * @return The serialized functional.
   */
    static %s  %s %s(Serializable%s serialized){
       return serialized;
    }

    """ % (className, paramJavaDoc, generics, classGeneric, mName, classGeneric)
    serializedClass += code
serializedClass += "\n}//END OF Serialized"
out = open("src/main/java/com/davidbracewell/function/Serialized.java", "w")
out.write(serializedClass)
out.close()
