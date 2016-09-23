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

import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public interface ParsingTableBuilder {

   void startTable(Grammar grammar, int states);

   void startState(int state);

   void shift(Terminal terminal, int targetState);

   void reduce(Terminal terminal, int production);

   void jump(Variable variable, int targetState);

   void endState(int state);

   void endTable(Grammar grammar, int states);

}