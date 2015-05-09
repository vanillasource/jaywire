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
import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;
import java.rmi.dgc.VMID;

/**
 * Stores the suppliers and gives back serializable keys
 * dissociated from the supplier instance to access it at any later point from
 * a static context.
 * The supplier instance is bound to the storage instance, and can be
 * garbage collected when the storage instance is garbage collected.
 * Note that if a storage object is garbage collected, all keys
 * emitted by the storage become automatically invalid.
 */
public class DissociatingSupplierStorage {
   private static final Map<Integer, DissociatingSupplierStorage> STORAGES = new WeakValueHashMap<>();
   private static final VMID THIS_VM = new VMID();
   private static int nextStorageId = 0;

   private int storageId;
   private Map<Object, Entry> entriesByKind;
   private Map<Integer, Entry> entriesById;

   public DissociatingSupplierStorage() {
      synchronized (STORAGES) {
         if (nextStorageId == Integer.MAX_VALUE) {
            throw new IllegalStateException("used more storage objects that can be stored in a map");
         }
         storageId = nextStorageId++;
         STORAGES.put(storageId, this);
      }
      entriesByKind = new HashMap<>();
      entriesById = new HashMap<>();
   }

   /**
    * Put a supplier for a specific kind into the store. Note: a supplier
    * of one <i>kind</i> will be stored only once.
    * @return A serializable key that is dissociated from the supplier
    * and kind objects. Both are garbage collected together with the
    * instance of the storage, at which point all already serialized keys
    * will become invalid.
    */
   public synchronized <T> Key<T> put(Object kind, Supplier<T> supplier) {
      int supplierId = ensureStoredAndGetId(kind, supplier);
      return new Key<T>(THIS_VM, storageId, supplierId);
   }

   private int ensureStoredAndGetId(Object kind, Supplier<?> supplier) {
      if (entriesById.size() == Integer.MAX_VALUE) {
         throw new IllegalStateException("used more kinds than could be stored in a map");
      }
      Entry entry = entriesByKind.get(kind);
      if (entry == null) {
         entry = new Entry(entriesById.size(), supplier);
         entriesByKind.put(kind, entry);
         entriesById.put(entry.getId(), entry);
      }
      return entry.getId();
   }

   /**
    * Retrieve a <code>Factory</code> from the storage
    * it was stored in.
    */
   public static <T> Supplier<T> get(Key<T> key) {
      if (!key.getVmId().equals(THIS_VM)) {
         throw new IllegalStateException("the supplier key was serialized in a different VM, context can not be restored");
      }
      DissociatingSupplierStorage storage = null;
      synchronized (STORAGES) {
         storage = STORAGES.get(key.getStorageId());
      }
      if (storage == null) {
         throw new IllegalStateException("the storage of the supplier key deserialized was already garbage collected, context can not be restored");
      }
      return storage.internalGet(key);
   }

   @SuppressWarnings("unchecked")
   private synchronized <T> Supplier<T> internalGet(Key<T> key) {
      Entry entry = entriesById.get(key.getKindId());
      if (entry == null) {
         throw new IllegalStateException("the supplier kind in the given key was not known in storage");
      }
      return (Supplier<T>) entry.getSupplier();
   }

   /**
    * A serializable key that can be indefinitely stored, and
    * used to retrieve the associated <code>Factory</code> as
    * long as the storage instance is referenced strongly.
    */
   public static class Key<T> implements Serializable {
      private VMID vmId;
      private int storageId;
      private int kindId;

      protected Key(VMID vmId, int storageId, int kindId) {
         this.vmId = vmId;
         this.storageId = storageId;
         this.kindId = kindId;
      }

      protected VMID getVmId() {
         return vmId;
      }

      protected int getStorageId() {
         return storageId;
      }

      protected int getKindId() {
         return kindId;
      }

      @Override
      public int hashCode() {
         return vmId.hashCode() ^ 5*storageId ^ 11*kindId;
      }

      @Override
      @SuppressWarnings("unchecked")
      public boolean equals(Object o) {
         if ((o == null) || (!(o instanceof Key))) {
            return false;
         }
         Key<?> otherKey = (Key<?>) o;
         return vmId.equals(otherKey.vmId) && storageId == otherKey.storageId &&
            kindId == otherKey.kindId;
      }
   }

   private static class Entry {
      private int id;
      private Supplier<?> supplier;

      private Entry(int id, Supplier<?> supplier) {
         this.id = id;
         this.supplier = supplier;
      }

      public int getId() {
         return id;
      }

      public Supplier<?> getSupplier() {
         return supplier;
      }
   }
}

