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

package com.vanillasource.jaywire.proxy;

import java.util.function.Function;
import java.util.function.Supplier;
import java.io.Serializable;

/**
 * Proxies a given supplier. If the supplier is serializable, then the resulting
 * proxy will also be serializable.
 */
public class SupplierProxy<T> implements Proxy<T>, Serializable {
   private Supplier<T> supplier;

   public SupplierProxy(Supplier<T> supplier) {
      this.supplier = supplier;
   }

   @Override
   public <R> R call(Function<T, R> body) {
      return body.apply(supplier.get());
   }
}

