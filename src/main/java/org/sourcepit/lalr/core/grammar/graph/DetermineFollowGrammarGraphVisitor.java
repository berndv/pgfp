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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class DetermineFollowGrammarGraphVisitor extends DetermineFirstGrammarGraphVisitor {
   private final Map<Variable, Set<Terminal>> symbolToFollow = new HashMap<>();

   public Map<Variable, Set<Terminal>> getSymbolToFollow() {
      return symbolToFollow;
   }

   @Override
   public void endGraph(GrammarGraph grammarGraph) {
      for (Variable variable : grammarGraph.getGrammar().getVariables()) {
         final VariableNode variableNode = grammarGraph.getVariableNode(variable);
         getFollow(new Stack<>(), grammarGraph, variableNode);
      }
   }

   private Set<Terminal> getFollow(Stack<VariableNode> trace, GrammarGraph graph, VariableNode referencedNode) {

      if (trace.contains(referencedNode)) {
         throw new IllegalStateException("Cannot determine follow set because of recursion in " + trace.toString());
      }

      trace.push(referencedNode);

      Set<Terminal> follow = getSymbolToFollow().get(referencedNode.getSymbol());
      if (follow == null) {
         follow = new LinkedHashSet<>();

         final Variable startSymbol = graph.getGrammar().getStartSymbol();
         if (referencedNode.getSymbol().equals(startSymbol)) {
            follow.add(null);
         }
         for (ProductionNode referencingAlt : referencedNode.getReferencedBy()) {
            addFollow(trace, graph, follow, referencingAlt, referencedNode);
         }

         getSymbolToFollow().put(referencedNode.getSymbol(), follow);
      }

      trace.pop();

      return follow;
   }

   private void addFollow(Stack<VariableNode> trace, GrammarGraph graph, Set<Terminal> follow,
      ProductionNode referencingAlt, VariableNode referencedNode) {
      final List<AbstractSymbolNode> nodes = referencingAlt.getRightSideNodes();

      boolean nullable = true;

      int nextIdx = 1;
      for (AbstractSymbolNode node : nodes) {

         if (node.equals(referencedNode)) {
            for (int j = nextIdx; j < nodes.size(); j++) {
               final AbstractSymbolNode followerNode = nodes.get(j);
               if (followerNode instanceof VariableNode) {
                  if (!followerNode.equals(referencedNode)) {
                     final Set<Terminal> firstOfFollower = new LinkedHashSet<>(
                        getSymbolToFirst().get(followerNode.getSymbol()));
                     nullable = firstOfFollower.remove(null);
                     follow.addAll(firstOfFollower);
                  }
               }
               else {
                  follow.add((Terminal) followerNode.getSymbol());
                  nullable = false;
               }
               if (!nullable) {
                  break;
               }
            }
            if (!nullable) {
               break;
            }
         }
         nextIdx++;
      }

      if (nullable) {
         if (!referencingAlt.getLeftSideNode().equals(referencedNode)) {
            follow.addAll(getFollow(trace, graph, referencingAlt.getLeftSideNode()));
         }
      }
   }
}
