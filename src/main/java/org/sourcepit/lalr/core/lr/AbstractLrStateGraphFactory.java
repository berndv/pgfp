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

package org.sourcepit.lalr.core.lr;

import static java.util.Collections.singletonList;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiFunction;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Variable;

public abstract class AbstractLrStateGraphFactory<C, I> {

   private final BiFunction<C, Set<I>, Set<I>> closureFunction;

   private final BiFunction<C, Set<I>, Map<AbstractSymbol, Set<I>>> gotoFunction;

   public AbstractLrStateGraphFactory(BiFunction<C, Set<I>, Set<I>> closureFunction,
      BiFunction<C, Set<I>, Map<AbstractSymbol, Set<I>>> gotoFunction) {
      this.closureFunction = closureFunction;
      this.gotoFunction = gotoFunction;
   }

   public LrStateGraph<I> createStateGraph(C context) {

      final LrStateGraph<I> stateGraph = new LrStateGraph<>();
      final List<Set<I>> states = stateGraph.getStates();
      final List<Map<AbstractSymbol, Integer>> transitions = stateGraph.getTransitions();

      final Production startProduction = getStartProduction(context);
      stateGraph.setDerivedStartSymbol(startProduction.getLeftSide());
      
      final I startItem = newStartItem(context, startProduction);
      final Set<I> currentState = closureFunction.apply(context, Collections.singleton(startItem));

      states.add(currentState);
      transitions.add(new LinkedHashMap<>());

      createStateGraph(context, states, transitions, currentState);

      return stateGraph;
   }

   private Production getStartProduction(C context) {
      final Grammar grammar = getGrammar(context);
      final Variable startSymbol = grammar.getStartSymbol();
      final Variable derivedStartSymbol = derivedVariable(grammar, startSymbol, "start");
      final Production startProduction = new Production(derivedStartSymbol, singletonList(startSymbol),
         derivedStartSymbol + " = " + startSymbol);
      return startProduction;
   }

   protected abstract Grammar getGrammar(C context);

   protected abstract I newStartItem(C context, Production startProduction);

   private static Variable derivedVariable(Grammar grammar, final Variable context, String name) {
      final Syntax syntax = grammar.getSyntax();
      Variable derivedVariable = syntax.derivedVariable(context, name);
      int i = 1;
      while (grammar.getVariables().contains(derivedVariable)) {
         derivedVariable = syntax.derivedVariable(context, name + i);
         i++;
      }
      return derivedVariable;
   }

   private void createStateGraph(C context, List<Set<I>> states, List<Map<AbstractSymbol, Integer>> transitions,
      Set<I> currentState) {
      final Map<AbstractSymbol, Integer> currentTransitions = transitions.get(states.indexOf(currentState));
      final Map<AbstractSymbol, Set<I>> symbolToTargetState = gotoFunction.apply(context, currentState);
      final Set<Set<I>> newStates = new LinkedHashSet<>();
      for (Entry<AbstractSymbol, Set<I>> entry : symbolToTargetState.entrySet()) {
         final AbstractSymbol symbol = entry.getKey();
         final Set<I> targetState = entry.getValue();
         int idx = states.indexOf(targetState);
         if (idx < 0) {
            idx = states.size();
            states.add(targetState);
            transitions.add(currentTransitions);
            newStates.add(targetState);
         }
         currentTransitions.put(symbol, idx);
      }
      for (Set<I> newState : newStates) {
         createStateGraph(context, states, transitions, newState);
      }
   }
}
