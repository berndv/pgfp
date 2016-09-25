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

import java.util.List;

import org.apache.commons.lang.Validate;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class ShortTableBuilder implements ParsingTableBuilder {
   private List<Terminal> terminals;

   private List<Variable> variables;

   private int actionTableSize, gotoTableSize;

   private short[][] actionTable, gotoTable;

   private short[] actionRow, gotoRow;

   public short[][] getActionTable() {
      return actionTable;
   }

   public short[][] getGotoTable() {
      return gotoTable;
   }

   private int indexOf(Terminal terminal) {
      if (terminal == null) {
         return terminals.size(); // return idx eof
      }
      return terminals.indexOf(terminal);
   }

   private int indexOf(Variable variable) {
      return variables.indexOf(variable);
   }

   static short merge(short action, int data) {
      Validate.isTrue((0b11111111111111111100000000000000 & data) == 0);
      return (short) (action | data);
   }

   @Override
   public void startTable(Grammar grammar, int states) {
      terminals = grammar.getTerminals();
      actionTableSize = terminals.size() + 1;
      variables = grammar.getVariables();
      gotoTableSize = variables.size();
      actionTable = new short[states][];
      gotoTable = new short[states][];
   }

   @Override
   public void startState(int state) {
      actionRow = new short[actionTableSize];
      actionTable[state] = actionRow;
      gotoRow = new short[gotoTableSize];
      gotoTable[state] = gotoRow;
   }

   @Override
   public void shift(Terminal terminal, int targetState) {
      // check for conflicts
      actionRow[indexOf(terminal)] = merge(ParsingTableTest.ACTION_SHIFT, targetState);
   }

   @Override
   public void reduce(Terminal terminal, int production) {
      if (production == -1) {
         // check for conflicts
         actionRow[indexOf(terminal)] = merge(ParsingTableTest.ACTION_ACCEPT, 0);
      }
      else {
         // check for conflicts
         actionRow[indexOf(terminal)] = merge(ParsingTableTest.ACTION_REDUCE, production);
      }
   }

   @Override
   public void jump(Variable variable, int targetState) {
      gotoRow[indexOf(variable)] = (short) targetState;
   }

   @Override
   public void endState(int state) {

   }

   @Override
   public void endTable(Grammar grammar, int states) {

   }
}