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
 * Pull this interface into a Module to define a dependency to a
 * singleton scope.
 */
public interface SingletonScopeSupport {
   /**
    * Returns a scope that will instantiate only once for each given
    * supplier class.
    */
   Scope getSingletonScope();

   /**
    * Convenience method to produce singleton objects easily. Equals
    * <code>getSingletonScope().get(&lt;supplier&gt;)</code>.
    */
   default <T> T singleton(Factory<T> factory) {
      return getSingletonScope().get(factory);
   }

   /**
    * Convenience method to produce a <code>Supplier</code> easily. Equals
    * <code>getSingletonScope().apply(&lt;supplier&gt;)</code>.
    */
   default <T> Supplier<T> singletonSupplier(Factory<T> factory) {
      return getSingletonScope().apply(factory);
   }
}

