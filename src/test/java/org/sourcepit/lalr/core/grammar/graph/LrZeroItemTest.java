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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Variable;

public class LrZeroItemTest {

   @Test
   public void test() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A A"));
      productions.add(syntax.parseProduction("A = a A"));
      productions.add(syntax.parseProduction("A = ε"));

      Grammar grammar = new Grammar(productions);

      Variable s = grammar.getVariable("S");
      Variable a = grammar.getVariable("A");

      Production production = grammar.getProductions(s).get(0);

      LrZeroItem item = new LrZeroItem(production, 0);
      assertFalse(item.isFinal());
      assertEquals(production, item.getProduction());
      assertEquals(0, item.getDot());
      assertEquals(a, item.getExpectedSymbol());
      assertEquals("S = .A A", item.toString());
      assertEquals(new LrZeroItem(production, 0), item);
      assertNotEquals(new LrZeroItem(production, 1), item);

      item = new LrZeroItem(production, 2);
      assertTrue(item.isFinal());
      assertEquals(production, item.getProduction());
      assertEquals(2, item.getDot());
      assertNull(item.getExpectedSymbol());
      assertEquals("S = A A.", item.toString());
      assertEquals(new LrZeroItem(production, 2), item);
      assertNotEquals(new LrZeroItem(production, 1), item);

      Production empty = grammar.getProductions(a).get(1);
      assertTrue(empty.isEmpty());

      item = new LrZeroItem(empty, 0);
      assertTrue(item.isFinal());
      assertEquals(empty, item.getProduction());
      assertEquals(0, item.getDot());
      assertNull(item.getExpectedSymbol());
      assertEquals("A = .", item.toString());
      assertEquals(new LrZeroItem(empty, 0), item);
      assertNotEquals(new LrZeroItem(empty, 1), item);
   }

}
