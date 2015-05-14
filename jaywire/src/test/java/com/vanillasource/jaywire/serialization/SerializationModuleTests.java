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
import com.vanillasource.jaywire.Scope;
import com.vanillasource.function.Supplier1;
import java.io.NotSerializableException;

@Test
public class SerializationModuleTests {
   private ModuleImpl module;

   @Test(expectedExceptions = NotSerializableException.class)
   public void testSupplier1NotSerializableByDefault() throws Exception {
      serializeThenDeserialize(module.getObjectForString());
   }

   public void testSupplier1MadeSerializableIsSerializable() throws Exception {
      serializeThenDeserialize(module.getSerializableObjectForString());
   }

   @BeforeMethod
   protected void setUp() {
      module = new ModuleImpl();
   }

   public static class ModuleImpl implements SerializationModule {
      private DissociatingStorage storage = new DissociatingStorage();
      @Override
      public Scope getSingletonScope() {
         throw new UnsupportedOperationException("no scope");
      }

      @Override
      public DissociatingStorage getDissociatingStorage() {
         return storage;
      }

      public Supplier1<String, Object> getObjectForString() {
         return param -> new Object();
      }

      public Supplier1<String, Object> getSerializableObjectForString() {
         return makeSerializable(param -> new Object());
      }
   }
}



