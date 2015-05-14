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

import com.vanillasource.jaywire.Scope;
import com.vanillasource.jaywire.Factory;
import com.vanillasource.jaywire.Factory1;
import com.vanillasource.jaywire.Factory2;
import com.vanillasource.jaywire.Factory3;
import com.vanillasource.jaywire.Factory4;
import com.vanillasource.jaywire.Factory5;
import com.vanillasource.jaywire.Factory6;
import com.vanillasource.jaywire.Factory7;
import com.vanillasource.jaywire.Factory8;
import com.vanillasource.jaywire.Factory9;
import java.util.function.Supplier;
import com.vanillasource.function.Supplier1;
import com.vanillasource.function.Supplier2;
import com.vanillasource.function.Supplier3;
import com.vanillasource.function.Supplier4;
import com.vanillasource.function.Supplier5;
import com.vanillasource.function.Supplier6;
import com.vanillasource.function.Supplier7;
import com.vanillasource.function.Supplier8;
import com.vanillasource.function.Supplier9;

/**
 * Pull this interface in your module fragment if you want to
 * emit serializable factories and suppliers.
 */
public interface SerializationSupport {
   /**
    * Create a wrapper scope that can emit serializable suppliers
    * on apply.
    */
   Scope makeSupplierSerializable(Scope scope);

   <T> Supplier<T> makeSerializable(Factory<T> factory);
   <P1, T> Supplier1<P1, T> makeSerializable(Factory1<P1, T> factory);
   <P1, P2, T> Supplier2<P1, P2, T> makeSerializable(Factory2<P1, P2, T> factory);
   <P1, P2, P3, T> Supplier3<P1, P2, P3, T> makeSerializable(Factory3<P1, P2, P3, T> factory);
   <P1, P2, P3, P4, T> Supplier4<P1, P2, P3, P4, T> makeSerializable(Factory4<P1, P2, P3, P4, T> factory);
   <P1, P2, P3, P4, P5, T> Supplier5<P1, P2, P3, P4, P5, T> makeSerializable(Factory5<P1, P2, P3, P4, P5, T> factory);
   <P1, P2, P3, P4, P5, P6, T> Supplier6<P1, P2, P3, P4, P5, P6, T> makeSerializable(Factory6<P1, P2, P3, P4, P5, P6, T> factory);
   <P1, P2, P3, P4, P5, P6, P7, T> Supplier7<P1, P2, P3, P4, P5, P6, P7, T> makeSerializable(Factory7<P1, P2, P3, P4, P5, P6, P7, T> factory);
   <P1, P2, P3, P4, P5, P6, P7, P8, T> Supplier8<P1, P2, P3, P4, P5, P6, P7, P8, T> makeSerializable(Factory8<P1, P2, P3, P4, P5, P6, P7, P8, T> factory);
   <P1, P2, P3, P4, P5, P6, P7, P8, P9, T> Supplier9<P1, P2, P3, P4, P5, P6, P7, P8, P9, T> makeSerializable(Factory9<P1, P2, P3, P4, P5, P6, P7, P8, P9, T> factory);
}

