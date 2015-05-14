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

import com.vanillasource.function.Supplier1;
import com.vanillasource.jaywire.Factory1;

public class SerializableSupplier1<P1, T> extends DissociableObject implements Supplier1<P1, T> {
   private Factory1<P1, T> factory;

   public SerializableSupplier1() {
      super();
   }

   public SerializableSupplier1(DissociatingStorage storage, Factory1<P1, T> factory) {
      super(storage, factory.getKind());
      this.factory = factory;
   }

   @Override
   public T get(P1 p1) {
      return factory.get(p1);
   }
}

