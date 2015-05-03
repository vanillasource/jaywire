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

import com.vanillasource.jaywire.Factory;
import com.vanillasource.jaywire.Scope;
import java.util.function.Supplier;
import java.io.Serializable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.rmi.dgc.VMID;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * A supplier that is made serializable by separating the input
 * scope and factory into a static state.
 */
public class SeparatingSupplier<T> implements Supplier<T>, Serializable {
   private static final Map<Scope, ScopeEntry> SCOPE_ENTRIES = new WeakHashMap<>();
   private static final VMID THIS_VM = new VMID();

   private VMID sourceVM;
   private long scopeId;
   private Serializable kind;
   private transient Scope scope;
   private transient Factory<T> factory;

   public SeparatingSupplier() {
      // Default constructor for deserialization
   }

   public SeparatingSupplier(Scope scope, Factory<T> factory) {
      this.scope = scope;
      this.factory = factory;
      ScopeEntry scopeEntry = getAndEnsureStored(scope, factory);
      sourceVM = THIS_VM;
      scopeId = scopeEntry.getId();
      kind = factory.getKind();
   }

   @Override
   public T get() {
      return scope.get(factory);
   }

   private ScopeEntry getAndEnsureStored(Scope scope, Factory<?> factory) {
      synchronized (SCOPE_ENTRIES) {
         ScopeEntry scopeEntry = SCOPE_ENTRIES.get(scope);
         if (scopeEntry == null) {
            scopeEntry = new ScopeEntry();
            SCOPE_ENTRIES.put(scope, scopeEntry);
         }
         scopeEntry.ensureStored(factory);
         return scopeEntry;
      }
   }

   /**
    * This is called during deserialization and is responsible to restore
    * transient references to scope and factory.
    */
   private void readObject(ObjectInputStream in)
         throws IOException, ClassNotFoundException {
      in.defaultReadObject(); 
      restoreRefences();
   }

   @SuppressWarnings("unchecked")
   private void restoreRefences() {
      if (!sourceVM.equals(THIS_VM)) {
         throw new IllegalStateException("deserialized supplier originates in another JVM, this is not allowed");
      }
      synchronized (SCOPE_ENTRIES) {
         for (Map.Entry<Scope, ScopeEntry> entry : SCOPE_ENTRIES.entrySet()) {
            if (entry.getValue().getId() == scopeId) {
               scope = entry.getKey();
               factory = (Factory<T>) entry.getValue().getFactory(kind);
               if (factory == null) {
                  throw new IllegalStateException("deserialized supplier does not refer to a known factory, with kind: "+kind);
               }
               return;
            }
         }
      }
      throw new IllegalStateException("deserialized supplier referes to an already closed scope");
   }

   // Testing method
   protected static void clear() {
      synchronized (SCOPE_ENTRIES) {
         SCOPE_ENTRIES.clear();
      }
   }

   // Testing method
   protected void resetVMID() {
      sourceVM = new VMID();
   }

   public static class ScopeEntry {
      private static AtomicLong NEXT_ID = new AtomicLong();
      private long id = NEXT_ID.getAndIncrement();
      private Map<Serializable, Factory<?>> factoriesByKind = new HashMap<>();

      public long getId() {
         return id;
      }

      public Factory<?> getFactory(Serializable kind) {
         return factoriesByKind.get(kind);
      }

      public void ensureStored(Factory<?> factory) {
         factoriesByKind.put(factory.getKind(), factory);
      }
   }
}


