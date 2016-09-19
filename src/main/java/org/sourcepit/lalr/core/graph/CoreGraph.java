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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.Validate;
import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.CoreGrammar;
import org.sourcepit.lalr.core.grammar.MetaSymbol;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.TerminalSymbol;

public class CoreGraph {

   private final CoreGrammar grammar;

   private final Map<MetaSymbol, MetaNode> metaNodes = new HashMap<>();

   private final Map<TerminalSymbol, TerminalNode> terminalNodes = new HashMap<>();

   public CoreGraph(CoreGrammar grammar) {
      this.grammar = grammar;

      for (MetaSymbol metaSymbol : grammar.getMetaSymbols()) {
         MetaNode metaNode = new MetaNode(metaSymbol);
         metaNodes.put(metaSymbol, metaNode);
      }

      for (TerminalSymbol terminalSymbol : grammar.getTerminalSymbols()) {
         terminalNodes.put(terminalSymbol, new TerminalNode(terminalSymbol));
      }

      for (MetaSymbol metaSymbol : grammar.getMetaSymbols()) {
         final MetaNode metaNode = metaNodes.get(metaSymbol);
         for (Production production : grammar.getProductions(metaSymbol)) {
            Alternative alt = new Alternative();
            alt.setParent(metaNode);
            metaNode.getAlternatives().add(alt);

            for (AbstractSymbol symbol : production.getRightSide()) {
               final AbstractSymbolNode symbolNode;
               switch (symbol.getType()) {
                  case META :
                     symbolNode = metaNodes.get(symbol);
                     break;
                  case TERMINAL :
                     symbolNode = terminalNodes.get(symbol);
                     break;
                  default :
                     throw new IllegalStateException();
               }
               Validate.notNull(symbolNode);
               alt.getSymbolNodes().add(symbolNode);
               final List<Alternative> referencedBy = symbolNode.getReferencedBy();
               if (!referencedBy.contains(alt)) {
                  referencedBy.add(alt);
               }
            }
         }
      }
      accept(new DetermineNullableCoreGraphVisitor());
   }

   public CoreGrammar getGrammar() {
      return grammar;
   }

   public MetaNode getMetaNode(String symbol) {
      final MetaSymbol metaSymbol = getGrammar().getMetaSymbol(symbol);
      return metaSymbol == null ? null : getMetaNode(metaSymbol);
   }

   public MetaNode getMetaNode(MetaSymbol symbol) {
      return metaNodes.get(symbol);
   }

   public TerminalNode getTerminalNode(TerminalSymbol symbol) {
      return terminalNodes.get(symbol);
   }

   public void accept(CoreGraphVisitor visitor) {
      Stack<Object> trace = new Stack<>();
      accept(trace, this, visitor);
   }

   private void accept(Stack<Object> trace, CoreGraph graph, CoreGraphVisitor visitor) {
      visitor.startGraph(this);
      for (MetaSymbol metaSymbol : graph.getGrammar().getMetaSymbols()) {
         accept(trace, graph.getMetaNode(metaSymbol), visitor);
      }
      visitor.endGraph(this);
   }

   private void accept(Stack<Object> trace, MetaNode metaNode, CoreGraphVisitor visitor) {

      if (trace.contains(metaNode)) {
         visitor.visitRecursion(new ArrayList<>(trace));
         return;
      }

      trace.push(metaNode);
      visitor.startMetaNode(metaNode);

      for (Alternative alternative : metaNode.getAlternatives()) {
         trace.push(alternative);
         visitor.startAlternative(alternative);
         for (AbstractSymbolNode symbolNode : alternative.getSymbolNodes()) {
            if (symbolNode instanceof MetaNode) {
               accept(trace, (MetaNode) symbolNode, visitor);
            }
            else {
               visitor.visitTerminalNode((TerminalNode) symbolNode);
            }
         }
         visitor.endAlternative(alternative);
         trace.pop();
      }

      visitor.endMetaNode(metaNode);
      trace.pop();
   }

}
