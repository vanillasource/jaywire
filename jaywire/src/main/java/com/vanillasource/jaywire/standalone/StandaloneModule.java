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
import com.vanillasource.jaywire.StandardScopesSupport;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Externalizable;
import java.io.IOException;

/**
 * A module that combines all available functionality from
 * the standalone scopes. Extend this class on the top of your
 * module hierarchy to pull all standalone scope implementations.
 */
public abstract class StandaloneModule implements CloseableModule, StandardScopesSupport, Externalizable {
   private final Scope singletonScope;
   private final Scope threadLocalScope;

   public StandaloneModule() {
      singletonScope = new SingletonScope(this::getSingletonScope);
      threadLocalScope = new ThreadLocalScope(this::getThreadLocalScope);
   }

   @Override
   public Scope getSingletonScope() {
      return singletonScope;
   }

   @Override
   public Scope getThreadLocalScope() {
      return threadLocalScope;
   }

   @Override
   public final void readExternal(ObjectInput in) throws IOException {
      // Do not read anything
   }

   @Override
   public final void writeExternal(ObjectOutput out) throws IOException {
      // Do not write anything
   }

   private final Object readResolve() {
      return null; // TODO: return some static instance
   }
}

