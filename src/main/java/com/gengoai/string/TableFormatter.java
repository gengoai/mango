package com.gengoai.string;

import com.gengoai.conversion.Cast;
import com.gengoai.io.resource.Resource;
import com.gengoai.io.resource.StringResource;
import lombok.NonNull;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;

/**
 * The type Table formatter.
 *
 * @author David B. Bracewell
 */
public class TableFormatter implements Serializable {
   private static final long serialVersionUID = 1L;
   private static final int MIN_CELL_WIDTH = 5;
   private static final DecimalFormat longNumberFormatter = new DecimalFormat("0.0E0");
   private DecimalFormat normalNumberFormatter = new DecimalFormat("0.000");
   private List<Object> header = new ArrayList<>();
   private List<List<Object>> content = new LinkedList<>();
   private List<List<Object>> footer = new LinkedList<>();
   private int longestCell = 2;
   private int longestRow;
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
    * Sets min cell width.
    *
    * @param cellWidth the cell width
    */
   public void setMinCellWidth(int cellWidth) {
      this.longestCell = cellWidth;
   }

   /**
    * Sets number formatter.
    *
    * @param decimalFormat the decimal format
    */
   public void setNumberFormatter(@NonNull DecimalFormat decimalFormat) {
      this.normalNumberFormatter = decimalFormat;
   }

   /**
    * Header table formatter.
    *
    * @param collection the collection
    * @return the table formatter
    */
   public <T> TableFormatter header(Collection<? extends T> collection) {
      header.addAll(collection);
      longestCell = (int) Math.max(longestCell, collection.stream()
                                                          .mapToDouble(o -> o.toString().length() + 2)
                                                          .max()
                                                          .orElse(0));
      return this;
   }

   private int length(Number number) {
      if (number instanceof Long || number instanceof Integer || number instanceof Short) {
         return Math.min(longNumberFormatter.format(number).length(), number.toString().length());
      }
      return Math.max(5,
                      Math.min(longNumberFormatter.format(number).length(),
                               normalNumberFormatter.format(number).length()));
   }

   /**
    * Content table formatter.
    *
    * @param collection the collection
    * @return the table formatter
    */
   public TableFormatter content(Collection<?> collection) {
      this.content.add(new ArrayList<>(collection));
      longestCell = (int) Math.max(longestCell, collection.stream()
                                                          .mapToDouble(o -> {
                                                                          if (o instanceof Number) {
                                                                             return Math.max(MIN_CELL_WIDTH, length(Cast
                                                                                                                       .as(o)));
                                                                          } else {
                                                                             return Math.max(MIN_CELL_WIDTH,
                                                                                             o.toString().length() + 2);
                                                                          }
                                                                       }
                                                                      ).max().orElse(0)
                                  );
      longestRow = Math.max(longestRow, collection.size());
      return this;
   }

   public TableFormatter footer(Collection<?> collection) {
      this.footer.add(new ArrayList<>(collection));
      longestCell = (int) Math.max(longestCell, collection.stream()
                                                          .mapToDouble(o -> {
                                                                          if (o instanceof Number) {
                                                                             return Math.max(MIN_CELL_WIDTH, length(Cast.as(o)));
                                                                          } else {
                                                                             return Math.max(MIN_CELL_WIDTH,
                                                                                             o.toString().length() + 2);
                                                                          }
                                                                       }
                                                                      ).max().orElse(0)
                                  );
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

   private String bar(String hbar, String left, String right, String between, int nC) {
      StringBuilder builder = new StringBuilder();
      builder.append(left).append(hbar);
      for (int i = 1; i < nC; i++) {
         builder.append(between).append(hbar);
      }
      builder.append(right);
      return builder.toString();
   }


   private String convert(Object o, int longestCell) {
      if (o instanceof Number) {
         Number number = Cast.as(o);
         if (number instanceof Long || number instanceof Integer || number instanceof Short) {
            String numString = Long.toString(number.longValue());
            return numString.length() <= longestCell ? numString : longNumberFormatter.format(number);
         } else {
            String numString = normalNumberFormatter.format(number);
            return numString.length() <= longestCell ? numString : longNumberFormatter.format(number);
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
    * Print the table to the give PrintStream .
    *
    * @param stream the print stream to write to
    */
   public void print(@NonNull PrintStream stream) {
      String horizontalBar = StringUtils.repeat("─", longestCell);
      String hline = middleCMBar(horizontalBar, longestRow);
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
         stream.println(bar(StringUtils.repeat("═", longestCell), "╞", "╡", "╪", header.size()));
      }

      for (int r = 0; r < content.size(); r++) {
         printRow(stream, content.get(r), longestCell, longestRow);
         if (r + 1 < content.size()) {
            stream.println(hline);
         }
      }

      if (!footer.isEmpty()) {
         stream.println(bar(StringUtils.repeat("═",longestCell), "╞", "╡", "╪", header.size()));
         for (int r = 0; r < footer.size(); r++) {
            printRow(stream, footer.get(r), longestCell, longestRow);
            if (r + 1 < footer.size()) {
               stream.println(hline);
            }
         }
      }


      stream.printf("└%s", horizontalBar);
      for (int i = 1; i < longestRow; i++) {
         stream.printf("┴%s", horizontalBar);
      }
      stream.println("┘");


   }

   /**
    * Writes the table to a resource.
    *
    * @param resource the resource to write to
    * @return the resource written to
    * @throws IOException Something went wrong writing to the resource
    */
   public Resource write(@NonNull Resource resource) throws IOException {
      Resource stringResource = new StringResource();
      try (PrintStream printStream = new PrintStream(stringResource.outputStream())) {
         print(printStream);
      }
      resource.write(stringResource.readToString());
      return resource;
   }


   public static void main(String[] args) throws Exception {
      TableFormatter f = new TableFormatter();
      f.header(Arrays.asList("A", "B", "C"));
      f.content(Arrays.asList("A", "B", "C"));
      f.footer(Arrays.asList("A", "B", "C"));
      f.print(System.out);
   }

}// END OF TableFormatter
