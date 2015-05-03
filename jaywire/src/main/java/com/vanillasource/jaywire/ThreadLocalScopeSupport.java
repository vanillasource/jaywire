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
 * thread local scope.
 */
public interface ThreadLocalScopeSupport {
   /**
    * Returns a scope that will instantiate only once for each new
    * thread. Please note, that threads might be pooled or otherwise
    * shared, then objects can leak between functions.
    */
   Scope getThreadLocalScope();

   /**
    * Convenience method to produce thread local supplier easily. Equals
    * <code>getThreadLocalScope().apply(&lt;supplier&gt;)</code>.
    */
   default <T> Supplier<T> threadLocal(Factory<T> supplier) {
      return getThreadLocalScope().apply(supplier);
   }
}

