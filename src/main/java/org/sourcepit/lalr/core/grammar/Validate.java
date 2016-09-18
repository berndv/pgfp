/*
 * Copyright 2016 Bernd Vogt and others.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sourcepit.lalr.core.grammar;

import static java.lang.String.format;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class Validate {
   private Validate() {
      super();
   }

   public static void noDupliatedElements(List<?> collection) {
      final Set<Object> noDups = new HashSet<>();
      int i = 0;
      for (Iterator<?> it = collection.iterator(); it.hasNext(); i++) {
         if (!noDups.add(it.next())) {
            throw new IllegalArgumentException("The validated collection contains duplicated element at index: " + i);
         }
      }
   }

   public static void notEmpty(String string, String message, Object... args) {
      if (string == null || string.length() == 0) {
         throw new IllegalArgumentException(format(message, args));
      }
   }

   public static void isTrue(boolean expression, String message, Object... args) {
      if (expression == false) {
         throw new IllegalArgumentException(format(message, args));
      }
   }
}
