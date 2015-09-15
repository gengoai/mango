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

import com.davidbracewell.SystemInfo;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.CharsetDetectingReader;
import com.davidbracewell.io.QuietIO;
import com.davidbracewell.io.serialization.JavaSerializer;
import com.davidbracewell.logging.Logger;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.LineProcessor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p> Information about a resource, which abstracts away the specific details on working with the resource. Gives the
 * ability to open an <code>InputStream</code> and <code>OutputStream</code> as well as manipulate the resource.
 * Manipulation is implementation specific. </p>
 *
 * @author David Bracewell
 */
public abstract class Resource implements Iterable<String>, Serializable {

  private static final Pattern ALL_FILE_PATTERN = Pattern.compile(".*");
  private static final Logger log = Logger.getLogger(Resource.class);
  private static final long serialVersionUID = 1L;
  private Charset charset = null;
  private boolean isCompressed;


  /**
   * <p>Gets the charset for reading and writing. The charset if not specified will be automatically determined during
   * read.</p>
   *
   * @return The charset used for writing and default when reading
   */
  public final Charset getCharset() {
    return charset;
  }

  /**
   * <p>Sets the charset for reading and writing.</p>
   *
   * @param charset The charset to use
   */
  public final void setCharset(Charset charset) {
    this.charset = charset;
  }

  private Charset determineCharset() {
    if (charset == null) {
      charset = Charsets.UTF_8;
    }
    return charset;
  }

  /**
   * <p> Appends content to this resource. </p>
   *
   * @param content The content to append
   * @throws java.io.IOException Something went wrong appending the content.
   */
  public void append(String content) throws IOException {
    append(Preconditions.checkNotNull(content).getBytes(determineCharset()));
  }

  /**
   * <p> Appends content to this resource. </p>
   *
   * @param byteArray The content to append
   * @throws java.io.IOException Something went wrong appending the content.
   */
  public void append(byte[] byteArray) throws IOException {
    throw new UnsupportedEncodingException();
  }

  /**
   * Opens a writer for writing
   *
   * @return A writer
   * @throws java.io.IOException Something went wrong opening the writer
   */
  public Writer openWriter() throws IOException {
    return new OutputStreamWriter(openOutputStream(), determineCharset());
  }

  /**
   * Gets the resource as a <code>File</code>.
   *
   * @return A <code>File</code> representing the resource.
   */
  public File asFile() {
    return null;
  }

  /**
   * Gets the resource as a <code>URL</code>.
   *
   * @return A <code>URL</code> representing the resource.
   * @throws java.net.MalformedURLException the malformed uRL exception
   */
  public URL asURL() throws MalformedURLException {
    return null;
  }

  /**
   * Gets path in the same mannar as {@link File#path}
   *
   * @return The full path to the resource including name
   */
  public String path() {
    return null;
  }

  /**
   * Gets the name (file name or directory name) of this resource.
   *
   * @return The name of the file or directory
   */
  public String baseName() {
    return null;
  }

  /**
   * @return The string representation of the resource with protocol
   */
  public abstract String resourceDescriptor();


  /**
   * Is directory.
   *
   * @return True if the resource is a directory
   */
  public boolean isDirectory() {
    return false;
  }

  /**
   * Can write.
   *
   * @return True if can write to the resource
   */
  public boolean canWrite() {
    return false;
  }

  /**
   * Can read.
   *
   * @return True if can read from the resource
   */
  public boolean canRead() {
    return false;
  }

  /**
   * <p> Creates a new Resource that is relative to this resource. </p>
   *
   * @param relativePath The relative path for the new resource.
   * @return A new resource that is relative to this resource.
   */
  public Resource getChild(String relativePath) {
    throw new UnsupportedOperationException();
  }

  /**
   * Exists boolean.
   *
   * @return True if the resource exists, False if the resource does not exist.
   */
  public abstract boolean exists();

  /**
   * <p> Lists all the resources that are directly under this resource. </p>
   *
   * @return A list of all the resources one level under this resource.
   */
  public final List<Resource> getChildren() {
    return getChildren(ALL_FILE_PATTERN, false);
  }

  /**
   * <p> Lists all the resources that are directly under this resource. </p>
   *
   * @param recursive Gets all children recursively
   * @return A list of all the resources one level under this resource.
   */
  public final List<Resource> getChildren(boolean recursive) {
    return getChildren(ALL_FILE_PATTERN, recursive);
  }

  private Pattern convertToRegex(String filePattern) {
    filePattern = Strings.isNullOrEmpty(filePattern) ? "\\*" : filePattern;
    filePattern = filePattern.replaceAll("\\.", "\\.");
    filePattern = filePattern.replaceAll("\\*", ".*");
    return Pattern.compile("^" + filePattern + "$");
  }

  /**
   * Iterator over the children
   *
   * @param pattern   The pattern to determine what files we want
   * @param recursive should we iterator recursively?
   * @return An iterator over the children
   */
  public final Iterator<Resource> childIterator(String pattern, boolean recursive) {
    Preconditions.checkArgument(Strings.isNullOrEmpty(pattern), "Pattern is invalid.");
    return new ChildIterator(this, convertToRegex(pattern), recursive);
  }

  /**
   * Iterator over the children
   *
   * @param recursive should we iterator recursively?
   * @return An iterator over the children
   */
  public final Iterator<Resource> childIterator(boolean recursive) {
    return new ChildIterator(this, ALL_FILE_PATTERN, recursive);
  }

  /**
   * <p> Lists all the resources that are directly under this resource. </p>
   *
   * @param pattern The file matching pattern
   * @return A list of all the resources one level under this resource.
   */
  public final List<Resource> getChildren(String pattern) {
    return getChildren(convertToRegex(pattern), false);
  }

  /**
   * <p> Lists all the resources that are directly under this resource. </p>
   *
   * @param pattern   The file matching pattern
   * @param recursive Gets all children recursively
   * @return A list of all the resources one level under this resource.
   */
  public final List<Resource> getChildren(String pattern, boolean recursive) {
    return getChildren(convertToRegex(pattern), recursive);
  }

  /**
   * <p> Lists all the resources that are directly under this resource. </p>
   *
   * @param pattern   The file matching pattern
   * @param recursive Gets all children recursively
   * @return A list of all the resources one level under this resource.
   */
  protected List<Resource> getChildren(Pattern pattern, boolean recursive) {
    return Collections.emptyList();
  }

  /**
   * Gets parent.
   *
   * @return The parent resource (directory for file, parent directory for a directory)
   */
  public Resource getParent() {
    return null;
  }

  /**
   * <p> Opens an input stream over this resource. </p>
   *
   * @return An input stream over this resource.
   * @throws java.io.IOException An error happened when obtaining the input stream
   */
  public final InputStream openInputStream() throws IOException {
    Preconditions.checkState(exists(), resourceDescriptor() + " does not exist.");
    PushbackInputStream is = new PushbackInputStream(createInputStream(), 2);
    if (isCompressed || isCompressed(is)) {
      return new GZIPInputStream(is);
    }
    return is;
  }

  /**
   * <p> Opens an input stream over this resource. </p>
   *
   * @return An input stream over this resource.
   * @throws java.io.IOException An error happened when obtaining the input stream
   */
  protected InputStream createInputStream() throws IOException {
    throw new UnsupportedOperationException();
  }


  protected boolean isCompressed(PushbackInputStream pushbackInputStream) throws IOException {
    if (pushbackInputStream == null) {
      return false;
    }
    byte[] buffer = new byte[2];
    int read = pushbackInputStream.read(buffer);
    boolean isCompressed = false;

    if (read == 2) {
      isCompressed = ((buffer[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (buffer[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8)));
    }

    if (read != -1) {
      pushbackInputStream.unread(buffer, 0, read);
    }

    return isCompressed;
  }


  /**
   * Opens a reader using UTF-8 encoding
   *
   * @return A reader
   * @throws java.io.IOException Something went wrong opening the reader
   */
  public Reader openReader() throws IOException {
    CharsetDetectingReader reader = new CharsetDetectingReader(openInputStream(), charset);
    charset = reader.getCharset();
    return reader;
  }

  /**
   * <p> Opens an output stream over this resource. </p>
   *
   * @return An output stream over this resource.
   * @throws java.io.IOException An error happened when obtaining the output stream
   */
  public final OutputStream openOutputStream() throws IOException {
    if (isCompressed) {
      return new GZIPOutputStream(createOutputStream());
    }
    return createOutputStream();
  }

  /**
   * <p> Creates an output stream over this resource. </p>
   *
   * @return An output stream over this resource.
   * @throws java.io.IOException An error happened when obtaining the output stream
   */
  protected OutputStream createOutputStream() throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * <p> Reads the resource into an array of bytes. </p>
   *
   * @return An array of bytes representing the content of the resource.
   * @throws java.io.IOException Something went wrong reading the resource.
   */
  public final byte[] readBytes() throws IOException {
    try (ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
         BufferedInputStream byteReader = new BufferedInputStream(openInputStream())) {
      int bytesRead;
      byte[] buffer = new byte[1024];
      while ((bytesRead = byteReader.read(buffer)) != -1) {
        byteWriter.write(buffer, 0, bytesRead);
      }
      return byteWriter.toByteArray();
    }
  }

  /**
   * <p> Reads in the resource as a String using UTF-8. </p>
   *
   * @return A string representing the contents of the file.
   * @throws java.io.IOException An error happened when reading the file.
   */
  public final String readToString() throws IOException {
    return read(new ReadLineToString());
  }

  /**
   * Reads lines in the resource to a list of string
   *
   * @return A list of string representing the contents of the file.
   * @throws java.io.IOException An error happened when reading the file.
   */
  public final List<String> readLines() throws IOException {
    return read(new ReadLineToStringList());
  }

  /**
   * <p> Reads in the resource line by line passing each line through the given procedure. </p>
   *
   * @param processor A <code>LineProcessor</code> to apply to each line in the resource.
   * @return the t
   * @throws java.io.IOException An error happened when reading the file.
   */
  public final <T> T read(LineProcessor<T> processor) throws IOException {
    if (!exists()) {
      throw new IOException(resourceDescriptor() + " does not exist!");
    }
    try (BufferedReader bufferedReader = new BufferedReader(openReader())) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        if (!processor.processLine(line)) {
          break;
        }
      }
    }
    return processor.getResult();
  }

  /**
   * Deletes the resource
   *
   * @return true if the deletion was successful
   */
  public final boolean delete() {
    return delete(false);
  }

  /**
   * Deletes the resource
   *
   * @param recursively true if should recursively delete everything under this resource
   * @return true if the deletion was successful
   */
  public boolean delete(boolean recursively) {
    return false;
  }

  /**
   * Deletes the resource on ext
   */
  public void deleteOnExit() {

  }

  /**
   * Serializes an object to the resource
   *
   * @param object The object to serialize
   * @throws java.io.IOException An error happened writing the object
   */
  public void writeObject(Object object) throws Exception {
    new JavaSerializer().serialize(object, this);
  }

  /**
   * Deserializes an object from a resource
   *
   * @return the t
   * @throws java.io.IOException An error happened reading the object
   */
  public <T> T readObject() throws Exception {
    return Cast.as(new JavaSerializer().deserialize(this, Object.class));
  }

  /**
   * <p> Writes a byte array to the resource. </p>
   *
   * @param content The content to write.
   * @throws java.io.IOException Something went wrong writing to the file
   */
  public void write(byte[] content) throws IOException {
    Preconditions.checkNotNull(content);
    try (OutputStream os = openOutputStream()) {
      os.write(content);
    }
  }

  /**
   * <p> Writes a string to the resource using UTF-8. </p>
   *
   * @param content The content to write.
   * @throws java.io.IOException Something went wrong writing to the file
   */
  public final void write(String content) throws IOException {
    write(Preconditions.checkNotNull(content).getBytes(determineCharset()));
  }

  /**
   * Mkdirs boolean.
   *
   * @return the boolean
   * @see java.io.File#mkdirs()
   */
  public boolean mkdirs() {
    return false;
  }

  /**
   * Mkdir boolean.
   *
   * @return the boolean
   * @see java.io.File#mkdir()
   */
  public boolean mkdir() {
    return false;
  }

  @Override
  public Iterator<String> iterator() {
    return new ResourceIterator();
  }


  /**
   * Is compressed.
   *
   * @return True if the resources is gzipped compressed
   */
  public boolean isCompressed() {
    return isCompressed;
  }

  /**
   * Sets is compressed.
   *
   * @param isCompressed True if the resources is gzipped compressed
   */
  public final void setIsCompressed(boolean isCompressed) {
    this.isCompressed = isCompressed;
  }

  @Override
  public String toString() {
    return resourceDescriptor();
  }

  private static class ChildIterator implements Iterator<Resource> {

    private final Pattern filePattern;
    private final Queue<Resource> queue = Lists.newLinkedList();
    private final boolean recursive;

    /**
     * Instantiates a new Child iterator.
     *
     * @param startingPoint the starting point
     * @param filePattern   the file pattern
     * @param recursive     the recursive
     */
    public ChildIterator(Resource startingPoint, Pattern filePattern, boolean recursive) {
      this.filePattern = filePattern;
      queue.addAll(startingPoint.getChildren(filePattern, false));
      this.recursive = recursive;
      advance();
    }

    private void advance() {
      if (queue.isEmpty()) {
        return;
      }
      if (queue.peek().isDirectory()) {
        if (recursive) {
          queue.addAll(queue.peek().getChildren(filePattern, false));
        }
      }
    }

    @Override
    public boolean hasNext() {
      return !queue.isEmpty();
    }

    @Override
    public Resource next() {
      if (queue.isEmpty()) {
        throw new NoSuchElementException();
      }
      Resource next = queue.remove();
      advance();
      return next;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * A LineProcessor that reads every line into a String
   */
  protected static class ReadLineToString implements LineProcessor<String> {

    private final StringBuilder sb = new StringBuilder();

    public boolean processLine(String element) {
      sb.append(element).append(SystemInfo.LINE_SEPARATOR);
      return true;
    }

    public String getResult() {
      return sb.toString();
    }


  }//END OF Resource$ReadLineToString

  /**
   * A LineProcessor that reads every line into a List of String
   */
  protected static class ReadLineToStringList implements LineProcessor<List<String>> {

    private final List<String> list = new ArrayList<>();

    public List<String> getResult() {
      return list;
    }

    public boolean processLine(String element) {
      list.add(element);
      return true;
    }

  }//END OF Resource$ReadLineToStringList

  private class ResourceIterator implements Iterator<String> {

    String line;
    BufferedReader reader;

    /**
     * Instantiates a new Resource iterator.
     */
    public ResourceIterator() {
      this.line = null;
      try {
        this.reader = new BufferedReader(openReader());
      } catch (IOException e) {
        log.severe(e);
        QuietIO.closeQuietly(reader);
      }
      readLine();
    }

    void readLine() {
      try {
        line = reader.readLine();
      } catch (IOException e) {
        log.severe(e);
        line = null;
      }
      if (line == null) {
        QuietIO.closeQuietly(reader);
      }
    }

    @Override
    public boolean hasNext() {
      return line != null;
    }

    @Override
    public String next() {
      String next = line;
      readLine();
      return next;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

}// END OF Resource
