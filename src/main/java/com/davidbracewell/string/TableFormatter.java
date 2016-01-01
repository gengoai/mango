package com.davidbracewell.string;

import com.davidbracewell.conversion.Cast;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Table formatter.
 *
 * @author David B. Bracewell
 */
public class TableFormatter {
  /**
   * The Number formatter.
   */
  final DecimalFormat longNumberFormatter = new DecimalFormat("#####E0");
  final DecimalFormat normalNumberFormatter = new DecimalFormat("0.000");
  private List<Object> header = new ArrayList<>();
  private List<List<Object>> content = new LinkedList<>();
  private int longestCell = 2;
  private int longestRow;
  private double maxNumber;
  private String title;

  /**
   * Clear.
   */
  public void clear() {
    this.header.clear();
    this.content.clear();
    this.longestCell = 2;
    this.longestRow = 0;
    this.title = null;
    this.maxNumber = 0;
  }

  /**
   * Title table formatter.
   *
   * @param title the title
   * @return the table formatter
   */
  public TableFormatter title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Header table formatter.
   *
   * @param collection the collection
   * @return the table formatter
   */
  public TableFormatter header(Collection<?> collection) {
    header.addAll(collection);
    longestCell = (int) Math.max(longestCell, collection.stream().mapToDouble(o -> o.toString().length() + 2).max().orElse(0));
    return this;
  }

  /**
   * Content table formatter.
   *
   * @param collection the collection
   * @return the table formatter
   */
  public TableFormatter content(Collection<?> collection) {
    this.content.add(new ArrayList<>(collection));
    longestCell = (int) Math.max(longestCell, collection.stream().filter(o -> o instanceof String).mapToDouble(o -> o.toString().length() + 2).max().orElse(0));
    longestRow = Math.max(longestRow, collection.size());
    return this;
  }

  private String middleCMBar(String hbar, int nC) {
    StringBuilder builder = new StringBuilder();
    builder.append("├").append(hbar);
    for (int i = 1; i < nC; i++) {
      builder.append("┼").append(hbar);
    }
    builder.append("┤");
    return builder.toString();
  }

  private String convert(Object o, int longestCell) {
    if (o instanceof Number) {
      Number number = Cast.as(o);
      double d = number.doubleValue();
      if (d >= maxNumber) {
        return longNumberFormatter.format(d);
      } else {
        return normalNumberFormatter.format(d);
      }
    }
    return StringUtils.abbreviate(o.toString(), longestCell - 2);
  }

  private void printRow(PrintStream stream, List<Object> row, int longestCell, int longestRow) {
    while (row.size() < longestRow) {
      row.add(StringUtils.EMPTY);
    }
    stream.printf("│%s", StringUtils.center(convert(row.get(0), longestCell), longestCell));
    for (int i = 1; i < longestRow; i++) {
      stream.printf("│%s", StringUtils.center(convert(row.get(i), longestCell), longestCell));
    }
    stream.println("│");
  }

  /**
   * Print.
   *
   * @param stream the stream
   */
  public void print(PrintStream stream) {
    String horizontalBar = StringUtils.repeat("─", longestCell);
    String hline = middleCMBar(horizontalBar, longestRow);
    maxNumber = Math.pow(10, longestCell);
    longestRow = Math.max(longestRow, header.size());

    if (!StringUtils.isNullOrBlank(title)) {
      stream.println(
        StringUtils.center(title, (longestCell * longestRow) + longestRow + 1)
      );
    }

    stream.printf("┌%s", horizontalBar);
    for (int i = 1; i < longestRow; i++) {
      stream.printf("┬%s", horizontalBar);
    }
    stream.println("┐");

    if (header.size() > 0) {
      printRow(stream, header, longestCell, longestRow);
      stream.println(hline);
    }

    for (int r = 0; r < content.size(); r++) {
      printRow(stream, content.get(r), longestCell, longestRow);
      if (r + 1 < content.size()) {
        stream.println(hline);
      }
    }


    stream.printf("└%s", horizontalBar);
    for (int i = 1; i < longestRow; i++) {
      stream.printf("┴%s", horizontalBar);
    }
    stream.println("┘");

  }

}// END OF TableFormatter
