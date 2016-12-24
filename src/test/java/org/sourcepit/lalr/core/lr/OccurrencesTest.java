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

package org.sourcepit.lalr.core.lr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;

public class OccurrencesTest {

   @Test
   public void test1() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("B = C"));
      productions.add(syntax.parseProduction("C = c"));

      Grammar grammar = new Grammar(syntax, productions);

      Occurrences occurrences = new Occurrences(grammar);

      assertTrue(occurrences.occursIn( grammar.getVariable("C"), grammar.getVariable("B")));
      assertTrue(occurrences.occursIn( grammar.getVariable("C"), grammar.getVariable("A")));
   }

   @Test
   public void test2() {
      Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("B = C"));
      productions.add(syntax.parseProduction("C = A"));
      productions.add(syntax.parseProduction("C = D"));
      productions.add(syntax.parseProduction("D = d"));

      Grammar grammar = new Grammar(syntax, productions);

      Occurrences occurrences = new Occurrences(grammar);

      assertTrue(occurrences.occursIn(grammar.getVariable("A"), grammar.getVariable("A")));
      assertTrue(occurrences.occursIn(grammar.getVariable("C"), grammar.getVariable("A")));
      assertTrue(occurrences.occursIn(grammar.getVariable("A"), grammar.getVariable("C")));
      assertTrue(occurrences.occursIn(grammar.getVariable("D"), grammar.getVariable("C")));
      assertFalse(occurrences.occursIn(grammar.getVariable("A"), grammar.getVariable("D")));
   }


}
