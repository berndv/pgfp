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

package org.sourcepit.lalr.core.grammar.graph;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParsingTableTest {

   final static short ACTION_SHIFT = (short) 0b0100000000000000;
   final static short ACTION_REDUCE = (short) 0b1000000000000000;
   final static short ACTION_ACCEPT = (short) 0b1100000000000000;

   private final static short MASK_ACTION = (short) 0b1100000000000000;
   private final static short MASK_DATA = (short) 0b0011111111111111;

   @Test
   public void testName() throws Exception {
      short s = (short) 0b1111111111111111;

      assertEquals(ACTION_ACCEPT, getAction(s));
      assertEquals((short) 0b0011111111111111, getData(s));
      System.out.println(getData(s));

      System.out.println(0xFFFFFFFF);
      System.out.println(0b11111111111111111111111111111111);
      System.out.println(0b11111111111111111110000000000000);

      ShortTableBuilder.merge(ACTION_ACCEPT, 0b0001111111111111);
      ShortTableBuilder.merge(ACTION_ACCEPT, 0b0001111111111111);
   }

   static short getData(short s) {
      return (short) (s & MASK_DATA);
   }

   static short getAction(short s) {
      return (short) (s & MASK_ACTION);
   }
}
