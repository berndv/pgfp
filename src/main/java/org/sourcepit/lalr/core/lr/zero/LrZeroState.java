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

package org.sourcepit.lalr.core.lr.zero;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sourcepit.lalr.core.grammar.AbstractSymbol;

public class LrZeroState {

   private int id;

   private Set<LrZeroItem> closure;

   private Map<AbstractSymbol, LrZeroState> transitions;

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public Set<LrZeroItem> getClosure() {
      return closure;
   }

   public void setClosure(Set<LrZeroItem> closure) {
      this.closure = closure;
   }

   public Map<AbstractSymbol, LrZeroState> getTransitions() {
      return transitions;
   }

   public void setTransitions(Map<AbstractSymbol, LrZeroState> transitions) {
      this.transitions = transitions;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("LrZeroState [id=");
      builder.append(id);
      builder.append(", ");
      if (closure != null) {
         builder.append("closure=");
         builder.append(closure);
         builder.append(", ");
      }
      if (transitions != null) {
         builder.append("transitions=[");

         boolean first = true;
         for (Entry<AbstractSymbol, LrZeroState> entry : transitions.entrySet()) {
            if (first) {
               first = false;
            }
            else {
               builder.append(", ");
            }
            builder.append(entry.getKey());
            builder.append("->");
            builder.append(entry.getValue().getId());
         }

         builder.append("]");
      }
      builder.append("]");
      return builder.toString();
   }
}
