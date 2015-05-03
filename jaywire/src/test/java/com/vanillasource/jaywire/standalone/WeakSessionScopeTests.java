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

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;
import com.vanillasource.jaywire.Factory;

@Test
public class WeakSessionScopeTests {
   private DelimitedRequestScope requestScope;
   private WeakSessionScope scope;

   public void testProducesOneObjectPerRequest() {
      Factory<Object> objectFactory = () -> new Object();
      Object session = new Object();
      scope.open(session);

      Object result1 = scope.get(objectFactory);
      Object result2 = scope.get(objectFactory);

      assertSame(result1, result2);
   }

   public void testProcudesAnotherObjectInNewSession() {
      Factory<Object> objectFactory = () -> new Object();
      Object session1 = new Object();
      Object session2 = new Object();

      scope.open(session1);
      Object result1 = scope.get(objectFactory);
      requestScope.open();
      scope.open(session2);
      Object result2 = scope.get(objectFactory);

      assertNotSame(result1, result2);
   }

   public void testProcudesSameObjectInNewRequest() {
      Factory<Object> objectFactory = () -> new Object();
      Object session1 = new Object();

      scope.open(session1);
      Object result1 = scope.get(objectFactory);
      requestScope.open();
      scope.open(session1);
      Object result2 = scope.get(objectFactory);

      assertSame(result1, result2);
   }

   @BeforeMethod
   protected void setUp() {
      requestScope = new DelimitedRequestScope(new ThreadLocalScope());
      scope = new WeakSessionScope(requestScope);
   }
}


