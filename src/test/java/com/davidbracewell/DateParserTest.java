package com.davidbracewell;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author David B. Bracewell
 */
public class DateParserTest {


  Date iday = new GregorianCalendar.Builder()
    .set(Calendar.YEAR, 1776)
    .set(Calendar.MONTH, 6)
    .set(Calendar.DAY_OF_MONTH, 4)
    .build().getTime();


  @Test
  public void parse() throws Exception {
    DateParser en = new DateParser(Locale.US);
    assertEquals(iday, en.parse("07/04/1776"));
    assertEquals(iday, en.parse("July 4, 1776"));
    assertEquals(iday, en.parse("1776-07-04"));
    assertEquals(iday, en.parse("Thursday, July 4, 1776"));
  }

  @Test
  public void parseQuietly() throws Exception {
    DateParser en = new DateParser(Locale.US);
    assertFalse(en.parseQuietly("Thu Jul 04 00:00:00 CST 1776").isPresent());
  }

  @Test
  public void parseOrDefault() throws Exception {
    DateParser en = new DateParser(Locale.US);
    assertEquals(iday, en.parseOrDefault("Thu Jul 04 00:00:00 CST 1776", iday));
  }

}