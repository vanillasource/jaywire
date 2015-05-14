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

package com.vanillasource.jaywire.web;

import com.vanillasource.jaywire.RequestScopeSupport;
import com.vanillasource.jaywire.SingletonScopeSupport;
import com.vanillasource.jaywire.ThreadLocalScopeSupport;
import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.serialization.SerializationSupport;

/**
 * A module implementation that provides a request scope based on
 * the thread local scope. It assumes that each request will be handled
 * inside a single thread, and a single thread can not have two
 * requests at the same time.
 */
public interface DelimitedRequestScopeModule 
   extends SingletonScopeSupport, RequestScopeSupport, ThreadLocalScopeSupport, SerializationSupport {

   default DelimitedRequestScope getDelimetedRequestScope() {
      return singleton(() -> new DelimitedRequestScope(getThreadLocalScope()));
   }

   @Override
   default Scope getRequestScope() {
      return singleton(() -> makeSupplierSerializable(getDelimetedRequestScope()));
   }
}

