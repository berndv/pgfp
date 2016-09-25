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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class LrZeroTest {
   private final Syntax syntax = new SimpleSyntax();

   private GrammarGraph newGrammarGraph(List<Production> productions) {
      final GrammarGraph graph = new GrammarGraph(new Grammar(productions));
      graph.accept(new DetermineNullableGrammarGraphVisitor());
      final DetermineFollowGrammarGraphVisitor firstAndFollow = new DetermineFollowGrammarGraphVisitor();
      graph.accept(firstAndFollow);
      for (Entry<Variable, Set<Terminal>> entry : firstAndFollow.getSymbolToFirst().entrySet()) {
         graph.getVariableNode(entry.getKey()).setFirstSet(entry.getValue());
      }
      for (Entry<Variable, Set<Terminal>> entry : firstAndFollow.getSymbolToFollow().entrySet()) {
         graph.getVariableNode(entry.getKey()).setFollowSet(entry.getValue());
      }
      return graph;
   }


   @Test
   public void testExample1() throws Exception {
      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A A"));
      productions.add(syntax.parseProduction("A = a A"));
      productions.add(syntax.parseProduction("A = b"));

      GrammarGraph graph = newGrammarGraph(productions);
      Grammar grammar = graph.getGrammar();

      ShortTableBuilder tblb = new ShortTableBuilder();
      new LrZeroParsingTable().build(grammar, tblb);


   }

}
