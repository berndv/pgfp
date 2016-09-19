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

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.String.format;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.sourcepit.lalr.core.grammar.SymbolType.TERMINAL;
import static org.sourcepit.lalr.core.grammar.SymbolType.VARIABLE;
import static org.sourcepit.lalr.core.grammar.Validate.isTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator.OfInt;
import java.util.StringTokenizer;

public class SimpleCoreSyntax implements CoreSyntax {

   @Override
   public Variable createVariable(String name) throws IllegalArgumentException {
      return (Variable) createSymbol(VARIABLE, name);
   }

   @Override
   public Terminal createTerminal(String name) throws IllegalArgumentException {
      return (Terminal) createSymbol(TERMINAL, name);
   }

   @Override
   public AbstractSymbol createSymbol(SymbolType type, String name) throws IllegalArgumentException {
      validateSymbolName(type, name);
      switch (type) {
         case VARIABLE :
            return new Variable(name);
         case TERMINAL :
            return new Terminal(name);
         default :
            throw new IllegalStateException();
      }
   }

   @Override
   public AbstractSymbol parseSymbol(String str) throws IllegalArgumentException {
      notEmpty(str, "Symbol must not be empty");
      final int firstCP = str.codePointAt(0);
      validateFirstCodePoint(str, firstCP);
      final SymbolType type;
      if (Character.isUpperCase(firstCP)) {
         type = VARIABLE;
      }
      else if (Character.isLowerCase(firstCP)) {
         type = TERMINAL;
      }
      else {
         throw new IllegalArgumentException(format("First char of symbol '%s' must be lower or upper case", str));
      }
      return createSymbol(type, str);
   }

   static void validateSymbolName(SymbolType type, String name) throws IllegalArgumentException {
      notEmpty(name, "Symbol names must not be null or empty");
      isTrue(!"\u03B5".equals(name), "'\u03B5' is reserved and cannot be used as symbol name");

      final OfInt codePoints = name.codePoints().iterator();

      final int firstCP = codePoints.next().intValue();

      validateFirstCodePoint(name, firstCP);

      switch (type) {
         case VARIABLE :
            isTrue(isUpperCase(firstCP), "First char of variable '%s' must be upper case", name);
            break;
         case TERMINAL :
            isTrue(isLowerCase(firstCP), "First char of terminal symbol '%s' must be lower case", name);
            break;
         default :
            throw new IllegalStateException();
      }

      codePoints.forEachRemaining((int cp) -> {
         isTrue(Character.isUnicodeIdentifierPart(cp), "Symbol '%s' contains invlaid char '%s'", name,
            String.valueOf(Character.toChars(cp)));
      });

   }

   private static void validateFirstCodePoint(String symbol, int codePoint) {
      isTrue(Character.isUnicodeIdentifierStart(codePoint),
         "First char of symbol '%s' must be a valid unicode identifier start char", symbol);
   }

   @Override
   public Production parseProduction(String str) throws IllegalArgumentException {
      final StringTokenizer tokenizer = new StringTokenizer(str, " \t\r\n\u000C");
      final Variable leftSide = leftSide(this, tokenizer);
      assignment(tokenizer);
      final List<AbstractSymbol> rightSide = rightSide(this, tokenizer);
      return new Production(leftSide, rightSide, toString(leftSide, rightSide));
   }

   private String toString(Variable leftSide, List<AbstractSymbol> rightSide) {
      final StringBuilder str = new StringBuilder();
      str.append(leftSide);
      str.append(" =");
      if (rightSide.isEmpty()) {
         str.append(" \u03B5");
      }
      else {
         for (AbstractSymbol symbol : rightSide) {
            str.append(" ");
            str.append(symbol);
         }
      }
      return str.toString();
   }

   private static Variable leftSide(CoreSyntax syntax, StringTokenizer tokenizer) {
      isTrue(tokenizer.hasMoreElements(), "Variable expected on the left-hand side");
      String t = tokenizer.nextToken();
      AbstractSymbol symbol = syntax.parseSymbol(t);
      isTrue(symbol.getType() == SymbolType.VARIABLE, "Variables must start with a upper case charachter");
      return (Variable) symbol;
   }

   private static void assignment(StringTokenizer tokenizer) {
      isTrue(tokenizer.hasMoreElements(), "Assignment expected");
      String t = tokenizer.nextToken();
      isTrue(t.equals("="), "Assignment expected");
   }

   private static List<AbstractSymbol> rightSide(CoreSyntax syntax, StringTokenizer tokenizer) {
      isTrue(tokenizer.hasMoreElements(), "Symbol expected on the right-hand side");
      List<AbstractSymbol> symbols = new ArrayList<>();
      String t = tokenizer.nextToken();
      if (!"\u03B5".equals(t)) {
         AbstractSymbol symbol = syntax.parseSymbol(t);
         symbols.add(symbol);
      }
      while (tokenizer.hasMoreElements()) {
         t = tokenizer.nextToken();
         isTrue(!"\u03B5".equals(t), "Empty word char '\u03B5' must only occur alone");
         symbols.add(syntax.parseSymbol(t));
      }
      return symbols;
   }
}
