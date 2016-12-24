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

package org.sourcepit.lalr.core.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.sourcepit.lalr.core.grammar.NullableTest.assertIsDeterministic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.graph.DetermineFirstGrammarGraphVisitor;
import org.sourcepit.lalr.core.grammar.graph.GrammarGraph;

public class FirstTest {

   @Test
   public void testExample1() throws Exception {
      SimpleSyntax syntax = new SimpleSyntax();

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

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      First first = new First(grammar, nullable);

      Variable s = grammar.getVariable("S");
      Variable a = grammar.getVariable("A");
      Variable b = grammar.getVariable("B");
      Variable c = grammar.getVariable("C");
      Variable d = grammar.getVariable("D");
      Variable e = grammar.getVariable("E");

      assertEquals("[a, b, c]", first.get(s).toString());
      assertEquals("[a]", first.get(a).toString());
      assertEquals("[b]", first.get(b).toString());
      assertEquals("[c]", first.get(c).toString());
      assertEquals("[d]", first.get(d).toString());
      assertEquals("[e]", first.get(e).toString());
   }

   @Test
   public void testExample2() throws Exception {
      SimpleSyntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = B b"));
      productions.add(syntax.parseProduction("S = C d"));
      productions.add(syntax.parseProduction("B = a B"));
      productions.add(syntax.parseProduction("B = "));
      productions.add(syntax.parseProduction("C = c C"));
      productions.add(syntax.parseProduction("C = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      First first = new First(grammar, nullable);

      Variable s = grammar.getVariable("S");
      Variable b = grammar.getVariable("B");
      Variable c = grammar.getVariable("C");

      assertEquals("[d, a, b, c]", first.get(s).toString());
      assertEquals("[a]", first.get(b).toString());
      assertEquals("[c]", first.get(c).toString());
   }

   @Test
   public void testExample3() throws Exception {
      SimpleSyntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("E = T EE"));
      productions.add(syntax.parseProduction("EE = plus T EE"));
      productions.add(syntax.parseProduction("EE = "));
      productions.add(syntax.parseProduction("T = F TT"));
      productions.add(syntax.parseProduction("TT = star F TT"));
      productions.add(syntax.parseProduction("TT = "));
      productions.add(syntax.parseProduction("F = id"));
      productions.add(syntax.parseProduction("F = lp E rp"));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      First first = new First(grammar, nullable);

      Variable e = grammar.getVariable("E");
      Variable ee = grammar.getVariable("EE");
      Variable t = grammar.getVariable("T");
      Variable tt = grammar.getVariable("TT");
      Variable f = grammar.getVariable("F");

      assertEquals("[lp, id]", first.get(e).toString());
      assertEquals("[plus]", first.get(ee).toString());
      assertEquals("[lp, id]", first.get(t).toString());
      assertEquals("[star]", first.get(tt).toString());
      assertEquals("[lp, id]", first.get(f).toString());
   }

   @Test
   public void testExample4() throws Exception {
      SimpleSyntax syntax = new SimpleSyntax();

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

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      First first = new First(grammar, nullable);

      Variable s = grammar.getVariable("S");
      Variable a = grammar.getVariable("A");
      Variable b = grammar.getVariable("B");
      Variable c = grammar.getVariable("C");

      assertEquals("[h, d, g, a, b]", first.get(s).toString());
      assertEquals("[h, d, g]", first.get(a).toString());
      assertEquals("[g]", first.get(b).toString());
      assertEquals("[h]", first.get(c).toString());
   }

   @Test
   public void testExample5() throws Exception {
      SimpleSyntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = a A B b"));
      productions.add(syntax.parseProduction("A = c"));
      productions.add(syntax.parseProduction("A = "));
      productions.add(syntax.parseProduction("B = d"));
      productions.add(syntax.parseProduction("B = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      First first = new First(grammar, nullable);

      Variable s = grammar.getVariable("S");
      Variable a = grammar.getVariable("A");
      Variable b = grammar.getVariable("B");

      assertEquals("[a]", first.get(s).toString());
      assertEquals("[c]", first.get(a).toString());
      assertEquals("[d]", first.get(b).toString());
   }


   @Test
   public void testRecursiveConflict() throws Exception {
      SimpleSyntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A"));
      productions.add(syntax.parseProduction("S = B"));
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = "));
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      First first = new First(grammar, nullable);
      
      Variable s = grammar.getVariable("S");
      Variable a = grammar.getVariable("A");
      Variable b = grammar.getVariable("B");
      
      assertEquals("[a, b]", first.get(s).toString());
      assertEquals("[a, b]", first.get(a).toString());
      assertEquals("[a, b]", first.get(b).toString());
   }

   @Test
   public void testLeftRecursion() throws Exception {
      SimpleSyntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = S s"));
      productions.add(syntax.parseProduction("S = f"));
      productions.add(syntax.parseProduction("S = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      First first = new First(grammar, nullable);

      Variable s = grammar.getVariable("S");

      assertEquals("[f, s]", first.get(s).toString());
   }
}
