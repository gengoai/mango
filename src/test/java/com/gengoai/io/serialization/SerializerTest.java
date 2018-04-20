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

package com.gengoai.io.serialization;

import com.gengoai.conversion.Cast;
import com.gengoai.io.resource.ByteArrayResource;
import com.gengoai.io.resource.Resource;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class SerializerTest {


  @Test
  public void testSerialize() throws Exception {
    Person person = new Person("John", 24);
    person.getHobbies().add("Tennis");
    person.getHobbies().add("TV");
    person.getHobbies().add("Pool");

    JavaSerializer javaSerializer = new JavaSerializer();

    Resource resource = new ByteArrayResource();
    javaSerializer.serialize(person, resource);
    Person deserialized = Cast.as(javaSerializer.deserialize(resource, Person.class));

    assertEquals(person, deserialized);
  }

}//END OF SerializerTest
