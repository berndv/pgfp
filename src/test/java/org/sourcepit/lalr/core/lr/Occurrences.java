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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.Variable;

public class Occurrences {
   private Map<Variable, Set<Variable>> variableToOccurrences = new LinkedHashMap<>();

   public Occurrences(Grammar grammar) {
      occurrences(variableToOccurrences, grammar);
   }

   public boolean occursIn(Variable v1, Variable v2) {
      return occursIn(new Stack<>(), v1, v2);
   }

   private boolean occursIn(Stack<Variable> stack, Variable v1, Variable v2) {

      if (stack.contains(v1)) {
         return false;
      }

      Set<Variable> ocurrences = variableToOccurrences.get(v1);

      if (ocurrences.contains(v2)) {
         return true;
      }

      stack.push(v1);
      try {
         for (Variable ocurrence : ocurrences) {
            if (occursIn(stack, ocurrence, v2)) {
               return true;
            }
         }
      }
      finally {
         stack.pop();
      }

      return false;
   }

   private static void occurrences(Map<Variable, Set<Variable>> variableToOccurrences, Grammar grammar) {
      for (Variable variable : grammar.getVariables()) {
         variableToOccurrences.put(variable, new LinkedHashSet<>());
      }
      for (Production production : grammar.getProductions()) {
         Variable leftSide = production.getLeftSide();
         for (AbstractSymbol symbol : production.getRightSide()) {
            if (symbol instanceof Variable) {
               variableToOccurrences.get(symbol).add(leftSide);
            }
         }
      }
   }
}
