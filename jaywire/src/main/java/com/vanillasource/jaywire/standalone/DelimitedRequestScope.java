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

package com.vanillasource.jaywire.standalone;

import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.Factory;

/**
 * A request scope which has to be explicitly delimited.
 * It is based on a containing scope (usually thread local)
 * that can guarantee that only one request is open
 * at any time.
 */
public class DelimitedRequestScope implements SeparatingScope {
   private Scope containerScope;

   public DelimitedRequestScope(Scope containerScope) {
      this.containerScope = containerScope;
   }

   /**
    * Open a new request, forget all previous objects if there were any.
    * There is only one open request per thread at any given time.
    */
   public void open() {
      getContainer().reset();
   }

   /**
    * Close a request, forget all previous objects.
    */
   public void close() {
      getContainer().reset();
   }

   private Container getContainer() {
      return containerScope.get(() -> new Container());
   }

   @Override
   public <T> T get(Factory<T> factory) {
      return getContainer().get().get(factory);
   }

   /**
    * Holds a single resettable singleton scope.
    */
   private class Container {
      private Scope scope = new SingletonScope();

      private Scope get() {
         return scope;
      }

      private void reset() {
         scope = new SingletonScope();
      }
   }
}


