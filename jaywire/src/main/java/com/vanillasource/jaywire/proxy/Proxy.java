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

package com.vanillasource.jaywire.proxy;

import java.util.function.Function;
import java.util.function.Consumer;
import java.util.List;
import java.util.ArrayList;

/**
 * Proxies calls to an unerlying object.
 */
@FunctionalInterface
public interface Proxy<T> {
   /**
    * Call object inside this proxy. The object will
    * be supplied to the function given.
    */
   <R> R call(Function<T, R> body);

   /**
    * Execute additional logic around the call. To execute
    * the body supplied by user, call the received runnable.
    * If the runnable is not called in the 'around' handler,
    * null will be returned.
    * @return A proxy that will execute the supplied 'around'
    * handler.
    */
   default Proxy<T> onAround(Consumer<Runnable> around) {
      return new Proxy<T>() {
         @Override
         public <R> R call(Function<T, R> body) {
            List<R> result = new ArrayList<>(1);
            around.accept(new Runnable() {
               @Override
               public void run() {
                  result.add(call(body));
               }
            });
            if (result.isEmpty()) {
               return null;
            }
            return result.get(0);
         }
      };
   }

   /**
    * Execute a certain logic before the user supplied body is run.
    */
   default Proxy<T> onBefore(Runnable before) {
      return onAround(body -> {
         before.run();
         body.run();
      });
   }

   /**
    * Execute a certain logic after the user supplied body is run.
    */
   default Proxy<T> onAfter(Runnable after) {
      return onAround(body -> {
         body.run();
         after.run();
      });
   }
}

