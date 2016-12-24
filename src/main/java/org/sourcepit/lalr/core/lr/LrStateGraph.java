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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;
import org.sourcepit.lalr.core.grammar.Variable;

public class LrStateGraph<LrItem> {
   private Variable derivedStartSymbol;
   private final List<Set<LrItem>> states = new ArrayList<>();
   private final List<Map<AbstractSymbol, Integer>> transitions = new ArrayList<>();

   public void setDerivedStartSymbol(Variable derivedStartSymbol) {
      this.derivedStartSymbol = derivedStartSymbol;
   }

   public Variable getDerivedStartSymbol() {
      return derivedStartSymbol;
   }

   public List<Set<LrItem>> getStates() {
      return states;
   }

   public List<Map<AbstractSymbol, Integer>> getTransitions() {
      return transitions;
   }
}
