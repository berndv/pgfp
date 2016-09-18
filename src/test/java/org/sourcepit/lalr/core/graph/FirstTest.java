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

public class FirstTest {

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

      DetermineFirstCoreGraphVisitor visitor = new DetermineFirstCoreGraphVisitor();
      graph.accept(visitor);
      
      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFirst = visitor.getSymbolToFirst();

      MetaSymbol s = findMetaSymbol(graph, "S");
      MetaSymbol a = findMetaSymbol(graph, "A");
      MetaSymbol b = findMetaSymbol(graph, "B");
      MetaSymbol c = findMetaSymbol(graph, "C");
      MetaSymbol d = findMetaSymbol(graph, "D");
      MetaSymbol e = findMetaSymbol(graph, "E");

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
      productions.add(syntax.parseProduction("B = ε"));
      productions.add(syntax.parseProduction("C = c C"));
      productions.add(syntax.parseProduction("C = ε"));

      CoreGraph graph = new CoreGraph(new CoreGrammar(productions));

      DetermineFirstCoreGraphVisitor visitor = new DetermineFirstCoreGraphVisitor();
      graph.accept(visitor);
      
      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFirst = visitor.getSymbolToFirst();

      MetaSymbol s = findMetaSymbol(graph, "S");
      MetaSymbol b = findMetaSymbol(graph, "B");
      MetaSymbol c = findMetaSymbol(graph, "C");

      assertEquals("[a, b, c, d]", symbolToFirst.get(s).toString());
      assertEquals("[a, null]", symbolToFirst.get(b).toString());
      assertEquals("[c, null]", symbolToFirst.get(c).toString());
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

      DetermineFirstCoreGraphVisitor visitor = new DetermineFirstCoreGraphVisitor();
      graph.accept(visitor);
      
      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFirst = visitor.getSymbolToFirst();

      MetaSymbol e = findMetaSymbol(graph, "E");
      MetaSymbol ee = findMetaSymbol(graph, "EE");
      MetaSymbol t = findMetaSymbol(graph, "T");
      MetaSymbol tt = findMetaSymbol(graph, "TT");
      MetaSymbol f = findMetaSymbol(graph, "F");

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
      productions.add(syntax.parseProduction("B = ε"));
      productions.add(syntax.parseProduction("C = h"));
      productions.add(syntax.parseProduction("C = ε"));

      CoreGraph graph = new CoreGraph(new CoreGrammar(productions));

      DetermineFirstCoreGraphVisitor visitor = new DetermineFirstCoreGraphVisitor();
      graph.accept(visitor);
      
      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFirst = visitor.getSymbolToFirst();

      MetaSymbol s = findMetaSymbol(graph, "S");
      MetaSymbol a = findMetaSymbol(graph, "A");
      MetaSymbol b = findMetaSymbol(graph, "B");
      MetaSymbol c = findMetaSymbol(graph, "C");

      assertEquals("[d, g, h, null, b, a]", symbolToFirst.get(s).toString());
      assertEquals("[d, g, h, null]", symbolToFirst.get(a).toString());
      assertEquals("[g, null]", symbolToFirst.get(b).toString());
      assertEquals("[h, null]", symbolToFirst.get(c).toString());
   }

   
   @Test
   public void testLeftRecursion() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = S s"));
      productions.add(syntax.parseProduction("S = f"));
      productions.add(syntax.parseProduction("S = ε"));

      CoreGraph graph = new CoreGraph(new CoreGrammar(productions));

      DetermineFirstCoreGraphVisitor visitor = new DetermineFirstCoreGraphVisitor();
      graph.accept(visitor);
      
      Map<MetaSymbol, Set<TerminalSymbol>> symbolToFirst = visitor.getSymbolToFirst();

      MetaSymbol s = findMetaSymbol(graph, "S");

      assertEquals("[s, f, null]", symbolToFirst.get(s).toString());
   }

   
   private MetaSymbol findMetaSymbol(CoreGraph graph, String str) {
      for (MetaSymbol symbol : graph.getGrammar().getMetaSymbols()) {
         if (str.equals(symbol.toString())) {
            return symbol;
         }
      }
      return null;
   }
}
