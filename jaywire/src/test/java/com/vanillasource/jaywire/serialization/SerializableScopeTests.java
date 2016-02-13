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
import static org.mockito.Mockito.*;
import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.Factory;
import java.io.NotSerializableException;
import static com.vanillasource.jaywire.serialization.SerializationUtils.*;
import java.util.function.Supplier;

@Test
public class SerializableScopeTests {
   private Scope delegateScope;
   private SerializableScope scope;

   @Test(expectedExceptions = NotSerializableException.class)
   public void testDelegateScopeIsNotSerializable() throws Exception {
      serialize(delegateScope);
   }

   public void testGetIsDelegated() {
      Factory<String> factory = () -> "Ni";

      scope.get(factory);

      verify(delegateScope).get(factory);
   }

   public void testSupplierIsSerializable() throws Exception {
      serialize( scope.apply( () -> "Ni" ) );
   }

   public void testSupplierIsDeserializable() throws Exception {
      serializeThenDeserialize( scope.apply( () -> "Ni" ) );
   }

   public void testDeserializedSupplierWillUseStaticallyDeterminedDelegate() throws Exception {
      Supplier<String> supplier = serializeThenDeserialize( scope.apply( () -> "Ni" ) );

      assertEquals(supplier.get(), "Nu");
   }

   @BeforeMethod
   protected void setUp() {
      delegateScope = mock(Scope.class);
      scope = new SerializableScope(delegateScope, () -> new FixedScope());
   }
}

