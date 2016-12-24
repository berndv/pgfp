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
import static org.sourcepit.lalr.core.grammar.Terminator.Terminating.MAYBE;
import static org.sourcepit.lalr.core.grammar.Terminator.Terminating.NO;
import static org.sourcepit.lalr.core.grammar.Terminator.Terminating.YES;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TerminatorTest {

   @Test
   public void test1() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("B = "));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(YES, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(0)));
      assertEquals(YES, terminator.isTerminating(grammar.getVariable("B")));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(1)));
   }

   @Test
   public void test2() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = A"));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(NO, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(0)));
   }

   @Test
   public void test3() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = A"));
      productions.add(syntax.parseProduction("A = "));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(YES, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(0)));
   }

   @Test
   public void test4() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("B = C"));
      productions.add(syntax.parseProduction("C = A"));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(NO, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(0)));
      assertEquals(NO, terminator.isTerminating(grammar.getVariable("B")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(1)));
      assertEquals(NO, terminator.isTerminating(grammar.getVariable("C")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(2)));
   }

   @Test
   public void test5() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = A a"));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(NO, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(0)));
   }
   
   @Test
   public void test9() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = A a"));
      productions.add(syntax.parseProduction("A = a"));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(YES, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(0)));
   }

   @Test
   public void test6() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("A = A a"));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(NO, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(0)));
      assertEquals(NO, terminator.isTerminating(grammar.getVariable("B")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(1)));
   }

   @Test
   public void test7() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = A a"));
      productions.add(syntax.parseProduction("B = A B"));
      productions.add(syntax.parseProduction("B = b"));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(NO, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(0)));
      assertEquals(MAYBE, terminator.isTerminating(grammar.getVariable("B")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(1)));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(2)));
   }

   @Test
   public void test8() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("B = B"));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(YES, terminator.isTerminating(grammar.getVariable("A")));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(0)));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(1)));
      assertEquals(YES, terminator.isTerminating(grammar.getVariable("B")));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(2)));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(3)));
   }
   
   @Test
   public void test10() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = A a"));
      productions.add(syntax.parseProduction("B = A B"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = B"));

      Grammar grammar = new Grammar(syntax, productions);

      Terminator terminator = new Terminator(grammar);

      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(0)));
      assertEquals(MAYBE, terminator.isTerminating(grammar.getVariable("B")));
      assertEquals(NO, terminator.isTerminating(grammar.getProductions().get(1)));
      assertEquals(YES, terminator.isTerminating(grammar.getProductions().get(2)));
      assertEquals(MAYBE, terminator.isTerminating(grammar.getProductions().get(3)));
   }
}
