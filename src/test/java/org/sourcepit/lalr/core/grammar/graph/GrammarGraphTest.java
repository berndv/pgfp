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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;

public class GrammarGraphTest {
   private final Syntax syntax = new SimpleSyntax();

   private GrammarGraph newGrammarGraph(List<Production> productions) {
      return new GrammarGraph(new Grammar(syntax, productions));
   }

   @Test
   public void testRecursion() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = E"));
      productions.add(syntax.parseProduction("E = e S"));

      List<VariableNode> visited = new ArrayList<>();
      List<List<Object>> recursions = new ArrayList<>();

      GrammarGraph graph = newGrammarGraph(productions);
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
