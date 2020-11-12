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

/**
 * A supplier of objects with 9 parameters.
 */
@FunctionalInterface
public interface Supplier9<P1, P2, P3, P4, P5, P6, P7, P8, P9, T> {
   /**
    * Gets an object with the given parameters.
    */
   T get(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, P6 p6, P7 p7, P8 p8, P9 p9);

   /**
    * Sets the first parameter and returns a supplier with
    * one less parameter to be set.
    */
   default Supplier8<P2, P3, P4, P5, P6, P7, P8, P9, T> param(P1 p1) {
      return (p2, p3, p4, p5, p6, p7, p8, p9) -> get(p1, p2, p3, p4, p5, p6, p7, p8, p9);
   }
}

