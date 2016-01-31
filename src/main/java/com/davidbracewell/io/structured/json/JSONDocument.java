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

package com.davidbracewell.io.structured.json;

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.StructuredDocument;
import com.google.common.base.Preconditions;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;

/**
 * @author David B. Bracewell
 */
public class JSONDocument extends JSONElement implements StructuredDocument {
  private static final long serialVersionUID = 1L;

  public JSONDocument() {
    setOwner(this);
  }

  @Override
  public void write(Resource resource) throws IOException {
    try {
      Preconditions.checkNotNull(resource).write(
        new GsonBuilder().setPrettyPrinting().create().toJson(node)
      );
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void read(Resource resource) throws IOException {
    try {
      JsonElement element = new JsonParser().parse(resource.reader());
      setNode(element);
    } catch (IOException e) {
      throw new IOException(e);
    }
  }


}//END OF JSONDocument
