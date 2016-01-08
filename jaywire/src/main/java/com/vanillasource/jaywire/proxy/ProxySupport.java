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

import java.util.function.Supplier;

/**
 * Adds support for proxy related functionality to a module.
 */
public interface ProxySupport {
   /**
    * Make a proxy out of a supplier. A proxy is a more controlled environment than
    * a supplier, because there is a definite 'end' to the usage.
    * @return A new proxy object for a given supplier.
    */
   default <T> Proxy<T> proxy(Supplier<T> supplier) {
      return new SupplierProxy<>(supplier);
   }
}

