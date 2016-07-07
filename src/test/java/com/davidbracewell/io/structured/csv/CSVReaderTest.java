package com.davidbracewell.io.structured.csv;

import com.davidbracewell.io.CSV;
import com.davidbracewell.io.Resources;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredReader;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * @author David B. Bracewell
 */
public class CSVReaderTest {

  @Test
  public void processMapTest() throws IOException {
    List<String> names = CSV.builder()
      .hasHeader()
      .reader(Resources.fromClasspath("com/davidbracewell/Schools.csv"))
      .processMaps(m -> Optional.of(m.get("schoolName").asString()));
    assertEquals(names.get(0), "Abilene Christian University");
    assertEquals(names.get(2), "Adrian College");
    assertEquals(names.get(4), "University of Akron");
    assertEquals(names.get(names.size() - 1), "Youngstown State University");
  }

  @Test
  public void consumeMapTest() throws IOException {
    List<String> names = new ArrayList<>();
    CSV.builder()
      .hasHeader()
      .reader(Resources.fromClasspath("com/davidbracewell/Schools.csv"))
      .consumeMaps(m -> names.add(m.get("schoolName").asString()));
    assertEquals(names.get(0), "Abilene Christian University");
    assertEquals(names.get(2), "Adrian College");
    assertEquals(names.get(4), "University of Akron");
    assertEquals(names.get(names.size() - 1), "Youngstown State University");
  }


  @Test
  public void processRowTest() throws IOException {
    List<String> names = CSV.builder()
      .hasHeader()
      .reader(Resources.fromClasspath("com/davidbracewell/Schools.csv"))
      .processRows(m -> Optional.of(m.get(1)));
    assertEquals(names.get(0), "Abilene Christian University");
    assertEquals(names.get(2), "Adrian College");
    assertEquals(names.get(4), "University of Akron");
    assertEquals(names.get(names.size() - 1), "Youngstown State University");
  }

  @Test
  public void headerTest() throws IOException {
    CSVReader reader = CSV.builder()
      .hasHeader()
      .reader(Resources.fromClasspath("com/davidbracewell/Schools.csv"));
    assertEquals(
      Arrays.asList("schoolID", "schoolName", "schoolCity", "schoolState", "schoolNick"),
      reader.getHeader()
    );
    reader.close();
  }

  @Test
  public void streamingTest() throws IOException {
    StructuredReader reader = CSV.builder()
      .hasHeader()
      .reader(Resources.fromClasspath("com/davidbracewell/Schools.csv"));
    reader.beginDocument();
    while (reader.peek() != ElementType.END_DOCUMENT) {
      reader.nextArray();
    }
    reader.endDocument();
    reader.close();
  }

  @Test
  public void readAllTest() throws IOException {
    List<List<String>> rows = CSV.builder()
      .hasHeader()
      .reader(Resources.fromClasspath("com/davidbracewell/Schools.csv"))
      .readAll();
    assertEquals(rows.get(0).get(1), "Abilene Christian University");
    assertEquals(rows.get(2).get(1), "Adrian College");
    assertEquals(rows.get(4).get(1), "University of Akron");
    assertEquals(rows.get(rows.size() - 1).get(1), "Youngstown State University");
  }

  @Test
  public void readMessy() throws IOException {
    List<List<String>> rows = CSV.builder()
      .reader(Resources.fromClasspath("com/davidbracewell/messy.csv"))
      .readAll();
    assertEquals(2, rows.size());
    assertEquals("row 1\n" +
      "is great", rows.get(0).get(0));
    assertEquals("row\"row", rows.get(0).get(2));
    assertEquals("#", rows.get(1).get(1));
  }

}