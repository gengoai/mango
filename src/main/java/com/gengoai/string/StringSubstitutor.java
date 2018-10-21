package com.gengoai.string;

import java.io.Serializable;
import java.util.Stack;
import java.util.function.Function;

/**
 * @author David B. Bracewell
 */
public class StringSubstitutor implements Serializable {

   private static String replace(String in, Function<String, String> replacement) {
      StringBuilder out = new StringBuilder();
      int openCount = 0;
      Stack<StringBuilder> buffers = new Stack<>();

      for (int i = 0; i < in.length(); i++) {
         switch (in.charAt(i)) {
            case '\\':
               i++;
               if (i < in.length()) {
                  out.append(in.charAt(i));
               } else {
                  throw new IllegalStateException("Escape character terminating string");
               }
               break;
            case '$':
               i++;
               if (i < in.length() && in.charAt(i) == '{') {
                  openCount++;
                  buffers.push(new StringBuilder());
               } else {
                  out.append(in.charAt(i - 1)).append(in.charAt(i));
               }
               break;
            case '}':
               if (openCount > 0) {
                  openCount--;
                  StringBuilder var = buffers.pop();
                  if (openCount == 0) {
                     out.append(replacement.apply(var.toString()));
                  } else {
                     buffers.peek().append(replacement.apply(var.toString()));
                  }
               } else {
                  out.append(in.charAt(i));
               }
               break;
            default:
               if (openCount == 0) {
                  out.append(in.charAt(i));
               } else {
                  buffers.peek().append(in.charAt(i));
               }
               break;
         }
      }
      return out.toString();
   }

   public static void main(String[] args) throws Exception {
      String p = "${a${z${v}}} \\${a}";
      String o = replace(p, (a) -> {
         switch (a) {
            case "zD":
               return "ZED";
            case "aZED":
               return "GABBA";
            case "v":
               return "D";
         }
         return a;
      });
      System.out.println(o);
   }


}//END OF StringSubstitutor
