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

import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.Factory;
import javax.servlet.ServletRequest;

public class ServletRequestScope implements Scope {
   private ThreadLocal<ServletRequest> requestThreadLocal = new ThreadLocal<>();

   @Override
   @SuppressWarnings("unchecked")
   public <T> T get(Factory<T> factory) {
      ServletRequest request = requestThreadLocal.get();
      if (request == null) {
         throw new IllegalStateException("there was no request trying to get request scoped object");
      }
      T object = (T) request.getAttribute(factory.getKind());
      if (object == null) {
         object = factory.get();
         request.setAttribute(factory.getKind(), object);
      }
      return object;
   }

   public void setServletRequest(ServletRequest request) {
      requestThreadLocal.set(request);
   }

   public void clearServletRequest() {
      requestThreadLocal.set(null);
   }
}

