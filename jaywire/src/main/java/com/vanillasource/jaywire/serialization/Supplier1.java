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

package com.vanillasource.jaywire.serialization;

import java.util.function.Supplier;

/**
 * A supplier of objects with 1 parameter.
 */
@FunctionalInterface
public interface Supplier1<P1, T> {
   /**
    * Gets an object with the given parameter.
    */
   T get(P1 p1);

   /**
    * Sets the parameter and returns a supplier with no parameters.
    */
   default Supplier<T> param(P1 p1) {
      return () -> get(p1);
   }
}

