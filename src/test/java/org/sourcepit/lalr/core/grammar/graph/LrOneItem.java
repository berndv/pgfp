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

package org.sourcepit.lalr.core.grammar.graph;

import java.util.Set;

import org.sourcepit.lalr.core.grammar.Terminal;

public class LrOneItem {
   private final LrZeroItem lrZeroItem;

   private final Set<Terminal> lookahead;

   public LrOneItem(LrZeroItem lrZeroItem, Set<Terminal> lookahead) {
      this.lrZeroItem = lrZeroItem;
      this.lookahead = lookahead;
   }

   public LrZeroItem getLrZeroItem() {
      return lrZeroItem;
   }

   public Set<Terminal> getLookahead() {
      return lookahead;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((lookahead == null) ? 0 : lookahead.hashCode());
      result = prime * result + ((lrZeroItem == null) ? 0 : lrZeroItem.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      LrOneItem other = (LrOneItem) obj;
      if (lookahead == null) {
         if (other.lookahead != null) {
            return false;
         }
      }
      else if (!lookahead.equals(other.lookahead)) {
         return false;
      }
      if (lrZeroItem == null) {
         if (other.lrZeroItem != null) {
            return false;
         }
      }
      else if (!lrZeroItem.equals(other.lrZeroItem)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append(lrZeroItem);
      builder.append(", ");
      builder.append(lookahead);
      return builder.toString();
   }

}
