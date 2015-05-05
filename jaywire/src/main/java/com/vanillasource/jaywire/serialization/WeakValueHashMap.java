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

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

public class WeakValueHashMap<K, V> extends AbstractMap<K, V> {
   private final Map<K, WeakReference<V>> backingMap = new HashMap<>();
   private final ReferenceQueue<V> referenceQueue = new ReferenceQueue<V>();

   @Override
   public V put(K key, V value) {
      clearDeletedObjects();
      WeakReference<V> previousWeakValue = backingMap.put(key, new KeyedWeakReference<K, V>(key, value, referenceQueue));
      if (previousWeakValue != null) {
         return previousWeakValue.get();
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   private void clearDeletedObjects() {
      KeyedWeakReference<K, V> keyedWeakReference = null;
      while ( (keyedWeakReference = (KeyedWeakReference<K, V>) referenceQueue.poll()) != null ) {
         backingMap.remove(keyedWeakReference.getKey());
      }
   }

   @Override
   public V get(Object key) {
      WeakReference<V> weakValue = backingMap.get(key);
      if (weakValue != null) {
         return weakValue.get();
      }
      return null;
   }

   @Override
   public boolean containsKey(Object key) {
      return backingMap.containsKey(key);
   }

   @Override
   public Set<Map.Entry<K, V>> entrySet() {
      Set<K> keySet = new HashSet<>(backingMap.keySet());
      return new AbstractSet<Map.Entry<K, V>>() {
         @Override
         public int size() {
            return keySet.size();
         }

         @Override
         public Iterator<Map.Entry<K, V>> iterator() {
            Iterator<K> keyIterator = keySet.iterator();
            return new Iterator<Map.Entry<K, V>>() {
               @Override
               public boolean hasNext() {
                  return keyIterator.hasNext();
               }

               @Override
               public Map.Entry<K, V> next() {
                  K key = keyIterator.next();
                  V value = null;
                  WeakReference<V> weakValue = backingMap.get(key);
                  if (weakValue != null) {
                     value = weakValue.get();
                  }
                  return new AbstractMap.SimpleEntry<K, V>(key, value);
               }
            };
         }
      };
   }

   @Override
   public V remove(Object key) {
      WeakReference<V> weakValue = backingMap.remove(key);
      if (weakValue != null) {
         return weakValue.get();
      }
      return null;
   }

   private static class KeyedWeakReference<K, T> extends WeakReference<T> {
      private K key;

      public KeyedWeakReference(K key, T value, ReferenceQueue<T> queue) {
         super(value, queue);
         this.key = key;
      }

      public K getKey() {
         return key;
      }
   }
}

