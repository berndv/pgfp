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

import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;
import org.sourcepit.lalr.core.lr.ParsingTableBuilder;

public class LrZeroParsingTable {
   public void build(Grammar grammar, ParsingTableBuilder tblb) {
      List<LrZeroState> states = new LrZeroStateGraphFactory().create(grammar);
      tblb.startTable(grammar, states.size());
      for (LrZeroState state : states) {
         tblb.startState(state.getId());
         for (Terminal terminal : grammar.getTerminals()) {
            LrZeroState target = state.getTransitions().get(terminal);
            if (target != null) {
               tblb.shift(terminal, target.getId());
            }
            for (LrZeroItem item : state.getClosure()) {
               if (item.isFinal()) {
                  if (!item.getProduction().getLeftSide().toString().equals("<start>")) {
                     int production = grammar.getProductions().indexOf(item.getProduction());
                     tblb.reduce(terminal, production);
                  }
               }
            }
         }
         for (LrZeroItem item : state.getClosure()) {
            if (item.isFinal()) {
               int production = grammar.getProductions().indexOf(item.getProduction());
               tblb.reduce(null, production);
            }
         }
         for (Variable variable : grammar.getVariables()) {
            LrZeroState target = state.getTransitions().get(variable);
            if (target != null) {
               tblb.jump(variable, target.getId());
            }
         }

         tblb.endState(state.getId());
      }
      tblb.endTable(grammar, states.size());
   }
}
