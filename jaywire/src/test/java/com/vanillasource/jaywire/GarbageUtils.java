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

import static org.testng.Assert.*;
import java.util.function.Supplier;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

public class GarbageUtils {
   private static final long WAIT_MILLIS = 1000;

   private GarbageUtils() {
   }

   public static void waitObjectCollected(Supplier<Object> supplier) {
      if (!isObjectCollected(supplier)) {
         fail("object was not garbage collected");
      }
   }

   public static boolean isObjectCollected(Supplier<Object> supplier) {
      ReferenceQueue<Object> referenceQueue = new ReferenceQueue<Object>();
      WeakReference<Object> reference = new WeakReference<Object>(supplier.get(), referenceQueue);
      System.gc();
      try {
         return referenceQueue.remove(WAIT_MILLIS) != null;
      } catch (InterruptedException e) {
         fail("waiting for object to be garbage collected was interrupted", e);
         return false;
      }
   }
}

