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

package com.vanillasource.jaywire;

import java.util.function.Supplier;

/**
 * Common interface for scopes for dependency injection. A scope may
 * decide when and how many times to instantiate objects from the
 * given factory.
 */
public interface Scope {
   /**
    * Get an instance of the given factory for this scope. This
    * instance may be a cached instance, or may be created through
    * the factory depending on the scope semantics.
    */
   <T> T get(Factory<T> factory);

   /**
    * Apply the semantics of this scope to the provided supplier. The returned
    * supplier behaves as the <code>get()</code> method of this scope.
    */
   default <T> Supplier<T> apply(Factory<T> factory) {
      return () -> get(factory);
   }
}

