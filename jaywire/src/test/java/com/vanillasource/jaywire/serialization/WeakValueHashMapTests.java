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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.util.Iterator;
import java.util.Map;
import static org.testng.Assert.*;
import static com.vanillasource.jaywire.GarbageUtils.*;

@Test
public class WeakValueHashMapTests {
   private WeakValueHashMap<String, Object> map;

   public void testGetReturnsObjectPutIn() {
      Object value = new Object();
      map.put("key", value);

      assertSame(map.get("key"), value);
   }

   public void testGetReturnsNullIfObjectNotFound() {
      assertNull(map.get("key"));
   }

   public void testReturnsNullAfterObjectRemoved() {
      Object value = new Object();
      map.put("key", value);
      map.remove("key");

      assertNull(map.get("key"));
   }

   public void testIteratorWorksEvenIfMapIsModified() {
      Object value = new Object();
      map.put("key", value);

      Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
      map.remove("key");

      assertTrue(iterator.hasNext());
      assertNull(iterator.next().getValue());
   }

   public void testEntryIsRemovedOnPutIfValueIsGarbageCollected() throws Exception {
      waitObjectCollected( () -> {
         Object value = new Object();
         map.put("key", value);
         return value;
      });
      Thread.sleep(10);

      map.put("key2", new Object());

      assertFalse(map.containsKey("key"));
   }

   @BeforeMethod
   protected void setUp() {
      map = new WeakValueHashMap<String, Object>();
   }
}

