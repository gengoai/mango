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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class JSONSerializerTest {

  @Test
  public void test() throws Exception {
    Person person = new Person("John", 24);
    person.getHobbies().add("Tennis");
    person.getHobbies().add("TV");
    person.getHobbies().add("Pool");

    JSONSerializer jsonSerializer = new JSONSerializer();

    Resource resource = new StringResource();
    jsonSerializer.serialize(person, resource);
    Person deserialized = Cast.as(jsonSerializer.deserialize(resource, Person.class));

    assertEquals(person, deserialized);
  }
}