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
import static org.mockito.Mockito.*;
import static com.vanillasource.jaywire.serialization.SerializationUtil.*;
import java.io.IOException;

@Test
public class SerializationUtilTests {
   public void testNotDeserializingIsRunIfNotInsiderDeserializing() {
      Runnable runnable = mock(Runnable.class);

      ifNotDeserializing(runnable);

      verify(runnable).run();
   }

   public void testNotDeserializingIsNotRunIfInsideDeserializing() throws Exception {
      Runnable runnable = mock(Runnable.class);

      deserializing( () -> {
         ifNotDeserializing(runnable);
      });

      verify(runnable, never()).run();
   }

   public void testNotDeserializingIsRunAfterDeserializing() throws Exception {
      Runnable runnable = mock(Runnable.class);

      deserializing( () -> { } );
      ifNotDeserializing(runnable);

      verify(runnable).run();
   }

   @Test(expectedExceptions = IOException.class)
   public void testDeserializingRethrowsException() throws Exception {
      deserializing( () -> { throw new IOException("test"); } );
   }

   public void testNotDeserializingEvenAfterDeserizalizingException() throws Exception {
      Runnable runnable = mock(Runnable.class);

      try {
         deserializing( () -> { throw new IOException("test"); } );
      } catch (IOException e) {
         // Ok
      }
      ifNotDeserializing(runnable);

      verify(runnable).run();
   }
}

