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
import com.vanillasource.jaywire.Factory;
import static org.mockito.Mockito.*;
import com.vanillasource.jaywire.Scope;

@Test
public class SerializableSupplierTests {
   private DissociatingStorage storage;
   private SerializableSupplier<Object> supplier;
   private Factory<Object> factory;
   private Scope scope;

   public void testSupplierGetsObjectFromFactory() {
      supplier.get();

      verify(factory).get();
   }

   @BeforeMethod
   @SuppressWarnings("unchecked")
   protected void setUp() {
      storage = new DissociatingStorage();
      factory = mock(Factory.class);
      scope = new Scope() {
         @Override
         public <T> T get(Factory<T> factory) {
            return factory.get();
         }
      };
      supplier = new SerializableSupplier<Object>(storage, scope, factory);
   }

}

