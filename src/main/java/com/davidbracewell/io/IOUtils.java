package com.davidbracewell.io;

import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.davidbracewell.collection.Streams.asStream;

/**
 * The interface Io utils.
 *
 * @author David B. Bracewell
 */
public interface IOUtils {

  /**
   * Read lines list.
   *
   * @param stream  the stream
   * @param charset the charset
   * @return the list
   * @throws IOException the io exception
   */
  static List<String> readLines(@NonNull InputStream stream, @NonNull Charset charset) throws IOException {
    try (Stream<String> s = asStream(new InputStreamReader(stream, charset))) {
      return s.collect(Collectors.toList());
    }
  }

  /**
   * Read lines list.
   *
   * @param stream the stream
   * @return the list
   * @throws IOException the io exception
   */
  static List<String> readLines(@NonNull InputStream stream) throws IOException {
    return readLines(stream, StandardCharsets.UTF_8);
  }

  /**
   * Read lines list.
   *
   * @param reader the reader
   * @return the list
   * @throws IOException the io exception
   */
  static List<String> readLines(@NonNull Reader reader) throws IOException {
    try (Stream<String> s = asStream(reader)) {
      return s.collect(Collectors.toList());
    }
  }

  /**
   * Read to string string.
   *
   * @param stream  the stream
   * @param charset the charset
   * @return the string
   * @throws IOException the io exception
   */
  static String readToString(@NonNull InputStream stream, @NonNull Charset charset) throws IOException {
    try (Stream<String> s = asStream(new InputStreamReader(stream, charset))) {
      return s.collect(Collectors.joining("\n"));
    }
  }

  /**
   * Read to string string.
   *
   * @param stream the stream
   * @return the string
   * @throws IOException the io exception
   */
  static String readToString(@NonNull InputStream stream) throws IOException {
    return readToString(stream, StandardCharsets.UTF_8);
  }

  /**
   * Read to string string.
   *
   * @param reader the reader
   * @return the string
   * @throws IOException the io exception
   */
  static String readToString(@NonNull Reader reader) throws IOException {
    try (Stream<String> s = asStream(reader)) {
      return s.collect(Collectors.joining("\n"));
    }
  }


}//END OF IOUtils
