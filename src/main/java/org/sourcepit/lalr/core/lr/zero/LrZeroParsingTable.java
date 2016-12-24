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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;
import org.sourcepit.lalr.core.lr.LrStateGraph;
import org.sourcepit.lalr.core.lr.ParsingTableBuilder;

public class LrZeroParsingTable {
   public void build(Grammar grammar, ParsingTableBuilder tblb) {
      final LrStateGraph<LrZeroItem> stateGraph = new LrZeroStateGraphFactory().createStateGraph(grammar);

      final List<Set<LrZeroItem>> states = stateGraph.getStates();
      final List<Map<AbstractSymbol, Integer>> transitions = stateGraph.getTransitions();

      tblb.startTable(grammar, states.size());

      for (int i = 0; i < states.size(); i++) {
         tblb.startState(i);

         final Set<LrZeroItem> currentState = states.get(i);
         final Map<AbstractSymbol, Integer> currentTransitions = transitions.get(i);

         for (Terminal terminal : grammar.getTerminals()) {
            Integer target = currentTransitions.get(terminal);
            if (target != null) {
               tblb.shift(terminal, target);
            }
            for (LrZeroItem item : currentState) {
               if (item.isFinal()) {
                  if (!item.getProduction().getLeftSide().toString().equals("<start>")) {
                     int production = grammar.getProductions().indexOf(item.getProduction());
                     tblb.reduce(terminal, production);
                  }
               }
            }
         }
         for (LrZeroItem item : currentState) {
            if (item.isFinal()) {
               int production = grammar.getProductions().indexOf(item.getProduction());
               tblb.reduce(null, production);
            }
         }
         for (Variable variable : grammar.getVariables()) {
            Integer target = currentTransitions.get(variable);
            if (target != null) {
               tblb.jump(variable, target);
            }
         }

         tblb.endState(i);
      }
      tblb.endTable(grammar, states.size());
   }
}
