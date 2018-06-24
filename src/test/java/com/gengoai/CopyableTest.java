package com.gengoai;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class CopyableTest {

  @Test
  public void testCopy() {
    CopyClass cc = new CopyClass("Voltaire");
    assertThat(cc, equalTo(cc.copy()));
    assertThat(cc.getName(), equalTo(cc.copy().getName()));
    assertFalse(cc == cc.copy());
  }

  public static class CopyClass implements Copyable<CopyClass> {
    public String name;

    @java.beans.ConstructorProperties({"name"})
    public CopyClass(String name) {
      this.name = name;
    }

    @Override
    public CopyClass copy() {
      return new CopyClass(name);
    }

    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof CopyClass)) return false;
      final CopyClass other = (CopyClass) o;
      final Object this$name = this.getName();
      final Object other$name = other.getName();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      return true;
    }

    public String getName() {
      return this.name;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $name = this.getName();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      return result;
    }

    public String toString() {
      return "CopyableTest.CopyClass(name=" + this.getName() + ")";
    }
  }

}