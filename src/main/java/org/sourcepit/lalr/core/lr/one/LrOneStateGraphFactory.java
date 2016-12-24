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

package org.sourcepit.lalr.core.lr.one;

import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.graph.GrammarGraph;
import org.sourcepit.lalr.core.lr.AbstractLrStateGraphFactory;

public class LrOneStateGraphFactory extends AbstractLrStateGraphFactory<GrammarGraph, LrOneItem> {

   public LrOneStateGraphFactory() {
      super(new LrOneClosureFunction(), new LrOneGotoFunction(new LrOneClosureFunction()));
   }

   @Override
   protected Grammar getGrammar(GrammarGraph context) {
      return context.getGrammar();
   }

   @Override
   protected LrOneItem newStartItem(GrammarGraph context, Production startProduction) {
      return LrOneItem.create(startProduction, 0, context.getGrammar().getSyntax().getEofTerminal());
   }

}
