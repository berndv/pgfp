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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;
import org.sourcepit.lalr.core.grammar.graph.DetermineFollowGrammarGraphVisitor;
import org.sourcepit.lalr.core.grammar.graph.DetermineNullableGrammarGraphVisitor;
import org.sourcepit.lalr.core.grammar.graph.GrammarGraph;
import org.sourcepit.lalr.core.lr.one.LrOneClosureFunction;
import org.sourcepit.lalr.core.lr.one.LrOneItem;

public class LrOneClosureFunctionTest {
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

      LrOneClosureFunction cf = new LrOneClosureFunction();

      final Terminal eof = syntax.getEofTerminal();

      Set<LrOneItem> inputItems;
      Set<LrOneItem> closure;

      // START = .S, $
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pStart, 0, eof));
      closure = cf.apply(graph, inputItems);
      assertEquals("[START = .S, [$], S = .A A, [$], A = .a A, [a, b], A = .b, [a, b]]", closure.toString());

      // START = S., $
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pStart, 1, eof));
      closure = cf.apply(graph, inputItems);
      assertEquals("[START = S., [$]]", closure.toString());

      // S = A .A, $
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pS, 1, eof));
      closure = cf.apply(graph, inputItems);
      assertEquals("[S = A .A, [$], A = .a A, [$], A = .b, [$]]", closure.toString());

      // S = A A., $
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pS, 2, eof));
      closure = cf.apply(graph, inputItems);
      assertEquals("[S = A A., [$]]", closure.toString());

      // A = a .A, $
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pA1, 1, eof));
      closure = cf.apply(graph, inputItems);
      assertEquals("[A = a .A, [$], A = .a A, [$], A = .b, [$]]", closure.toString());

      // A = a .A, a, b
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pA1, 1, tA, tB));
      closure = cf.apply(graph, inputItems);
      assertEquals("[A = a .A, [a, b], A = .a A, [a, b], A = .b, [a, b]]", closure.toString());

      // A = a A., a, b
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pA1, 2, tA, tB));
      closure = cf.apply(graph, inputItems);
      assertEquals("[A = a A., [a, b]]", closure.toString());

      // A = b., $
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pA2, 1, eof));
      closure = cf.apply(graph, inputItems);
      assertEquals("[A = b., [$]]", closure.toString());

      // A = b., a, b
      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pA2, 1, tA, tB));
      closure = cf.apply(graph, inputItems);
      assertEquals("[A = b., [a, b]]", closure.toString());
   }

   @Test
   public void testAmbiguous() {

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A b"));
      productions.add(syntax.parseProduction("S = A c"));
      productions.add(syntax.parseProduction("A = a"));

      GrammarGraph graph = newGrammarGraph(productions);
      Grammar grammar = graph.getGrammar();

      Terminal tB = grammar.getTerminals().get(0);
      assertEquals("b", tB.toString());
      Terminal tC = grammar.getTerminals().get(1);
      assertEquals("c", tC.toString());
      Terminal tA = grammar.getTerminals().get(2);
      assertEquals("a", tA.toString());

      Variable vS = grammar.getVariable("S");
      Variable vA = grammar.getVariable("A");

      Production pS1 = grammar.getProductions(vS).get(0);
      assertEquals("S = A b", pS1.toString());
      Production pS2 = grammar.getProductions(vS).get(1);
      assertEquals("S = A c", pS2.toString());
      Production pA = grammar.getProductions(vA).get(0);
      assertEquals("A = a", pA.toString());

      LrOneClosureFunction cf = new LrOneClosureFunction();

      Set<LrOneItem> inputItems;
      Set<LrOneItem> closure;

      final Terminal eof = syntax.getEofTerminal();

      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pS1, 0, eof));
      inputItems.add(LrOneItem.create(pS2, 0, eof));
      closure = cf.apply(graph, inputItems);
      assertEquals("[S = .A b, [$], S = .A c, [$], A = .a, [b, c]]", closure.toString());
   }

   @Test
   public void testAmbiguous2() {

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A"));
      productions.add(syntax.parseProduction("S = A s"));
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = B c"));
      productions.add(syntax.parseProduction("A = B d"));
      productions.add(syntax.parseProduction("B = b"));

      GrammarGraph graph = newGrammarGraph(productions);
      Grammar grammar = graph.getGrammar();

      Terminal tS = grammar.getTerminals().get(0);
      assertEquals("s", tS.toString());
      Terminal tA = grammar.getTerminals().get(1);
      assertEquals("a", tA.toString());
      Terminal tC = grammar.getTerminals().get(2);
      assertEquals("c", tC.toString());
      Terminal tD = grammar.getTerminals().get(3);
      assertEquals("d", tD.toString());
      Terminal tB = grammar.getTerminals().get(4);
      assertEquals("b", tB.toString());

      Variable vS = grammar.getVariable("S");
      Variable vA = grammar.getVariable("A");
      Variable vB = grammar.getVariable("B");

      Production pS1 = grammar.getProductions(vS).get(0);
      assertEquals("S = A", pS1.toString());
      Production pS2 = grammar.getProductions(vS).get(1);
      assertEquals("S = A s", pS2.toString());
      Production pA1 = grammar.getProductions(vA).get(0);
      assertEquals("A = B", pA1.toString());
      Production pA2 = grammar.getProductions(vA).get(1);
      assertEquals("A = a", pA2.toString());
      Production pA3 = grammar.getProductions(vA).get(2);
      assertEquals("A = B c", pA3.toString());
      Production pA4 = grammar.getProductions(vA).get(3);
      assertEquals("A = B d", pA4.toString());
      Production pB = grammar.getProductions(vB).get(0);
      assertEquals("B = b", pB.toString());

      LrOneClosureFunction cf = new LrOneClosureFunction();

      Set<LrOneItem> inputItems;
      Set<LrOneItem> closure;

      final Terminal eof = syntax.getEofTerminal();

      inputItems = new LinkedHashSet<>();
      inputItems.add(LrOneItem.create(pS1, 0, eof));
      inputItems.add(LrOneItem.create(pS2, 0, eof));
      closure = cf.apply(graph, inputItems);
      assertEquals(
         "[S = .A, [$], S = .A s, [$], A = .B, [$, s], B = .b, [$, s, c, d], A = .a, [$, s], A = .B c, [$, s], A = .B d, [$, s]]",
         closure.toString());
   }

}
