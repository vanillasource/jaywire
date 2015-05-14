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
import static com.vanillasource.jaywire.SerializationUtils.*;
import java.util.function.Supplier;

@Test
public class StandaloneModuleTests {
   private StandaloneModule module;

   public void testSingletonSupplierSerializable() throws Exception {
      serializeThenDeserialize(module.getSingletonScope().apply(() -> new Object()));
   }

   public void testThreadLocalSupplierSerializable() throws Exception {
      serializeThenDeserialize(module.threadLocal(() -> new Object()));
   }

   @BeforeMethod
   protected void setUp() {
      module = new StandaloneModule() {
      };
   }
}


