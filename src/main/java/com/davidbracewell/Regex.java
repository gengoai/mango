package com.davidbracewell;

import com.davidbracewell.string.StringUtils;
import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;
import java.util.regex.Pattern;

import static com.davidbracewell.Re.re;

/**
 * The type Regex.
 *
 * @author David B. Bracewell
 */
@Value
public final class Regex implements Serializable {
  private static final long serialVersionUID = 1L;
  private final String pattern;

  /**
   * Instantiates a new Regex.
   *
   * @param pattern the pattern
   * @param quote   the quote
   */
  public Regex(String pattern, boolean quote) {
    if (pattern != null) {
      if (quote) {
        this.pattern = Pattern.quote(pattern);
      } else {
        this.pattern = pattern;
      }
    } else {
      this.pattern = StringUtils.EMPTY;
    }

  }

  /**
   * To chars string.
   *
   * @param p the p
   *
   * @return the string
   */
  static String toChars(String p) {
    if (p.length() >= 3 && p.charAt(0) == '[' && p.charAt(p.length() - 1) == ']') {
      return p.substring(1, p.length() - 1);
    }
    return p;
  }

  /**
   * Followed by regex.
   *
   * @param regex the regex
   *
   * @return the regex
   */
  public Regex then(Regex regex) {
    if (regex == null) {
      return this;
    }
    return re(this.pattern + regex.pattern);
  }

  /**
   * End line regex.
   *
   * @return the regex
   */
  public Regex endLine() {
    return re(this.pattern + "$");
  }


  /**
   * Or regex.
   *
   * @param others the others
   *
   * @return the regex
   */
  public Regex or(@NonNull Regex... others) {
    if (others == null) {
      return this;
    }
    return Re.or(this, others);
  }

  /**
   * And regex.
   *
   * @param other the other
   *
   * @return the regex
   */
  public Regex and(@NonNull Regex other) {
    if (other.pattern.length() > 0) {
      return re(this.pattern + "&&" + other.pattern);
    }
    return this;
  }

  /**
   * Group regex.
   *
   * @param name the name
   *
   * @return the regex
   */
  public Regex group(String name) {
    return re("(" + (StringUtils.isNotNullOrBlank(name) ? "?<" + name + ">" : StringUtils.EMPTY) + pattern + ")");
  }

  /**
   * Non matching group regex.
   *
   * @return the regex
   */
  public Regex nmGroup() {
    return re("(?:" + pattern + ")");
  }

  /**
   * Group regex.
   *
   * @return the regex
   */
  public Regex group() {
    return re("(" + this.pattern + ")");
  }

  /**
   * Not regex.
   *
   * @return the regex
   */
  public Regex not() {
    if (this.pattern.length() > 0) {
      if (this.pattern.charAt(0) == '[' && this.pattern.length() > 1) {
        return re("[^" + this.pattern.substring(1));
      }
      return re("^" + this.pattern);
    }
    return this;
  }

  /**
   * N times regex.
   *
   * @param n the n
   *
   * @return the regex
   */
  public Regex nTimes(int n) {
    return re(this.pattern + "{" + Integer.toString(n) + "}");
  }

  /**
   * Range regex.
   *
   * @param min the min
   * @param max the max
   *
   * @return the regex
   */
  public Regex range(int min, int max) {
    return re(this.pattern + "{" + Integer.toString(min) + "," + Integer.toString(max) + "}");
  }

  /**
   * Greedy regex.
   *
   * @return the regex
   */
  public Regex reluctant() {
    return re(this.pattern + "?");
  }

  /**
   * One or more regex.
   *
   * @return the regex
   */
  public Regex plus() {
    return re(this.pattern + "+");
  }

  /**
   * Zero or more regex.
   *
   * @return the regex
   */
  public Regex star() {
    return re(this.pattern + "*");
  }


  /**
   * Zero or one regex.
   *
   * @return the regex
   */
  public Regex question() {
    return re(this.pattern + "?");
  }


  /**
   * To pattern pattern.
   *
   * @return the pattern
   */
  public Pattern toPattern() {
    return Pattern.compile(pattern);
  }


  /**
   * To pattern pattern.
   *
   * @param flags the flags
   *
   * @return the pattern
   */
  public Pattern toPattern(int flags) {
    return Pattern.compile(pattern, flags);
  }

  @Override
  public String toString() {
    return pattern;
  }

  /**
   * Match line regex.
   *
   * @return the regex
   */
  public Regex matchLine() {
    return re("^" + this.pattern + "$");
  }

  /**
   * Chars regex.
   *
   * @return the regex
   */
  public Regex chars() {
    return Re.chars(this);
  }


}// END OF Regex

