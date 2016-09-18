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

package org.sourcepit.lalr.core.graph;

import java.util.ArrayList;
import java.util.List;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;

public abstract class AbstractSymbolNode {
   protected final AbstractSymbol symbol;

   protected final List<Alternative> referencedBy = new ArrayList<>();

   public AbstractSymbolNode(AbstractSymbol symbol) {
      this.symbol = symbol;
   }

   public AbstractSymbol getSymbol() {
      return symbol;
   }

   public List<Alternative> getReferencedBy() {
      return referencedBy;
   }

   @Override
   public String toString() {
      return toString(true);
   }

   public String toString(boolean full) {
      StringBuilder builder = new StringBuilder();
      builder.append(getClass().getSimpleName());
      builder.append(" [");
      if (symbol != null) {
         builder.append("symbol=");
         builder.append(symbol);
      }
      builder.append("]");
      return builder.toString();
   }

}
