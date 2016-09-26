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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class LrOneGotoFunctionTest {

   private final Syntax syntax = new SimpleSyntax();

   private GrammarGraph newGrammarGraph(List<Production> productions) {
      final GrammarGraph graph = new GrammarGraph(new Grammar(productions));
      graph.accept(new DetermineNullableGrammarGraphVisitor());
      final DetermineFollowGrammarGraphVisitor firstAndFollow = new DetermineFollowGrammarGraphVisitor();
      graph.accept(firstAndFollow);
      for (Entry<Variable, Set<Terminal>> entry : firstAndFollow.getSymbolToFirst().entrySet()) {
         graph.getVariableNode(entry.getKey()).setFirstSet(entry.getValue());
      }
      for (Entry<Variable, Set<Terminal>> entry : firstAndFollow.getSymbolToFollow().entrySet()) {
         graph.getVariableNode(entry.getKey()).setFollowSet(entry.getValue());
      }
      return graph;
   }

   @Test
   public void test() {

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("START = S"));
      productions.add(syntax.parseProduction("S = A A"));
      productions.add(syntax.parseProduction("A = a A"));
      productions.add(syntax.parseProduction("A = b"));

      GrammarGraph graph = newGrammarGraph(productions);
      Grammar grammar = graph.getGrammar();

      Terminal tA = grammar.getTerminals().get(0);
      assertEquals("a", tA.toString());
      Terminal tB = grammar.getTerminals().get(1);
      assertEquals("b", tB.toString());

      Variable vStart = grammar.getVariable("START");
      Variable vS = grammar.getVariable("S");
      Variable vA = grammar.getVariable("A");

      Production pStart = grammar.getProductions(vStart).get(0);
      Production pS = grammar.getProductions(vS).get(0);
      Production pA1 = grammar.getProductions(vA).get(0);
      Production pA2 = grammar.getProductions(vA).get(1);


      LrOneGotoFunction gotoFunction = new LrOneGotoFunction(new LrOneClosureFunction());

      Set<LrOneItem> inputItems;
      Map<AbstractSymbol, Set<LrOneItem>> symbolGotoClosure;

      // START = .S, $
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pStart, 0, syntax.getEofTerminal()));


      List<Set<LrOneItem>> states = new ArrayList<>();

      Set<LrOneItem> stateZero = gotoFunction.getClosureFunction().apply(graph, inputItems);
      states.add(stateZero);

      List<Map<AbstractSymbol, Integer>> transitions = new ArrayList<>();
      transitions.add(new LinkedHashMap<>());

      foo(graph, gotoFunction, states, transitions, stateZero);


      System.out.println();
   }

   private void foo(GrammarGraph graph, LrOneGotoFunction gotoFunction, List<Set<LrOneItem>> states,
      List<Map<AbstractSymbol, Integer>> transitions, Set<LrOneItem> currentState) {

      Map<AbstractSymbol, Set<LrOneItem>> symbolGotoClosure = gotoFunction.apply(graph, currentState);


      final Map<AbstractSymbol, Integer> currentTransitions = transitions.get(states.indexOf(currentState));

      final Set<Set<LrOneItem>> newStates = new LinkedHashSet<>();
      for (Entry<AbstractSymbol, Set<LrOneItem>> entry : symbolGotoClosure.entrySet()) {
         final AbstractSymbol symbol = entry.getKey();
         final Set<LrOneItem> targetState = entry.getValue();
         int targetIdx = states.indexOf(targetState);
         if (targetIdx < 0) {
            states.add(targetState);
            targetIdx = states.indexOf(targetState);
            newStates.add(targetState);
            transitions.add(new LinkedHashMap<>());
         }
         currentTransitions.put(symbol, targetIdx);
      }
      for (Set<LrOneItem> newState : newStates) {
         foo(graph, gotoFunction, states, transitions, newState);
      }
   }


}
