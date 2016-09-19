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

import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang.Validate.noNullElements;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.util.List;

public final class Production {
   private final Variable leftSide;

   private final List<AbstractSymbol> rightSide;

   private final String toString;

   public Production(Variable leftSide, List<AbstractSymbol> rightSide, String toString) {
      notNull(leftSide);
      noNullElements(rightSide);
      notEmpty(toString);
      this.leftSide = leftSide;
      this.rightSide = unmodifiableList(rightSide);
      this.toString = toString;
   }

   public Variable getLeftSide() {
      return leftSide;
   }

   public List<AbstractSymbol> getRightSide() {
      return rightSide;
   }

   public boolean isEmpty() {
      return rightSide.isEmpty();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((leftSide == null) ? 0 : leftSide.hashCode());
      result = prime * result + ((rightSide == null) ? 0 : rightSide.hashCode());
      result = prime * result + ((toString == null) ? 0 : toString.hashCode());
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
      Production other = (Production) obj;
      if (leftSide == null) {
         if (other.leftSide != null) {
            return false;
         }
      }
      else if (!leftSide.equals(other.leftSide)) {
         return false;
      }
      if (rightSide == null) {
         if (other.rightSide != null) {
            return false;
         }
      }
      else if (!rightSide.equals(other.rightSide)) {
         return false;
      }
      if (toString == null) {
         if (other.toString != null) {
            return false;
         }
      }
      else if (!toString.equals(other.toString)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return toString;
   }

}
