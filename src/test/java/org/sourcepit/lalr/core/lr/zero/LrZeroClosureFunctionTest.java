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

package org.sourcepit.lalr.core.lr.zero;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Variable;
import org.sourcepit.lalr.core.lr.zero.LrZeroClosureFunction;
import org.sourcepit.lalr.core.lr.zero.LrZeroItem;

public class LrZeroClosureFunctionTest {

   @Test
   public void testAmbiguousInput() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = b C"));
      productions.add(syntax.parseProduction("B = b D"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("D = d"));

      Grammar grammar = new Grammar(syntax, productions);

      LrZeroClosureFunction cf = new LrZeroClosureFunction();

      Variable a = grammar.getVariable("A");
      Variable b = grammar.getVariable("B");

      Production pA = grammar.getProductions(a).get(0);
      Production pB = grammar.getProductions(b).get(0);

      Set<LrZeroItem> inputItems = new LinkedHashSet<>();
      inputItems.add(new LrZeroItem(pA, 1));
      inputItems.add(new LrZeroItem(pB, 1));

      Set<LrZeroItem> closure = cf.apply(grammar, inputItems);
      assertEquals("[A = b .C, B = b .D, C = .c, D = .d]", closure.toString());
   }

   @Test
   public void test() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A A"));
      productions.add(syntax.parseProduction("A = a A"));
      productions.add(syntax.parseProduction("A = b"));

      Grammar grammar = new Grammar(syntax, productions);

      LrZeroClosureFunction cf = new LrZeroClosureFunction();

      Variable s = grammar.getVariable("S");
      Variable a = grammar.getVariable("A");

      Production ps = grammar.getProductions(s).get(0);
      Production pa1 = grammar.getProductions(a).get(0);
      Production pa2 = grammar.getProductions(a).get(1);

      Set<LrZeroItem> closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(ps, 0)));
      assertEquals("[S = .A A, A = .a A, A = .b]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(ps, 1)));
      assertEquals("[S = A .A, A = .a A, A = .b]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(ps, 2)));
      assertEquals("[S = A A.]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa1, 0)));
      assertEquals("[A = .a A]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa1, 1)));
      assertEquals("[A = a .A, A = .a A, A = .b]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa1, 2)));
      assertEquals("[A = a A.]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa2, 0)));
      assertEquals("[A = .b]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa2, 1)));
      assertEquals("[A = b.]", closure.toString());
   }

   @Test
   public void testWithEmptyWord() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A A"));
      productions.add(syntax.parseProduction("A = a A"));
      productions.add(syntax.parseProduction("A = "));

      Grammar grammar = new Grammar(syntax, productions);

      LrZeroClosureFunction cf = new LrZeroClosureFunction();

      Variable s = grammar.getVariable("S");
      Variable a = grammar.getVariable("A");

      Production ps = grammar.getProductions(s).get(0);
      Production pa1 = grammar.getProductions(a).get(0);
      Production pa2 = grammar.getProductions(a).get(1);

      Set<LrZeroItem> closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(ps, 0)));
      assertEquals("[S = .A A, A = .a A, A = .]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(ps, 1)));
      assertEquals("[S = A .A, A = .a A, A = .]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(ps, 2)));
      assertEquals("[S = A A.]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa1, 0)));
      assertEquals("[A = .a A]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa1, 1)));
      assertEquals("[A = a .A, A = .a A, A = .]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa1, 2)));
      assertEquals("[A = a A.]", closure.toString());

      closure = cf.apply(grammar, Collections.singleton(new LrZeroItem(pa2, 0)));
      assertEquals("[A = .]", closure.toString());
   }

}
