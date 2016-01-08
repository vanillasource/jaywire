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

package com.vanillasource.jaywire.proxy;

import org.testng.annotations.*;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.InOrder;
import java.util.function.Function;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Test
public class ProxyTests {
   private Proxy<String> proxy;

   @SuppressWarnings("unchecked")
   public void testAroundReceivesObject() {
      BiConsumer<String, Runnable> handler = mock(BiConsumer.class);

      proxy.onAround(handler).call(value -> value.length());

      verify(handler).accept(eq("Value"), any(Runnable.class));
   }

   public void testIfAroundDoesNotCallBodyNullIsReturned() {
      BiConsumer<String, Runnable> handler = (object, runnable) -> {};

      assertNull(proxy.onAround(handler).call(value -> value.length()));
   }

   public void testIfAroundDoesCallBodyItsReturnValueIsReturned() {
      BiConsumer<String, Runnable> handler = (object, runnable) -> runnable.run();

      assertEquals(proxy.onAround(handler).call(value -> value.length()), Integer.valueOf(5));
   }

   @SuppressWarnings("unchecked")
   public void testBeforeGetsCalledBeforeFunction() {
      Runnable handler = mock(Runnable.class);
      Function<String, Integer> body = mock(Function.class);

      proxy.onBefore(handler).call(body);

      InOrder order = inOrder(handler, body);
      order.verify(handler).run();
      order.verify(body).apply("Value");
   }

   @SuppressWarnings("unchecked")
   public void testAfterGetsCalledAfterFunction() {
      Runnable handler = mock(Runnable.class);
      Function<String, Integer> body = mock(Function.class);

      proxy.onAfter(handler).call(body);

      InOrder order = inOrder(handler, body);
      order.verify(body).apply("Value");
      order.verify(handler).run();
   }

   @BeforeMethod
   protected void setUp() {
      proxy = new Proxy<String>() {
         @Override
         public <R> R call(Function<String, R> body) {
            return body.apply("Value");
         }
      };
   }
}

