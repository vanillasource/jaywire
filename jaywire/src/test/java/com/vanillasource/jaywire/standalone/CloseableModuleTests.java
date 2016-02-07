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
import java.util.function.Supplier;

@Test
public class CloseableModuleTests {
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

   public void testAllRegisteredCloseablesGetClosedEvenIfTheyThrowExceptionsOnClose() throws Exception {
      AutoCloseable closeable1 = mock(AutoCloseable.class);
      doThrow(new Exception("fail")).when(closeable1).close();
      AutoCloseable closeable2 = mock(AutoCloseable.class);
      doThrow(new Exception("fail")).when(closeable2).close();

      try {
         try (TestModule module = new TestModule()) {
            module.closeWithModule(closeable1);
            module.closeWithModule(closeable2);
         }
      } catch (Exception e) {
         // All ok
      }

      verify(closeable1).close();
      verify(closeable2).close();
   }

   @Test(expectedExceptions = Exception.class)
   public void testLastCloseExceptionGetsRethrown() throws Exception {
      AutoCloseable closeable = mock(AutoCloseable.class);
      doThrow(new Exception("fail")).when(closeable).close();

      try (TestModule module = new TestModule()) {
         module.closeWithModule(closeable);
      }

   }

   private static class TestModule extends SingletonScopeModule
         implements CloseableModule {
   }
}


