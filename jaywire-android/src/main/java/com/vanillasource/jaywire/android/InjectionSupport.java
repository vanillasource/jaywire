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

import java.util.function.Consumer;
import com.vanillasource.jaywire.SingletonScopeSupport;

/**
 * Provides external injection to classes.
 */
public interface InjectionSupport extends SingletonScopeSupport {
   /**
    * Override this method to define injection rules for different
    * classes.
    */
   void onInjection();

   /**
    * Use this method to execute the injection rule
    * for the given object.
    */
   default void inject(Object targetObject) {
      getInjector().inject(targetObject);
   }

   default <T> Injection<T> inject(Class<T> targetClass) {
      return consumer -> getInjector().addInjection(targetClass, consumer);
   }

   default Injector getInjector() {
      return singleton(() -> new Injector());
   }

   interface Injection<T> {
      void with(Consumer<T> consumer);
   }
}
