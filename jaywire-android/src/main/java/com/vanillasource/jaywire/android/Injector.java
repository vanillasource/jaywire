/**
 * Copyright (C) 2019 VanillaSource
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

package com.vanillasource.jaywire.android;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public final class Injector {
   private final Map<Class<?>, Consumer<Object>> injections = new HashMap<>();

   @SuppressWarnings("unchecked")
   public void addInjection(Class<?> targetClass, Consumer<?> injection) {
      injections.put(targetClass, (Consumer<Object>)injection);
   }

   public void inject(Object targetObject) {
      Optional.ofNullable(injections.get(targetObject.getClass()))
         .orElseThrow(() -> new IllegalStateException("no injection defined for class: "+targetObject.getClass()))
         .accept(targetObject);
   }
}
