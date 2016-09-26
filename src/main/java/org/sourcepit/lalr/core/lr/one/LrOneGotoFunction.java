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

package org.sourcepit.lalr.core.lr.one;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.graph.GrammarGraph;
import org.sourcepit.lalr.core.lr.zero.LrZeroItem;

public class LrOneGotoFunction
   implements
      BiFunction<GrammarGraph, Set<LrOneItem>, Map<AbstractSymbol, Set<LrOneItem>>> {

   private final BiFunction<GrammarGraph, Set<LrOneItem>, Set<LrOneItem>> closureFunction;

   public LrOneGotoFunction(BiFunction<GrammarGraph, Set<LrOneItem>, Set<LrOneItem>> closureFunction) {
      this.closureFunction = closureFunction;
   }

   public BiFunction<GrammarGraph, Set<LrOneItem>, Set<LrOneItem>> getClosureFunction() {
      return closureFunction;
   }

   @Override
   public Map<AbstractSymbol, Set<LrOneItem>> apply(GrammarGraph graph, Set<LrOneItem> closure) {
      final Map<AbstractSymbol, Set<LrOneItem>> symbolGotoClosure = new LinkedHashMap<>();
      apply(graph, symbolGotoClosure, closure);
      return symbolGotoClosure;
   }

   private void apply(GrammarGraph graph, Map<AbstractSymbol, Set<LrOneItem>> symbolGotoClosure,
      Set<LrOneItem> closure) {
      final Set<LrOneItem> startItems = getGotoClosuresStartItems(closure);

      // final Set<AbstractSymbol> staleGotos = new LinkedHashSet<>();
      for (LrOneItem startItem : startItems) {
         final LrZeroItem zeroStartItem = startItem.getLrZeroItem();
         final AbstractSymbol symbol = zeroStartItem.getProduction().getRightSide().get(zeroStartItem.getDot() - 1);
         Set<LrOneItem> gotoClosure = symbolGotoClosure.get(symbol);
         if (gotoClosure == null) {
            gotoClosure = new LinkedHashSet<>();
            symbolGotoClosure.put(symbol, gotoClosure);
            // staleGotos.add(symbol);
         }
         if (gotoClosure.add(startItem)) {
            // staleGotos.add(symbol);
         }
      }

      for (AbstractSymbol symbol : symbolGotoClosure.keySet()) {
         final Set<LrOneItem> gotoClosure = symbolGotoClosure.get(symbol);
         final Set<LrOneItem> closedGotoClosure = closureFunction.apply(graph, gotoClosure);
         if (!closedGotoClosure.equals(gotoClosure)) {
            symbolGotoClosure.put(symbol, closedGotoClosure);
            // apply(graph, symbolGotoClosure, closedGotoClosure);
         }
      }
   }

   private static Set<LrOneItem> getGotoClosuresStartItems(Set<LrOneItem> closure) {
      final Map<LrZeroItem, Set<Terminal>> gotoItems = new LinkedHashMap<>();
      for (LrOneItem oItem : closure) {
         final LrZeroItem zeroItem = oItem.getLrZeroItem();
         if (!zeroItem.isFinal()) {
            final LrZeroItem nextZeroItem = new LrZeroItem(zeroItem.getProduction(), zeroItem.getDot() + 1);
            Set<Terminal> lookahead = gotoItems.get(nextZeroItem);
            if (lookahead == null) {
               lookahead = new LinkedHashSet<>();
               gotoItems.put(nextZeroItem, lookahead);
            }
            lookahead.addAll(oItem.getLookahead());
         }
      }
      final Set<LrOneItem> result = new LinkedHashSet<>(gotoItems.size());
      for (Entry<LrZeroItem, Set<Terminal>> entry : gotoItems.entrySet()) {
         result.add(new LrOneItem(entry.getKey(), entry.getValue()));
      }
      return result;
   }

}
