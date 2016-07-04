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

package com.davidbracewell.stream;

/**
 * The interface Spark.
 *
 * @author David B. Bracewell
 */
public final class Spark {

//  /**
//   * The constant SPARK_MASTER.
//   */
//  public static String SPARK_MASTER = "spark.master";
//  /**
//   * The constant SPARK_APPNAME.
//   */
//  public static String SPARK_APPNAME = "spark.appName";
//
//  private static volatile JavaSparkContext context;
//
//
//  /**
//   * Gets context.
//   *
//   * @return the context
//   */
//  public static JavaSparkContext context() {
//    if (context == null) {
//      synchronized (Spark.class) {
//        if (context == null) {
//          SparkConf conf = new SparkConf();
//          if (Config.hasProperty(SPARK_MASTER)) {
//            conf.setMaster(Config.get(SPARK_MASTER).asString());
//          }
//          conf.setAppName(Config.get(SPARK_APPNAME).asString(StringUtils.randomHexString(20)));
//          context = new JavaSparkContext(conf);
//        }
//      }
//    }
//    return context;
//  }
//
//  /**
//   * Context java spark context.
//   *
//   * @param stream the stream
//   * @return the java spark context
//   */
//  public static JavaSparkContext context(MStream<?> stream) {
//    if (stream == null) {
//      return context();
//    } else if (stream instanceof SparkStream) {
//      return context(Cast.<SparkStream>as(stream).getRDD());
//    }
//    return context();
//  }
//
//  /**
//   * Context java spark context.
//   *
//   * @param rdd the rdd
//   * @return the java spark context
//   */
//  public static JavaSparkContext context(JavaRDD<?> rdd) {
//    if (rdd == null) {
//      return context();
//    }
//    return new JavaSparkContext(rdd.context());
//  }
//
//  /**
//   * Context java spark context.
//   *
//   * @param rdd the rdd
//   * @return the java spark context
//   */
//  public static JavaSparkContext context(JavaPairRDD<?, ?> rdd) {
//    if (rdd == null) {
//      return context();
//    }
//    return new JavaSparkContext(rdd.context());
//  }

}//END OF Spark
