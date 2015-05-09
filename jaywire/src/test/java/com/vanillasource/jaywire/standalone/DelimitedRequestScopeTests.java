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
import com.vanillasource.jaywire.Factory;
import static com.vanillasource.jaywire.SerializationUtils.*;
import java.util.function.Supplier;

@Test
public class DelimitedRequestScopeTests {
   public void testProducesOneObjectPerRequest() {
      DelimitedRequestScope scope = new DelimitedRequestScope(new SingletonScope());
      Factory<Object> objectSupplier = () -> new Object();

      Object result1 = scope.get(objectSupplier);
      Object result2 = scope.get(objectSupplier);

      assertSame(result1, result2);
   }

   public void testProcudesAnotherObjectAfterOpen() {
      DelimitedRequestScope scope = new DelimitedRequestScope(new SingletonScope());
      Factory<Object> objectSupplier = () -> new Object();

      Object result1 = scope.get(objectSupplier);
      scope.open();
      Object result2 = scope.get(objectSupplier);

      assertNotSame(result1, result2);
   }

   public void testProcudesAnotherObjectAfterClose() {
      DelimitedRequestScope scope = new DelimitedRequestScope(new SingletonScope());
      Factory<Object> objectSupplier = () -> new Object();

      Object result1 = scope.get(objectSupplier);
      scope.open();
      Object result2 = scope.get(objectSupplier);

      assertNotSame(result1, result2);
   }

   public void testProducesOneObjectPerRequestEvenAfterDeserialization() throws Exception {
      DelimitedRequestScope scope = new DelimitedRequestScope(new SingletonScope());
      Supplier<Object> objectSupplier = scope.apply(() -> new Object());

      Object result1 = objectSupplier.get();
      Supplier<Object> deserializedObjectSupplier = serializeThenDeserialize(objectSupplier);
      Object result2 = deserializedObjectSupplier.get();

      assertSame(result1, result2);
   }

   @SuppressWarnings("unchecked")
   public void testProcudesAnotherObjectAfterOpenAndDeserialization() throws Exception {
      DelimitedRequestScope scope = new DelimitedRequestScope(new SingletonScope());
      Supplier<Object> objectSupplier = scope.apply(() -> new Object());

      Object result1 = objectSupplier.get();
      byte[] supplierBytes = serialize(objectSupplier);
      scope.open();
      Supplier<Object> deserializedObjectSupplier = deserialize(objectSupplier.getClass(), supplierBytes);
      Object result2 = deserializedObjectSupplier.get();

      assertNotSame(result1, result2);
   }

}


