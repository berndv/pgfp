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

import org.sourcepit.lalr.core.grammar.MetaSymbol;
import org.sourcepit.lalr.core.grammar.TerminalSymbol;

public class DetermineFirstCoreGraphVisitor extends AbstractCoreGraphVisitor {

   private final Map<MetaSymbol, Set<TerminalSymbol>> symbolToFirst = new HashMap<>();

   private List<Runnable> delayed = new ArrayList<>();

   public Map<MetaSymbol, Set<TerminalSymbol>> getSymbolToFirst() {
      return symbolToFirst;
   }

   @Override
   protected void onEndMetaNode(MetaNode metaNode) {
      isTrue(!symbolToFirst.containsKey(metaNode.getSymbol()));
      final Set<TerminalSymbol> first = new LinkedHashSet<>();

      for (Alternative alternative : metaNode.getAlternatives()) {
         addFirstOfAlternative(first, alternative);
      }

      put(metaNode.getSymbol(), first);
   }

   private void put(MetaSymbol symbol, Set<TerminalSymbol> first) {
      symbolToFirst.put(symbol, first);

      // final List<Runnable> resolvers = recursionResolvers.get(symbol);
      // if (resolvers != null) {
      // for (Runnable runnable : resolvers) {
      // runnable.run();
      // }
      // }
   }

   // private final Map<MetaSymbol, List<Runnable>> recursionResolvers = new HashMap<>();
   //
   // private void addRecursionResolver(MetaNode leftSide, MetaNode required) {
   //
   // List<Runnable> resolvers = recursionResolvers.get(required.getSymbol());
   // if (resolvers == null) {
   // resolvers = new ArrayList<>();
   // recursionResolvers.put(required.getSymbol(), resolvers);
   // }
   //
   // resolvers.add(new Runnable() {
   // public void run() {
   // final Set<TerminalSymbol> first = symbolToFirst.get(leftSide.getSymbol());
   // final Set<TerminalSymbol> otherFirst = new LinkedHashSet<>(symbolToFirst.get(required.getSymbol()));
   // final boolean nullable = otherFirst.remove(null);
   // first.addAll(otherFirst);
   // // safety check
   // isTrue(nullable == required.isNullable());
   // }
   // });
   //
   // }

   private void onRecursiveConflic(MetaNode leftSide, MetaNode required) {

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

   public void addFirstOfAlternative(Set<TerminalSymbol> first, Alternative alternative) {
      final MetaNode leftSide = alternative.getParent();

      for (AbstractSymbolNode node : alternative.getSymbolNodes()) {
         if (node.equals(leftSide)) {
            continue;
         }

         if (node instanceof TerminalNode) {
            final TerminalNode terminalNode = (TerminalNode) node;
            first.add(terminalNode.getSymbol());
            return;
         }

         final MetaNode metaNode = (MetaNode) node;
         addFirstOfMetaNodeWithoutNull(leftSide, first, metaNode);
         if (!metaNode.isNullable()) {
            return;
         }
      }

      // add null if
      // - alternative is empty or
      // - no terminal in alternative and
      // - each meta node is nullable
      first.add(null);
   }

   private void addFirstOfMetaNodeWithoutNull(MetaNode leftSide, Set<TerminalSymbol> first, MetaNode required) {

      final MetaSymbol symbol = required.getSymbol();

      final Set<TerminalSymbol> firstOfMetaNode = symbolToFirst.get(symbol);
      if (firstOfMetaNode == null) {
         // recursive conflict detected
         onRecursiveConflic(leftSide, required);
      }
      else {
         final Set<TerminalSymbol> otherFirst = new LinkedHashSet<>(firstOfMetaNode);
         final boolean nullable = otherFirst.remove(null);
         first.addAll(otherFirst);
         // safety check
         isTrue(nullable == required.isNullable());
      }
   }

}