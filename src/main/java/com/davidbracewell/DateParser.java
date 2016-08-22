package com.davidbracewell;

import lombok.NonNull;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

/**
 * <p>Attempts to parse a date from a string using a number of different formats.</p>
 *
 * @author David B. Bracewell
 */
public final class DateParser implements Serializable {
  /**
   * ISO_8601 date format.
   */
  public static final DateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd");
  /**
   * Standard format for dates used in the United States
   */
  public static final DateFormat US_STANDARD = new SimpleDateFormat("MM/dd/yyyy");

  private static final long serialVersionUID = 1L;
  private final DateFormat[] formats;

  /**
   * Instantiates a new Date parser using the default locale.
   */
  public DateParser() {
    this(Locale.getDefault());
  }

  /**
   * Instantiates a new Date parser.
   *
   * @param locale the locale to focus the parser on
   */
  public DateParser(@NonNull Locale locale) {
    formats = new DateFormat[]{
      SimpleDateFormat.getDateTimeInstance(),
      DateFormat.getDateInstance(DateFormat.SHORT, locale),
      DateFormat.getDateInstance(DateFormat.MEDIUM, locale),
      DateFormat.getDateInstance(DateFormat.LONG, locale),
      DateFormat.getDateInstance(DateFormat.FULL, locale),
      ISO_8601,
      US_STANDARD
    };
  }


  /**
   * <p>Attempt to parse the date represented in the input string.</p>
   *
   * @param input the input string representing the date
   * @return the parsed date
   * @throws ParseException Couldn't parse the string into a valid date
   */
  public Date parse(String input) throws ParseException {
    Optional<Date> date = parseQuietly(input);
    if (date.isPresent()) {
      return date.get();
    }
    throw new ParseException(input, 0);
  }

  /**
   * <p>Attempt to parse the date represented in the input string suppressing an errors.</p>
   *
   * @param input the input string representing the date
   * @return An optional representing the parsed date or null if not parsable.
   */
  public Optional<Date> parseQuietly(String input) {
    if (input == null) {
      return Optional.empty();
    }
    for (DateFormat format : formats) {
      try {
        return Optional.of(format.parse(input));
      } catch (Exception ignored) {
      }
    }
    return Optional.empty();
  }

  /**
   * <p>Attempt to parse the date represented in the input string suppressing an errors.</p>
   *
   * @param input       the input string representing the date
   * @param defaultDate the default date
   * @return The parsed date or the default if the input was not parsable.
   */
  public Date parseOrDefault(String input, @NonNull Date defaultDate) {
    return parseQuietly(input).orElse(defaultDate);
  }


}// END OF DateParser
