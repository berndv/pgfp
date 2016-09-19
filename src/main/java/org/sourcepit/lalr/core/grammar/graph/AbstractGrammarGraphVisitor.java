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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractGrammarGraphVisitor implements GrammarGraphVisitor {

   protected Set<VariableNode> visted;

   protected final boolean skipVisited;

   public AbstractGrammarGraphVisitor() {
      this(true);
   }

   public AbstractGrammarGraphVisitor(boolean skipVisited) {
      this.skipVisited = skipVisited;
   }

   @Override
   public void startGraph(GrammarGraph grammarGraph) {
      if (skipVisited) {
         visted = new HashSet<>();
      }
   }

   @Override
   public final boolean startVariableNode(VariableNode variableNode) {
      if (visted != null && visted.contains(variableNode)) {
         return false;
      }
      return onStartVariableNode(variableNode);
   }

   protected boolean onStartVariableNode(VariableNode variableNode) {
      return true;
   }

   @Override
   public void startProductionNode(ProductionNode productionNode) {
   }

   @Override
   public void visitRecursion(List<Object> trace) {
   }

   @Override
   public void visitTerminalNode(TerminalNode symbolNode) {
   }

   @Override
   public void endProductionNode(ProductionNode productionNode) {
   }

   protected void onEndVariableNode(VariableNode variableNode) {
   }

   @Override
   public final void endVariableNode(VariableNode variableNode) {
      if (visted == null || !visted.contains(variableNode)) {
         onEndVariableNode(variableNode);
      }

      if (visted != null) {
         visted.add(variableNode);
      }
   }

   @Override
   public void endGraph(GrammarGraph grammarGraph) {
      visted = null;
   }

}
