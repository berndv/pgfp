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

import java.util.Iterator;
import java.util.Stack;

import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.lr.AbstractParserAction;
import org.sourcepit.lalr.core.lr.ParsingTable;
import org.sourcepit.lalr.core.lr.Reduce;
import org.sourcepit.lalr.core.lr.Shift;

public class Parser<T> {

   private final ParsingTable<T, Production> parsingTable;

   public Parser(ParsingTable<T, Production> parsingTable) {
      this.parsingTable = parsingTable;
   }

   private Stack<Integer> stateStack = new Stack<Integer>();

   private Stack<Object> tokenStack = new Stack<>();

   public void parse(Iterator<T> tokens) {

      stateStack.push(Integer.valueOf(0));

      T token = nextToken(tokens);

      while (true) {

         Integer state = stateStack.peek();

         final AbstractParserAction<Production> action = parsingTable.getAction(state, token);
         switch (action.getType()) {
            case SHIFT :
               final Shift<Production> shift = action.asShift();
               tokenStack.push(token);
               token = nextToken(tokens);
               stateStack.push(shift.getNextState());
               break;
            case REDUCE :
               final Reduce<Production> reduce = action.asReduce();
               Production production = reduce.getProduction();
               for (int i = 0; i < production.getRightSide().size(); i++) {
                  stateStack.pop();
                  tokenStack.pop();
               }
               state = stateStack.peek();
               tokenStack.push(production);
               state = parsingTable.getGoto(state.intValue(), production);
               stateStack.push(state);
               break;
            case ACCEPT :
               return;
         }
      }
   }

   private T nextToken(Iterator<T> tokens) {
      return tokens.hasNext() ? tokens.next() : null;
   }


}
