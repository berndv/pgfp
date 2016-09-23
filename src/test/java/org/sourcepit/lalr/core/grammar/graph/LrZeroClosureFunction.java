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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.Variable;

public class LrZeroClosureFunction implements BiFunction<Grammar, LrZeroItem, Set<LrZeroItem>> {

   @Override
   public Set<LrZeroItem> apply(Grammar grammar, LrZeroItem item) {
      final LinkedHashSet<LrZeroItem> closure = new LinkedHashSet<>();
      closure(grammar, closure, item);
      return closure;
   }

   private void closure(Grammar grammar, Set<LrZeroItem> closure, LrZeroItem item) {
      if (closure.add(item) && !item.isFinal()) {
         final AbstractSymbol symbol = item.getExpectedSymbol();
         if (symbol instanceof Variable) {
            final Variable variable = (Variable) symbol;
            for (Production production : grammar.getProductions(variable)) {
               closure(grammar, closure, new LrZeroItem(production, 0));
            }
         }
      }
   }
}
