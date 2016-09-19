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
import org.sourcepit.lalr.core.grammar.Grammar;
import org.sourcepit.lalr.core.grammar.Production;
import org.sourcepit.lalr.core.grammar.Terminal;
import org.sourcepit.lalr.core.grammar.Variable;

public class GrammarGraph {

   private final Grammar grammar;

   private final Map<Variable, VariableNode> variableNodes = new HashMap<>();

   private final Map<Terminal, TerminalNode> terminalNodes = new HashMap<>();

   public GrammarGraph(Grammar grammar) {
      this.grammar = grammar;

      for (Variable variable : grammar.getVariables()) {
         VariableNode variableNode = new VariableNode(variable);
         variableNodes.put(variable, variableNode);
      }

      for (Terminal terminal : grammar.getTerminals()) {
         terminalNodes.put(terminal, new TerminalNode(terminal));
      }

      for (Variable variable : grammar.getVariables()) {
         final VariableNode variableNode = variableNodes.get(variable);
         for (Production production : grammar.getProductions(variable)) {
            ProductionNode alt = new ProductionNode();
            alt.setParent(variableNode);
            variableNode.getProductionNodes().add(alt);

            for (AbstractSymbol symbol : production.getRightSide()) {
               final AbstractSymbolNode symbolNode;
               switch (symbol.getType()) {
                  case VARIABLE :
                     symbolNode = variableNodes.get(symbol);
                     break;
                  case TERMINAL :
                     symbolNode = terminalNodes.get(symbol);
                     break;
                  default :
                     throw new IllegalStateException();
               }
               Validate.notNull(symbolNode);
               alt.getRightSideNodes().add(symbolNode);
               final List<ProductionNode> referencedBy = symbolNode.getReferencedBy();
               if (!referencedBy.contains(alt)) {
                  referencedBy.add(alt);
               }
            }
         }
      }
      accept(new DetermineNullableGrammarGraphVisitor());
   }

   public Grammar getGrammar() {
      return grammar;
   }

   public VariableNode getVariableNode(String symbol) {
      final Variable variable = getGrammar().getVariable(symbol);
      return variable == null ? null : getVariableNode(variable);
   }

   public VariableNode getVariableNode(Variable symbol) {
      return variableNodes.get(symbol);
   }

   public TerminalNode getTerminalNode(Terminal symbol) {
      return terminalNodes.get(symbol);
   }

   public void accept(GrammarGraphVisitor visitor) {
      Stack<Object> trace = new Stack<>();
      accept(trace, this, visitor);
   }

   private void accept(Stack<Object> trace, GrammarGraph graph, GrammarGraphVisitor visitor) {
      visitor.startGraph(this);
      for (Variable variable : graph.getGrammar().getVariables()) {
         accept(trace, graph.getVariableNode(variable), visitor);
      }
      visitor.endGraph(this);
   }

   private void accept(Stack<Object> trace, VariableNode variableNode, GrammarGraphVisitor visitor) {

      if (trace.contains(variableNode)) {
         visitor.visitRecursion(new ArrayList<>(trace));
         return;
      }

      trace.push(variableNode);
      visitor.startVariableNode(variableNode);

      for (ProductionNode productionNode : variableNode.getProductionNodes()) {
         trace.push(productionNode);
         visitor.startProductionNode(productionNode);
         for (AbstractSymbolNode symbolNode : productionNode.getRightSideNodes()) {
            if (symbolNode instanceof VariableNode) {
               accept(trace, (VariableNode) symbolNode, visitor);
            }
            else {
               visitor.visitTerminalNode((TerminalNode) symbolNode);
            }
         }
         visitor.endProductionNode(productionNode);
         trace.pop();
      }

      visitor.endVariableNode(variableNode);
      trace.pop();
   }

}
