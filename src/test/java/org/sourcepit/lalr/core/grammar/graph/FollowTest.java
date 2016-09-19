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
import org.sourcepit.lalr.core.grammar.CoreSyntax;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleCoreSyntax;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;
import org.sourcepit.lalr.core.grammar.graph.DetermineFollowGrammarGraphVisitor;
import org.sourcepit.lalr.core.grammar.graph.GrammarGraph;

public class FollowTest {
   private final CoreSyntax syntax = new SimpleCoreSyntax();

   @Test
   public void testExample1() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A B C D E"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = ε"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = ε"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("D = d"));
      productions.add(syntax.parseProduction("D = ε"));
      productions.add(syntax.parseProduction("E = e"));
      productions.add(syntax.parseProduction("E = ε"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

      DetermineFollowGrammarGraphVisitor visitor = new DetermineFollowGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFollow = visitor.getSymbolToFollow();

      Variable s = graph.getGrammar().getVariable("S");
      Variable a = graph.getGrammar().getVariable("A");
      Variable b = graph.getGrammar().getVariable("B");
      Variable c = graph.getGrammar().getVariable("C");
      Variable d = graph.getGrammar().getVariable("D");
      Variable e = graph.getGrammar().getVariable("E");

      assertEquals("[null]", symbolToFollow.get(s).toString());
      assertEquals("[b, c]", symbolToFollow.get(a).toString());
      assertEquals("[c]", symbolToFollow.get(b).toString());
      assertEquals("[d, e, null]", symbolToFollow.get(c).toString());
      assertEquals("[e, null]", symbolToFollow.get(d).toString());
      assertEquals("[null]", symbolToFollow.get(e).toString());
   }

   @Test
   public void testExample2() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = B b"));
      productions.add(syntax.parseProduction("S = C d"));
      productions.add(syntax.parseProduction("B = a B"));
      productions.add(syntax.parseProduction("B = ε"));
      productions.add(syntax.parseProduction("C = c C"));
      productions.add(syntax.parseProduction("C = ε"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

      DetermineFollowGrammarGraphVisitor visitor = new DetermineFollowGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFollow = visitor.getSymbolToFollow();

      Variable s = graph.getGrammar().getVariable("S");
      Variable b = graph.getGrammar().getVariable("B");
      Variable c = graph.getGrammar().getVariable("C");

      assertEquals("[null]", symbolToFollow.get(s).toString());
      assertEquals("[b]", symbolToFollow.get(b).toString());
      assertEquals("[d]", symbolToFollow.get(c).toString());
   }

   @Test
   public void testExample3() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("E = T EE"));
      productions.add(syntax.parseProduction("EE = plus T EE"));
      productions.add(syntax.parseProduction("EE = ε"));
      productions.add(syntax.parseProduction("T = F TT"));
      productions.add(syntax.parseProduction("TT = star F TT"));
      productions.add(syntax.parseProduction("TT = ε"));
      productions.add(syntax.parseProduction("F = id"));
      productions.add(syntax.parseProduction("F = lp E rp"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

      DetermineFollowGrammarGraphVisitor visitor = new DetermineFollowGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFollow = visitor.getSymbolToFollow();

      Variable e = graph.getGrammar().getVariable("E");
      Variable ee = graph.getGrammar().getVariable("EE");
      Variable t = graph.getGrammar().getVariable("T");
      Variable tt = graph.getGrammar().getVariable("TT");
      Variable f = graph.getGrammar().getVariable("F");

      assertEquals("[null, rp]", symbolToFollow.get(e).toString());
      assertEquals("[null, rp]", symbolToFollow.get(ee).toString());
      assertEquals("[plus, null, rp]", symbolToFollow.get(t).toString());
      assertEquals("[plus, null, rp]", symbolToFollow.get(tt).toString());
      assertEquals("[star, plus, null, rp]", symbolToFollow.get(f).toString());
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
      productions.add(syntax.parseProduction("B = ε"));
      productions.add(syntax.parseProduction("C = h"));
      productions.add(syntax.parseProduction("C = ε"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

      DetermineFollowGrammarGraphVisitor visitor = new DetermineFollowGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFollow = visitor.getSymbolToFollow();

      Variable s = graph.getGrammar().getVariable("S");
      Variable a = graph.getGrammar().getVariable("A");
      Variable b = graph.getGrammar().getVariable("B");
      Variable c = graph.getGrammar().getVariable("C");

      assertEquals("[null]", symbolToFollow.get(s).toString());
      assertEquals("[h, g, null]", symbolToFollow.get(a).toString());
      assertEquals("[null, a, h, g]", symbolToFollow.get(b).toString());
      assertEquals("[g, null, b, h]", symbolToFollow.get(c).toString());
   }

   @Test
   public void testExample5() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = a A B b"));
      productions.add(syntax.parseProduction("A = c"));
      productions.add(syntax.parseProduction("A = ε"));
      productions.add(syntax.parseProduction("B = d"));
      productions.add(syntax.parseProduction("B = ε"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

      DetermineFollowGrammarGraphVisitor visitor = new DetermineFollowGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFollow = visitor.getSymbolToFollow();

      Variable s = graph.getGrammar().getVariable("S");
      Variable a = graph.getGrammar().getVariable("A");
      Variable b = graph.getGrammar().getVariable("B");

      assertEquals("[null]", symbolToFollow.get(s).toString());
      assertEquals("[d, b]", symbolToFollow.get(a).toString());
      assertEquals("[b]", symbolToFollow.get(b).toString());
   }

   @Test
   public void testRecursiveConflict() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A c"));
      productions.add(syntax.parseProduction("S = B f"));
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = ε"));
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = ε"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

      DetermineFollowGrammarGraphVisitor visitor = new DetermineFollowGrammarGraphVisitor();

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
      productions.add(syntax.parseProduction("S = ε"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

      DetermineFollowGrammarGraphVisitor visitor = new DetermineFollowGrammarGraphVisitor();
      graph.accept(visitor);

      Map<Variable, Set<Terminal>> symbolToFollow = visitor.getSymbolToFollow();

      Variable s = graph.getGrammar().getVariable("S");

      assertEquals("[null, s]", symbolToFollow.get(s).toString());
   }

}
