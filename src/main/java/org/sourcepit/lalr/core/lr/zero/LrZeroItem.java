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

package org.sourcepit.lalr.core.lr.zero;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Production;

public class LrZeroItem {
   private final Production production;
   private final int dot;

   public LrZeroItem(Production production, int dot) {
      this.production = production;
      this.dot = dot;
   }

   public Production getProduction() {
      return production;
   }

   public int getDot() {
      return dot;
   }

   public boolean isFinal() {
      return dot >= production.getRightSide().size();
   }

   public AbstractSymbol getExpectedSymbol() {
      return isFinal() ? null : production.getRightSide().get(dot);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + dot;
      result = prime * result + ((production == null) ? 0 : production.hashCode());
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
      LrZeroItem other = (LrZeroItem) obj;
      if (dot != other.dot) {
         return false;
      }
      if (production == null) {
         if (other.production != null) {
            return false;
         }
      }
      else if (!production.equals(other.production)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      final StringBuilder builder = new StringBuilder();
      builder.append(production.getLeftSide());
      builder.append(" =");
      if (production.getRightSide().isEmpty()) {
         builder.append(" .");
      }
      else {
         for (int i = 0; i < production.getRightSide().size(); i++) {
            builder.append(" ");
            if (dot == i) {
               builder.append(".");
            }
            builder.append(production.getRightSide().get(i));
         }
         if (isFinal()) {
            builder.append(".");
         }
      }
      return builder.toString();
   }
}
