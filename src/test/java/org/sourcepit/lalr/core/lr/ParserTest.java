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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.SimpleSyntax;
import org.sourcepit.lalr.core.grammar.Syntax;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.lr.zero.LrZeroParsingTable;

public class ParserTest {

   @Test
   public void test() {

      final Syntax syntax = new SimpleSyntax();

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = A A"));
      productions.add(syntax.parseProduction("A = a A"));
      productions.add(syntax.parseProduction("A = b"));

      Grammar grammar = new Grammar(syntax, productions);

      ShortTableBuilder tblb = new ShortTableBuilder();
      new LrZeroParsingTable().build(grammar, tblb);

      short[][] actionTable = tblb.getActionTable();
      short[][] gotoTable = tblb.getGotoTable();

      List<Terminal> terminals = grammar.getTerminals();

      ParsingTable<String, Production> parsingTable = new FooParsingTable(grammar, actionTable, gotoTable);

      String tA = terminals.get(0).toString();
      String tB = terminals.get(1).toString();
      String t$ = null;

      AbstractParserAction<Production> action;
      /* state 0 */
      action = parsingTable.getAction(0, tA);
      assertEquals(ParserActionType.SHIFT, action.getType());
      assertEquals(4, action.asShift().getNextState());

      action = parsingTable.getAction(0, tB);
      assertEquals(ParserActionType.SHIFT, action.getType());
      assertEquals(6, action.asShift().getNextState());

      action = parsingTable.getAction(0, t$);
      assertNull(action);

      /* state 1 */
      action = parsingTable.getAction(1, tA);
      assertNull(action);

      action = parsingTable.getAction(1, tB);
      assertNull(action);

      action = parsingTable.getAction(1, t$);
      assertEquals(ParserActionType.ACCEPT, action.getType());

      /* state 2 */
      action = parsingTable.getAction(2, tA);
      assertEquals(ParserActionType.SHIFT, action.getType());
      assertEquals(4, action.asShift().getNextState());

      action = parsingTable.getAction(2, tB);
      assertEquals(ParserActionType.SHIFT, action.getType());
      assertEquals(6, action.asShift().getNextState());

      action = parsingTable.getAction(2, t$);
      assertNull(action);

      /* state 3 */
      action = parsingTable.getAction(3, tA);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(0), action.asReduce().getProduction());

      action = parsingTable.getAction(3, tB);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(0), action.asReduce().getProduction());

      action = parsingTable.getAction(3, t$);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(0), action.asReduce().getProduction());

      /* state 4 */
      action = parsingTable.getAction(4, tA);
      assertEquals(ParserActionType.SHIFT, action.getType());
      assertEquals(4, action.asShift().getNextState());

      action = parsingTable.getAction(4, tB);
      assertEquals(ParserActionType.SHIFT, action.getType());
      assertEquals(6, action.asShift().getNextState());

      action = parsingTable.getAction(2, t$);
      assertNull(action);

      /* state 5 */
      action = parsingTable.getAction(5, tA);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(1), action.asReduce().getProduction());

      action = parsingTable.getAction(5, tB);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(1), action.asReduce().getProduction());

      action = parsingTable.getAction(5, t$);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(1), action.asReduce().getProduction());

      /* state 6 */
      action = parsingTable.getAction(6, tA);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(2), action.asReduce().getProduction());

      action = parsingTable.getAction(6, tB);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(2), action.asReduce().getProduction());

      action = parsingTable.getAction(6, t$);
      assertEquals(ParserActionType.REDUCE, action.getType());
      assertEquals(productions.get(2), action.asReduce().getProduction());

      Parser<String> parser = new Parser<>(parsingTable);

      Iterator<String> tokens = Arrays.asList("a", "b", "b").iterator();

      parser.parse(tokens);
   }

}
