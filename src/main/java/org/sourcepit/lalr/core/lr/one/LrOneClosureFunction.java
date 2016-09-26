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

import static org.apache.commons.lang.Validate.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;
import org.sourcepit.lalr.core.grammar.graph.GrammarGraph;
import org.sourcepit.lalr.core.grammar.graph.VariableNode;
import org.sourcepit.lalr.core.lr.zero.LrZeroItem;

public class LrOneClosureFunction implements BiFunction<GrammarGraph, Set<LrOneItem>, Set<LrOneItem>> {

   @Override
   public Set<LrOneItem> apply(GrammarGraph graph, Set<LrOneItem> inputItems) {
      final Map<LrZeroItem, Set<Terminal>> zeroItemToLookahead = new LinkedHashMap<>();
      for (LrOneItem item : inputItems) {
         Set<Terminal> lookahead = zeroItemToLookahead.get(item.getLrZeroItem());
         if (lookahead == null) {
            zeroItemToLookahead.put(item.getLrZeroItem(), item.getLookahead());
         }
         else {
            lookahead.addAll(item.getLookahead());
         }
      }

      final Map<LrZeroItem, Set<LrZeroItem>> zeroItemToOrigins = new LinkedHashMap<>();
      for (LrOneItem item : inputItems) {
         determineZeroItemsAndWhereTheyComeFrom(graph.getGrammar(), zeroItemToOrigins, item.getLrZeroItem(), null);
      }

      final List<LrZeroItem> allItems = new ArrayList<>(zeroItemToOrigins.keySet());
      Collections.sort(allItems, new Comparator<LrZeroItem>() {
         @Override
         public int compare(LrZeroItem o1, LrZeroItem o2) {
            if (zeroItemToOrigins.get(o1).contains(o2)) {
               return 1;
            }
            if (zeroItemToOrigins.get(o2).contains(o1)) {
               return -1;
            }
            return 0;
         }
      });

      final Set<LrOneItem> closure = new LinkedHashSet<>();
      for (LrZeroItem zItem : allItems) {
         final Set<Terminal> lookahead = getLookahead(graph, zeroItemToLookahead, zeroItemToOrigins, zItem);
         closure.add(new LrOneItem(zItem, lookahead));
      }
      return closure;
   }

   private Set<Terminal> getLookahead(GrammarGraph graph, Map<LrZeroItem, Set<Terminal>> zeroItemToLookahead,
      Map<LrZeroItem, Set<LrZeroItem>> zeroItemToOrigins, LrZeroItem zItem) {
      Set<Terminal> lookahead = zeroItemToLookahead.get(zItem);
      if (lookahead == null) {
         lookahead = new LinkedHashSet<>();
         for (LrZeroItem origin : zeroItemToOrigins.get(zItem)) {
            if (origin == null) {
               lookahead.addAll(zeroItemToLookahead.get(origin));
            }
            else {
               final AbstractSymbol firstOfFirst = getSymbolAfterExpectedSymbol(origin);
               lookahead.addAll(first(graph, firstOfFirst, zeroItemToLookahead, origin));
            }
         }
         zeroItemToLookahead.put(zItem, lookahead);
      }
      return lookahead;
   }

   private static void determineZeroItemsAndWhereTheyComeFrom(Grammar grammar,
      Map<LrZeroItem, Set<LrZeroItem>> zeroItemToOrigins, LrZeroItem zeroItem, LrZeroItem origin) {

      Set<LrZeroItem> origins = zeroItemToOrigins.get(zeroItem);
      if (origins == null) {
         origins = new LinkedHashSet<>();
         zeroItemToOrigins.put(zeroItem, origins);
      }

      if (origins.add(origin) && !zeroItem.isFinal()) {
         final AbstractSymbol symbol = zeroItem.getExpectedSymbol();
         if (symbol instanceof Variable) {
            final Variable variable = (Variable) symbol;
            for (Production production : grammar.getProductions(variable)) {
               final LrZeroItem nextZeroItem = new LrZeroItem(production, 0);
               determineZeroItemsAndWhereTheyComeFrom(grammar, zeroItemToOrigins, nextZeroItem, zeroItem);
            }
         }
      }
   }

   private static Set<Terminal> first(GrammarGraph graph, AbstractSymbol firstOfFirst,
      Map<LrZeroItem, Set<Terminal>> zeroItemToLookahead, LrZeroItem origin) {
      if (firstOfFirst == null) {
         final Set<Terminal> lookaheadOfOrigin = zeroItemToLookahead.get(origin);
         notNull(lookaheadOfOrigin, "Unexpected recursive attempt");
         return new LinkedHashSet<>(lookaheadOfOrigin);
      }
      else if (firstOfFirst instanceof Terminal) {
         final Set<Terminal> first = new LinkedHashSet<>(1);
         first.add((Terminal) firstOfFirst);
         return first;
      }
      else {
         final VariableNode node = graph.getVariableNode((Variable) firstOfFirst);
         final Set<Terminal> nodeFirst = node.getFirstSet();
         final boolean nullable = node.isNullable();
         final Set<Terminal> first;
         if (nullable) {
            final Set<Terminal> lookaheadOfOrigin = zeroItemToLookahead.get(origin);
            notNull(lookaheadOfOrigin, "Unexpected recursive attempt");
            first = new LinkedHashSet<>(lookaheadOfOrigin.size() + nodeFirst.size());
            first.addAll(nodeFirst);
            first.addAll(lookaheadOfOrigin);
         }
         else {
            first = new LinkedHashSet<>(nodeFirst);
         }
         return first;
      }
   }

   private static AbstractSymbol getSymbolAfterExpectedSymbol(LrZeroItem zeroItem) {
      final int idx = zeroItem.getDot() + 1;
      final List<AbstractSymbol> rightSide = zeroItem.getProduction().getRightSide();
      return idx < rightSide.size() ? rightSide.get(idx) : null;
   }

}
