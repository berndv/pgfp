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

import org.sourcepit.lalr.core.grammar.MetaSymbol;

public class MetaNode extends AbstractSymbolNode {

   private boolean nullable = false;

   private final List<Alternative> alternatives = new ArrayList<>();

   public MetaNode(MetaSymbol symbol) {
      super(symbol);
   }

   @Override
   public MetaSymbol getSymbol() {
      return (MetaSymbol) super.getSymbol();
   }

   public List<Alternative> getAlternatives() {
      return alternatives;
   }

   public void setNullable(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return nullable;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("MetaNode [");
      if (symbol != null) {
         builder.append("symbol=");
         builder.append(symbol);
         builder.append(", ");
      }
      builder.append("nullable=");
      builder.append(nullable);
      builder.append("]");
      return builder.toString();
   }

}
