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
 * Defines helper methods to levarage serialization related functions
 * in a module.
 */
public interface SerializationSupport {
   /**
    * Make a regular scope a singleton, and wrap so all produced
    * suppliers of this scope would be serializable themselves. Neither
    * the scope object, nor the issued objects need to be serializable.
    */
   Scope makeSerializableSingleton(Factory<Scope> scopeFactory);

   /**
    * Make a scope serializable by providing a serializable supplier that
    * can produce said scope. This <code>Supplier</code> <strong>must</strong>
    * be serializable, preferably generated from another scope.
    */
   Scope makeSerializable(Supplier<Scope> scopeSupplier);
}

