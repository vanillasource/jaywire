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
import java.util.function.Supplier;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A session scope which stores all objects weakly to the given
 * session object. Session objects will be keyed with any object
 * supplied as identifier. If this identifier is garbage collected,
 * then the objects are deleted. Please note, that if session objects
 * are serialized or otherwise stored this scope may not work properly,
 * as the weakly connected objects will be deleted.
 * The scope is implemented with the help of a container scope, which
 * is assumed to belong to a single user at any given time. In this scope
 * the session must be opened, possibly each time the container scope
 * is created. It is expected that the container scope is request scope,
 * and after opening the request scope, the session is (re-)opened.
 */
public class WeakSessionScope implements Scope {
   private Map<Object, Scope> singletonScopes = new WeakHashMap<>();
   private Scope containerScope;

   public WeakSessionScope(Scope containerScope) {
      this.containerScope = containerScope;
   }

   /**
    * Open the session scope denoted by given identifier. This
    * will cause the scope to be copied into the given containing scope.
    * @param session The session object that can be used as a weak key.
    * If this object is garbage collected, every allocated objects will be too.
    */
   public void open(Object session) {
      Scope currentScope = singletonScopes.get(session);
      if (currentScope == null) {
         currentScope = new SingletonScope();
         singletonScopes.put(session, currentScope);
      }
      getContainer().set(currentScope);
   }

   private Container getContainer() {
      return containerScope.get(() -> new Container());
   }

   @Override
   public <T> T get(Supplier<T> factory) {
      return getContainer().get().get(factory);
   }

   /**
    * Holds a single resettable singleton scope.
    */
   private class Container {
      private Scope scope = null;

      private Scope get() {
         return scope;
      }

      private void set(Scope scope) {
         this.scope = scope;
      }
   }
}

