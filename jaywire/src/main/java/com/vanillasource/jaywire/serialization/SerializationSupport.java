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

import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.Factory;
import java.util.function.Supplier;
import com.vanillasource.function.Supplier1;
import com.vanillasource.function.Supplier2;
import com.vanillasource.function.Supplier3;
import com.vanillasource.function.Supplier4;
import com.vanillasource.function.Supplier5;
import com.vanillasource.function.Supplier6;
import com.vanillasource.function.Supplier7;
import com.vanillasource.function.Supplier8;
import com.vanillasource.function.Supplier9;

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
   Scope makeScopeSerializableSingleton(Factory<Scope> scopeSerializableFactory);

   /**
    * Make a scope serializable by providing a serializable supplier that
    * can produce said scope. This <code>Supplier</code> <strong>must</strong>
    * be serializable, preferably generated from another scope.
    */
   Scope makeScopeSerializable(SerializableSupplier<Scope> scopeSupplier);

   <T> Supplier<T> makeSerializable(SerializableSupplier<T> supplier);

   <P1, T> Supplier1<P1, T> makeSerializable(SerializableSupplier1<P1, T> supplier);

   <P1, P2, T> Supplier2<P1, P2, T> makeSerializable(SerializableSupplier2<P1, P2, T> supplier);

   <P1, P2, P3, T> Supplier3<P1, P2, P3, T> makeSerializable(SerializableSupplier3<P1, P2, P3, T> supplier);

   <P1, P2, P3, P4, T> Supplier4<P1, P2, P3, P4, T> makeSerializable(SerializableSupplier4<P1, P2, P3, P4, T> supplier);

   <P1, P2, P3, P4, P5, T> Supplier5<P1, P2, P3, P4, P5, T> makeSerializable(SerializableSupplier5<P1, P2, P3, P4, P5, T> supplier);

   <P1, P2, P3, P4, P5, P6, T> Supplier6<P1, P2, P3, P4, P5, P6, T> makeSerializable(SerializableSupplier6<P1, P2, P3, P4, P5, P6, T> supplier);

   <P1, P2, P3, P4, P5, P6, P7, T> Supplier7<P1, P2, P3, P4, P5, P6, P7, T> makeSerializable(SerializableSupplier7<P1, P2, P3, P4, P5, P6, P7, T> supplier);

   <P1, P2, P3, P4, P5, P6, P7, P8, T> Supplier8<P1, P2, P3, P4, P5, P6, P7, P8, T> makeSerializable(SerializableSupplier8<P1, P2, P3, P4, P5, P6, P7, P8, T> supplier);

   <P1, P2, P3, P4, P5, P6, P7, P8, P9, T> Supplier9<P1, P2, P3, P4, P5, P6, P7, P8, P9, T> makeSerializable(SerializableSupplier9<P1, P2, P3, P4, P5, P6, P7, P8, P9, T> supplier);
}

