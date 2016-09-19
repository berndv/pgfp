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

import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;
import static org.sourcepit.lalr.core.grammar.Validate.noDupliatedElements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CoreGrammar {
   private final List<Variable> variables;

   private final List<Terminal> terminals;

   private final List<Production> productions;

   private final Map<Variable, List<Production>> variableToProductions;

   private final Variable startSymbol;

   public CoreGrammar(List<Production> productions) {
      this(productions, productions.get(0).getLeftSide());
   }

   public CoreGrammar(List<Production> productions, Variable startSymbol) {
      notEmpty(productions);
      noDupliatedElements(productions);

      LinkedHashSet<Variable> variables = new LinkedHashSet<>();
      LinkedHashSet<Variable> allVariables = new LinkedHashSet<>();
      LinkedHashSet<Terminal> terminals = new LinkedHashSet<>();

      allVariables.add(startSymbol);

      LinkedHashMap<Variable, LinkedHashSet<Production>> variableToProductions = new LinkedHashMap<>();
      for (Production production : productions) {
         final Variable leftSide = production.getLeftSide();
         variables.add(leftSide);
         allVariables.add(leftSide);
         for (AbstractSymbol symbol : production.getRightSide()) {
            switch (symbol.getType()) {
               case VARIABLE :
                  allVariables.add((Variable) symbol);
                  break;
               case TERMINAL :
                  terminals.add((Terminal) symbol);
                  break;
               default :
                  throw new IllegalStateException();
            }
         }
         LinkedHashSet<Production> alternatives = variableToProductions.get(leftSide);
         if (alternatives == null) {
            alternatives = new LinkedHashSet<>();
            variableToProductions.put(leftSide, alternatives);
         }
         alternatives.add(production);
      }

      allVariables.removeAll(variables);
      isTrue(allVariables.isEmpty(), "Grammar contains undefined variables: " + allVariables);

      this.variables = unmodifiableList(new ArrayList<>(variables));
      this.terminals = unmodifiableList(new ArrayList<>(terminals));
      this.productions = productions;
      this.variableToProductions = toUnmodifiableProductionsMap(variableToProductions);
      notNull(startSymbol);
      this.startSymbol = startSymbol;
   }

   private static Map<Variable, List<Production>> toUnmodifiableProductionsMap(
      LinkedHashMap<Variable, LinkedHashSet<Production>> map) {
      final LinkedHashMap<Variable, List<Production>> result = new LinkedHashMap<>(map.size());
      for (Entry<Variable, LinkedHashSet<Production>> entry : map.entrySet()) {
         result.put(entry.getKey(), unmodifiableList(new ArrayList<>(entry.getValue())));
      }
      return result;
   }

   public List<Variable> getVariables() {
      return variables;
   }

   public Variable getVariable(String str) {
      for (Variable symbol : getVariables()) {
         if (str.equals(symbol.toString())) {
            return symbol;
         }
      }
      return null;
   }

   public List<Terminal> getTerminals() {
      return terminals;
   }

   public List<Production> getProductions() {
      return productions;
   }

   public List<Production> getProductions(Variable variable) {
      return variableToProductions.get(variable);
   }

   public Variable getStartSymbol() {
      return startSymbol;
   }

   @Override
   public String toString() {
      final StringBuilder str = new StringBuilder();
      str.append("G = (V, T, P, S)\n");
      str.append("V = {");
      for (Variable symbol : variables) {
         str.append(symbol);
         str.append(", ");
      }
      if (!variables.isEmpty()) {
         str.deleteCharAt(str.length() - 1);
         str.deleteCharAt(str.length() - 1);
      }
      str.append("}\n");

      str.append("T = {");
      for (Terminal symbol : terminals) {
         str.append(symbol);
         str.append(", ");
      }
      if (!terminals.isEmpty()) {
         str.deleteCharAt(str.length() - 1);
         str.deleteCharAt(str.length() - 1);
      }
      str.append("}\n");

      str.append("S = ");
      str.append(startSymbol);
      str.append("\n");

      str.append("P = {\n");
      for (Variable leftSide : variables) {
         for (Production production : variableToProductions.get(leftSide)) {
            str.append("  ");
            str.append(production);
            str.append("\n");
         }
      }
      str.append("}\n");
      return str.toString();
   }

}
