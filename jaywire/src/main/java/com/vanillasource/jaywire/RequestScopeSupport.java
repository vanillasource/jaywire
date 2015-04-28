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
 * request scope.
 */
public interface RequestScopeSupport {
   /**
    * Returns a scope that will instantiate objects only once
    * for one call/request. Semantics are defined in detail by
    * implementation, mostly used in web context.
    */
   Scope getRequestScope();

   /**
    * Convenience method to produce request scope suppliers easily. Equals
    * <code>getRequestScope().apply(&lt;supplier&gt;)</code>.
    */
   default <T> Supplier<T> requestScope(Supplier<T> supplier) {
      return getRequestScope().apply(supplier);
   }
}

