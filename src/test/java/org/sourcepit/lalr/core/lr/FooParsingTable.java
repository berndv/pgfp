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

import java.util.List;

import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class FooParsingTable implements ParsingTable<String, Production> {
   private final Grammar grammar;
   private short[][] actionTable, gotoTable;

   public FooParsingTable(Grammar grammar, short[][] actionTable, short[][] gotoTable) {
      this.grammar = grammar;
      this.actionTable = actionTable;
      this.gotoTable = gotoTable;
   }

   @Override
   public AbstractParserAction<Production> getAction(int state, String token) {
      final short[] row = actionTable[state];
      final short cell = row[indexOf(token)];
      final short action = ParsingTableTest.getAction(cell);
      switch (action) {
         case ParsingTableTest.ACTION_SHIFT :
            return new Shift<Production>(ParsingTableTest.getData(cell));
         case ParsingTableTest.ACTION_REDUCE :
            return new Reduce<Production>(grammar.getProductions().get(ParsingTableTest.getData(cell)));
         case ParsingTableTest.ACTION_ACCEPT :
            return new Accept<Production>();
         default :
            return null;
      }
   }

   @Override
   public int getGoto(int state, Production production) {
      final Variable variable = production.getLeftSide();
      return gotoTable[state][grammar.getVariables().indexOf(variable)];
   }

   private int indexOf(String token) {
      if (token == null) {
         return grammar.getTerminals().size();
      }
      for (int i = 0; i < grammar.getTerminals().size(); i++) {
         if (grammar.getTerminals().get(i).toString().equals(token)) {
            return i;
         }
      }
      return -1;
   }

}
