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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.Validate;

public class Nullable {
   private final Map<Variable, Boolean> variableToNullable;

   public Nullable(Grammar grammar) {
      final Map<Variable, Boolean> variableToNullable = new LinkedHashMap<Variable, Boolean>();
      nullable(variableToNullable, grammar);
      this.variableToNullable = variableToNullable;
   }

   public boolean isNullable(Variable variable) {
      return variableToNullable.get(variable).booleanValue();
   }

   public boolean isNullable(Production production) {
      for (AbstractSymbol symbol : production.getRightSide()) {
         if (symbol instanceof Terminal) {
            return false;
         }
         else {
            if (!isNullable((Variable) symbol)) {
               return false;
            }
         }
      }
      return true;
   }

   private static void nullable(Map<Variable, Boolean> variableToNullable, Grammar grammar) {
      for (Variable leftSide : grammar.getVariables()) {
         nullable(new Stack<>(), variableToNullable, grammar, leftSide);
      }
      for (Variable leftSide : grammar.getVariables()) {
         Validate.notNull(variableToNullable.get(leftSide));
      }
   }

   private static Boolean nullable(Stack<Variable> trace, Map<Variable, Boolean> variableToNullable, Grammar grammar,
      Variable leftSide) {
      final Boolean oNullable = variableToNullable.get(leftSide);
      if (oNullable != null) {
         return oNullable;
      }

      Boolean variableNullable = null;
      boolean maybe = false;

      for (Production production : grammar.getProductions(leftSide)) {
         variableNullable = nullable(trace, variableToNullable, grammar, production);

         if (variableNullable == null) {
            maybe = true;
            continue;
         }

         if (variableNullable.booleanValue()) {
            maybe = false;
            break;
         }
      }

      if (!maybe) {
         variableToNullable.put(leftSide, variableNullable);
      }
      else {
         System.out.println();
      }

      return variableNullable;
   }

   private static Boolean nullable(Stack<Variable> trace, Map<Variable, Boolean> variableToNullable, Grammar grammar,
      Production production) {

      Boolean productionNullable = Boolean.TRUE;

      for (AbstractSymbol symbol : production.getRightSide()) {
         if (symbol instanceof Terminal) {
            productionNullable = Boolean.FALSE;
            break;
         }

         final Variable var = (Variable) symbol;
         if (trace.contains(var)) {
            productionNullable = null;
            continue;
         }

         trace.push(var);
         try {
            final Boolean varNullable = nullable(trace, variableToNullable, grammar, var);
            if (varNullable == null) {
               productionNullable = null;
               continue;
            }
            if (!varNullable.booleanValue()) {
               productionNullable = Boolean.FALSE;
               break;
            }
         }
         finally {
            trace.pop();
         }
      }

      return productionNullable;
   }
}
