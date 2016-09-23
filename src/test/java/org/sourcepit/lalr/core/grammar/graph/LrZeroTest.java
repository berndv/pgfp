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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class LrZeroTest {
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

   private class State {
      private int index;

      private Set<LrZeroItem> closure;

      private Map<AbstractSymbol, State> gotoMap;

      public void setClosure(Set<LrZeroItem> closure) {
         this.closure = closure;
      }

      public Set<LrZeroItem> getClosure() {
         return closure;
      }

      public void setGotoMap(Map<AbstractSymbol, State> gotoMap) {
         this.gotoMap = gotoMap;
      }

      public Map<AbstractSymbol, State> getGotoMap() {
         return gotoMap;
      }

      @Override
      public String toString() {
         StringBuilder builder = new StringBuilder();
         builder.append("State [index=");
         builder.append(index);
         builder.append(", ");
         if (closure != null) {
            builder.append("closure=");
            builder.append(closure);
            builder.append(", ");
         }
         if (gotoMap != null) {
            builder.append("gotoMap=[");

            boolean first = true;
            for (Entry<AbstractSymbol, State> entry : gotoMap.entrySet()) {
               if (first) {
                  first = false;
               }
               else {
                  builder.append(", ");
               }
               builder.append(entry.getKey());
               builder.append("->");
               builder.append(entry.getValue().index);
            }

            builder.append("]");
         }
         builder.append("]");
         return builder.toString();
      }


   }

   private class Goto {
      private List<State> states = new ArrayList<>();

      public Goto(Grammar grammar) {
         Variable startSymbol = grammar.getStartSymbol();

         Production start = new Production(new Variable("<start>"), Collections.singletonList(startSymbol),
            "<start> = " + startSymbol);

         Set<LrZeroItem> closure = closure(grammar, new LrZeroItem(start, 0));
         getState(grammar, closure);
      }

      private State getState(Grammar grammar, Set<LrZeroItem> closure) {
         State state = findState(closure);
         if (state == null) {
            state = new State();
            state.index = states.size();
            state.setClosure(closure);
            states.add(state);
            final Set<Entry<AbstractSymbol, Set<LrZeroItem>>> gotos = gotos(grammar, closure).entrySet();
            final Map<AbstractSymbol, State> gotoMap = new LinkedHashMap<>(gotos.size());
            for (Entry<AbstractSymbol, Set<LrZeroItem>> entry : gotos) {
               final AbstractSymbol symbol = entry.getKey();
               final Set<LrZeroItem> gotoClosure = entry.getValue();
               final State gotoState = getState(grammar, gotoClosure);
               gotoMap.put(symbol, gotoState);
            }
            state.setGotoMap(gotoMap);
         }
         return state;
      }

      private State findState(Set<LrZeroItem> closure) {
         for (State state : states) {
            if (state.getClosure().equals(closure)) {
               return state;
            }
         }
         return null;
      }

   }

   @Test
   public void testExample1() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A A"));
      productions.add(syntax.parseProduction("A = a A"));
      productions.add(syntax.parseProduction("A = b"));

      GrammarGraph graph = newGrammarGraph(productions);
      Grammar grammar = graph.getGrammar();

      List<LrZeroState> states = new LrZeroStateGraphFactory().create(grammar);
      for (LrZeroState lrZeroState : states) {
         System.out.println(lrZeroState);
      }

      System.out.println();

      int p = 0;
      for (Production production : grammar.getProductions()) {
         System.out.println(p + " " + production);
         p++;
      }

      System.out.println();

      FooParsingTableBuilder tblb = new FooParsingTableBuilder();
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

      String[][] tbl = tblb.tbl;

      System.out.println(grammar.getTerminals() + " " + grammar.getVariables());

      int i = 0;
      for (String[] strings : tbl) {
         System.out.println(i + " " + Arrays.toString(strings));
         i++;
      }

      System.out.println();


   }

   private Map<AbstractSymbol, Set<LrZeroItem>> gotos(Grammar grammar, Set<LrZeroItem> closure) {
      Map<AbstractSymbol, Set<LrZeroItem>> gotos = new LinkedHashMap<>();
      for (LrZeroItem item : closure) {
         if (!item.isFinal()) {
            final AbstractSymbol symbol = item.getExpectedSymbol();

            Set<LrZeroItem> gotoClosure = gotos.get(symbol);
            if (gotoClosure == null) {
               gotoClosure = new LinkedHashSet<>();
               gotos.put(symbol, gotoClosure);
            }

            LrZeroItem lrZeroItem = new LrZeroItem(item.getProduction(), item.getDot() + 1);
            closure(grammar, gotoClosure, lrZeroItem);
         }
      }
      return gotos;
   }

   private Set<LrZeroItem> closure(Grammar grammar, LrZeroItem ssItem) {
      final LinkedHashSet<LrZeroItem> closure = new LinkedHashSet<>();
      closure(grammar, closure, ssItem);
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
