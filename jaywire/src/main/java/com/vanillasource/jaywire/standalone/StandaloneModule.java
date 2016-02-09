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
import com.vanillasource.jaywire.StandardScopesSupport;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectStreamException;
import static com.vanillasource.jaywire.util.SerializationUtil.*;

/**
 * A module that combines all available functionality from
 * the standalone scopes. Extend this class on the top of your
 * module hierarchy to pull all standalone scope implementations.
 */
public abstract class StandaloneModule implements CloseableModule, StandardScopesSupport, Externalizable {
   private static Object INSTANCE_MUTEX = new Object();
   protected static StandaloneModule INSTANCE = null;
   protected static boolean INSTANCE_AMBIGOUS = false;
   private final Scope singletonScope;
   private final Scope threadLocalScope;

   public StandaloneModule() {
      singletonScope = new SingletonScope(this::getSingletonScope);
      threadLocalScope = new ThreadLocalScope(this::getThreadLocalScope);
      ifNotDeserializing( () -> {
         synchronized (INSTANCE_MUTEX) {
            if (INSTANCE == null) {
               INSTANCE = this;
            } else {
               INSTANCE_AMBIGOUS = true;
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
      synchronized (INSTANCE_MUTEX) {
         if (INSTANCE == null) {
            throw new IllegalArgumentException("there was no instance of a StandaloneModule initialized yet, please override getStaticDeserializationModule() to provide one");
         }
         if (INSTANCE_AMBIGOUS) {
            throw new IllegalArgumentException("there were multiple instances of StandaloneModule initialized, please override getStaticDeserializationModule() to disambiguate");
         }
         return INSTANCE;
      }
   }

   protected final Object readResolve() throws ObjectStreamException {
      return getStaticDeserializationModule();
   }
}

