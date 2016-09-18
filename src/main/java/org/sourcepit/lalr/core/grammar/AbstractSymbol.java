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

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

public abstract class AbstractSymbol {

   private final SymbolType type;

   private final String toString;

   protected AbstractSymbol(SymbolType type, String toString) {
      notNull(type);
      this.type = type;
      notEmpty(toString);
      this.toString = toString;
   }

   public SymbolType getType() {
      return type;
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((toString == null) ? 0 : toString.hashCode());
      result = prime * result + ((type == null) ? 0 : type.hashCode());
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
      AbstractSymbol other = (AbstractSymbol) obj;
      if (toString == null) {
         if (other.toString != null) {
            return false;
         }
      }
      else if (!toString.equals(other.toString)) {
         return false;
      }
      if (type != other.type) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return toString;
   }

}
