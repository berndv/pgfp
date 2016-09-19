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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.CoreSyntax;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleCoreSyntax;

public class GrammarGraphTest {
   private final CoreSyntax syntax = new SimpleCoreSyntax();

   @Test
   public void testNullable() {

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = a B"));
      productions.add(syntax.parseProduction("B = C D"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("C = C"));
      productions.add(syntax.parseProduction("C = c"));
      productions.add(syntax.parseProduction("C = ε"));
      productions.add(syntax.parseProduction("D = ε"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

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
      productions.add(syntax.parseProduction("C = ε"));
      productions.add(syntax.parseProduction("D = d"));

      graph = new GrammarGraph(new Grammar(productions));

      assertFalse(graph.getVariableNode(productions.get(0).getLeftSide()).isNullable());
      assertFalse(graph.getVariableNode(productions.get(1).getLeftSide()).isNullable());
      assertTrue(graph.getVariableNode(productions.get(3).getLeftSide()).isNullable());
      assertFalse(graph.getVariableNode(productions.get(6).getLeftSide()).isNullable());

      productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A S"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = ε"));

      graph = new GrammarGraph(new Grammar(productions));

      assertTrue(graph.getVariableNode(productions.get(0).getLeftSide()).isNullable());
      assertTrue(graph.getVariableNode(productions.get(1).getLeftSide()).isNullable());
   }

   @Test
   public void testFoo() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A"));
      productions.add(syntax.parseProduction("S = B"));
      productions.add(syntax.parseProduction("A = a"));
      productions.add(syntax.parseProduction("A = B"));
      productions.add(syntax.parseProduction("A = ε"));
      productions.add(syntax.parseProduction("B = A"));
      productions.add(syntax.parseProduction("B = b"));
      productions.add(syntax.parseProduction("B = ε"));

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));

      VariableNode s = graph.getVariableNode("S");
      VariableNode a = graph.getVariableNode("A");
      VariableNode b = graph.getVariableNode("B");

      assertTrue(s.isNullable());
      assertTrue(a.isNullable());
      assertTrue(b.isNullable());
   }

   @Test
   public void testRecursion() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = E"));
      productions.add(syntax.parseProduction("E = e S"));

      List<VariableNode> visited = new ArrayList<>();
      List<List<Object>> recursions = new ArrayList<>();

      GrammarGraph graph = new GrammarGraph(new Grammar(productions));
      graph.accept(new AbstractGrammarGraphVisitor(false) {
         @Override
         public void visitRecursion(List<Object> trace) {
            recursions.add(trace);
         }

         @Override
         protected void onEndVariableNode(VariableNode variableNode) {
            visited.add(variableNode);
         }
      });

      assertEquals(2, recursions.size());
      assertEquals(4, visited.size());
   }
}
