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

import com.vanillasource.jaywire.CloseableSupport;
import com.vanillasource.jaywire.SingletonScopeSupport;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Collection;

/**
 * Module implementation that supports registering closeables and
 * closes them all when the module itself is close. Module
 * is thread-safe, concurrent registrations of closeables is possible,
 * although registering a closeable when the module is already shutting
 * down may result in the added object being not closed.
 */
public interface CloseableModule extends CloseableSupport, SingletonScopeSupport, AutoCloseable {
   default Collection<AutoCloseable> getCloseables() {
      return singleton(() -> new ConcurrentLinkedDeque<AutoCloseable>());
   }

   @Override
   default void closeWithModule(AutoCloseable closeable) {
      getCloseables().add(closeable);
   }

   /**
    * Tries to close all registered objects with catching exceptions. Only the
    * last exception is re-thrown.
    */
   @Override
   default void close() throws Exception {
      Exception lastException = null;
      for (AutoCloseable closeable : getCloseables()) {
         try {
            closeable.close();
         } catch (Exception e) {
            lastException = e;
         }
      }
      if (lastException != null) {
         throw lastException;
      }
   }
}

