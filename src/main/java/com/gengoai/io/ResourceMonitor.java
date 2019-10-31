/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.gengoai.io;

import com.gengoai.conversion.Cast;
import com.gengoai.stream.MStream;

import java.io.*;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
public class ResourceMonitor extends Thread {
   protected static final ResourceMonitor MONITOR = new ResourceMonitor();
   private final ConcurrentHashMap<Object, KeyedWeakReference> map = new ConcurrentHashMap<>();
   private final ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();

   private ResourceMonitor() {
      setPriority(Thread.MAX_PRIORITY);
      setName("GarbageCollectingConcurrentMap-cleanupthread");
      setDaemon(true);
   }


   public static Connection monitor(Connection connection) {
      return Cast.as(Proxy.newProxyInstance(notNull(connection).getClass().getClassLoader(),
                                            new Class[]{Connection.class},
                                            new ConnectionInvocationHandler(connection)));
   }

   public static InputStream monitor(InputStream stream) {
      return new MonitoredInputStream(notNull(stream));
   }

   public static OutputStream monitor(OutputStream stream) {
      return new MonitoredOutputStream(notNull(stream));
   }

   public static Reader monitor(Reader reader) {
      return new MonitoredReader(notNull(reader));
   }

   public static Writer monitor(Writer writer) {
      return new MonitoredWriter(notNull(writer));
   }

   public static <T> Stream<T> monitor(Stream<T> stream) {
      return Cast.as(Proxy.newProxyInstance(Stream.class.getClassLoader(),
                                            new Class[]{Stream.class},
                                            new StreamInvocationHandler<>(notNull(stream))));
   }

   public static <T> MStream<T> monitor(MStream<T> stream) {
      return Cast.as(Proxy.newProxyInstance(MStream.class.getClassLoader(),
                                            new Class[]{MStream.class},
                                            new MStreamInvocationHandler<>(notNull(stream))));
   }

   public static <T> MonitoredObject<T> monitor(T object) {
      return new MonitoredObject<>(object);
   }

   public static <T> MonitoredObject<T> monitor(T object, Consumer<T> onClose) {
      return new MonitoredObject<>(object, onClose);
   }

   protected <T> T addResource(final Object referent, T resource) {
      KeyedObject<T> monitoredResource = KeyedObject.create(resource);
      map.put(monitoredResource.key, new KeyedWeakReference(referent, monitoredResource));
      return resource;
   }

   protected <T> T addResource(final Object referent, T resource, Consumer<T> onClose) {
      KeyedObject<T> monitoredResource = KeyedObject.create(resource, onClose);
      map.put(monitoredResource.key, new KeyedWeakReference(referent, monitoredResource));
      return resource;
   }


   @Override
   public void run() {
      try {
         while (true) {
            KeyedWeakReference ref = (KeyedWeakReference) referenceQueue.remove();
            System.out.println(ref);
            try {
               map.remove(ref.monitoredResource.key);
               ref.monitoredResource.close();
            } catch (Exception e) {
               if (!e.getMessage().toLowerCase().contains("already closed")) {
                  e.printStackTrace();
               }
            }
         }
      } catch (InterruptedException e) {
         //
      }
   }

   private static class ConnectionInvocationHandler implements InvocationHandler {
      final Connection backing;

      private ConnectionInvocationHandler(Connection backing) {
         this.backing = MONITOR.addResource(this, backing);
      }

      @Override
      public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
         return method.invoke(backing, objects);
      }
   }

   private static class MStreamInvocationHandler<T> implements InvocationHandler {
      final MStream<T> backing;

      private MStreamInvocationHandler(MStream<T> backing) {
         this.backing = MONITOR.addResource(this, backing);
      }

      @Override
      public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
         return method.invoke(backing, objects);
      }
   }

   private static class MonitoredInputStream extends InputStream {
      final InputStream backing;

      private MonitoredInputStream(InputStream backing) {
         this.backing = MONITOR.addResource(this, backing);
      }

      @Override
      public void close() throws IOException {
         backing.close();
      }

      @Override
      public int read() throws IOException {
         return backing.read();
      }
   }

   private static class MonitoredOutputStream extends OutputStream {
      final OutputStream backing;

      private MonitoredOutputStream(OutputStream backing) {
         this.backing = MONITOR.addResource(this, backing);
      }

      @Override
      public void close() throws IOException {
         backing.close();
      }

      @Override
      public void write(int i) throws IOException {
         backing.write(i);
      }
   }

   private static class MonitoredReader extends Reader {
      final Reader backing;

      private MonitoredReader(Reader backing) {
         this.backing = MONITOR.addResource(this, backing);
         ;
      }

      @Override
      public void close() throws IOException {
         backing.close();
      }

      @Override
      public int read(char[] chars, int i, int i1) throws IOException {
         return backing.read(chars, i, i1);
      }
   }

   private static class MonitoredWriter extends Writer {
      final Writer backing;

      private MonitoredWriter(Writer backing) {
         this.backing = MONITOR.addResource(this, backing);
      }

      @Override
      public void close() throws IOException {
         backing.close();
      }

      @Override
      public void flush() throws IOException {
         backing.flush();
      }

      @Override
      public void write(char[] chars, int i, int i1) throws IOException {
         backing.write(chars, i, i1);
      }
   }

   private static class StreamInvocationHandler<T> implements InvocationHandler {
      final Stream<T> backingStream;

      private StreamInvocationHandler(Stream<T> backingStream) {
         this.backingStream = MONITOR.addResource(this, backingStream);
      }

      @Override
      public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
         return method.invoke(backingStream, objects);
      }
   }

   private class KeyedWeakReference extends WeakReference<Object> {
      public final KeyedObject<?> monitoredResource;

      public KeyedWeakReference(Object referenceObject, KeyedObject<?> monitoredResource) {
         super(referenceObject, referenceQueue);
         this.monitoredResource = monitoredResource;
      }
   }

   static {
      MONITOR.start();
   }

}//END OF ReferenceMonitor
