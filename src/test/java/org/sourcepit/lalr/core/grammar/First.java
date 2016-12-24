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

package org.sourcepit.lalr.core.grammar;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class First {

   private final Map<Variable, Set<Terminal>> variableToFirst;

   public First(Grammar grammar, Nullable nullable) {
      final Map<Variable, Set<Terminal>> variableToFirst = new LinkedHashMap<>();
      for (Variable variable : grammar.getVariables()) {
         first(variableToFirst, new Stack<>(), grammar, variable, nullable);
      }
      this.variableToFirst = variableToFirst;
   }

   private static Set<Terminal> first(Map<Variable, Set<Terminal>> variableToFirst, Stack<Variable> trace,
      Grammar grammar, Variable variable, Nullable nullable) {

      Set<Terminal> first = variableToFirst.get(variable);
      if (first == null) {
         first = new HashSet<>();
      }

      for (Production production : grammar.getProductions(variable)) {
         first.addAll(first(variableToFirst, trace, grammar, production, nullable));
      }

      variableToFirst.put(variable, first);

      return first;
   }

   private static Set<Terminal> first(Map<Variable, Set<Terminal>> variableToFirst, Stack<Variable> trace,
      Grammar grammar, Production production, Nullable nullable) {

      final Set<Terminal> first = new HashSet<>();

      for (AbstractSymbol symbol : production.getRightSide()) {

         if (symbol instanceof Terminal) {
            first.add((Terminal) symbol);
            break;
         }

         if (trace.contains(symbol)) {
            continue;
         }

         final Variable variable = (Variable) symbol;
         trace.push(variable);
         try {
            first.addAll(first(variableToFirst, trace, grammar, variable, nullable));
         }
         finally {
            trace.pop();
         }

         if (!nullable.isNullable(variable)) {
            break;
         }
      }

      return first;

   }

   public Set<Terminal> get(Variable variable) {
      return variableToFirst.get(variable);
   }
}
