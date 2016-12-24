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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;
import org.sourcepit.lalr.core.grammar.graph.DetermineFirstGrammarGraphVisitor;
import org.sourcepit.lalr.core.grammar.graph.GrammarGraph;

public class FirstTest {

   private final Syntax syntax = new SimpleSyntax();

   private GrammarGraph newGrammarGraph(List<Production> productions) {
      final GrammarGraph graph = new GrammarGraph(new Grammar(syntax, productions));
      graph.accept(new DetermineNullableGrammarGraphVisitor());
      return graph;
   }

   @Test
   public void testExample1() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A B C D E"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = "));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = "));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("D = d"));
      productions.add(syntax.parseProduction("D = "));
      productions.add(syntax.parseProduction("E = e"));
      productions.add(syntax.parseProduction("E = "));

      GrammarGraph graph = newGrammarGraph(productions);

      DetermineFirstGrammarGraphVisitor visitor = new DetermineFirstGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFirst = visitor.getSymbolToFirst();

      Variable s = graph.getGrammar().getVariable("S");
      Variable a = graph.getGrammar().getVariable("A");
      Variable b = graph.getGrammar().getVariable("B");
      Variable c = graph.getGrammar().getVariable("C");
      Variable d = graph.getGrammar().getVariable("D");
      Variable e = graph.getGrammar().getVariable("E");

      assertEquals("[a, b, c]", symbolToFirst.get(s).toString());
      assertEquals("[a, null]", symbolToFirst.get(a).toString());
      assertEquals("[b, null]", symbolToFirst.get(b).toString());
      assertEquals("[c]", symbolToFirst.get(c).toString());
      assertEquals("[d, null]", symbolToFirst.get(d).toString());
      assertEquals("[e, null]", symbolToFirst.get(e).toString());
   }

   @Test
   public void testExample2() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = B b"));
      productions.add(syntax.parseProduction("S = C d"));
      productions.add(syntax.parseProduction("B = a B"));
      productions.add(syntax.parseProduction("B = "));
      productions.add(syntax.parseProduction("C = c C"));
      productions.add(syntax.parseProduction("C = "));

      GrammarGraph graph = newGrammarGraph(productions);

      DetermineFirstGrammarGraphVisitor visitor = new DetermineFirstGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFirst = visitor.getSymbolToFirst();

      Variable s = graph.getGrammar().getVariable("S");
      Variable b = graph.getGrammar().getVariable("B");
      Variable c = graph.getGrammar().getVariable("C");

      assertEquals("[a, b, c, d]", symbolToFirst.get(s).toString());
      assertEquals("[a, null]", symbolToFirst.get(b).toString());
      assertEquals("[c, null]", symbolToFirst.get(c).toString());
   }

   @Test
   public void testExample3() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("E = T EE"));
      productions.add(syntax.parseProduction("EE = plus T EE"));
      productions.add(syntax.parseProduction("EE = "));
      productions.add(syntax.parseProduction("T = F TT"));
      productions.add(syntax.parseProduction("TT = star F TT"));
      productions.add(syntax.parseProduction("TT = "));
      productions.add(syntax.parseProduction("F = id"));
      productions.add(syntax.parseProduction("F = lp E rp"));

      GrammarGraph graph = newGrammarGraph(productions);

      DetermineFirstGrammarGraphVisitor visitor = new DetermineFirstGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFirst = visitor.getSymbolToFirst();

      Variable e = graph.getGrammar().getVariable("E");
      Variable ee = graph.getGrammar().getVariable("EE");
      Variable t = graph.getGrammar().getVariable("T");
      Variable tt = graph.getGrammar().getVariable("TT");
      Variable f = graph.getGrammar().getVariable("F");

      assertEquals("[id, lp]", symbolToFirst.get(e).toString());
      assertEquals("[plus, null]", symbolToFirst.get(ee).toString());
      assertEquals("[id, lp]", symbolToFirst.get(t).toString());
      assertEquals("[star, null]", symbolToFirst.get(tt).toString());
      assertEquals("[id, lp]", symbolToFirst.get(f).toString());
   }

   @Test
   public void testExample4() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A C B"));
      productions.add(syntax.parseProduction("S = C b B"));
      productions.add(syntax.parseProduction("S = B a"));
      productions.add(syntax.parseProduction("A = d a"));
      productions.add(syntax.parseProduction("A = B C"));
      productions.add(syntax.parseProduction("B = g"));
      productions.add(syntax.parseProduction("B = "));
      productions.add(syntax.parseProduction("C = h"));
      productions.add(syntax.parseProduction("C = "));

      GrammarGraph graph = newGrammarGraph(productions);

      DetermineFirstGrammarGraphVisitor visitor = new DetermineFirstGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFirst = visitor.getSymbolToFirst();

      Variable s = graph.getGrammar().getVariable("S");
      Variable a = graph.getGrammar().getVariable("A");
      Variable b = graph.getGrammar().getVariable("B");
      Variable c = graph.getGrammar().getVariable("C");

      assertEquals("[d, g, h, null, b, a]", symbolToFirst.get(s).toString());
      assertEquals("[d, g, h, null]", symbolToFirst.get(a).toString());
      assertEquals("[g, null]", symbolToFirst.get(b).toString());
      assertEquals("[h, null]", symbolToFirst.get(c).toString());
   }

   @Test
   public void testExample5() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = a A B b"));
      productions.add(syntax.parseProduction("A = c"));
      productions.add(syntax.parseProduction("A = "));
      productions.add(syntax.parseProduction("B = d"));
      productions.add(syntax.parseProduction("B = "));

      GrammarGraph graph = newGrammarGraph(productions);

      DetermineFirstGrammarGraphVisitor visitor = new DetermineFirstGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFirst = visitor.getSymbolToFirst();

      Variable s = graph.getGrammar().getVariable("S");
      Variable a = graph.getGrammar().getVariable("A");
      Variable b = graph.getGrammar().getVariable("B");

      assertEquals("[a]", symbolToFirst.get(s).toString());
      assertEquals("[c, null]", symbolToFirst.get(a).toString());
      assertEquals("[d, null]", symbolToFirst.get(b).toString());
   }

   @Test
   public void testRecursiveConflict() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A"));
      productions.add(syntax.parseProduction("S = B"));
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = "));
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = "));

      GrammarGraph graph = newGrammarGraph(productions);

      DetermineFirstGrammarGraphVisitor visitor = new DetermineFirstGrammarGraphVisitor();
      try {
         graph.accept(visitor);
         fail();
      }
      catch (IllegalStateException e) {
      }
   }

   @Test
   public void testLeftRecursion() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = S s"));
      productions.add(syntax.parseProduction("S = f"));
      productions.add(syntax.parseProduction("S = "));

      GrammarGraph graph = newGrammarGraph(productions);

      DetermineFirstGrammarGraphVisitor visitor = new DetermineFirstGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFirst = visitor.getSymbolToFirst();

      Variable s = graph.getGrammar().getVariable("S");

      assertEquals("[s, f, null]", symbolToFirst.get(s).toString());
   }


}
