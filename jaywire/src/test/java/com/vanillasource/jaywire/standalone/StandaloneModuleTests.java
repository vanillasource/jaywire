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
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;
import static com.vanillasource.jaywire.standalone.SerializationUtils.*;
import static org.mockito.Mockito.*;
import java.util.function.Supplier;

@Test
public class StandaloneModuleTests {

   public void testSingletonSupplierSerializable() throws Exception {
      TestModule module = new TestModule();

      serializeThenDeserialize(module.getSingletonObject());
   }

   public void testThreadLocalSupplierSerializable() throws Exception {
      TestModule module = new TestModule();

      serializeThenDeserialize(module.getThreadLocalObject());
   }

   public void testAdditionalAttributesAreNotSerialized() throws Exception {
      AdditionalAttributeTestModule module = new AdditionalAttributeTestModule();

      serializeThenDeserialize(module.getSingletonObject());
   }

   public void testSerializedSingletonDeserializesAsCounterpartyInOtherModule() throws Exception {
      ParameterizedSingletonTestModule module1 = new ParameterizedSingletonTestModule("ModuleOne");
      Supplier<ParameterizedSingletonTestModule.ParameterizedSingleton> singletonSupplier1 = module1.getSingletonObject();

      ParameterizedSingletonTestModule module2 = new ParameterizedSingletonTestModule("ModuleTwo");
      Supplier<ParameterizedSingletonTestModule.ParameterizedSingleton> singletonSupplier2 = serializeThenDeserialize(singletonSupplier1);

      assertEquals(singletonSupplier2.get().getParameter(), "ModuleTwo");
   }

   public void testNonAmbigousModuleCanDeserializeSupplier() throws Exception {
      AmbigousTestModule module = new AmbigousTestModule();

      serializeThenDeserialize(module.getSingletonObject());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAmbigousModuleThrowsException() throws Exception {
      AmbigousTestModule module1 = new AmbigousTestModule();
      AmbigousTestModule module2 = new AmbigousTestModule();

      serializeThenDeserialize(module1.getSingletonObject());
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNonExistentModuleWillThrowException() throws Exception {
      AmbigousTestModule module = new AmbigousTestModule();
      StandaloneModule.DESERIALIZATION_INSTANCE = null;

      serializeThenDeserialize(module.getSingletonObject());
   }

   public void testRegisteredCloseableGetsClosed() throws Exception {
      AutoCloseable closeable = mock(AutoCloseable.class);

      try (TestModule module = new TestModule()) {
         module.closeWithModule(closeable);
      }

      verify(closeable).close();
   }

   public void testAllRegisteredCloseablesGetClosed() throws Exception {
      AutoCloseable closeable1 = mock(AutoCloseable.class);
      AutoCloseable closeable2 = mock(AutoCloseable.class);

      try (TestModule module = new TestModule()) {
         module.closeWithModule(closeable1);
         module.closeWithModule(closeable2);
      }

      verify(closeable1).close();
      verify(closeable2).close();
   }

   public void testCloseAbortsOnFirstFailure() throws Exception {
      AutoCloseable closeable1 = mock(AutoCloseable.class);
      doThrow(new Exception("fail")).when(closeable1).close();
      AutoCloseable closeable2 = mock(AutoCloseable.class);

      try {
         try (TestModule module = new TestModule()) {
            module.closeWithModule(closeable1);
            module.closeWithModule(closeable2);
         }
      } catch (Exception e) {
         // All ok
      }

      verify(closeable1).close();
      verify(closeable2, never()).close();
   }

   @Test(expectedExceptions = Exception.class)
   public void testLastCloseExceptionGetsRethrown() throws Exception {
      AutoCloseable closeable = mock(AutoCloseable.class);
      doThrow(new Exception("fail")).when(closeable).close();

      try (TestModule module = new TestModule()) {
         module.closeWithModule(closeable);
      }

   }

   @BeforeMethod
   protected void setUp() {
      StandaloneModule.DESERIALIZATION_INSTANCE = null;
      StandaloneModule.DESERIALIZATION_INSTANCE_AMBIGOUS = false;
   }
}


