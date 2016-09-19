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

import static java.lang.String.format;
import static org.apache.commons.lang.Validate.isTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class DetermineFirstCoreGraphVisitor extends AbstractCoreGraphVisitor {

   private final Map<Variable, Set<Terminal>> symbolToFirst = new HashMap<>();

   private List<Runnable> delayed = new ArrayList<>();

   public Map<Variable, Set<Terminal>> getSymbolToFirst() {
      return symbolToFirst;
   }

   @Override
   protected void onEndVariableNode(VariableNode variableNode) {
      isTrue(!symbolToFirst.containsKey(variableNode.getSymbol()));
      final Set<Terminal> first = new LinkedHashSet<>();

      for (ProductionNode productionNode : variableNode.getProductionNodes()) {
         addFirstOfProductionNode(first, productionNode);
      }

      put(variableNode.getSymbol(), first);
   }

   private void put(Variable symbol, Set<Terminal> first) {
      symbolToFirst.put(symbol, first);

      // final List<Runnable> resolvers = recursionResolvers.get(symbol);
      // if (resolvers != null) {
      // for (Runnable runnable : resolvers) {
      // runnable.run();
      // }
      // }
   }

   // private final Map<Variable, List<Runnable>> recursionResolvers = new HashMap<>();
   //
   // private void addRecursionResolver(VariableNode leftSide, VariableNode required) {
   //
   // List<Runnable> resolvers = recursionResolvers.get(required.getSymbol());
   // if (resolvers == null) {
   // resolvers = new ArrayList<>();
   // recursionResolvers.put(required.getSymbol(), resolvers);
   // }
   //
   // resolvers.add(new Runnable() {
   // public void run() {
   // final Set<Terminal> first = symbolToFirst.get(leftSide.getSymbol());
   // final Set<Terminal> otherFirst = new LinkedHashSet<>(symbolToFirst.get(required.getSymbol()));
   // final boolean nullable = otherFirst.remove(null);
   // first.addAll(otherFirst);
   // // safety check
   // isTrue(nullable == required.isNullable());
   // }
   // });
   //
   // }

   private void onRecursiveConflic(VariableNode leftSide, VariableNode required) {

      // addRecursionResolver(leftSide, required);

      throw new IllegalStateException(
         format("Cannot determine first set of symbol '%s' because of recursive conflict with '%s'",
            leftSide.getSymbol(), required.getSymbol()));
   }

   @Override
   public void endGraph(CoreGraph coreGraph) {
      for (Runnable r : delayed) {
         r.run();
      }
   }

   public void addFirstOfProductionNode(Set<Terminal> first, ProductionNode productionNode) {
      final VariableNode leftSide = productionNode.getLeftSideNode();

      for (AbstractSymbolNode node : productionNode.getRightSideNodes()) {
         if (node.equals(leftSide)) {
            continue;
         }

         if (node instanceof TerminalNode) {
            final TerminalNode terminalNode = (TerminalNode) node;
            first.add(terminalNode.getSymbol());
            return;
         }

         final VariableNode variableNode = (VariableNode) node;
         addFirstOfVariableNodeWithoutNull(leftSide, first, variableNode);
         if (!variableNode.isNullable()) {
            return;
         }
      }

      // add null if
      // - production is empty or
      // - no terminal in production and
      // - each variable node is nullable
      first.add(null);
   }

   private void addFirstOfVariableNodeWithoutNull(VariableNode leftSide, Set<Terminal> first, VariableNode required) {

      final Variable symbol = required.getSymbol();

      final Set<Terminal> firstOfVariableNode = symbolToFirst.get(symbol);
      if (firstOfVariableNode == null) {
         // recursive conflict detected
         onRecursiveConflic(leftSide, required);
      }
      else {
         final Set<Terminal> otherFirst = new LinkedHashSet<>(firstOfVariableNode);
         final boolean nullable = otherFirst.remove(null);
         first.addAll(otherFirst);
         // safety check
         isTrue(nullable == required.isNullable());
      }
   }

}