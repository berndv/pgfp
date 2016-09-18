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
import java.util.List;

public class Alternative {

   private MetaNode parent;

   private final List<AbstractSymbolNode> symbolNodes = new ArrayList<>();

   public void setParent(MetaNode parent) {
      this.parent = parent;
   }

   public MetaNode getParent() {
      return parent;
   }

   public List<AbstractSymbolNode> getSymbolNodes() {
      return symbolNodes;
   }

   @Override
   public String toString() {
      return toString(true);
   }

   public String toString(boolean full) {
      StringBuilder builder = new StringBuilder();
      builder.append("Alternative [");
      if (parent != null) {
         builder.append("parent=");
         builder.append(parent.getSymbol());
         builder.append(", ");
         builder.append("index=");
         builder.append(parent.getAlternatives().indexOf(this));
      }
      builder.append("]");
      return builder.toString();
   }

}
