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

import com.davidbracewell.Primitives;
import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.*;
import com.davidbracewell.logging.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.function.Function;

/**
 * Functions for converting objects into IO related objects
 *
 * @author David B. Bracewell
 */
public final class IOConverter {

  /**
   * Converts an object to a Charset
   */
  public static final Function<Object, Charset> CHARSET = new Function<Object, Charset>() {

    @Override
    public Charset apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof Charset) {
        return Cast.as(input);
      } else if (input instanceof CharSequence) {
        try {
          return Charset.forName(input.toString());
        } catch (Exception e) {
          log.fine("Error creating charset for name '{0}': {1}", input, e);
          return null;
        }
      }

      log.fine("Unable to convert '{0}' into a charset. Returning default charset", input.getClass());
      return null;
    }
  };
  /**
   * Converts an object to a File
   */
  public static final Function<Object, File> FILE = new Function<Object, File>() {


    @Override
    public File apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof File) {
        return Cast.as(input);
      } else if (input instanceof Path) {
        return Cast.as(input, Path.class).toFile();
      } else if (input instanceof URI) {
        return new File(Cast.as(input, URI.class));
      } else if (input instanceof URL) {
        try {
          return new File(Cast.as(input, URL.class).toURI());
        } catch (Exception e) {
          log.fine("Error converting URL to File: {0}", e);
          return null;
        }
      } else if (input instanceof CharSequence) {
        return new File(input.toString());
      }

      log.fine("Unable to convert '{0}' into a File.", input.getClass());
      return null;
    }
  };
  /**
   * Converts an object to an InputStream
   */
  public static final Function<Object, InputStream> INPUT_STREAM = new Function<Object, InputStream>() {

    @Override
    public InputStream apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof InputStream) {
        return Cast.as(input);
      } else if (input instanceof File) {
        try {
          return new FileInputStream(Cast.as(input, File.class));
        } catch (FileNotFoundException e) {
          log.fine(e);
          return null;
        }
      } else if (input instanceof Path) {
        try {
          return Files.newInputStream(Cast.as(input, Path.class));
        } catch (IOException e) {
          log.fine(e);
          return null;
        }
      } else if (input instanceof URI) {
        try {
          return Cast.as(input, URI.class).toURL().openStream();
        } catch (IOException e) {
          log.fine(e);
          return null;
        }
      } else if (input instanceof URL) {
        try {
          return Cast.as(input, URL.class).openStream();
        } catch (IOException e) {
          log.fine(e);
          return null;
        }
      } else if (input instanceof CharSequence) {
        return new ByteArrayInputStream(input.toString().getBytes());
      } else if (input instanceof char[]) {
        return new ByteArrayInputStream(new String(Cast.<char[]>as(input)).getBytes());
      } else if (input instanceof Character[]) {
        return new ByteArrayInputStream(new String(Convert.convert(input, char[].class)).getBytes());
      } else if (input instanceof byte[]) {
        return new ByteArrayInputStream(Cast.as(input));
      } else if (input instanceof Byte[]) {
        return new ByteArrayInputStream(Primitives.toByteArray(Cast.<Byte[]>as(input)));
      } else if (input instanceof Blob) {
        try {
          return Cast.<Blob>as(input).getBinaryStream();
        } catch (SQLException e) {
          log.fine("SQL Exception reading blob: {0}", e);
          return null;
        }
      } else if (input instanceof Iterable) {
        byte[] bytes = Convert.convert(input, byte[].class);
        if (bytes != null) {
          return new ByteArrayInputStream(bytes);
        }
      }
      log.fine("Unable to convert '{0}' into an InputStream.", input.getClass());
      return null;
    }
  };
  /**
   * Converts an object to an OutputStream
   */
  public static final Function<Object, OutputStream> OUTPUT_STREAM = new Function<Object, OutputStream>() {

    @Override
    public OutputStream apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof OutputStream) {
        return Cast.as(input);
      } else if (input instanceof Path) {
        try {
          return new FileOutputStream(Cast.as(input, Path.class).toFile());
        } catch (FileNotFoundException e) {
          log.fine(e);
          return null;
        }
      } else if (input instanceof File) {
        try {
          return new FileOutputStream(Cast.as(input, File.class));
        } catch (FileNotFoundException e) {
          log.fine(e);
          return null;
        }
      }
      log.fine("Unable to convert '{0}' into an OutputStream.", input.getClass());
      return null;
    }
  };
  /**
   * Converts an object to a Path
   */
  public static final Function<Object, Path> PATH = new Function<Object, Path>() {


    @Override
    public Path apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof Path) {
        return Cast.as(input);
      } else if (input instanceof File) {
        return Paths.get(Cast.as(input, File.class).getPath());
      } else if (input instanceof URI) {
        return Paths.get(Cast.as(input, URI.class));
      } else if (input instanceof URL) {
        try {
          return Paths.get(Cast.as(input, URL.class).toURI());
        } catch (URISyntaxException e) {
          log.fine("Error converting URL to Path: {0}", e);
          return null;
        }
      } else if (input instanceof CharSequence) {
        return Paths.get(input.toString());
      }

      log.fine("Unable to convert '{0}' into a Path.", input.getClass());
      return null;
    }
  };
  /**
   * Converts an object to a Reader
   */
  public static final Function<Object, Reader> READER = new Function<Object, Reader>() {

    @Override
    public Reader apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof Reader) {
        return Cast.as(input);
      }
      InputStream is = INPUT_STREAM.apply(input);
      if (is != null) {
        return new InputStreamReader(is, StandardCharsets.UTF_8);
      }
      log.fine("Unable to convert '{0}' into an Writer.", input.getClass());
      return null;
    }
  };
  public static final Function<Object, Resource> RESOUCE = new Function<Object, Resource>() {

    @Override
    public Resource apply(Object obj) {

      if (obj == null) {
        return null;
      }

      if (obj instanceof Resource) {
        return Cast.as(obj);
      }


      if (obj instanceof File) {
        return Resources.fromFile((File) obj);
      }

      if (obj instanceof URL) {
        return Resources.fromUrl((URL) obj);
      }

      if (obj instanceof URI) {
        try {
          return Resources.fromUrl(((URI) obj).toURL());
        } catch (MalformedURLException e) {
          log.fine("Error converting to URL: {0}", e);
          return null;
        }
      }

      if (obj instanceof Reader) {
        return new ReaderResource(Cast.<Reader>as(obj));
      }

      if (obj instanceof InputStream) {
        return new InputStreamResource(Cast.<InputStream>as(obj));
      }

      if (obj instanceof OutputStream) {
        return new OutputStreamResource(Cast.<OutputStream>as(obj));
      }

      if (obj instanceof byte[]) {
        return new ByteArrayResource(Cast.<byte[]>as(obj));
      }

      if (obj instanceof CharSequence) {
        return Resources.from(obj.toString());
      }

      log.fine("Could not convert {0} into a Resource", obj.getClass());
      return null;
    }
  };
  /**
   * Converts an object to a URI
   */
  public static final Function<Object, URI> URI = new Function<Object, java.net.URI>() {

    @Override
    public URI apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof URI) {
        return Cast.as(input);
      } else if (input instanceof Path) {
        return Cast.as(input, Path.class).toUri();
      } else if (input instanceof File) {
        return Cast.as(input, File.class).toURI();
      } else if (input instanceof URL) {
        try {
          return Cast.as(input, URL.class).toURI();
        } catch (URISyntaxException e) {
          log.fine("Error converting URL to URI: {0}", e);
          return null;
        }
      } else if (input instanceof CharSequence) {
        try {
          return new URI(input.toString());
        } catch (URISyntaxException e) {
          log.fine("Error converting String to URI: {0}", e);
          return null;
        }
      }

      log.fine("Unable to convert '{0}' into a URI.", input.getClass());
      return null;

    }
  };
  /**
   * Converts an object to a URL
   */
  public static final Function<Object, URL> URL = new Function<Object, java.net.URL>() {

    @Override
    public URL apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof URL) {
        return Cast.as(input);
      } else if (input instanceof Path) {
        try {
          return Cast.as(input, Path.class).toUri().toURL();
        } catch (MalformedURLException e) {
          log.fine("Error converting Path into URL: {0}", e);
          return null;
        }
      } else if (input instanceof File) {
        try {
          return Cast.as(input, File.class).toURI().toURL();
        } catch (MalformedURLException e) {
          log.fine("Error converting File into URL: {0}", e);
          return null;
        }
      } else if (input instanceof CharSequence) {
        try {
          return new URL(input.toString());
        } catch (MalformedURLException e) {
          log.fine("Error converting String into URL: {0}", e);
          return null;
        }
      } else if (input instanceof URI) {
        try {
          return Cast.as(input, URI.class).toURL();
        } catch (MalformedURLException e) {
          log.fine("Error converting URI into URL: {0}", e);
          return null;
        }
      }

      log.fine("Unable to convert '{0}' into a URL.", input.getClass());
      return null;
    }
  };
  /**
   * Converts an object to a Writer
   */
  public static final Function<Object, Writer> WRITER = new Function<Object, Writer>() {

    @Override
    public Writer apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof Writer) {
        return Cast.as(input);
      }

      OutputStream stream = OUTPUT_STREAM.apply(input);
      if (stream != null) {
        return new OutputStreamWriter(stream, StandardCharsets.UTF_8);
      }

      log.fine("Unable to convert '{0}' into an Writer.", input.getClass());
      return null;
    }
  };

  private static final Logger log = Logger.getLogger(IOConverter.class);

  private IOConverter() {
  }


}//END OF IOConverter
