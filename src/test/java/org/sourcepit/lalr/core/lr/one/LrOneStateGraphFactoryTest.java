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
import org.sourcepit.lalr.core.grammar.graph.DetermineFollowGrammarGraphVisitor;
import org.sourcepit.lalr.core.grammar.graph.DetermineNullableGrammarGraphVisitor;
import org.sourcepit.lalr.core.grammar.graph.GrammarGraph;
import org.sourcepit.lalr.core.lr.LrStateGraph;

public class LrOneStateGraphFactoryTest {
   private final Syntax syntax = new SimpleSyntax();

   private GrammarGraph newGrammarGraph(List<Production> productions) {
      final GrammarGraph graph = new GrammarGraph(new Grammar(syntax, productions));
      graph.accept(new DetermineNullableGrammarGraphVisitor());
      final DetermineFollowGrammarGraphVisitor firstAndFollow = new DetermineFollowGrammarGraphVisitor();
      graph.accept(firstAndFollow);
      for (Entry<Variable, Set<Terminal>> entry : firstAndFollow.getSymbolToFirst().entrySet()) {
         graph.getVariableNode(entry.getKey()).setFirstSet(entry.getValue());
      }
      // for (Entry<Variable, Set<Terminal>> entry : firstAndFollow.getSymbolToFollow().entrySet()) {
      // graph.getVariableNode(entry.getKey()).setFollowSet(entry.getValue());
      // }
      return graph;
   }

   @Test
   public void test() {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B C D E"));
      productions.add(syntax.parseProduction("A = "));
      productions.add(syntax.parseProduction("B = x"));
      productions.add(syntax.parseProduction("B = y"));
      productions.add(syntax.parseProduction("B = "));
      productions.add(syntax.parseProduction("C = z"));
      productions.add(syntax.parseProduction("C = h"));
      productions.add(syntax.parseProduction("C = "));
      productions.add(syntax.parseProduction("D = k"));
      productions.add(syntax.parseProduction("D = "));
      productions.add(syntax.parseProduction("E = m"));
      productions.add(syntax.parseProduction("E = n"));

      GrammarGraph grammarGraph = newGrammarGraph(productions);

      LrOneStateGraphFactory stateGraphFactory = new LrOneStateGraphFactory();

      LrStateGraph<LrOneItem> stateGraph = stateGraphFactory.createStateGraph(grammarGraph);
      assertEquals(13, stateGraph.getStates().size());
   }

   @Test
   public void test2() {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B e"));
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("B = C D"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("C = "));
      productions.add(syntax.parseProduction("D = d"));
      productions.add(syntax.parseProduction("D = "));

      GrammarGraph grammarGraph = newGrammarGraph(productions);

      LrOneStateGraphFactory stateGraphFactory = new LrOneStateGraphFactory();

      LrStateGraph<LrOneItem> stateGraph = stateGraphFactory.createStateGraph(grammarGraph);
      assertEquals(8, stateGraph.getStates().size());
   }

   @Test
   public void testRecursive() {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = b"));

      Grammar grammar = new Grammar(syntax, productions);

      Map<Variable, Boolean> variableToNullable = new LinkedHashMap<>();
      nullable(variableToNullable, grammar);
      
      
   }

   private void nullable(Map<Variable, Boolean> variableToNullable, Grammar grammar) {
      for (Variable leftSide : grammar.getVariables()) {
         nullable(variableToNullable, grammar, leftSide);
      }
   }

   private boolean nullable(Map<Variable, Boolean> variableToNullable, Grammar grammar, Variable leftSide) {
      final Boolean oNullable = variableToNullable.get(leftSide);
      if (oNullable != null) {
         return oNullable.booleanValue();
      }

      boolean nullable = true;
      for (Production production : grammar.getProductions(leftSide)) {
         for (AbstractSymbol symbol : production.getRightSide()) {
            if (symbol instanceof Terminal) {
               nullable = false;
               break;
            }
            else {
               Variable var = (Variable) symbol;
               if (!var.equals(leftSide)) {
                  if (!nullable(variableToNullable, grammar, var)) {
                     nullable = false;
                     break;
                  }
               }
            }
         }
         if (!nullable) {
            break;
         }
      }
      variableToNullable.put(leftSide, Boolean.valueOf(nullable));

      return nullable;
   }

   private void first(Map<Variable, Set<Terminal>> variableToFirstSet, Production production) {

      Variable var = production.getLeftSide();

      Set<Terminal> firstSet = variableToFirstSet.get(var);
      if (firstSet == null) {
         firstSet = new LinkedHashSet<>();
         variableToFirstSet.put(var, firstSet);
      }


      boolean nullable = true;

      for (AbstractSymbol symbol : production.getRightSide()) {
         if (symbol instanceof Terminal) {
            firstSet.add((Terminal) symbol);
            nullable = false;
            break;
         }
         else {
            Variable v = (Variable) symbol;


         }
      }
   }
}
