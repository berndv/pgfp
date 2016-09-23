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

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class ParsingTableTest {

   private final static short ACTION_SHIFT = (short) 0b1000000000000000;
   private final static short ACTION_REDUCE = (short) 0b1010000000000000;
   private final static short ACTION_ACCEPT = (short) 0b1100000000000000;
   private final static short ACTION_JUMP = (short) 0b1110000000000000;

   private final static short MASK_ACTION = (short) 0b1110000000000000;
   private final static short MASK_DATA = (short) 0b0001111111111111;

   @Test
   public void testName() throws Exception {
      short s = (short) 0b1111111111111111;

      assertEquals(ACTION_JUMP, getAction(s));
      assertEquals((short) 0b0001111111111111, getData(s));
      System.out.println(getData(s));

      System.out.println(0xFFFFFFFF);
      System.out.println(0b11111111111111111111111111111111);
      System.out.println(0b11111111111111111110000000000000);

      ShortTableBuilder.merge(ACTION_JUMP, 0b0001111111111111);
      ShortTableBuilder.merge(ACTION_JUMP, 0b0001111111111111);
   }

   private short getData(short s) {
      return (short) (s & MASK_DATA);
   }

   private short getAction(short s) {
      return (short) (s & MASK_ACTION);
   }

   public static class ShortTableBuilder implements ParsingTableBuilder {
      private List<Terminal> terminals;

      private List<Variable> variables;

      private int rowSize;

      private short[][] tbl;

      private short[] row;

      private int indexOf(Terminal terminal) {
         if (terminal == null) {
            return terminals.size(); // return idx eof
         }
         return terminals.indexOf(terminal);
      }

      private int indexOf(Variable variable) {
         return terminals.size() + 1 + variables.indexOf(variable);
      }

      private static short merge(short action, int data) {
         Validate.isTrue((0b11111111111111111110000000000000 & data) == 0);
         return (short) (ACTION_SHIFT | data);
      }

      private void set(int idx, short action, int value) {
         //check for conflicts
         row[idx] = merge(ACTION_SHIFT, value);
      }

      @Override
      public void startTable(Grammar grammar, int states) {
         terminals = grammar.getTerminals();
         variables = grammar.getVariables();
         rowSize = terminals.size() + variables.size() + 1;
         tbl = new short[states][];
      }

      @Override
      public void startState(int state) {
         row = new short[rowSize];
         tbl[state] = row;
      }

      @Override
      public void shift(Terminal terminal, int targetState) {
         set(indexOf(terminal), ACTION_SHIFT, targetState);
      }

      @Override
      public void reduce(Terminal terminal, int production) {
         final int idx = indexOf(terminal);
         if (production == -1) {
            set(idx, ACTION_ACCEPT, 0);
         }
         else {
            set(idx, ACTION_REDUCE, production);
         }
      }

      @Override
      public void jump(Variable variable, int targetState) {
         set(indexOf(variable), ACTION_JUMP, targetState);
      }

      @Override
      public void endState(int state) {

      }

      @Override
      public void endTable(Grammar grammar, int states) {

      }
   }
}
