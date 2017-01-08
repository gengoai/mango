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

package com.davidbracewell.io.resource;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.SerializableConsumer;
import com.davidbracewell.function.Unchecked;
import com.davidbracewell.io.CharsetDetectingReader;
import com.davidbracewell.io.FileUtils;
import com.davidbracewell.io.serialization.JavaSerializer;
import com.davidbracewell.stream.LocalStream;
import com.davidbracewell.stream.MStream;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import lombok.NonNull;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * <p> Information about a resource, which abstracts away the specific details on working with the resource. Gives the
 * ability to open an <code>InputStream</code> and <code>OutputStream</code> as well as manipulate the resource.
 * Manipulation is implementation specific. </p>
 *
 * @author David Bracewell
 */
public interface Resource {

   /**
    * The constant ALL_FILE_PATTERN.
    */
   Pattern ALL_FILE_PATTERN = Pattern.compile(".*");


   /**
    * Copies the contents of this resource to another
    *
    * @param copyTo The resource to copy to
    * @throws IOException Something went wrong copying.
    */
   default void copy(@NonNull Resource copyTo) throws IOException {
      Preconditions.checkState(copyTo.canWrite(), "The resource being copied to cannot be written to.");
      if (isDirectory()) {
         copyTo.mkdirs();
         for (Resource child : getChildren(true)) {
            Resource copyToChild = copyTo.getChild(child.path().substring(path().length()).replaceAll("^[\\/]+", ""));
            copyTo.getParent().mkdirs();
            child.copy(copyToChild);
         }
      } else {
         Preconditions.checkState(canRead(), "This resource cannot be read from.");
         try (InputStream is = inputStream(); OutputStream os = copyTo.outputStream()) {
            ByteStreams.copy(is, os);
         }
      }
   }

   /**
    * <p> Appends content to this resource. </p>
    *
    * @param content The content to append
    * @return the resource
    * @throws IOException the io exception
    */
   default Resource append(String content) throws IOException {
      Preconditions.checkState(canWrite(), "This resource cannot be written to.");
      if (content == null) {
         return this;
      }
      return append(content.getBytes(getCharset()));
   }

   /**
    * <p> Appends content to this resource. </p>
    *
    * @param byteArray The content to append
    * @return the resource
    * @throws IOException the io exception
    */
   Resource append(byte[] byteArray) throws IOException;

   /**
    * Gets the resource as a <code>File</code>.
    *
    * @return A <code>File</code> representing the resource.
    */
   Optional<File> asFile();

   /**
    * As path optional.
    *
    * @return the optional
    */
   default Optional<Path> asPath() {
      return asFile().map(File::toPath);
   }

   /**
    * As uri optional.
    *
    * @return the optional
    */
   Optional<URI> asURI();

   /**
    * Gets the resource as a <code>URL</code>.
    *
    * @return A <code>URL</code> representing the resource.
    */
   default Optional<URL> asURL() {
      return asURI().map(Unchecked.function(URI::toURL));
   }

   /**
    * Gets the name (file name or directory name) of this resource.
    *
    * @return The name of the file or directory
    */
   String baseName();

   /**
    * Can read.
    *
    * @return True if can read from the resource
    */
   boolean canRead();

   /**
    * Can write.
    *
    * @return True if can write to the resource
    */
   boolean canWrite();

   /**
    * Iterator over the children
    *
    * @param pattern   The pattern to determine what files we want
    * @param recursive should we iterator recursively?
    * @return An iterator over the children
    */
   default Iterator<Resource> childIterator(String pattern, boolean recursive) {
      return new ResourceChildIterator(this, FileUtils.createFilePattern(pattern), recursive);
   }

   /**
    * Iterator over the children
    *
    * @param recursive should we iterator recursively?
    * @return An iterator over the children
    */
   default Iterator<Resource> childIterator(boolean recursive) {
      return new ResourceChildIterator(this, ALL_FILE_PATTERN, recursive);
   }

   /**
    * Sets is compressed.
    *
    * @return the resource
    */
   Resource compressed();

   /**
    * Deletes the resource
    *
    * @return true if the deletion was successful
    */
   default boolean delete() {
      return delete(false);
   }

   /**
    * Deletes the resource
    *
    * @param recursively true if should recursively delete everything under this resource
    * @return true if the deletion was successful
    */
   default boolean delete(boolean recursively) {
      return false;
   }

   /**
    * Deletes the resource on ext
    */
   default Resource deleteOnExit() {
      return this;
   }

   /**
    * Descriptor string.
    *
    * @return The string representation of the resource with protocol
    */
   String descriptor();

   /**
    * Exists boolean.
    *
    * @return True if the resource exists, False if the resource does not exist.
    */
   boolean exists();

   /**
    * <p>Gets the charset for reading and writing. The charset if not specified will be automatically determined during
    * read.</p>
    *
    * @return The charset used for writing and default when reading
    */
   Charset getCharset();

   /**
    * <p>Sets the charset for reading and writing.</p>
    *
    * @param charset The charset to use
    * @return the charset
    */
   Resource setCharset(Charset charset);

   /**
    * <p> Creates a new Resource that is relative to this resource. </p>
    *
    * @param relativePath The relative path for the new resource.
    * @return A new resource that is relative to this resource.
    */
   Resource getChild(String relativePath);

   /**
    * <p> Lists all the resources that are directly under this resource. </p>
    *
    * @return A list of all the resources one level under this resource.
    */
   default List<Resource> getChildren() {
      return getChildren(ALL_FILE_PATTERN, false);
   }

   /**
    * <p> Lists all the resources that are directly under this resource. </p>
    *
    * @param recursive Gets all children recursively
    * @return A list of all the resources one level under this resource.
    */
   default List<Resource> getChildren(boolean recursive) {
      return getChildren(ALL_FILE_PATTERN, recursive);
   }

   /**
    * <p> Lists all the resources that are directly under this resource. </p>
    *
    * @param pattern The file matching pattern
    * @return A list of all the resources one level under this resource.
    */
   default List<Resource> getChildren(String pattern) {
      return getChildren(FileUtils.createFilePattern(pattern), false);
   }

   /**
    * <p> Lists all the resources that are directly under this resource. </p>
    *
    * @param pattern   The file matching pattern
    * @param recursive Gets all children recursively
    * @return A list of all the resources one level under this resource.
    */
   default List<Resource> getChildren(String pattern, boolean recursive) {
      return getChildren(FileUtils.createFilePattern(pattern), recursive);
   }

   /**
    * <p> Lists all the resources that are directly under this resource. </p>
    *
    * @param pattern   The file matching pattern
    * @param recursive Gets all children recursively
    * @return A list of all the resources one level under this resource.
    */
   default List<Resource> getChildren(Pattern pattern, boolean recursive) {
      return Collections.emptyList();
   }

   /**
    * Gets parent.
    *
    * @return The parent resource (directory for file, parent directory for a directory)
    */
   Resource getParent();

   /**
    * <p> Opens an input stream over this resource. </p>
    *
    * @return An input stream over this resource.
    * @throws IOException the io exception
    */
   InputStream inputStream() throws IOException;

   /**
    * Is compressed.
    *
    * @return True if the resources is gzipped compressed
    */
   boolean isCompressed();

   /**
    * Is directory.
    *
    * @return True if the resource is a directory
    */
   boolean isDirectory();

   /**
    * Lines m stream.
    *
    * @return the m stream
    * @throws IOException the io exception
    */
   default MStream<String> lines() throws IOException {
      return new LocalStream<>(new BufferedReader(reader()).lines());
   }


   /**
    * For each.
    *
    * @param consumer the consumer
    * @throws IOException the io exception
    */
   default void forEach(@NonNull SerializableConsumer<String> consumer) throws IOException {
      Preconditions.checkState(canRead(), "This is resource cannot be read from.");
      try (MStream<String> stream = lines()) {
         stream.forEach(consumer);
      } catch (IOException ioe) {
         throw ioe;
      } catch (Exception e) {
         throw new IOException(e);
      }
   }

   /**
    * Mkdir boolean.
    *
    * @return the boolean
    * @see java.io.File#mkdir() java.io.File#mkdir()java.io.File#mkdir()java.io.File#mkdir()
    */
   default boolean mkdir() {
      return false;
   }

   /**
    * Mkdirs boolean.
    *
    * @return the boolean
    * @see java.io.File#mkdirs() java.io.File#mkdirs()java.io.File#mkdirs()java.io.File#mkdirs()
    */
   default boolean mkdirs() {
      return false;
   }

   /**
    * Opens a writer for writing
    *
    * @return A writer
    * @throws IOException the io exception
    */
   default Writer writer() throws IOException {
      Preconditions.checkState(canWrite(), "This is resource cannot be written to.");
      return new OutputStreamWriter(outputStream(), getCharset());
   }

   /**
    * <p> Opens an output stream over this resource. </p>
    *
    * @return An output stream over this resource.
    * @throws IOException the io exception
    */
   OutputStream outputStream() throws IOException;

   /**
    * Gets path in the same mannar as {@link File#path}
    *
    * @return The full path to the resource including name
    */
   String path();

   /**
    * <p> Reads the resource into an array of bytes. </p>
    *
    * @return An array of bytes representing the content of the resource.
    * @throws IOException the io exception
    */
   default byte[] readBytes() throws IOException {
      Preconditions.checkState(canRead(), "This is resource cannot be read from.");
      try (ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
           BufferedInputStream byteReader = new BufferedInputStream(inputStream())) {
         int bytesRead;
         byte[] buffer = new byte[1024];
         while ((bytesRead = byteReader.read(buffer)) != -1) {
            byteWriter.write(buffer, 0, bytesRead);
         }
         return byteWriter.toByteArray();
      }
   }

   /**
    * Reads lines in the resource to a list of string
    *
    * @return A list of string representing the contents of the file.
    * @throws IOException the io exception
    */
   default List<String> readLines() throws IOException {
      Preconditions.checkState(canRead(), "This is resource cannot be read from.");
      try (MStream<String> stream = lines()) {
         return stream.collect();
      } catch (IOException e) {
         throw e;
      } catch (Exception e) {
         throw new IOException(e);
      }
   }

   /**
    * Deserializes an object from a resource
    *
    * @param <T> the type parameter
    * @return the t
    * @throws Exception the exception
    */
   default <T> T readObject() throws Exception {
      Preconditions.checkState(canRead(), "This is resource cannot be read from.");
      return Cast.as(new JavaSerializer().deserialize(this, Object.class));
   }

   /**
    * <p> Reads in the resource as a String using UTF-8. </p>
    *
    * @return A string representing the contents of the file.
    * @throws IOException the io exception
    */
   default String readToString() throws IOException {
      Preconditions.checkState(canRead(), "This is resource cannot be read from.");
      return new String(readBytes(), getCharset());
   }

   /**
    * Opens a reader using UTF-8 encoding
    *
    * @return A reader
    * @throws IOException the io exception
    */
   default Reader reader() throws IOException {
      Preconditions.checkState(canRead(), "This is resource cannot be read from.");
      return new CharsetDetectingReader(inputStream(), getCharset());
   }

   /**
    * Sets is compressed.
    *
    * @param isCompressed the is compressed
    * @return the is compressed
    */
   Resource setIsCompressed(boolean isCompressed);

   /**
    * Uncompressed resource.
    *
    * @return the resource
    */
   Resource uncompressed();

   /**
    * <p> Writes a byte array to the resource. </p>
    *
    * @param content The content to write.
    * @return the resource
    * @throws IOException the io exception
    */
   default Resource write(byte[] content) throws IOException {
      Preconditions.checkState(canWrite(), "This is resource cannot be written to.");
      if (content != null) {
         try (OutputStream os = outputStream()) {
            os.write(content);
         }
      }
      return this;
   }

   /**
    * <p> Writes a string to the resource using UTF-8. </p>
    *
    * @param content The content to write.
    * @return the resource
    * @throws IOException the io exception
    */
   default Resource write(String content) throws IOException {
      Preconditions.checkState(canWrite(), "This is resource cannot be written to.");
      if (content != null) {
         write(content.getBytes(getCharset()));
      }
      return this;
   }

   /**
    * Serializes an object to the resource
    *
    * @param object The object to serialize
    * @return the resource
    * @throws Exception the exception
    */
   default Resource writeObject(Object object) throws Exception {
      Preconditions.checkState(canWrite(), "This is resource cannot be written to.");
      new JavaSerializer().serialize(object, this);
      return this;
   }


}// END OF Resource
