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

package com.vanillasource.jaywire.standalone;

import org.testng.annotations.Test;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import java.io.*;
import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.Factory;
import java.util.function.Supplier;

@Test
public class SeparatingSupplierTests {
   @Test(expectedExceptions = NotSerializableException.class)
   public void testNormalSupplierIsNotSerializable() throws Exception {
      Supplier<Object> supplier = new NonSerializableScope().apply( () -> new Object() );

      serializeThenDeserialize(supplier);
   }

   public void testSeparatingScopeReturnsASerializableSupplier() throws Exception {
      Supplier<Object> supplier = new SingletonInstanceScope().apply( () -> new Object() );

      serializeThenDeserialize(supplier);
   }

   public void testDeserializedSupplierReturnsSameInstance() throws Exception {
      Supplier<Object> supplier = new SingletonInstanceScope().apply( () -> new Object() );
      Object instance = supplier.get();

      Supplier<Object> deserializedSupplier = serializeThenDeserialize(supplier);
      Object instanceFromDeserializedSupplier = deserializedSupplier.get();

      assertSame(instance, instanceFromDeserializedSupplier);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testIfScopeIsClosedDeserializationThrowsException() throws Exception {
      Supplier<Object> supplier = new SingletonInstanceScope().apply( () -> new Object() );
      SeparatingSupplier.clear();

      serializeThenDeserialize(supplier);
   }

   @Test(expectedExceptions = IllegalStateException.class)
   public void testDeserializedInDifferentVMDeserializationThrowsException() throws Exception {
      Supplier<Object> supplier = new SingletonInstanceScope().apply( () -> new Object() );
      ((SeparatingSupplier<Object>) supplier).resetVMID();

      serializeThenDeserialize(supplier);
   }

   @SuppressWarnings("unchecked")
   private <T> T serializeThenDeserialize(T object) throws Exception {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
      objectOut.writeObject(object);
      objectOut.close();

      ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
      ObjectInputStream objectIn = new ObjectInputStream(byteIn);
      return (T) objectIn.readObject();
   }

   public static class NonSerializableScope implements Scope {
      @Override
      public <T> T get(Factory<T> factory) {
         return factory.get();
      }
   }

   public static class SingletonInstanceScope implements SeparatingScope {
      private Object instance;

      @Override
      @SuppressWarnings("unchecked")
      public <T> T get(Factory<T> factory) {
         return (T) instance;
      }
   }
}


