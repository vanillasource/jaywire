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
import com.vanillasource.jaywire.Scope;
import java.util.Map;
import java.util.HashMap;

/**
 * An implementation of a singleton scope that keeps all
 * already instantiated objects in an internal map indexed by
 * the <i>kind</i> object returned by the factory. This scope is thread-safe,
 * and reentrant, which means during an initialization of a
 * single object other may also initialize as singleton scope.
 */
public class SingletonScope extends SerializableScope {
   private Map<Object, Object> instances = new HashMap<>();

   public SingletonScope(SerializableSupplier<Scope> indirectScopeSupplier) {
      super(indirectScopeSupplier);
   }

   @Override
   @SuppressWarnings("unchecked")
   public synchronized <T> T get(Factory<T> factory) {
      T instance = (T) instances.get(factory.getKind());
      if (instance == null) {
         instance = factory.get();
         instances.put(factory.getKind(), instance);
      }
      return instance;
   }
}

