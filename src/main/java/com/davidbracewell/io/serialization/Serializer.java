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

package com.davidbracewell.io.serialization;


import com.davidbracewell.io.resource.Resource;

/**
 * <p>A common interface for serializing and deserializing objects.</p>
 *
 * @author David B. Bracewell
 */
public interface Serializer {

   /**
    * Serializes the object saving the results to the resource
    *
    * @param o        The object
    * @param resource The resource to serialize to
    * @throws Exception Something went wrong serializing
    */
   void serialize(Object o, Resource resource) throws Exception;

   /**
    * Deserializes an object from a resource
    *
    * @param resource The resource containing the object
    * @param clazz    Class information for the object
    * @param <T>      the type of object
    * @return The deserialized object
    * @throws Exception Something went wrong deserializing the object
    */
   <T> T deserialize(Resource resource, Class<T> clazz) throws Exception;


}//END OF Serializer
