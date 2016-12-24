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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.sourcepit.lalr.core.grammar.Terminator.Terminating.NO;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class NullableTest {
   @Test
   public void testRecursive() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = b"));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      assertFalse(nullable.isNullable(grammar.getVariable("A")));
      assertFalse(nullable.isNullable(productions.get(0)));
      assertFalse(nullable.isNullable(productions.get(1)));
   }

   public static void assertIsDeterministic(Grammar grammar) {
      Terminator terminator = new Terminator(grammar);
      assertTrue(terminator.getProductions(NO).isEmpty());
   }

   @Test
   public void test1() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = a B"));
      productions.add(syntax.parseProduction("B = C D"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("C = C"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("C = "));
      productions.add(syntax.parseProduction("D = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      assertFalse(nullable.isNullable(grammar.getVariable("S")));
      assertFalse(nullable.isNullable(productions.get(0)));

      assertTrue(nullable.isNullable(grammar.getVariable("B")));
      assertTrue(nullable.isNullable(productions.get(1)));
      assertFalse(nullable.isNullable(productions.get(2)));

      assertTrue(nullable.isNullable(grammar.getVariable("C")));
      assertTrue(nullable.isNullable(productions.get(3)));
      assertFalse(nullable.isNullable(productions.get(4)));
      assertTrue(nullable.isNullable(productions.get(5)));

      assertTrue(nullable.isNullable(grammar.getVariable("D")));
      assertTrue(nullable.isNullable(productions.get(6)));
   }

   @Test
   public void test2() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = a B"));
      productions.add(syntax.parseProduction("B = C D"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("C = C"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("C = "));
      productions.add(syntax.parseProduction("D = d"));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      assertFalse(nullable.isNullable(grammar.getVariable("S")));
      assertFalse(nullable.isNullable(productions.get(0)));
      
      assertFalse(nullable.isNullable(grammar.getVariable("B")));
      assertFalse(nullable.isNullable(productions.get(1)));
      assertFalse(nullable.isNullable(productions.get(2)));
      
      assertTrue(nullable.isNullable(grammar.getVariable("C")));
      assertTrue(nullable.isNullable(productions.get(3)));
      assertFalse(nullable.isNullable(productions.get(4)));
      assertTrue(nullable.isNullable(productions.get(5)));
      
      assertFalse(nullable.isNullable(grammar.getVariable("D")));
      assertFalse(nullable.isNullable(productions.get(6)));
   }

   @Test
   public void test3() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      assertTrue(nullable.isNullable(grammar.getVariable("S")));
      assertTrue(nullable.isNullable(productions.get(0)));
      
      assertTrue(nullable.isNullable(grammar.getVariable("A")));
      assertFalse(nullable.isNullable(productions.get(1)));
      assertTrue(nullable.isNullable(productions.get(2)));
   }

   @Test
   public void test4() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = A"));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      assertFalse(nullable.isNullable(grammar.getVariable("A")));
      assertFalse(nullable.isNullable(productions.get(0)));
      assertFalse(nullable.isNullable(productions.get(1)));
      
      assertFalse(nullable.isNullable(grammar.getVariable("B")));
      assertFalse(nullable.isNullable(productions.get(2)));
      assertFalse(nullable.isNullable(productions.get(3)));
   }

   @Test
   public void test5() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("B = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      assertTrue(nullable.isNullable(grammar.getVariable("A")));
      assertTrue(nullable.isNullable(productions.get(0)));
      assertFalse(nullable.isNullable(productions.get(1)));
      
      assertTrue(nullable.isNullable(grammar.getVariable("B")));
      assertFalse(nullable.isNullable(productions.get(2)));
      assertTrue(nullable.isNullable(productions.get(3)));
      assertTrue(nullable.isNullable(productions.get(4)));
   }

   @Test
   public void test6() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = "));
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = a"));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);
      
      assertTrue(nullable.isNullable(grammar.getVariable("B")));
      assertTrue(nullable.isNullable(productions.get(0)));
      assertFalse(nullable.isNullable(productions.get(1)));
      assertTrue(nullable.isNullable(productions.get(2)));
      
      assertTrue(nullable.isNullable(grammar.getVariable("A")));
      assertTrue(nullable.isNullable(productions.get(3)));
      assertFalse(nullable.isNullable(productions.get(4)));
   }

   @Test
   public void test7() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("B = C"));
      productions.add(syntax.parseProduction("C = A"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("C = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      assertTrue(nullable.isNullable(grammar.getVariable("A")));
      assertTrue(nullable.isNullable(productions.get(0)));
      
      assertTrue(nullable.isNullable(grammar.getVariable("B")));
      assertTrue(nullable.isNullable(productions.get(1)));

      assertTrue(nullable.isNullable(grammar.getVariable("C")));
      assertTrue(nullable.isNullable(productions.get(2)));
      assertFalse(nullable.isNullable(productions.get(3)));
      assertTrue(nullable.isNullable(productions.get(4)));
   }

   @Test
   public void test8() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = A B"));
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = "));

      Grammar grammar = new Grammar(syntax, productions);
      assertIsDeterministic(grammar);

      Nullable nullable = new Nullable(grammar);

      assertTrue(nullable.isNullable(grammar.getVariable("A")));
      assertTrue(nullable.isNullable(productions.get(0)));
      assertTrue(nullable.isNullable(productions.get(1)));
      
      assertTrue(nullable.isNullable(grammar.getVariable("B")));
      assertTrue(nullable.isNullable(productions.get(2)));
      assertFalse(nullable.isNullable(productions.get(3)));
      assertTrue(nullable.isNullable(productions.get(4)));
   }


}
