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

package org.sourcepit.lalr.core.graph;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.MetaSymbol;
import org.sourcepit.lalr.core.grammar.TerminalSymbol;

public class DetermineFirstCoreGraphVisitor extends AbstractCoreGraphVisitor {

   private final Map<MetaSymbol, Set<TerminalSymbol>> symbolToFirst = new HashMap<>();

   public Map<MetaSymbol, Set<TerminalSymbol>> getSymbolToFirst() {
      return symbolToFirst;
   }

   @Override
   public void endAlternative(Alternative alternative) {
      final MetaNode parent = alternative.getParent();

      Set<TerminalSymbol> first = symbolToFirst.get(parent.getSymbol());
      if (first == null) {
         first = new LinkedHashSet<>();
         symbolToFirst.put(parent.getSymbol(), first);
      }

      if (alternative.getSymbolNodes().isEmpty()) {
         first.add(null);
      }
      else {

         boolean nullable = false;

         for (AbstractSymbolNode node : alternative.getSymbolNodes()) {

            if (node.equals(parent)) {
               continue;
            }

            final AbstractSymbol symbol = node.getSymbol();
            if (symbol instanceof TerminalSymbol) {
               TerminalSymbol terminalSymbol = (TerminalSymbol) symbol;
               first.add(terminalSymbol);
               nullable = false;
               break;
            }
            else {
               MetaSymbol metaSymbol = (MetaSymbol) symbol;

               Set<TerminalSymbol> otherFirst = new LinkedHashSet<>(symbolToFirst.get(metaSymbol));
               nullable = otherFirst.remove(null);

               first.addAll(otherFirst);

               Validate.isTrue(nullable == ((MetaNode) node).isNullable());

               if (!nullable) {
                  break;
               }
            }
         }

         if (nullable) {
            first.add(null);
         }
      }
   }
}