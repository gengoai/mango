package com.davidbracewell;

import com.davidbracewell.string.StringUtils;
import lombok.NonNull;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * The type Regex.
 *
 * @author David B. Bracewell
 */
public final class Regex implements Serializable {

  public static final Regex QUOTE = re("\"");


  /**
   * The constant ANY.
   */
  public static final Regex ANY = re(".");

  /**
   * The constant WHITESPACE.
   */
  public static final Regex WHITESPACE = re("\\p{Z}\t\n\r\f");
  /**
   * The constant LETTER.
   */
  public static final Regex LETTER = re("\\p{L}");
  /**
   * The constant LOWERCASE_LETTER.
   */
  public static final Regex LOWERCASE_LETTER = re("\\p{Ll}");
  /**
   * The constant UPPERCASE_LETTER.
   */
  public static final Regex UPPERCASE_LETTER = re("\\p{Lu}");
  /**
   * The constant MARK.
   */
  public static final Regex MARK = re("\\p{m}");
  /**
   * The constant SYMBOL.
   */
  public static final Regex SYMBOL = re("\\p{S}");
  /**
   * The constant MATH_SYMBOL.
   */
  public static final Regex MATH_SYMBOL = re("\\p{Sm}");
  /**
   * The constant CURRENCY_SYMBOL.
   */
  public static final Regex CURRENCY_SYMBOL = re("\\p{Sc}");
  /**
   * The constant MODIFIER_SYMBOL.
   */
  public static final Regex MODIFIER_SYMBOL = re("\\p{Sk}");
  /**
   * The constant OTHER_SYMBOL.
   */
  public static final Regex OTHER_SYMBOL = re("\\p{So}");
  /**
   * The constant NUMBER.
   */
  public static final Regex NUMBER = re("\\p{N}");
  /**
   * The constant DIGIT.
   */
  public static final Regex DIGIT = re("\\p{Nd}");
  /**
   * The constant PUNCTUATION.
   */
  public static final Regex PUNCTUATION = re("\\p{P}");
  /**
   * The constant DASH_PUNCTUATION.
   */
  public static final Regex DASH_PUNCTUATION = re("\\p{Pd}");
  /**
   * The constant OPEN_PUNCTUATION.
   */
  public static final Regex OPEN_PUNCTUATION = re("\\p{Ps}");
  /**
   * The constant CLOSE_PUNCTUATION.
   */
  public static final Regex CLOSE_PUNCTUATION = re("\\p{Pe}");
  /**
   * The constant INITIAL_PUNCTUATION.
   */
  public static final Regex INITIAL_PUNCTUATION = re("\\p{Pi}");
  /**
   * The constant FINAL_PUNCTUATION.
   */
  public static final Regex FINAL_PUNCTUATION = re("\\p{Pf}");
  /**
   * The constant CONNECTOR_PUNCTUATION.
   */
  public static final Regex CONNECTOR_PUNCTUATION = re("\\p{Pc}");

  /**
   * The constant BACKSLASH.
   */
  public static final Regex BACKSLASH = re("\\");

  /**
   * The constant DOUBLE_BACKSLASH.
   */
  public static final Regex DOUBLE_BACKSLASH = re("\\\\");

  /**
   * The constant WORD_BOUNDRY.
   */
  public static final Regex WORD_BOUNDRY = re("\b");

  /**
   * The constant NON_WORD_BOUNDRY.
   */
  public static final Regex NON_WORD_BOUNDRY = re("\\B");

  private static final long serialVersionUID = 1L;
  private String pattern = StringUtils.EMPTY;

  private Regex(String pattern, boolean quote) {
    if (pattern != null) {
      if (quote) {
        this.pattern = Pattern.quote(pattern);
      } else {
        this.pattern = pattern;
      }
    }
  }

  /**
   * Pos look ahead regex.
   *
   * @param regex the regex
   * @return the regex
   */
  public static Regex posLookAhead(@NonNull Regex regex) {
    return re("(?=" + regex.pattern + ")");
  }

  /**
   * Neg look ahead regex.
   *
   * @param regex the regex
   * @return the regex
   */
  public static Regex negLookAhead(@NonNull Regex regex) {
    return re("(?!" + regex.pattern + ")");
  }

  /**
   * Pos look behind regex.
   *
   * @param regex the regex
   * @return the regex
   */
  public static Regex posLookBehind(@NonNull Regex regex) {
    return re("(?<=" + regex.pattern + ")");
  }

  /**
   * Neg look behind regex.
   *
   * @param regex the regex
   * @return the regex
   */
  public static Regex negLookBehind(@NonNull Regex regex) {
    return re("(?<!" + regex.pattern + ")");
  }


  private static String toChars(String p) {
    if (p.length() >= 3 && p.charAt(0) == '[' && p.charAt(p.length() - 1) == ']') {
      return p.substring(1, p.length() - 1);
    }
    return p;
  }


  /**
   * Chars regex.
   *
   * @param first  the first
   * @param others the others
   * @return the regex
   */
  public static Regex chars(@NonNull Regex first, Regex... others) {
    StringBuilder p = new StringBuilder(toChars(first.pattern));
    if (others != null) {
      for (Regex other : others) {
        if (other != null) {
          p.append(toChars(other.pattern));
        }
      }
    }
    return chars(p.toString());
  }


  /**
   * Chars regex.
   *
   * @param chars the chars
   * @return the regex
   */
  public static Regex chars(String chars) {
    return chars(chars, false);
  }

  /**
   * Chars regex.
   *
   * @param chars  the chars
   * @param negate the negate
   * @return the regex
   */
  public static Regex chars(String chars, boolean negate) {
    if (chars != null) {
      return new Regex("[" + (negate ? "^" : "") + chars + "]", false);
    }
    return new Regex(null, false);
  }

  /**
   * Re regex.
   *
   * @param pattern the pattern
   * @return the regex
   */
  public static Regex re(String pattern) {
    return new Regex(pattern, false);
  }

  /**
   * Begin line regex.
   *
   * @param regex the regex
   * @return the regex
   */
  public static Regex beginLine(Regex regex) {
    if (regex == null) {
      return re("^");
    }
    return re("^" + regex.pattern);
  }


  /**
   * Quote regex.
   *
   * @param word the word
   * @return the regex
   */
  public static Regex quote(String word) {
    return new Regex(word, true);
  }

  /**
   * Or regex.
   *
   * @param first   the first
   * @param regexes the regexes
   * @return the regex
   */
  public static Regex or(@NonNull Regex first, Regex... regexes) {
    StringBuilder pattern = new StringBuilder(first.pattern);
    if (regexes != null) {
      for (Regex rp : regexes) {
        if (rp != null) {
          pattern.append("|").append(rp.pattern);
        }
      }
    }
    return re(pattern.toString());
  }

  /**
   * And regex.
   *
   * @param first   the first
   * @param regexes the regexes
   * @return the regex
   */
  public static Regex and(@NonNull Regex first, Regex... regexes) {
    StringBuilder pattern = new StringBuilder(first.pattern);
    if (regexes != null) {
      for (Regex rp : regexes) {
        if (rp != null) {
          pattern.append("&&").append(rp.pattern);
        }
      }
    }
    return re(pattern.toString());
  }

  /**
   * Group regex.
   *
   * @param first   the first
   * @param regexes the regexes
   * @return the regex
   */
  public static Regex group(@NonNull Regex first, Regex... regexes) {
    return group(null, first, regexes);
  }

  /**
   * Non matching group regex.
   *
   * @param first   the first
   * @param regexes the regexes
   * @return the regex
   */
  public static Regex nmGroup(@NonNull Regex first, Regex... regexes) {
    String pattern = first.pattern;
    if (regexes != null) {
      for (Regex rp : regexes) {
        if (rp != null) {
          pattern += rp.pattern;
        }
      }
    }
    return re("(?:" + pattern + ")");
  }


  /**
   * Group regex.
   *
   * @param name    the name
   * @param first   the first
   * @param regexes the regexes
   * @return the regex
   */
  public static Regex group(String name, @NonNull Regex first, Regex... regexes) {
    String pattern = first.pattern;
    if (regexes != null) {
      for (Regex rp : regexes) {
        if (rp != null) {
          pattern += rp.pattern;
        }
      }
    }
    return re("(" + (StringUtils.isNotNullOrBlank(name) ? "?<" + name + ">" : StringUtils.EMPTY) + pattern + ")");
  }

  /**
   * Seq regex.
   *
   * @param first   the first
   * @param regexes the regexes
   * @return the regex
   */
  public static Regex seq(@NonNull Regex first, Regex... regexes) {
    StringBuilder pattern = new StringBuilder(first.toString());
    if (regexes != null) {
      for (Regex rp : regexes) {
        if (rp != null) {
          pattern.append(rp.pattern);
        }
      }
    }
    return re(pattern.toString());
  }

  /**
   * Followed by regex.
   *
   * @param regex the regex
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
   * @param other the other
   * @return the regex
   */
  public Regex or(@NonNull Regex other) {
    if (other.pattern.length() > 0) {
      return re(this.pattern + "|" + other.pattern);
    }
    return this;
  }

  /**
   * And regex.
   *
   * @param other the other
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

  public static Regex not(Regex regex){
    return regex.not();
  }


  /**
   * N times regex.
   *
   * @param n the n
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
    return chars(this);
  }


}// END OF Regex

