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

package com.vanillasource.jaywire.web;

import org.testng.annotations.*;
import static org.testng.Assert.*;
import com.vanillasource.jaywire.Factory;
import java.util.Map;
import java.util.HashMap;

@Test
public class ThreadLocalStorageScopeTests {
   private Factory<String> factory = () -> "Ni";
   private ThreadLocalStorageScope<Map<String, Object>> scope;
   private Map<String, Object> storage;

   @Test(expectedExceptions = IllegalStateException.class)
   public void testNoStorageSetYetThrowsException() {
      scope.get(factory);
   }

   public void testScopeReturnsCreatedValue() {
      scope.setStorage(storage);

      assertEquals(scope.get(factory), "Ni");
   }

   public void testScopeStoresValueInStorage() {
      scope.setStorage(storage);
      scope.get(factory);

      assertEquals(storage.get(factory.getKind()), "Ni");
   }

   public void testScopeRetrivesValueFromStorage() {
      scope.setStorage(storage);
      storage.put(factory.getKind(), "Nu");

      assertEquals(scope.get(factory), "Nu");
   }

   @BeforeMethod
   protected void setUp() {
      scope = new ThreadLocalStorageScope<Map<String, Object>>(
            (map, key) -> map.get(key),
            (map, key, object) -> map.put(key, object)
      );
      storage = new HashMap<>();
   }
}


