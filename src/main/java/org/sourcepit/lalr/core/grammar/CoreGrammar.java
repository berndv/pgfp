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
   private final List<MetaSymbol> metaSymbols;

   private final List<TerminalSymbol> terminalSymbols;

   private final List<Production> productions;

   private final Map<MetaSymbol, List<Production>> metaSymbolToProductions;

   private final MetaSymbol startSymbol;

   public CoreGrammar(List<Production> productions) {
      this(productions, productions.get(0).getLeftSide());
   }

   public CoreGrammar(List<Production> productions, MetaSymbol startSymbol) {
      notEmpty(productions);
      noDupliatedElements(productions);

      LinkedHashSet<MetaSymbol> metaSymbols = new LinkedHashSet<>();
      LinkedHashSet<MetaSymbol> allMetaSymbols = new LinkedHashSet<>();
      LinkedHashSet<TerminalSymbol> terminalSymbols = new LinkedHashSet<>();

      allMetaSymbols.add(startSymbol);

      LinkedHashMap<MetaSymbol, LinkedHashSet<Production>> metaSymbolToProductions = new LinkedHashMap<>();
      for (Production production : productions) {
         final MetaSymbol leftSide = production.getLeftSide();
         metaSymbols.add(leftSide);
         allMetaSymbols.add(leftSide);
         for (AbstractSymbol symbol : production.getRightSide()) {
            switch (symbol.getType()) {
               case META :
                  allMetaSymbols.add((MetaSymbol) symbol);
                  break;
               case TERMINAL :
                  terminalSymbols.add((TerminalSymbol) symbol);
                  break;
               default :
                  throw new IllegalStateException();
            }
         }
         LinkedHashSet<Production> alternatives = metaSymbolToProductions.get(leftSide);
         if (alternatives == null) {
            alternatives = new LinkedHashSet<>();
            metaSymbolToProductions.put(leftSide, alternatives);
         }
         alternatives.add(production);
      }

      allMetaSymbols.removeAll(metaSymbols);
      isTrue(allMetaSymbols.isEmpty(), "Grammar contains undefined meta symbols: " + allMetaSymbols);

      this.metaSymbols = unmodifiableList(new ArrayList<>(metaSymbols));
      this.terminalSymbols = unmodifiableList(new ArrayList<>(terminalSymbols));
      this.productions = productions;
      this.metaSymbolToProductions = toUnmodifiableProductionsMap(metaSymbolToProductions);
      notNull(startSymbol);
      this.startSymbol = startSymbol;
   }

   private static Map<MetaSymbol, List<Production>> toUnmodifiableProductionsMap(
      LinkedHashMap<MetaSymbol, LinkedHashSet<Production>> map) {
      final LinkedHashMap<MetaSymbol, List<Production>> result = new LinkedHashMap<>(map.size());
      for (Entry<MetaSymbol, LinkedHashSet<Production>> entry : map.entrySet()) {
         result.put(entry.getKey(), unmodifiableList(new ArrayList<>(entry.getValue())));
      }
      return result;
   }

   public List<MetaSymbol> getMetaSymbols() {
      return metaSymbols;
   }

   public MetaSymbol getMetaSymbol(String str) {
      for (MetaSymbol symbol : getMetaSymbols()) {
         if (str.equals(symbol.toString())) {
            return symbol;
         }
      }
      return null;
   }

   public List<TerminalSymbol> getTerminalSymbols() {
      return terminalSymbols;
   }

   public List<Production> getProductions() {
      return productions;
   }

   public List<Production> getProductions(MetaSymbol metaSymbol) {
      return metaSymbolToProductions.get(metaSymbol);
   }

   public MetaSymbol getStartSymbol() {
      return startSymbol;
   }

   @Override
   public String toString() {
      final StringBuilder str = new StringBuilder();
      str.append("G = (N, T, P, S)\n");
      str.append("N = {");
      for (MetaSymbol symbol : metaSymbols) {
         str.append(symbol);
         str.append(", ");
      }
      if (!metaSymbols.isEmpty()) {
         str.deleteCharAt(str.length() - 1);
         str.deleteCharAt(str.length() - 1);
      }
      str.append("}\n");

      str.append("T = {");
      for (TerminalSymbol symbol : terminalSymbols) {
         str.append(symbol);
         str.append(", ");
      }
      if (!terminalSymbols.isEmpty()) {
         str.deleteCharAt(str.length() - 1);
         str.deleteCharAt(str.length() - 1);
      }
      str.append("}\n");

      str.append("S = ");
      str.append(startSymbol);
      str.append("\n");

      str.append("P = {\n");
      for (MetaSymbol leftSide : metaSymbols) {
         for (Production production : metaSymbolToProductions.get(leftSide)) {
            str.append("  ");
            str.append(production);
            str.append("\n");
         }
      }
      str.append("}\n");
      return str.toString();
   }

}
