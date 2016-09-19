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

public class ProductionNode {

   private VariableNode parent;

   private final List<AbstractSymbolNode> rightSideNodes = new ArrayList<>();

   public void setParent(VariableNode parent) {
      this.parent = parent;
   }

   public VariableNode getLeftSideNode() {
      return parent;
   }

   public List<AbstractSymbolNode> getRightSideNodes() {
      return rightSideNodes;
   }

   @Override
   public String toString() {
      return toString(true);
   }

   public String toString(boolean full) {
      StringBuilder builder = new StringBuilder();
      builder.append("ProductionNode [");
      if (parent != null) {
         builder.append("parent=");
         builder.append(parent.getSymbol());
         builder.append(", ");
         builder.append("index=");
         builder.append(parent.getProductionNodes().indexOf(this));
      }
      builder.append("]");
      return builder.toString();
   }

}
