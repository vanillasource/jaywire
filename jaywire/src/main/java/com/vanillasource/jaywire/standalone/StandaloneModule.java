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

import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.CloseableSupport;
import com.vanillasource.jaywire.StandardScopesSupport;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;
import static com.vanillasource.jaywire.standalone.SerializationUtil.*;

/**
 * A module that combines all available functionality from
 * the standalone scopes. Extend this class on the top of your
 * module hierarchy to pull all standalone scope implementations.
 */
public abstract class StandaloneModule implements StandardScopesSupport, CloseableSupport, Externalizable {
   private static Object DESERIALIZATION_INSTANCE_MUTEX = new Object();
   protected static StandaloneModule DESERIALIZATION_INSTANCE = null;
   protected static boolean DESERIALIZATION_INSTANCE_AMBIGOUS = false;
   private final Scope singletonScope;
   private final Scope threadLocalScope;

   public StandaloneModule() {
      singletonScope = new SerializableScope(new SingletonScope(), this::getSingletonScope);
      threadLocalScope = new ThreadLocalScope();
      ifNotDeserializing( () -> {
         synchronized (DESERIALIZATION_INSTANCE_MUTEX) {
            if (DESERIALIZATION_INSTANCE == null) {
               DESERIALIZATION_INSTANCE = this;
            } else {
               DESERIALIZATION_INSTANCE_AMBIGOUS = true;
            }
         }
      });
   }

   @Override
   public Scope getSingletonScope() {
      return singletonScope;
   }

   @Override
   public Scope getThreadLocalScope() {
      return threadLocalScope;
   }

   private Collection<AutoCloseable> getCloseables() {
      return singleton(() -> new ConcurrentLinkedDeque<AutoCloseable>());
   }

   @Override
   public void closeWithModule(AutoCloseable closeable) {
      getCloseables().add(closeable);
   }

   /**
    * Tries to close all registered objects with catching exceptions. Only the
    * last exception is re-thrown.
    */
   @Override
   public void close() throws Exception {
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

   @Override
   public final void readExternal(ObjectInput in) throws IOException {
      // Do not read anything
   }

   @Override
   public final void writeExternal(ObjectOutput out) throws IOException {
      // Do not write anything
   }

   /**
    * Override this method to decide what static reference of a module should
    * be used when deserializing suppliers. This default implementations works,
    * if there is only one instance of a Module in the JVM, otherwise it will
    * throw an exception.<br>
    * <strong>Note:</strong> There are no instance variables available during the
    * call to this method. It should rely purely on static information.
    */
   protected Object getStaticDeserializationModule() {
      synchronized (DESERIALIZATION_INSTANCE_MUTEX) {
         if (DESERIALIZATION_INSTANCE == null) {
            throw new IllegalArgumentException("there was no instance of a StandaloneModule initialized yet, please override getStaticDeserializationModule() to provide one");
         }
         if (DESERIALIZATION_INSTANCE_AMBIGOUS) {
            throw new IllegalArgumentException("there were multiple instances of StandaloneModule initialized, please override getStaticDeserializationModule() to disambiguate");
         }
         return DESERIALIZATION_INSTANCE;
      }
   }

   protected final Object readResolve() throws ObjectStreamException {
      return getStaticDeserializationModule();
   }
}

