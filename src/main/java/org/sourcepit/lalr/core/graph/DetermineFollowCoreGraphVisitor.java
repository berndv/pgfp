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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.sourcepit.lalr.core.grammar.MetaSymbol;
import org.sourcepit.lalr.core.grammar.TerminalSymbol;

public class DetermineFollowCoreGraphVisitor extends DetermineFirstCoreGraphVisitor {
   private final Map<MetaSymbol, Set<TerminalSymbol>> symbolToFollow = new HashMap<>();

   public Map<MetaSymbol, Set<TerminalSymbol>> getSymbolToFollow() {
      return symbolToFollow;
   }

   @Override
   public void endGraph(CoreGraph coreGraph) {
      for (MetaSymbol metaSymbol : coreGraph.getGrammar().getMetaSymbols()) {
         final MetaNode metaNode = coreGraph.getMetaNode(metaSymbol);
         getFollow(new Stack<>(), coreGraph, metaNode);
      }
   }

   private Set<TerminalSymbol> getFollow(Stack<MetaNode> trace, CoreGraph graph, MetaNode referencedNode) {

      if (trace.contains(referencedNode)) {
         throw new IllegalStateException("Cannot determine follow set because of recursion in " + trace.toString());
      }

      trace.push(referencedNode);

      Set<TerminalSymbol> follow = getSymbolToFollow().get(referencedNode.getSymbol());
      if (follow == null) {
         follow = new LinkedHashSet<>();

         final MetaSymbol startSymbol = graph.getGrammar().getStartSymbol();
         if (referencedNode.getSymbol().equals(startSymbol)) {
            follow.add(null);
         }
         for (Alternative referencingAlt : referencedNode.getReferencedBy()) {
            addFollow(trace, graph, follow, referencingAlt, referencedNode);
         }

         getSymbolToFollow().put(referencedNode.getSymbol(), follow);
      }

      trace.pop();

      return follow;
   }

   private void addFollow(Stack<MetaNode> trace, CoreGraph graph, Set<TerminalSymbol> follow,
      Alternative referencingAlt, MetaNode referencedNode) {
      final List<AbstractSymbolNode> nodes = referencingAlt.getSymbolNodes();

      boolean nullable = true;

      int nextIdx = 1;
      for (AbstractSymbolNode node : nodes) {

         if (node.equals(referencedNode)) {
            for (int j = nextIdx; j < nodes.size(); j++) {
               final AbstractSymbolNode followerNode = nodes.get(j);
               if (followerNode instanceof MetaNode) {
                  if (!followerNode.equals(referencedNode)) {
                     final Set<TerminalSymbol> firstOfFollower = new LinkedHashSet<>(
                        getSymbolToFirst().get(followerNode.getSymbol()));
                     nullable = firstOfFollower.remove(null);
                     follow.addAll(firstOfFollower);
                  }
               }
               else {
                  follow.add((TerminalSymbol) followerNode.getSymbol());
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
         if (!referencingAlt.getParent().equals(referencedNode)) {
            follow.addAll(getFollow(trace, graph, referencingAlt.getParent()));
         }
      }
   }
}
