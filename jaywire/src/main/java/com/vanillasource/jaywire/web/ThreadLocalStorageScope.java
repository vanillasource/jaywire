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

import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.Factory;
import javax.servlet.http.HttpSession;
import java.util.function.BiFunction;

/**
 * An scope that stores all kinds in a "storage" object
 * that is kept as a thread-local variable.
 */
public class ThreadLocalStorageScope<S> implements Scope {
   private ThreadLocal<S> storageThreadLocal = new ThreadLocal<>();
   private BiFunction<S, String, Object> retrievalFunction;
   private TriConsumer<S, String, Object> storageFunction;

   public ThreadLocalStorageScope(BiFunction<S, String, Object> retrievalFunction,
         TriConsumer<S, String, Object> storageFunction) {
      this.retrievalFunction = retrievalFunction;
      this.storageFunction = storageFunction;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <T> T get(Factory<T> factory) {
      S storage = storageThreadLocal.get();
      if (storage == null) {
         throw new IllegalStateException("there was no storage object while trying to get scoped object");
      }
      T object = (T) retrievalFunction.apply(storage, factory.getKind());
      if (object == null) {
         object = factory.get();
         storageFunction.accept(storage, factory.getKind(), object);
      }
      return object;
   }

   public void setStorage(S storage) {
      storageThreadLocal.set(storage);
   }

   public void clearStorage() {
      storageThreadLocal.set(null);
   }

   public interface TriConsumer<A1, A2, A3> {
      void accept(A1 a1, A2 a2, A3 a3);
   }
}

