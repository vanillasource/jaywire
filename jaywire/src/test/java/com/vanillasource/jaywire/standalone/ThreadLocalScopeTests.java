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
import java.util.function.Supplier;

@Test
public class ThreadLocalScopeTests {
   public void testProducesOneObjectPerThread() throws InterruptedException {
      ThreadLocalScope scope = new ThreadLocalScope();
      Supplier<Object> objectSupplier = scope.apply(() -> new Object());

      Object result1 = threadExecute(objectSupplier);
      Object result2 = threadExecute(objectSupplier);

      assertNotSame(result1, result2);
   }

   public void testReturnsSameObjectInSameThread() {
      ThreadLocalScope scope = new ThreadLocalScope();
      Factory<Object> objectSupplier = () -> new Object();

      Object result1 = scope.get(objectSupplier);
      Object result2 = scope.get(objectSupplier);

      assertSame(result1, result2);
   }

   private Object threadExecute(Supplier<Object> supplier) throws InterruptedException {
      ObjectProducer producer = new ObjectProducer(supplier);
      Thread thread = new Thread(producer);
      thread.start();
      thread.join();
      return producer.result;
   }


   private static class ObjectProducer implements Runnable {
      private Object result;
      private Supplier<Object> supplier;

      private ObjectProducer(Supplier<Object> supplier) {
         this.supplier = supplier;
      }

      public void run() {
         result = supplier.get();
      }
   }
}


