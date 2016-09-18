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

package org.sourcepit.lalr.core.graph;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.CoreGrammar;
import org.sourcepit.lalr.core.grammar.CoreSyntax;
import org.sourcepit.lalr.core.grammar.MetaSymbol;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleCoreSyntax;
import org.sourcepit.lalr.core.grammar.TerminalSymbol;

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

      CoreGraph graph = new CoreGraph(new CoreGrammar(productions));

      DetermineFollowCoreGraphVisitor visitor = new DetermineFollowCoreGraphVisitor();
      graph.accept(visitor);

      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFollow = visitor.getSymbolToFollow();

      MetaSymbol s = graph.getGrammar().getMetaSymbol("S");
      MetaSymbol a = graph.getGrammar().getMetaSymbol("A");
      MetaSymbol b = graph.getGrammar().getMetaSymbol("B");
      MetaSymbol c = graph.getGrammar().getMetaSymbol("C");
      MetaSymbol d = graph.getGrammar().getMetaSymbol("D");
      MetaSymbol e = graph.getGrammar().getMetaSymbol("E");

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

      CoreGraph graph = new CoreGraph(new CoreGrammar(productions));

      DetermineFollowCoreGraphVisitor visitor = new DetermineFollowCoreGraphVisitor();
      graph.accept(visitor);

      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFollow = visitor.getSymbolToFollow();

      MetaSymbol s = graph.getGrammar().getMetaSymbol("S");
      MetaSymbol b = graph.getGrammar().getMetaSymbol("B");
      MetaSymbol c = graph.getGrammar().getMetaSymbol("C");

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

      CoreGraph graph = new CoreGraph(new CoreGrammar(productions));

      DetermineFollowCoreGraphVisitor visitor = new DetermineFollowCoreGraphVisitor();
      graph.accept(visitor);

      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFollow = visitor.getSymbolToFollow();

      MetaSymbol e = graph.getGrammar().getMetaSymbol("E");
      MetaSymbol ee = graph.getGrammar().getMetaSymbol("EE");
      MetaSymbol t = graph.getGrammar().getMetaSymbol("T");
      MetaSymbol tt = graph.getGrammar().getMetaSymbol("TT");
      MetaSymbol f = graph.getGrammar().getMetaSymbol("F");

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

      CoreGraph graph = new CoreGraph(new CoreGrammar(productions));

      DetermineFollowCoreGraphVisitor visitor = new DetermineFollowCoreGraphVisitor();
      graph.accept(visitor);

      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFollow = visitor.getSymbolToFollow();

      MetaSymbol s = graph.getGrammar().getMetaSymbol("S");
      MetaSymbol a = graph.getGrammar().getMetaSymbol("A");
      MetaSymbol b = graph.getGrammar().getMetaSymbol("B");
      MetaSymbol c = graph.getGrammar().getMetaSymbol("C");

      assertEquals("[null]", symbolToFollow.get(s).toString());
      assertEquals("[h, g, null]", symbolToFollow.get(a).toString());
      assertEquals("[null, a, h, g]", symbolToFollow.get(b).toString());
      assertEquals("[g, null, b, h]", symbolToFollow.get(c).toString());
   }

   @Test
   public void testLeftRecursion() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = S s"));
      productions.add(syntax.parseProduction("S = f"));
      productions.add(syntax.parseProduction("S = ε"));

      CoreGraph graph = new CoreGraph(new CoreGrammar(productions));

      DetermineFollowCoreGraphVisitor visitor = new DetermineFollowCoreGraphVisitor();
      graph.accept(visitor);

      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFollow = visitor.getSymbolToFollow();

      MetaSymbol s = graph.getGrammar().getMetaSymbol("S");

      assertEquals("[null, s]", symbolToFollow.get(s).toString());
   }

}
