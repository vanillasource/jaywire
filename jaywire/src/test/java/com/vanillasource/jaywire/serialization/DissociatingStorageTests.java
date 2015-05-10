/*
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
import java.util.function.Supplier;
import com.vanillasource.jaywire.serialization.DissociatingStorage.Key;
import java.util.List;
import java.util.ArrayList;
import java.rmi.dgc.VMID;

@Test
public class DissociatingStorageTests {
   private DissociatingStorage storage;
   private Object kind;
   private Supplier<Object> supplier;

   public void testStoredSuppliersAreReturnsWithKey() {
      Key<Supplier<Object>> key = storage.put(kind, supplier);

      Supplier<Object> supplierGot = DissociatingStorage.get(key);

      assertSame(supplierGot, supplier);
   }

   public void testKeyIsSerializable() throws Exception {
      Key<Supplier<Object>> key = storage.put(kind, supplier);

      Key<Supplier<Object>> deserializedKey = serializeThenDeserialize(key);

      assertEquals(deserializedKey, key);
   }

   public void testStoredSupplierCanBeReciveredWithDeserializedKey() throws Exception {
      Key<Supplier<Object>> key = storage.put(kind, supplier);

      Supplier<Object> supplierGot = DissociatingStorage.get(serializeThenDeserialize(key));

      assertSame(supplierGot, supplier);
   }

   public void testSupplierGetsGarbageCollectedWithStorage() {
      waitObjectCollected( () -> {
         DissociatingStorage localStorage = new DissociatingStorage();
         Supplier<Object> localSupplier = new DummySupplier();
         Key<Supplier<Object>> key = localStorage.put(kind, localSupplier);
         return localSupplier;
      });
   }

   public void testSupplierGetsDissociatedFromKeyAndGetsGarbageCollectedEvenIfKeySurvives() {
      List<Key<Supplier<Object>>> keys = new ArrayList<>();

      waitObjectCollected( () -> {
         DissociatingStorage localStorage = new DissociatingStorage();
         Supplier<Object> localSupplier = new DummySupplier();
         Key<Supplier<Object>> key = localStorage.put(kind, localSupplier);
         keys.add(key);
         return localSupplier;
      });
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testSupplierCanNotBeRecoveredAfterStorageGetsGarbageCollected() {
      List<Key<Supplier<Object>>> keys = new ArrayList<>();

      waitObjectCollected( () -> {
         DissociatingStorage localStorage = new DissociatingStorage();
         Supplier<Object> localSupplier = new DummySupplier();
         Key<Supplier<Object>> key = localStorage.put(kind, localSupplier);
         keys.add(key);
         return localSupplier;
      });

      DissociatingStorage.get(keys.get(0));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testKeysFromOtherVMThrowException() {
      DissociatingStorage.get(new Key<Supplier<Object>>(new VMID(), 1, 1));
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testKeyForUnknownKindThrowsException() {
      Key<Supplier<Object>> key = storage.put(kind, supplier);

      Key<Supplier<Object>> key2 = new Key<Supplier<Object>>(key.getVmId(), key.getStorageId(), key.getKindId()+1);

      DissociatingStorage.get(key2);
   }

   @BeforeMethod
   protected void setUp() {
      storage = new DissociatingStorage();
      kind = new Object();
      supplier = () -> new Object();
   }

   public class DummySupplier implements Supplier<Object> {
      @Override
      public Object get() {
         return new Object();
      }
   }
}

