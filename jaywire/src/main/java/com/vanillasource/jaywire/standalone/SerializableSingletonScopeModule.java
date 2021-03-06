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
import com.vanillasource.jaywire.serialization.SerializableModule;
import com.vanillasource.jaywire.serialization.SerializableScope;
import com.vanillasource.jaywire.SingletonScopeSupport;

/**
 * Implements the singleton scope, which is in fact an abstraction over
 * instance variables. With these two features (singletons and serialization)
 * all other features can be implemented as a mixin.
 */
public abstract class SerializableSingletonScopeModule extends SerializableModule implements SingletonScopeSupport {
   private final Scope singletonScope;

   public SerializableSingletonScopeModule() {
      singletonScope = new SerializableScope(new SingletonScope(), this::getSingletonScope);
   }

   @Override
   public Scope getSingletonScope() {
      return singletonScope;
   }
}

