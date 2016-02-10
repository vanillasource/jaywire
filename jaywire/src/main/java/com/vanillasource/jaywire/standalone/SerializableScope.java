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

import java.util.function.Supplier;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import static com.vanillasource.jaywire.standalone.SerializationUtil.*;
import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.Factory;

/**
 * A scope implementation that delegates to a given scope when directly asked for
 * objects or suppliers. Additionally is generates serializable suppliers which
 * can be sent across JVM boundaries, provided the same scopes/classes are present
 * at the other JVM. Serialization works by serializing the factory given, with
 * the supplier that can programmatically get the delegate scope if needed.
 * The delegate scope does not have to be serializable for this to work.
 */
public class SerializableScope implements Scope {
   private SerializableSupplier<Scope> indirectDelegateSupplier;
   private Scope delegate;

   public SerializableScope(Scope delegate, SerializableSupplier<Scope> indirectDelegateSupplier) {
      this.indirectDelegateSupplier = indirectDelegateSupplier;
      this.delegate = delegate;
   }

   @Override
   public <T> T get(Factory<T> factory) {
      return delegate.get(factory);
   }

   /**
    * Return a supplier that only indirectly refers to the delegate scope.
    */
   @Override
   public <T> Supplier<T> apply(Factory<T> factory) {
      return new IndirectSerializableSupplier<T>(delegate, indirectDelegateSupplier, factory);
   }

   public interface SerializableSupplier<T> extends Supplier<T>, Serializable {
   }

   public static class IndirectSerializableSupplier<T> implements SerializableSupplier<T> {
      private transient Scope delegate;
      private Factory<T> factory;
      private SerializableSupplier<Scope> indirectDelegateSupplier;

      public IndirectSerializableSupplier(Scope delegate, SerializableSupplier<Scope> indirectDelegateSupplier, Factory<T> factory) {
         this.delegate = delegate;
         this.factory = factory;
         this.indirectDelegateSupplier = indirectDelegateSupplier;
      }

      @Override
      public T get() {
         return delegate.get(factory);
      }

      private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         deserializing(() -> in.defaultReadObject());
         delegate = indirectDelegateSupplier.get();
      }
   }
}


