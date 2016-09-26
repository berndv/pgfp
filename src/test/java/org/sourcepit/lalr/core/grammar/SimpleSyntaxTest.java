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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.sourcepit.lalr.core.grammar.SymbolType.TERMINAL;
import static org.sourcepit.lalr.core.grammar.SymbolType.VARIABLE;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

import org.junit.Test;

public class SimpleSyntaxTest {


   @Test
   public void testParseProductionEpsilon() {
      Syntax syntax = new SimpleSyntax();
      Production p = syntax.parseProduction("S = ");
      assertTrue(p.isEmpty());
      assertEquals("S = ", p.toString());
   }

   @Test
   public void testEof() {
      Syntax syntax = new SimpleSyntax();
      Terminal eof = syntax.createTerminal("$");
      assertEquals(syntax.getEofTerminal(), eof);

      syntax.parseProduction("S = s $");
      try {
         syntax.parseProduction("S = $ s");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("EOF terminal '$' may only occur at the end of the right-hand side", e.getMessage());
      }
   }

   @Test
   public void testParseProductionEmptyString() {
      Syntax syntax = new SimpleSyntax();
      try {
         syntax.parseProduction("");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Variable expected on the left-hand side", e.getMessage());
      }
   }

   @Test
   public void testParseProductionNoAssignment() {
      Syntax syntax = new SimpleSyntax();
      try {
         syntax.parseProduction("S");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Assignment expected", e.getMessage());
      }

      try {
         syntax.parseProduction("S f");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Assignment expected", e.getMessage());
      }
   }

   @Test
   public void testParseProductionNoRightHandSide() {
      Syntax syntax = new SimpleSyntax();
      assertTrue(syntax.parseProduction("S = ").isEmpty());
   }

   @Test
   public void testParseProductionIllegalRightHandSide() {
      Syntax syntax = new SimpleSyntax();
      try {
         syntax.parseProduction(".");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of symbol '.' must be a valid unicode identifier start char", e.getMessage());
      }

      try {
         syntax.parseProduction("S = .");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of symbol '.' must be a valid unicode identifier start char", e.getMessage());
      }
   }

   @Test
   public void testParseProduction() {
      Syntax syntax = new SimpleSyntax();
      Production p = syntax.parseProduction("S = a b C");

      assertEquals(syntax.parseSymbol("S"), p.getLeftSide());

      List<AbstractSymbol> rightSide = p.getRightSide();
      assertEquals(3, rightSide.size());

      assertEquals(syntax.parseSymbol("a"), rightSide.get(0));
      assertEquals(syntax.parseSymbol("b"), rightSide.get(1));
      assertEquals(syntax.parseSymbol("C"), rightSide.get(2));
   }


   @Test
   public void testFromString() {
      final Syntax syntax = new SimpleSyntax();

      AbstractSymbol s = syntax.parseSymbol("s");
      assertEquals(Terminal.class, s.getClass());
      assertEquals(SymbolType.TERMINAL, s.getType());
      assertEquals("s", s.toString());

      s = syntax.parseSymbol("S");
      assertEquals(Variable.class, s.getClass());
      assertEquals(SymbolType.VARIABLE, s.getType());
      assertEquals("S", s.toString());

      s = syntax.parseSymbol("foo");
      assertEquals(Terminal.class, s.getClass());
      assertEquals(SymbolType.TERMINAL, s.getType());
      assertEquals("foo", s.toString());

      try {
         syntax.parseSymbol(" Foo ");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of symbol ' Foo ' must be a valid unicode identifier start char", e.getMessage());
      }

      try {
         syntax.parseSymbol("");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Symbol must not be empty", e.getMessage());
      }
   }


   @Test
   public void testValidateSymbolName() throws Exception {
      SimpleSyntax.validateSymbolName(VARIABLE, "F");
      SimpleSyntax.validateSymbolName(VARIABLE, "Foo");
      SimpleSyntax.validateSymbolName(VARIABLE, "FoO");
      try {
         SimpleSyntax.validateSymbolName(VARIABLE, "ε");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of variable 'ε' must be upper case", e.getMessage());
      }
      try {
         SimpleSyntax.validateSymbolName(VARIABLE, "f");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of variable 'f' must be upper case", e.getMessage());
      }
      try {
         SimpleSyntax.validateSymbolName(VARIABLE, "$");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of symbol '$' must be a valid unicode identifier start char", e.getMessage());
      }
      try {
         SimpleSyntax.validateSymbolName(VARIABLE, "Foo$");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Symbol 'Foo$' contains invlaid char '$'", e.getMessage());
      }

      SimpleSyntax.validateSymbolName(TERMINAL, "f");
      SimpleSyntax.validateSymbolName(TERMINAL, "foo");
      SimpleSyntax.validateSymbolName(TERMINAL, "foO");
      SimpleSyntax.validateSymbolName(TERMINAL, "ε");
      try {
         SimpleSyntax.validateSymbolName(TERMINAL, "F");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of terminal symbol 'F' must be lower case", e.getMessage());
      }
      SimpleSyntax.validateSymbolName(TERMINAL, "$");
      try {
         SimpleSyntax.validateSymbolName(TERMINAL, "foo$");
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Symbol 'foo$' contains invlaid char '$'", e.getMessage());
      }
   }

   @Test
   public void testValidateSymbolName32BitUnicode() throws Exception {
      int codePoint = Character.toCodePoint('\uD801', '\uDC00');
      String str = new String(Character.toChars(codePoint));
      assertTrue(Character.isUnicodeIdentifierStart(codePoint) && Character.isUpperCase(codePoint));
      SimpleSyntax.validateSymbolName(VARIABLE, str);
      try {
         SimpleSyntax.validateSymbolName(TERMINAL, str);
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of terminal symbol '𐐀' must be lower case", e.getMessage());
      }

      codePoint = Character.toCodePoint('\uD801', '\uDC28');
      str = new String(Character.toChars(codePoint));
      assertTrue(Character.isUnicodeIdentifierStart(codePoint) && Character.isLowerCase(codePoint));
      try {
         SimpleSyntax.validateSymbolName(VARIABLE, str);
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of variable '𐐨' must be upper case", e.getMessage());
      }
      SimpleSyntax.validateSymbolName(TERMINAL, str);

      codePoint = Character.toCodePoint('\uD800', '\uDDFD');
      str = new String(Character.toChars(codePoint));
      assertTrue(!Character.isUnicodeIdentifierStart(codePoint) && Character.isUnicodeIdentifierPart(codePoint));
      try {
         SimpleSyntax.validateSymbolName(VARIABLE, str);
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of symbol '𐇽' must be a valid unicode identifier start char", e.getMessage());
      }
      try {
         SimpleSyntax.validateSymbolName(TERMINAL, str);
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("First char of symbol '𐇽' must be a valid unicode identifier start char", e.getMessage());
      }
      SimpleSyntax.validateSymbolName(VARIABLE, "S" + str);
      SimpleSyntax.validateSymbolName(TERMINAL, "s" + str);

      codePoint = Character.toCodePoint('\uD800', '\uDC0C');
      str = new String(Character.toChars(codePoint));
      assertTrue(!Character.isUnicodeIdentifierStart(codePoint) && !Character.isUnicodeIdentifierPart(codePoint));
      try {
         SimpleSyntax.validateSymbolName(VARIABLE, "S" + str);
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Symbol 'S𐀌' contains invlaid char '𐀌'", e.getMessage());
      }
      try {
         SimpleSyntax.validateSymbolName(TERMINAL, "s" + str);
         fail();
      }
      catch (IllegalArgumentException e) {
         assertEquals("Symbol 's𐀌' contains invlaid char '𐀌'", e.getMessage());
      }
   }

   public void print32BitUnicodeCodePoints() {


      // Character.isUnicodeIdentifierStart(codePoint) && Character.isUpperCase(codePoint)
      // ---------------------------------------------------------------------
      // new String("\uD801\uDC00");
      //
      // Character.isUnicodeIdentifierStart(codePoint) && Character.isLowerCase(codePoint)
      // ---------------------------------------------------------------------
      // new String("\uD801\uDC28");
      //
      // !Character.isUnicodeIdentifierStart(codePoint) && Character.isUnicodeIdentifierPart(codePoint)
      // ---------------------------------------------------------------------
      // new String("\uD800\uDDFD");
      //
      // !Character.isUnicodeIdentifierStart(codePoint) && !Character.isUnicodeIdentifierPart(codePoint)
      // ---------------------------------------------------------------------
      // new String("\uD800\uDC0C");


      System.out.println();
      System.out.println("Character.isUnicodeIdentifierStart(codePoint) && Character.isUpperCase(codePoint)");
      System.out.println("---------------------------------------------------------------------");
      System.out.println(toNewStr(findFirst((Integer codePoint) -> {
         return Character.isUnicodeIdentifierStart(codePoint) && Character.isUpperCase(codePoint);
      })));

      System.out.println();
      System.out.println("Character.isUnicodeIdentifierStart(codePoint) && Character.isLowerCase(codePoint)");
      System.out.println("---------------------------------------------------------------------");
      System.out.println(toNewStr(findFirst((Integer codePoint) -> {
         return Character.isUnicodeIdentifierStart(codePoint) && Character.isLowerCase(codePoint);
      })));

      System.out.println();
      System.out
         .println("!Character.isUnicodeIdentifierStart(codePoint) && Character.isUnicodeIdentifierPart(codePoint)");
      System.out.println("---------------------------------------------------------------------");
      System.out.println(toNewStr(findFirst((Integer codePoint) -> {
         return !Character.isUnicodeIdentifierStart(codePoint) && Character.isUnicodeIdentifierPart(codePoint);
      })));

      System.out.println();
      System.out
         .println("!Character.isUnicodeIdentifierStart(codePoint) && !Character.isUnicodeIdentifierPart(codePoint)");
      System.out.println("---------------------------------------------------------------------");
      System.out.println(toNewStr(findFirst((Integer codePoint) -> {
         return !Character.isUnicodeIdentifierStart(codePoint) && !Character.isUnicodeIdentifierPart(codePoint);
      })));

   }

   private static int findFirst(Predicate<Integer> predicate) {
      int hsLow = Character.MIN_HIGH_SURROGATE; // 0xD800;
      int hsHigh = Character.MAX_HIGH_SURROGATE; // 0xDBFF;

      int lsLow = Character.MIN_LOW_SURROGATE; // 0xDC00;
      int lsHigh = Character.MAX_LOW_SURROGATE; // 0xDFFF;

      for (int hs = hsLow; hs <= hsHigh; hs++) {
         for (int ls = lsLow; ls <= lsHigh; ls++) {
            final int codePoint = Character.toCodePoint((char) hs, (char) ls);
            if (predicate.test(Integer.valueOf(codePoint))) {
               return codePoint;
            }
         }
      }
      throw new NoSuchElementException();
   }

   private String toNewStr(int codePoint) {
      char[] chars = Character.toChars(codePoint);
      switch (chars.length) {
         case 1 :
            return String.format("new String(\"\\u%04X\");", (int) chars[0]);
         case 2 :
            return String.format("new String(\"\\u%04X\\u%04X\");", (int) chars[0], (int) chars[1]);
         default :
            throw new IllegalStateException();
      }
   }

}
