/*
 * (c) 2005 David B. Bracewell
 *
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

package com.gengoai.mango;

/**
 * <p>An interface that defines a tag name and a methodology to determine if one tag is an instance of another.
 * Individual implementations may define the <code>isInstance</code> method to take into account a hierarchy or other
 * attributes that define a tag.</p>
 *
 * @author David B. Bracewell
 */
public interface Tag {

  /**
   * Determines if this tag is an instance of a given tag.
   *
   * @param tag The given tag
   * @return True if this tag is an instance of the given tag
   */
  boolean isInstance(Tag tag);

  /**
   * Determines if this tag is an instance of any of the given tags.
   *
   * @param tags the tags to check against
   * @return True if this tag is an instance of any one of the given tags
   */
  default boolean isInstance(Tag... tags) {
    if (tags != null) {
      for (Tag other : tags) {
        if (isInstance(other)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * The name of the enum value
   *
   * @return The name of the enum value
   */
  String name();

}//END OF Tag
