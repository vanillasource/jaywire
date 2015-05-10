/**
 * Copyright (C) 2015 VanillaSource
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.vanillasource.jaywire.serialization;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;
import static com.vanillasource.jaywire.SerializationUtils.*;
import static com.vanillasource.jaywire.GarbageUtils.*;

@Test
public class DissociableObjectTests {
   private DissociatingStorage storage;
   private ObjectA a;
   private ObjectB b;

   public void testObjectIsSerializable() throws Exception {
      serializeThenDeserialize(a);
   }

   public void testSameObjectIsReturnedAfterDeserialization() throws Exception {
      Object result = serializeThenDeserialize(a);

      assertSame(result, a);
   }

   public void testObjectCanBeSerializedAgainAfterDeserialization() throws Exception {
      serializeThenDeserialize(serializeThenDeserialize(b));
   }

   public void testObjectsDoNotSerializeInstanceVariables() throws Exception {
      byte[] aBytes = serialize(a);
      byte[] bBytes = serialize(a);
      
      assertEquals(aBytes.length, bBytes.length);
   }

   public static class ObjectA extends DissociableObject {
      private byte value1;

      public ObjectA() {
      }

      public ObjectA(DissociatingStorage storage, Object kind) {
         super(storage, kind);
      }
   }

   public static class ObjectB extends DissociableObject {
      private byte value1;
      private String value2;
      private long value3;

      public ObjectB() {
      }

      public ObjectB(DissociatingStorage storage, Object kind) {
         super(storage, kind);
      }
   }

   @BeforeMethod
   @SuppressWarnings("unchecked")
   protected void setUp() {
      storage = new DissociatingStorage();
      a = new ObjectA(storage, new Object());
      b = new ObjectB(storage, new Object());
   }

}

