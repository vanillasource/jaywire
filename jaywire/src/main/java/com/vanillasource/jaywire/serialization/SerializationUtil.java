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

import java.io.IOException;

public class SerializationUtil {
   private static ThreadLocal<Boolean> DESERIALIZING = new ThreadLocal<Boolean>() {
      @Override
      protected Boolean initialValue() {
         return false;
      }
   };

   private SerializationUtil() {
   }

   public static void ifNotDeserializing(Runnable runnable) {
      if (!DESERIALIZING.get()) {
         runnable.run();
      }
   }

   public static void deserializing(DeserializingRunnable runnable) throws IOException, ClassNotFoundException {
      try {
         DESERIALIZING.set(true);
         runnable.run();
      } finally {
         DESERIALIZING.set(false);
      }
   }

   public interface DeserializingRunnable {
      void run() throws IOException, ClassNotFoundException;
   }
}

