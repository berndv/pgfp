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


public interface Syntax {

   AbstractSymbol createSymbol(SymbolType type, String name) throws IllegalArgumentException;

   Variable createVariable(String name) throws IllegalArgumentException;

   Terminal createTerminal(String name) throws IllegalArgumentException;
   
   Terminal getEofTerminal();

   AbstractSymbol parseSymbol(String str) throws IllegalArgumentException;

   Production parseProduction(String str) throws IllegalArgumentException;

   Variable derivedVariable(Variable startSymbol, String string);
}