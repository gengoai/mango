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

package com.davidbracewell.io.resource.spi;


import com.davidbracewell.io.resource.Resource;

import java.util.Map;

/**
 * <p>Creates resources for a given protocol. This is used in the {@link com.davidbracewell.io.Resources} class to
 * determine which type of resource to create when calling {@link com.davidbracewell.io.Resources#from(String)}.</p>
 *
 * @author David B. Bracewell
 */
public interface ResourceProvider {

  /**
   * @return The protocols that this provider supports (e.g. http, file, string, etc.)
   */
  String[] getProtocols();

  /**
   * Creates a resource with the given specification and properties using a {@link
   * com.davidbracewell.reflection.BeanMap}.
   *
   * @param specification The specification
   * @param properties    The properties
   * @return A resource
   */
  Resource createResource(String specification, Map<String, String> properties);

  /**
   * @return True if the protocol is required as part of the specification
   */
  boolean requiresProtocol();

}//END OF ResourceProvider
