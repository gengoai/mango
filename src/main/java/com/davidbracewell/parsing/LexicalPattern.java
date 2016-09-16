package com.davidbracewell.parsing;

import com.davidbracewell.Regex;
import com.davidbracewell.string.CharPredicate;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.davidbracewell.collection.list.Lists.list;
import static com.davidbracewell.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public abstract class LexicalPattern implements Serializable {
  public static final int NO_MATCH = -1;
  private static final long serialVersionUID = 1L;

  public static LexicalPattern stringLiteral(@NonNull String literal) {
    return new LiteralPattern(literal);
  }

  public static LexicalPattern charLiteral(@NonNull char literal) {
    return new CharLiteralPattern(CharPredicate.anyOf(Character.toString(literal)));
  }

  public static LexicalPattern charLiteral(@NonNull CharPredicate literal) {
    return new CharLiteralPattern(literal);
  }


  public static LexicalPattern charPredicate(@NonNull CharPredicate predicate) {
    return new CharPredicatePattern(predicate);
  }

  public static LexicalPattern regex(@NonNull Pattern pattern) {
    return new RegexPattern(pattern);
  }

  public static LexicalPattern regex(@NonNull String pattern) {
    return new RegexPattern(Pattern.compile(pattern));
  }

  public static LexicalPattern regex(@NonNull Regex pattern) {
    return new RegexPattern(pattern.toPattern());
  }

  public static void main(String[] args) {
    CharPredicate reserved = CharPredicate.anyOf("={}[],\\\"");
    List<Tuple2<String, LexicalPattern>> main = list(
      $("ASSIGNMENT", charLiteral('=')),
      $("BEGIN_ARRAY", charLiteral('[')),
      $("END_ARRAY", charLiteral(']')),
      $("BEGIN_MAP", charLiteral('{')),
      $("END_MAP", charLiteral('}')),
      $("COMMA", charLiteral(',')),
      $("QUOTED_VALUE", regex("\"(\"\"|[^\"])+\"")),
      $("VALUE", charPredicate(reserved.or(CharPredicate.anyOf("\r\n")).negate()))
    );


    String text = "key = \"value = 1\"\nkey = {value}\nblah = [\"1\",2,3,4,5]\n";
    for (int i = 0; i < text.length(); ) {
      int longest = -1;
      String longestType = "";
      for (Tuple2<String, LexicalPattern> entry : main) {
        int match = entry.v2.match(text, i);

        if (!entry.v1.equals("QUOTED_VALUE") && match > 1) {
          int r = reserved.indexIn(text, i);
          if (r >= 0 && r <= i + match) {
            match = r - i;
          }
        }

        if (match > longest) {
          longest = match;
          longestType = entry.v1;
        }
      }

      if (longest > 0) {
        System.out.println(longestType + " : " + text.substring(i, i + longest).trim().replaceAll("\\\\(.)", "$1"));
        i += longest;
      } else {
        i++;
      }
    }
  }

  public abstract int match(CharSequence sequence, int start);

  @Value
  private static class LiteralPattern extends LexicalPattern {
    private static final long serialVersionUID = 1L;
    private final String literal;

    private LiteralPattern(String literal) {
      this.literal = literal;
    }

    @Override
    public int match(@NonNull CharSequence sequence, int start) {
      Preconditions.checkPositionIndex(start, sequence.length());
      if (literal.length() > (sequence.length() - start)) {
        return NO_MATCH;
      }
      for (int i = 0; i < literal.length(); i++) {
        if (sequence.charAt(i + start) != literal.charAt(i)) {
          return NO_MATCH;
        }
      }
      return literal.length();
    }
  }

  @Value
  private static class CharLiteralPattern extends LexicalPattern {
    private static final long serialVersionUID = 1L;
    private final CharPredicate predicate;

    @Override
    public int match(@NonNull CharSequence sequence, int start) {
      Preconditions.checkPositionIndex(start, sequence.length());
      return predicate.matches(sequence.charAt(start)) ? 1 : NO_MATCH;
    }
  }

  @Value
  private static class CharPredicatePattern extends LexicalPattern {
    private static final long serialVersionUID = 1L;
    private final CharPredicate pattern;

    private CharPredicatePattern(CharPredicate pattern) {
      this.pattern = pattern;
    }

    @Override
    public int match(@NonNull CharSequence sequence, int start) {
      Preconditions.checkPositionIndex(start, sequence.length());
      int length = start;
      while (length < sequence.length() && pattern.matches(sequence.charAt(length))) {
        length++;
      }
      return length == start ? NO_MATCH : (length - start);
    }
  }

  @Value
  private static class RegexPattern extends LexicalPattern {
    private static final long serialVersionUID = 1L;
    private final Pattern pattern;

    @Override
    public int match(@NonNull CharSequence sequence, int start) {
      Preconditions.checkPositionIndex(start, sequence.length());
      Matcher m = pattern.matcher(sequence);
      if (m.find(start) && m.start() == start) {
        return m.group().length();
      }
      return NO_MATCH;
    }
  }

}// END OF LexicalPattern
