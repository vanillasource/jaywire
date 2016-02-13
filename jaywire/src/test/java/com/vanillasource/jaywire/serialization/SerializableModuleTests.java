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
import static com.vanillasource.jaywire.serialization.SerializationUtils.*;
import static com.vanillasource.jaywire.serialization.SerializationUtil.*;

@Test
public class SerializableModuleTests {
   public void testNonAmbigousModuleCanDeserialize() throws Exception {
      AmbigousTestModule module = new AmbigousTestModule();

      deserializing( () -> serializeThenDeserialize(module) );
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testAmbigousModuleThrowsException() throws Exception {
      AmbigousTestModule module1 = new AmbigousTestModule();
      AmbigousTestModule module2 = new AmbigousTestModule();

      deserializing( () -> serializeThenDeserialize(module1) );
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testNonExistentModuleWillThrowException() throws Exception {
      AmbigousTestModule module = new AmbigousTestModule();
      SerializableModule.clearStaticInstance();

      deserializing( () -> serializeThenDeserialize(module) );
   }


   @BeforeMethod
   protected void setUp() {
      SerializableModule.clearStaticInstance();
   }
}

