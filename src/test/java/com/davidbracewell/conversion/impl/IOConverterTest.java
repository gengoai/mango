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

package com.davidbracewell.conversion.impl;

import com.davidbracewell.conversion.Convert;
import com.davidbracewell.conversion.IOConverter;
import com.davidbracewell.io.QuietIO;
import com.davidbracewell.string.StringUtils;
import com.google.common.io.CharStreams;
import com.google.common.primitives.Bytes;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class IOConverterTest {

  @Test
  public void testFile() throws Exception {
    assertNull(IOConverter.FILE.apply(null));

    //cast
    assertEquals(new File("ham.txt"), Convert.convert(new File("ham.txt"), File.class));

    //String conversion
    assertEquals(new File("ham.txt"), Convert.convert("ham.txt", File.class));

    //Path conversion
    assertEquals(new File("ham.txt"), Convert.convert(Paths.get("ham.txt"), File.class));

    //URI conversion
    assertEquals(new File("/ham.txt"), Convert.convert(new URI("file:/ham.txt"), File.class));

    //URL conversion
    assertEquals(new File("/ham.txt"), Convert.convert(new URL("file:/ham.txt"), File.class));

    assertNull(Convert.convert(new ArrayList<>(), File.class));
  }

  @Test
  public void testPath() throws Exception {
    assertNull(IOConverter.PATH.apply(null));

    //cast
    assertEquals(Paths.get("ham.txt"), Convert.convert(Paths.get("ham.txt"), Path.class));


    //String conversion
    assertEquals(Paths.get("ham.txt"), Convert.convert("ham.txt", Path.class));

    //Path conversion
    assertEquals(Paths.get("food/ham.txt"), Convert.convert(new File("food/ham.txt"), Path.class));

    //URI conversion
    assertEquals(Paths.get("/ham.txt"), Convert.convert(new URI("file:/ham.txt"), Path.class));

    //URL conversion
    assertEquals(Paths.get("/ham.txt"), Convert.convert(new URL("file:/ham.txt"), Path.class));

    assertNull(Convert.convert(new ArrayList<>(), Path.class));
  }

  @Test
  public void testCharset() throws Exception {
    assertNull(IOConverter.CHARSET.apply(null));

    //cast
    assertEquals(StandardCharsets.UTF_8, Convert.convert(Charset.forName("UTF-8"), Charset.class));

    //string conversion
    assertEquals(StandardCharsets.UTF_8, Convert.convert("UTF-8", Charset.class));

    //bad string conversion
    assertNull(Convert.convert("MY AWESOME ENCODING", Charset.class));

    //other
    assertNull(Convert.convert(new Object[0], Charset.class));
  }


  @Test
  public void testInputStream() throws Exception {
    assertNull(IOConverter.INPUT_STREAM.apply(null));

    //cast
    ByteArrayInputStream inputStream = new ByteArrayInputStream("TEST".getBytes());
    assertEquals(inputStream, Convert.convert(inputStream, InputStream.class));

    //path
    Path path = Files.createTempFile("espresso-test", StringUtils.randomHexString(10));
    Files.write(path, "This is a test".getBytes());
    assertEquals("This is a test", readStringInputStream(path));

    //File
    File file = path.toFile();
    file.deleteOnExit();
    assertEquals("This is a test", readStringInputStream(file));

    //URI
    assertEquals("This is a test", readStringInputStream(file.toURI()));

    //URL
    assertEquals("This is a test", readStringInputStream(file.toURI().toURL()));

    //String
    assertEquals("This is a test", readStringInputStream("This is a test"));

    //bytes
    assertEquals("This is a test", readStringInputStream("This is a test".getBytes()));

    //chars
    assertEquals("This is a test", readStringInputStream("This is a test".toCharArray()));

    //bad path
    assertNull(readStringInputStream(Paths.get("BLADF>325oikokl;fadfs")));

    //bad file
    assertNull(readStringInputStream(Paths.get("BLADF>325oikokl;fadfs").toFile()));

    //Collection
    assertEquals("This is a test", readStringInputStream(Bytes.asList("This is a test".getBytes())));

    //other
    assertNull(Convert.convert(new Object[0], InputStream.class));
  }

  @Test
  public void testOutputStream() throws Exception {
    assertNull(IOConverter.OUTPUT_STREAM.apply(null));

    //cast
    OutputStream outputStream = new ByteArrayOutputStream();
    assertEquals(outputStream, Convert.convert(outputStream, OutputStream.class));

    //path
    Path path = Files.createTempFile("espresso-test", StringUtils.randomHexString(10));
    path.toFile().deleteOnExit();
    assertEquals("This is a test", writeStringOutputStream(path, "This is a test"));

    //Bad Path
    assertNull(writeStringOutputStream(Paths.get("/dkfasd;fl/fasdfasdf.txt"), "This is a test"));

    //File
    assertEquals("This is a test", writeStringOutputStream(path.toFile(), "This is a test"));

    //Bad File
    assertNull(writeStringOutputStream(new File("/dkfasd;fl/fasdfasdf.txt"), "This is a test"));


    //other
    assertNull(Convert.convert(new Object[0], OutputStream.class));
  }

  @Test
  public void testURI() throws Exception {
    assertNull(IOConverter.URI.apply(null));

    //cast
    assertEquals(Paths.get("ham.txt").toUri(), Convert.convert(Paths.get("ham.txt").toUri(), URI.class));

    //String conversion
    assertEquals(Paths.get("/ham.txt").toUri(), Convert.convert("file:/ham.txt", URI.class));

    //Path conversion
    assertEquals(Paths.get("/food/ham.txt").toUri(), Convert.convert(Paths.get("/food/ham.txt"), URI.class));

    //File conversion
    assertEquals(Paths.get("/ham.txt").toUri(), Convert.convert(new File("/ham.txt"), URI.class));

    //URL conversion
    assertEquals(Paths.get("/ham.txt").toUri(), Convert.convert(new URL("file:/ham.txt"), URI.class));

    assertNull(Convert.convert(new ArrayList<>(), URI.class));
  }

  @Test
  public void testURL() throws Exception {
    assertNull(IOConverter.URI.apply(null));

    //cast
    assertEquals(Paths.get("ham.txt").toUri().toURL(), Convert.convert(Paths.get("ham.txt").toUri().toURL(), URL.class));

    //String conversion
    assertEquals(Paths.get("/ham.txt").toUri().toURL(), Convert.convert("file:/ham.txt", URL.class));

    //Path conversion
    assertEquals(Paths.get("/food/ham.txt").toUri().toURL(), Convert.convert(Paths.get("/food/ham.txt"), URL.class));

    //File conversion
    assertEquals(Paths.get("/ham.txt").toUri().toURL(), Convert.convert(new File("/ham.txt"), URL.class));

    //URI conversion
    assertEquals(Paths.get("/ham.txt").toUri().toURL(), Convert.convert(new URI("file:/ham.txt"), URL.class));

    assertNull(Convert.convert(new ArrayList<>(), URI.class));
  }


  private boolean checkCloseable(Closeable c){
    if( c == null ){
      return false;
    }
    QuietIO.closeQuietly(c);
    return true;
  }

  @Test
  public void testWriter() throws Exception {
    //null
    assertFalse(checkCloseable(IOConverter.WRITER.apply(null)));

    //other
    assertFalse(checkCloseable(IOConverter.WRITER.apply(new ArrayList<>())));


    Path path = Files.createTempFile("espresso-test", StringUtils.randomHexString(10));
    path.toFile().deleteOnExit();

    //cast
    assertTrue(checkCloseable(IOConverter.WRITER.apply(new FileWriter(path.toFile()))));

    //path
    assertTrue(checkCloseable(IOConverter.WRITER.apply(path)));

    //file
    assertTrue(checkCloseable(IOConverter.WRITER.apply(path.toFile())));

    //URL
    assertFalse(checkCloseable(IOConverter.WRITER.apply(path.toFile().toURI().toURL())));

    //URI
    assertFalse(checkCloseable(IOConverter.WRITER.apply(path.toFile().toURI())));

    //Bad File
    assertFalse(checkCloseable(IOConverter.WRITER.apply(new File("http://theinternetisbig.boing"))));
  }

  @Test
  public void testReader() throws Exception {
    //null
    assertFalse(checkCloseable(IOConverter.READER.apply(null)));

    //other
    assertFalse(checkCloseable(IOConverter.READER.apply(new ArrayList<>())));


    Path path = Files.createTempFile("espresso-test", StringUtils.randomHexString(10));
    path.toFile().deleteOnExit();

    //cast
    assertTrue(checkCloseable(IOConverter.READER.apply(new FileReader(path.toFile()))));

    //path
    assertTrue(checkCloseable(IOConverter.READER.apply(path)));

    //file
    assertTrue(checkCloseable(IOConverter.READER.apply(path.toFile())));

    //URL
    assertTrue(checkCloseable(IOConverter.READER.apply(path.toFile().toURI().toURL())));

    //URI
    assertTrue(checkCloseable(IOConverter.READER.apply(path.toFile().toURI())));

    //Bad File
    assertFalse(checkCloseable(IOConverter.READER.apply(new File("http://theinternetisbig.boing"))));
  }

  private String writeStringOutputStream(Object obj, String string) throws IOException{
    OutputStream os = Convert.convert(obj,OutputStream.class);
    if( os == null ){
      return null;
    }
    try( Writer writer = new OutputStreamWriter(os) ){
      writer.write(string);
    }
    return readStringInputStream(obj);
  }

  private String readStringInputStream(Object obj) throws IOException {
    InputStream is = Convert.convert(obj, InputStream.class);
    if (is == null) {
      return null;
    }
    try (Reader reader = new InputStreamReader(is)
    ) {
      return CharStreams.toString(reader).trim();
    }
  }


}//END OF IOConverterTest
