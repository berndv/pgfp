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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class VariableNode extends AbstractSymbolNode {

   private boolean nullable = false;

   private final List<ProductionNode> productionNodes = new ArrayList<>();

   private Set<Terminal> firstSet, followSet;

   public VariableNode(Variable symbol) {
      super(symbol);
   }

   @Override
   public Variable getSymbol() {
      return (Variable) super.getSymbol();
   }

   public List<ProductionNode> getProductionNodes() {
      return productionNodes;
   }

   public void setNullable(boolean nullable) {
      this.nullable = nullable;
   }

   public boolean isNullable() {
      return nullable;
   }

   public void setFirstSet(Set<Terminal> firstSet) {
      this.firstSet = firstSet;
   }

   public Set<Terminal> getFirstSet() {
      return firstSet;
   }

   public void setFollowSet(Set<Terminal> followSet) {
      this.followSet = followSet;
   }

   public Set<Terminal> getFollowSet() {
      return followSet;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("VariableNode [");
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
