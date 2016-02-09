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

import com.vanillasource.jaywire.Factory;
import com.vanillasource.jaywire.SerializableScope;
import com.vanillasource.jaywire.Scope;

/**
 * An implementation of a thread local scope that is basically
 * a singleton scope in a thread local variable. Scope is thread-safe.
 */
public class ThreadLocalScope extends SerializableScope {
   private final ThreadLocal<SingletonScope> threadLocalSingletons;

   public ThreadLocalScope(SerializableSupplier<Scope> indirectScopeSupplier) {
      super(indirectScopeSupplier);
      threadLocalSingletons = ThreadLocal.withInitial(() -> new SingletonScope(indirectScopeSupplier));
   }

   @Override
   public <T> T get(Factory<T> factory) {
      return threadLocalSingletons.get().get(factory);
   }
}

