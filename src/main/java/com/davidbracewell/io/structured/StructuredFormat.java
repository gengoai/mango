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

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.json.JSONDocument;
import com.davidbracewell.io.structured.json.JSONReader;
import com.davidbracewell.io.structured.json.JSONWriter;
import com.davidbracewell.io.structured.xml.XMLDocument;
import com.davidbracewell.io.structured.xml.XMLReader;
import com.davidbracewell.io.structured.xml.XMLWriter;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;

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
   * @throws StructuredIOException the structured iO exception
   */
  StructuredReader createReader(Resource resource) throws StructuredIOException;


  /**
   * Create writer.
   *
   * @param resource the resource
   * @return the structured writer
   * @throws StructuredIOException the structured iO exception
   */
  StructuredWriter createWriter(Resource resource) throws StructuredIOException;


  /**
   * Create document.
   *
   * @param resource the resource
   * @return the structured document
   * @throws StructuredIOException the structured iO exception
   */
  StructuredDocument createDocument(Resource resource) throws StructuredIOException;

  /**
   * XML Format
   */
  StructuredFormat XML = new StructuredFormat() {

    private static final long serialVersionUID = 1297100190960314727L;

    @Override
    public StructuredReader createReader(Resource resource) throws StructuredIOException {
      return new XMLReader(resource);
    }

    @Override
    public StructuredWriter createWriter(Resource resource) throws StructuredIOException {
      return new XMLWriter(resource);
    }

    @Override
    public StructuredDocument createDocument(Resource resource) throws StructuredIOException {
      return XMLDocument.from(resource);
    }
  };


  /**
   * JSON Format
   */
  StructuredFormat JSON = new StructuredFormat() {
    private static final long serialVersionUID = 1297100190960314727L;

    @Override
    public StructuredReader createReader(Resource resource) throws StructuredIOException {
      return new JSONReader(resource);
    }

    @Override
    public StructuredWriter createWriter(Resource resource) throws StructuredIOException {
      return new JSONWriter(resource);
    }

    @Override
    public StructuredDocument createDocument(Resource resource) throws StructuredIOException {
      JSONDocument document = new JSONDocument();
      document.read(resource);
      return document;
    }
  };


  class DSVFormat implements StructuredFormat {
    private static final long serialVersionUID = 1L;
    private final com.davidbracewell.io.CSV format;

    public DSVFormat(@NonNull com.davidbracewell.io.CSV format) {
      this.format = format;
    }

    @Override
    public StructuredReader createReader(Resource resource) throws StructuredIOException {
      try {
        return format.reader(resource);
      } catch (IOException e) {
        throw new StructuredIOException(e);
      }
    }

    @Override
    public StructuredWriter createWriter(Resource resource) throws StructuredIOException {
      try {
        return format.writer(resource);
      } catch (IOException e) {
        throw new StructuredIOException(e);
      }
    }

    @Override
    public StructuredDocument createDocument(Resource resource) throws StructuredIOException {
      throw new UnsupportedOperationException();
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
