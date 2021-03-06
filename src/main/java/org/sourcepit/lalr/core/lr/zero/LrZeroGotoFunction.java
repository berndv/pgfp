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

package org.sourcepit.lalr.core.lr.zero;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;

public class LrZeroGotoFunction implements BiFunction<Grammar, Set<LrZeroItem>, Map<AbstractSymbol, Set<LrZeroItem>>> {

   private final BiFunction<Grammar, Set<LrZeroItem>, Set<LrZeroItem>> closureFunction;

   public LrZeroGotoFunction(BiFunction<Grammar, Set<LrZeroItem>, Set<LrZeroItem>> closureFunction) {
      this.closureFunction = closureFunction;
   }

   public BiFunction<Grammar, Set<LrZeroItem>, Set<LrZeroItem>> getClosureFunction() {
      return closureFunction;
   }

   @Override
   public Map<AbstractSymbol, Set<LrZeroItem>> apply(Grammar grammar, Set<LrZeroItem> closure) {
      final Map<AbstractSymbol, Set<LrZeroItem>> symbolGotoClosure = new LinkedHashMap<>();
      apply(grammar, symbolGotoClosure, closure);
      return symbolGotoClosure;
   }

   private void apply(Grammar grammar, Map<AbstractSymbol, Set<LrZeroItem>> symbolToTargetClosure,
      Set<LrZeroItem> closure) {
      for (LrZeroItem item : closure) {
         if (!item.isFinal()) {
            final AbstractSymbol symbol = item.getExpectedSymbol();
            Set<LrZeroItem> targetClosure = symbolToTargetClosure.get(symbol);
            if (targetClosure == null) {
               targetClosure = new LinkedHashSet<>();
               symbolToTargetClosure.put(symbol, targetClosure);
            }
            targetClosure.add(new LrZeroItem(item.getProduction(), item.getDot() + 1));
         }
      }
      for (Entry<AbstractSymbol, Set<LrZeroItem>> entry : symbolToTargetClosure.entrySet()) {
         symbolToTargetClosure.put(entry.getKey(), closureFunction.apply(grammar, entry.getValue()));
      }
   }
}
