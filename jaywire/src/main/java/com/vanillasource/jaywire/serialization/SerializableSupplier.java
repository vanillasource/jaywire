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

package com.vanillasource.jaywire.serialization;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.function.Supplier;
import com.vanillasource.jaywire.Factory;
import com.vanillasource.jaywire.serialization.DissociatingSupplierStorage.Key;

/**
 * A supplier that uses the <code>DissociatingSupplierStorage</code> to dissociate
 * itself from its context, serialize only the key from the storage, and then
 * reconstruct its context after deserialization.
 */
public class SerializableSupplier<T> implements Supplier<T>, Serializable {
   private transient DissociatingSupplierStorage storage;
   private transient Factory<T> factory;
   private Key<T> key;

   public SerializableSupplier(DissociatingSupplierStorage storage, Factory<T> factory) {
      this.storage = storage;
      this.factory = factory;
   }

   @Override
   public T get() {
      return factory.get();
   }

   /**
    * Called before serialization, it initializes the dissociated key to be stored.
    */
   private void writeObject(ObjectOutputStream out) throws IOException {
      key = storage.put(factory.getKind(), this);
      out.defaultWriteObject();
   }

   /**
    * Called after object deserialized, filling up transient fields from stored object.
    */
   @SuppressWarnings("unchecked")
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      SerializableSupplier<T> stored = (SerializableSupplier<T>) DissociatingSupplierStorage.get(key);
      this.factory = stored.factory;
      this.storage = stored.storage;
   }
}

