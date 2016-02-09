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

package com.vanillasource.jaywire;

import java.util.function.Supplier;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import static com.vanillasource.jaywire.util.SerializationUtil.*;

/**
 * A scope that does not refer to itself directly when serialized, so
 * it does not get pulled in with the supplier it returnes.
 */
public abstract class SerializableScope implements Scope {
   private SerializableSupplier<Scope> indirectScopeSupplier;

   public SerializableScope(SerializableSupplier<Scope> indirectScopeSupplier) {
      this.indirectScopeSupplier = indirectScopeSupplier;
   }

   /**
    * Return a supplier that only indirectly refers to
    * this scope.
    */
   @Override
   public <T> Supplier<T> apply(Factory<T> factory) {
      return new IndirectSerializableSupplier<T>(this, indirectScopeSupplier, factory);
   }

   public interface SerializableSupplier<T> extends Supplier<T>, Serializable {
   }

   public static class IndirectSerializableSupplier<T> implements SerializableSupplier<T> {
      private transient Scope scope;
      private Factory<T> factory;
      private SerializableSupplier<Scope> indirectScopeSupplier;

      public IndirectSerializableSupplier(Scope scope, SerializableSupplier<Scope> indirectScopeSupplier, Factory<T> factory) {
         this.scope = scope;
         this.factory = factory;
         this.indirectScopeSupplier = indirectScopeSupplier;
      }

      @Override
      public T get() {
         return scope.get(factory);
      }

      private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         deserializing(() -> in.defaultReadObject());
         scope = indirectScopeSupplier.get();
      }
   }
}


