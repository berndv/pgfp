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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;

public class NullableTest {
   private final Syntax syntax = new SimpleSyntax();

   private GrammarGraph newGrammarGraph(List<Production> productions) {
      final GrammarGraph graph = new GrammarGraph(new Grammar(syntax, productions));
      graph.accept(new DetermineNullableGrammarGraphVisitor());
      return graph;
   }

   @Test
   public void testNullable() {

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = a B"));
      productions.add(syntax.parseProduction("B = C D"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("C = C"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("C = "));
      productions.add(syntax.parseProduction("D = "));

      GrammarGraph graph = newGrammarGraph(productions);

      assertFalse(graph.getVariableNode(productions.get(0).getLeftSide()).isNullable());
      assertTrue(graph.getVariableNode(productions.get(1).getLeftSide()).isNullable());
      assertTrue(graph.getVariableNode(productions.get(3).getLeftSide()).isNullable());
      assertTrue(graph.getVariableNode(productions.get(6).getLeftSide()).isNullable());

      productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = a B"));
      productions.add(syntax.parseProduction("B = C D"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("C = C"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("C = "));
      productions.add(syntax.parseProduction("D = d"));

      graph = newGrammarGraph(productions);

      assertFalse(graph.getVariableNode(productions.get(0).getLeftSide()).isNullable());
      assertFalse(graph.getVariableNode(productions.get(1).getLeftSide()).isNullable());
      assertTrue(graph.getVariableNode(productions.get(3).getLeftSide()).isNullable());
      assertFalse(graph.getVariableNode(productions.get(6).getLeftSide()).isNullable());

      productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A S"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = "));

      graph = newGrammarGraph(productions);

      assertTrue(graph.getVariableNode(productions.get(0).getLeftSide()).isNullable());
      assertTrue(graph.getVariableNode(productions.get(1).getLeftSide()).isNullable());
   }
}
