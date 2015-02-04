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

package com.vanillasource.jaywire.impl;

import com.vanillasource.jaywire.Scope;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * An implementation of a singleton scope that keeps all
 * instances in an internal map. This scope is thread-safe,
 * and reentrant, which means during an initialization of a
 * single object other may also initialize as singleton scope.
 */
public class SingletonScope implements Scope {
   private Map<Supplier<?>, Object> instances = new HashMap<>();

   @Override
   @SuppressWarnings("unchecked")
   public synchronized <T> T get(Supplier<T> supplier) {
      T instance = (T) instances.get(supplier);
      if (instance == null) {
         instance = supplier.get();
         instances.put(supplier, instance);
      }
      return instance;
   }
}

