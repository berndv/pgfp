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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.Variable;

public class LrZeroStateGraphFactory {

   private final LrZeroClosureFunction closureFunction = new LrZeroClosureFunction();

   private final LrZeroGotoFunction gotoFunction = new LrZeroGotoFunction(closureFunction);

   public List<LrZeroState> create(Grammar grammar) {
      List<LrZeroState> states = new ArrayList<>();

      Variable startSymbol = grammar.getStartSymbol();
      Production start = new Production(new Variable("<start>"), Collections.singletonList(startSymbol),
         "<start> = " + startSymbol);

      getState(states, grammar, closureFunction.apply(grammar, new LrZeroItem(start, 0)));

      return states;
   }

   private LrZeroState getState(List<LrZeroState> states, Grammar grammar, Set<LrZeroItem> closure) {
      LrZeroState state = findState(states, closure);
      if (state == null) {
         state = new LrZeroState();
         state.setId(states.size());
         state.setClosure(closure);
         states.add(state);

         final Set<Entry<AbstractSymbol, Set<LrZeroItem>>> gotos = gotoFunction.apply(grammar, closure).entrySet();
         final Map<AbstractSymbol, LrZeroState> transitions = new LinkedHashMap<>(gotos.size());
         for (Entry<AbstractSymbol, Set<LrZeroItem>> entry : gotos) {
            final AbstractSymbol symbol = entry.getKey();
            final Set<LrZeroItem> gotoClosure = entry.getValue();
            final LrZeroState gotoState = getState(states, grammar, gotoClosure);
            transitions.put(symbol, gotoState);
         }
         state.setTransitions(transitions);
      }
      return state;
   }

   private LrZeroState findState(List<LrZeroState> states, Set<LrZeroItem> closure) {
      for (LrZeroState state : states) {
         if (state.getClosure().equals(closure)) {
            return state;
         }
      }
      return null;
   }
}
