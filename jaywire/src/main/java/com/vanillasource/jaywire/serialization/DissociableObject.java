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

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectStreamException;
import com.vanillasource.jaywire.serialization.DissociatingStorage.Key;

/**
 * An object which will be dissociated on serialization from the
 * actual instance. None of the instance variables of subclasses
 * will be serialized. Subclass needs to have a constructor
 * with no parameters in order for the serialization to work.
 * This implementation is lazy, it will only register the object
 * to the storage on serialization.
 */
public abstract class DissociableObject implements Externalizable {
   private DissociatingStorage storage;
   private Object kind;
   private Key<?> key;

   protected DissociableObject() {
   }

   public DissociableObject(DissociatingStorage storage, Object kind) {
      this.storage = storage;
      this.kind = kind;
   }

   @Override
   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      key = (Key<?>) in.readObject();
   }

   @Override
   public void writeExternal(ObjectOutput out) throws IOException {
      key = storage.put(kind, this);
      out.writeObject(key);
   }

   /**
    * Replaces the deserialized object with the stored object.
    */
   protected Object readResolve() throws ObjectStreamException {
      return storage.get(key);
   }
}

