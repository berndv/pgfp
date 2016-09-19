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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class GrammarTest {

   private final CoreSyntax syntax = new SimpleCoreSyntax();

   @Test
   public void test() throws Exception {

      List<Production> productions = new ArrayList<>();
      productions.add(syntax.parseProduction("S = E F"));
      productions.add(syntax.parseProduction("E = e"));
      productions.add(syntax.parseProduction("F = f"));
      productions.add(syntax.parseProduction("E = ε"));

      System.out.println(new Grammar(productions));

   }

   @Test
   public void testUndefinedVariable() throws Exception {
      List<Production> productions = new ArrayList<Production>();
      productions.add(syntax.parseProduction("S = E"));
      try {
         new Grammar(productions);
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Grammar contains undefined variables: [E]", e.getMessage());
      }
   }

   @Test
   public void testStartSymbolNotDefined() throws Exception {
      List<Production> productions = new ArrayList<Production>();
      productions.add(syntax.parseProduction("S = \u03B5"));
      try {
         new Grammar(productions, syntax.createVariable("F"));
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Grammar contains undefined variables: [F]", e.getMessage());
      }
   }

}
