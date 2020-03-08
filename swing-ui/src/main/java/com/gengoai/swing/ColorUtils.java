/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.gengoai.swing;

import lombok.NonNull;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Random;

public final class ColorUtils {

   public ColorUtils() {
      throw new IllegalAccessError();
   }

   public static String getHTMLColorString(Color color) {
      String red = Integer.toHexString(color.getRed());
      String green = Integer.toHexString(color.getGreen());
      String blue = Integer.toHexString(color.getBlue());

      return "#" +
            (red.length() == 1? "0" + red : red) +
            (green.length() == 1? "0" + green : green) +
            (blue.length() == 1? "0" + blue : blue);
   }

   public static Color stringToColor(final String value) {
      if(value == null) {
         return Color.GRAY;
      }
      try {
         // get color by hex or octal value
         return Color.decode(value);
      } catch(NumberFormatException nfe) {
         // if we can't decode lets try to get it by name
         try {
            // try to get a color by name using reflection
            final Field f = Color.class.getField(value);

            return (Color) f.get(null);
         } catch(Exception ce) {
            // if we can't get any color return black
            return Color.black;
         }
      }
   }

   public static Color randomColor() {
      return randomColor(Color.WHITE);
   }

   public static Color randomColor(@NonNull Color baseColor) {
      Random rnd = new Random();
      int red = (rnd.nextInt(256) + baseColor.getRed()) / 2;
      int green = (rnd.nextInt(256) + baseColor.getGreen()) / 2;
      int blue = (rnd.nextInt(256) + baseColor.getBlue()) / 2;
      return new Color(red, green, blue);
   }


   public static Color getContrastingFontColor(@NonNull Color background) {
      if((background.getRed() * 0.299 + background.getGreen() * 0.587 + background.getBlue() * 0.114) > 120) {
         return Color.BLACK;
      }
      return Color.WHITE;
   }
}//END OF ColorUtils
