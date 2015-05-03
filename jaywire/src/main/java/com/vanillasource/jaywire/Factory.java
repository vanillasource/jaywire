/**
 * Copyright (C) 2015 Robert Braeutigam.
 *
 * All rights reserved.
 */

package com.vanillasource.jaywire;

import java.util.function.Supplier;

/**
 * Responsible for creating an object instance of a certain
 * <i>kind</i>. By default this <i>kind</i> is the classname of
 * this factory.
 */
@FunctionalInterface
public interface Factory<T> extends Supplier<T> {
   /**
    * Returns an object representing the <i>kind</i>
    * of object. This returned object should have its
    * identity methods implemented.
    */
   default Object getKind() {
      return getClass().getName();
   }
}

