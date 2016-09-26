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

public abstract class AbstractParserAction<P> {
   private final ParserActionType actionType;

   public AbstractParserAction(ParserActionType actionType) {
      this.actionType = actionType;
   }

   public ParserActionType getType() {
      return actionType;
   }

   public Shift<P> asShift() {
      throw new ClassCastException();
   }
   
   public Reduce<P> asReduce() {
      throw new ClassCastException();
   }
   
   public Accept<P> asAccept() {
      throw new ClassCastException();
   }
}