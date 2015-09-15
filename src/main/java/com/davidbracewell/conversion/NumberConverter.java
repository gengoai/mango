/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell.conversion;

import com.davidbracewell.logging.Logger;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Functions for converting objects into Numbers and Boolean
 *
 * @author David B. Bracewell
 */
public final class NumberConverter {
  private static final Logger log = Logger.getLogger(NumberConverter.class);


  /**
   * Converts an object into a BigDecimal
   */
  public static final Function<Object, BigDecimal> BIG_DECIMAL = new Function<Object, BigDecimal>() {
    @Override
    public BigDecimal apply(Object object) {
      if (object == null) {
        return null;
      } else if (object instanceof BigDecimal) {
        return Cast.as(object);
      } else if (object instanceof BigInteger) {
        return new BigDecimal(Cast.as(object, BigInteger.class));
      } else if (object instanceof Number) {
        return new BigDecimal(Cast.as(object, Number.class).doubleValue());
      } else if (object instanceof Boolean) {
        return new BigDecimal(Cast.<Boolean>as(object) ? 1d : 0d);
      } else if (object instanceof Character) {
        return new BigDecimal((int) Cast.<Character>as(object));
      }
      try {
//        return new BigDecimal(MathEvaluator.evaluate(object.toString()));
        return new BigDecimal(object.toString());
      } catch (Exception e) {
        log.fine("Error converting " + object + " to a Number");
      }
      return null;
    }
  };

  /**
   * Converts an object into a BigInteger
   */
  public static final Function<Object, BigInteger> BIG_INTEGER = new Function<Object, BigInteger>() {
    @Override
    public BigInteger apply(Object object) {
      if (object == null) {
        return null;
      } else if (object instanceof BigInteger) {
        return Cast.as(object);
      } else if (object instanceof Boolean) {
        return new BigInteger(Integer.toString(Cast.<Boolean>as(object) ? 1 : 0));
      } else if (object instanceof Character) {
        return BigInteger.valueOf((long) Cast.<Character>as(object));
      }
      try {
//        return new BigInteger(Double.toString(MathEvaluator.evaluate(object.toString())));
        return new BigInteger(object.toString());
      } catch (Exception e) {
        //ignore
      }

      try {
        return BigInteger.valueOf(new Double(object.toString()).longValue());
      } catch (Exception e) {
        log.fine("Error converting " + object + " to a Number");
      }

      return null;
    }
  };
  /**
   * Converts an object into a BOOLEAN
   */
  public static final Function<Object, Boolean> BOOLEAN = new Function<Object, Boolean>() {
    @Nullable
    @Override
    public Boolean apply(@Nullable Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof Boolean) {
        return Cast.as(input);
      } else if (input instanceof Number) {
        return Cast.as(input, Number.class).intValue() == 1;
      } else if (input instanceof CharSequence) {
        return Boolean.parseBoolean(input.toString());
      }

      log.fine("Cannot convert {0} to Boolean.", input.getClass());
      return null;
    }
  };
  /**
   * Converts an object into a BYTE
   */
  public static final Function<Object, Byte> BYTE = new Function<Object, Byte>() {
    @Override
    public Byte apply(Object object) {
      if (object == null) {
        return null;
      } else if (object instanceof CharSequence) {
        try {
          return (byte) Double.parseDouble(object.toString());
        } catch (Exception e) {
          byte[] bytes = object.toString().getBytes();
          if (bytes.length == 1) {
            return bytes[0];
          }
        }
      }
      Number number = DOUBLE.apply(object);
      return number == null ? null : number.byteValue();
    }
  };
  /**
   * Converts an object into a DOUBLE
   */
  public static final Function<Object, Double> DOUBLE = new Function<Object, Double>() {
    @Override
    public Double apply(Object object) {
      if (object == null) {
        return null;
      } else if (object instanceof BigDecimal) {
        return Cast.as(object,BigDecimal.class).doubleValue();
      } else if (object instanceof BigInteger) {
        return Cast.as(object, BigInteger.class).doubleValue();
      } else if (object instanceof Number) {
        return Cast.as(object, Number.class).doubleValue();
      } else if (object instanceof Boolean) {
        return Cast.as(object, Boolean.class) ? 1d : 0d;
      } else if (object instanceof Character) {
        return (double) ((int) Cast.as(object, Character.class));
      }
      try {
        return Double.parseDouble(object.toString());
        //return MathEvaluator.evaluate(object.toString());
      } catch (Exception e) {
        log.fine("Error converting " + object + " to a Number");
      }
      return null;
    }
  };
  /**
   * Converts an object into a FLOAT
   */
  public static final Function<Object, Float> FLOAT = new Function<Object, Float>() {
    @Override
    public Float apply(Object object) {
      Number number = DOUBLE.apply(object);
      return number == null ? null : number.floatValue();
    }
  };
  /**
   * Converts an object into a INTEGER
   */
  public static final Function<Object, Integer> INTEGER = new Function<Object, Integer>() {
    @Override
    public Integer apply(Object object) {
      Number number = DOUBLE.apply(object);
      return number == null ? null : number.intValue();
    }
  };
  /**
   * Converts an object into a LONG
   */
  public static final Function<Object, Long> LONG = new Function<Object, Long>() {
    @Override
    public Long apply(Object object) {
      Number number = DOUBLE.apply(object);
      return number == null ? null : number.longValue();
    }
  };
  /**
   * Converts an object into a SHORT
   */
  public static final Function<Object, Short> SHORT = new Function<Object, Short>() {
    @Override
    public Short apply(Object object) {
      Number number = DOUBLE.apply(object);
      return number == null ? null : number.shortValue();
    }
  };


  private NumberConverter() {
  }


}//END OF DoubleConverter
