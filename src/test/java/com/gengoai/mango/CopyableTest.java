package com.gengoai.mango;

import lombok.Value;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

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

  @Value
  public static class CopyClass implements Copyable<CopyClass> {
    public String name;

    @Override
    public CopyClass copy() {
      return new CopyClass(name);
    }
  }

}