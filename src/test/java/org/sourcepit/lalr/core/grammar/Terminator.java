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

import static org.sourcepit.lalr.core.grammar.Terminator.Terminating.MAYBE;
import static org.sourcepit.lalr.core.grammar.Terminator.Terminating.NO;
import static org.sourcepit.lalr.core.grammar.Terminator.Terminating.YES;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang.Validate;

public class Terminator {

   public enum Terminating {
      YES, NO, MAYBE
   }

   private Set<Production> unterminatingProductions = new LinkedHashSet<>();

   private final Map<Variable, Terminating> variableToTerminating;
   private final Map<Production, Terminating> productionToTerminating;

   private Map<Variable, Boolean> variableToMayTerminates = new LinkedHashMap<Variable, Boolean>();

   private final Grammar grammar;

   private Stack<Variable> variablesTrace = new Stack<>();

   private Set<Production> maybes = new LinkedHashSet<>();

   public Terminator(Grammar grammar) {
      this.grammar = grammar;
      for (Variable variable : grammar.getVariables()) {
         terminates(variable);
      }

      // now with cached values
      for (Production maybe : maybes) {
         final Boolean terminates = terminates(maybe);
         Validate.notNull(terminates);
         if (!terminates.booleanValue()) {
            unterminatingProductions.add(maybe);
         }
      }
      maybes = null;
      Validate.isTrue(variablesTrace.isEmpty());
      variablesTrace = null;
      variableToMayTerminates = null;

      Set<Variable> terminating = new HashSet<>();
      Set<Variable> unterminating = new HashSet<>();
      for (Production production : grammar.getProductions()) {
         if (unterminatingProductions.contains(production)) {
            unterminating.add(production.getLeftSide());
         }
         else {
            terminating.add(production.getLeftSide());
         }
      }

      unterminatingProductions = null;

      variableToTerminating = new LinkedHashMap<>(grammar.getVariables().size());
      for (Variable variable : grammar.getVariables()) {
         variableToTerminating.put(variable, getVariableDeterministic(terminating, unterminating, variable));
      }

      productionToTerminating = new LinkedHashMap<>(grammar.getVariables().size());
      for (Production production : grammar.getProductions()) {
         Terminating result = YES;
         for (AbstractSymbol symbol : production.getRightSide()) {
            if (symbol instanceof Variable) {
               Terminating deterministic = isTerminating((Variable) symbol);
               if (deterministic == YES) {
                  // noop
               }
               else if (deterministic == MAYBE) {
                  result = MAYBE;
               }
               else if (deterministic == NO) {
                  result = NO;
                  break;
               }
               else {
                  throw new IllegalStateException();
               }
            }
         }
         productionToTerminating.put(production, result);
      }
   }

   private Terminating getVariableDeterministic(Set<Variable> terminating, Set<Variable> unterminating,
      Variable variable) {
      if (terminating.contains(variable)) {
         return unterminating.contains(variable) ? MAYBE : YES;
      }
      return Terminating.NO;
   }

   public Terminating isTerminating(Production production) {
      return productionToTerminating.get(production);
   }

   public Terminating isTerminating(Variable variable) {
      return variableToTerminating.get(variable);
   }

   public Set<Production> getProductions(Terminating condition) {
      final Set<Production> productions = new LinkedHashSet<>();
      for (Entry<Production, Terminating> entry : productionToTerminating.entrySet()) {
         if (entry.getValue().equals(condition)) {
            productions.add(entry.getKey());
         }
      }
      return productions;
   }

   private boolean terminates(Variable variable) {

      Boolean cached = variableToMayTerminates.get(variable);
      if (cached != null) {
         return cached.booleanValue();
      }

      List<Production> productions = grammar.getProductions(variable);

      boolean varMayTerminate = false;

      for (Production production : productions) {
         final Boolean terminates = terminates(production);

         if (terminates == null) {
            // until now we are not able to determine if this production terminates or not
            maybes.add(production);
            continue;
         }

         if (terminates.booleanValue()) {
            // the production terminates -> thle left side variable may terminate
            variableToMayTerminates.put(variable, terminates);
            maybes.remove(production);
            varMayTerminate = true;
         }
         else {
            // the production cannot terminate
            maybes.remove(production);
            unterminatingProductions.add(production);
         }
      }

      variableToMayTerminates.put(variable, Boolean.valueOf(varMayTerminate));

      return varMayTerminate;

   }

   private Boolean terminates(Production production) {

      Boolean terminates = Boolean.TRUE;

      Set<Variable> foos = new LinkedHashSet<>();

      for (AbstractSymbol symbol : production.getRightSide()) {

         if (variablesTrace.contains(symbol)) {
            terminates = null; // maybe
            foos.add((Variable) symbol);
            continue;
         }

         if (symbol instanceof Variable) {
            variablesTrace.push((Variable) symbol);
            try {
               if (!terminates((Variable) symbol)) {
                  terminates = Boolean.FALSE;
                  break;
               }
            }
            finally {
               variablesTrace.pop();
            }
         }
      }

      return terminates;
   }
}
