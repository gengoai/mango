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

package com.davidbracewell.io.structured;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import com.davidbracewell.io.structured.json.JSONReader;
import com.davidbracewell.io.structured.json.JSONWriter;
import com.davidbracewell.io.structured.xml.XMLReader;
import com.davidbracewell.io.structured.xml.XMLWriter;
import com.davidbracewell.tuple.Tuple2;
import com.davidbracewell.tuple.Tuples;
import com.google.common.base.Throwables;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

/**
 * The type Structured format.
 *
 * @author David B. Bracewell
 */
public interface StructuredFormat extends Serializable {

  /**
   * Create reader.
   *
   * @param resource the resource
   * @return the structured reader
   * @throws IOException the structured iO exception
   */
  StructuredReader createReader(Resource resource) throws IOException;


  /**
   * Create writer.
   *
   * @param resource the resource
   * @return the structured writer
   * @throws IOException the structured iO exception
   */
  StructuredWriter createWriter(Resource resource) throws IOException;

  default Map<String,?> loads(String data) {
    Map<String,?> r;
    try( StructuredReader reader = createReader(new StringResource(data))){
      reader.beginDocument();
      r= reader.nextMap();
      reader.endDocument();;
    }catch (IOException e ){
      throw Throwables.propagate(e);
    }
    return r;
  }

  default String dumps(@NonNull Map<String,?> map) {
    Resource strResource = new StringResource();
    try( StructuredWriter writer = createWriter(strResource)){
      writer.beginDocument();
      for( Map.Entry<String,?> entry : map.entrySet()){
          writer.writeKeyValue(entry.getKey(), entry.getValue());
      }
      writer.endDocument();
    } catch (IOException e ){
      throw Throwables.propagate(e);
    }
    try {
      return strResource.readToString().trim();
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }



  /**
   * XML Format
   */
  StructuredFormat XML = new StructuredFormat() {

    private static final long serialVersionUID = 1297100190960314727L;

    @Override
    public StructuredReader createReader(Resource resource) throws IOException {
      return new XMLReader(resource);
    }

    @Override
    public StructuredWriter createWriter(Resource resource) throws IOException {
      return new XMLWriter(resource);
    }
  };


  /**
   * JSON Format
   */
  StructuredFormat JSON = new StructuredFormat() {
    private static final long serialVersionUID = 1297100190960314727L;

    @Override
    public StructuredReader createReader(Resource resource) throws IOException {
      return new JSONReader(resource);
    }

    @Override
    public StructuredWriter createWriter(Resource resource) throws IOException {
      return new JSONWriter(resource);
    }

  };


  class DSVFormat implements StructuredFormat {
    private static final long serialVersionUID = 1L;
    private final com.davidbracewell.io.CSV format;

    public DSVFormat(@NonNull com.davidbracewell.io.CSV format) {
      this.format = format;
    }

    @Override
    public StructuredReader createReader(Resource resource) throws IOException {
      try {
        return format.reader(resource);
      } catch (IOException e) {
        throw new IOException(e);
      }
    }

    @Override
    public StructuredWriter createWriter(Resource resource) throws IOException {
      try {
        return format.writer(resource);
      } catch (IOException e) {
        throw new IOException(e);
      }
    }

  }


  /**
   * CSV with no header
   */
  StructuredFormat CSV = new DSVFormat(com.davidbracewell.io.CSV.builder());

  /**
   * TSV with no header
   */
  StructuredFormat TSV = new DSVFormat(com.davidbracewell.io.CSV.builder().delimiter('\t'));

  /**
   * CSV with header
   */
  StructuredFormat CSV_HEADER = new DSVFormat(com.davidbracewell.io.CSV.builder().hasHeader());

  /**
   * TSV with header
   */
  StructuredFormat TSV_HEADER = new DSVFormat(com.davidbracewell.io.CSV.builder().delimiter('\t').hasHeader());


}//END OF StructuredFormat
