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

import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class FooParsingTableBuilder implements ParsingTableBuilder {

   String[][] tbl;
   private int symbols;

   private Grammar grammar;

   private int indexOf(Terminal terminal) {
      if (terminal == null) {
         return idxEof;
      }
      return grammar.getTerminals().indexOf(terminal);
   }

   private int indexOf(Variable variable) {
      return grammar.getTerminals().size() + 1 + grammar.getVariables().indexOf(variable);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startTable(Grammar grammar, int states) {

      this.grammar = grammar;

      idxEof = grammar.getTerminals().size();
      symbols = idxEof + 1 + grammar.getVariables().size();

      tbl = new String[states][symbols];
   }

   private String[] currentState;
   private int idxEof;

   /**
    * {@inheritDoc}
    */
   @Override
   public void startState(int state) {
      currentState = new String[symbols];
      tbl[state] = currentState;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void shift(Terminal terminal, int targetState) {
      currentState[indexOf(terminal)] = "s" + targetState;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void reduce(Terminal terminal, int production) {
      if (production == -1) {
         currentState[indexOf(terminal)] = "ac";
      }
      else {
         currentState[indexOf(terminal)] = "r" + production;
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void jump(Variable variable, int targetState) {
      currentState[indexOf(variable)] = "" + targetState;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endState(int state) {
      currentState = null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endTable(Grammar grammar, int states) {

   }

}
