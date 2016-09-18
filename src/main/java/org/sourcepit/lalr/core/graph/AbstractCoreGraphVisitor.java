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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractCoreGraphVisitor implements CoreGraphVisitor {

   protected Set<MetaNode> visted;

   protected final boolean skipVisited;

   public AbstractCoreGraphVisitor() {
      this(true);
   }

   public AbstractCoreGraphVisitor(boolean skipVisited) {
      this.skipVisited = skipVisited;
   }

   @Override
   public void startGraph(CoreGraph coreGraph) {
      if (skipVisited) {
         visted = new HashSet<>();
      }
   }

   @Override
   public final boolean startMetaNode(MetaNode metaNode) {
      if (visted != null && visted.contains(metaNode)) {
         return false;
      }
      return onStartMetaNode(metaNode);
   }

   protected boolean onStartMetaNode(MetaNode metaNode) {
      return true;
   }

   @Override
   public void startAlternative(Alternative alternative) {
   }

   @Override
   public void visitRecursion(List<Object> trace) {
   }

   @Override
   public void visitTerminalNode(TerminalNode symbolNode) {
   }

   @Override
   public void endAlternative(Alternative alternative) {
   }

   protected void onEndMetaNode(MetaNode metaNode) {
   }

   @Override
   public final void endMetaNode(MetaNode metaNode) {
      if (visted == null || !visted.contains(metaNode)) {
         onEndMetaNode(metaNode);
      }
      
      if (visted != null) {
         visted.add(metaNode);
      }
   }

   @Override
   public void endGraph(CoreGraph coreGraph) {
      visted = null;
   }

}
