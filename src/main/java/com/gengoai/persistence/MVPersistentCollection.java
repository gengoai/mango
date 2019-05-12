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

package com.gengoai.persistence;

import com.gengoai.function.SerializableConsumer;
import com.gengoai.stream.MStream;
import com.gengoai.stream.StreamingContext;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Iterator;

/**
 * The type Mv persistent collection.
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public class MVPersistentCollection<E> implements PersistentCollection<E>, Serializable {
   private static final long serialVersionUID = 1L;
   private final File dbLocation;
   private final Type eType;
   private final String name;
   private volatile transient MVMap<Long, DBDocument> map;

   public MVPersistentCollection(String name, File dbLocation, Type eType) {
      this.name = name;
      this.dbLocation = dbLocation;
      this.eType = eType;
   }

   @Override
   public void add(E element) {
      DBDocument document = new DBDocument(nextKey(), element);
      map().put(document.get("@id", Long.class), document);
   }

   @Override
   public void close() throws Exception {
      if (map != null) {
         map.getStore().close();
      }
   }

   @Override
   public void commit() {
      if (map != null) {
         map.getStore().commit();
      }
   }

   @Override
   public boolean contains(E element) {
      return map().containsValue(element);
   }

   @Override
   public MStream<E> find(Filter filter) {
      return null;
   }

   @Override
   public E get(long index) {
      DBDocument document = map().get(index);
      if (document == null) {
         return null;
      }
      return document.getAs(eType);
   }

   @Override
   public Iterator<E> iterator() {
      return stream().iterator();
   }

   private MVMap<Long, DBDocument> map() {
      if (map == null) {
         synchronized (this) {
            if (map == null) {
               map = new MVStore.Builder()
                        .fileName(dbLocation.getAbsolutePath())
                        .compress()
                        .open()
                        .openMap(name);
            }
         }
      }
      return map;
   }

   @Override
   public String name() {
      return name;
   }

   private long nextKey() {
      return map().lastKey() == null ? 0L : map().lastKey() + 1;
   }

   @Override
   public E remove(long index) {
      DBDocument document = map().get(index);
      map().remove(index);
      return document == null ? null : document.getAs(eType);
   }

   @Override
   public boolean remove(E element) {
      return map().values().remove(element);
   }

   @Override
   public long size() {
      return map().size();
   }

   @Override
   public MStream<E> stream() {
      return StreamingContext.local().stream(map().values().stream().map(doc -> doc.getAs(eType)));
   }

   @Override
   public void update(long index, E element) {
      if (map().containsKey(index)) {
         map().put(index, new DBDocument(index, element));
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public void update(long index, SerializableConsumer<? super E> updater) {
      DBDocument document = map().get(index);
      if (document == null) {
         throw new IndexOutOfBoundsException();
      }
      E object = document.getAs(eType);
      updater.accept(object);
      map().put(index, new DBDocument(index, object));
   }


   public static class Person implements Serializable {
      private int age;
      private String name;

      public Person(String name, int age) {
         this.name = name;
         this.age = age;
      }

      public int getAge() {
         return age;
      }

      public void setAge(int age) {
         this.age = age;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return "Person{" +
                   "name='" + name + '\'' +
                   ", age=" + age +
                   '}';
      }
   }


   public static void main(String[] args) throws Exception {
      try (MVPersistentCollection<Person> people = new MVPersistentCollection<>(
         "people", new File("/home/ik/people.db"), Person.class
      )) {


         System.out.println(people.size());
         people.add(new Person("Joe", 42));
         people.add(new Person("Peanut", 41));
         people.add(new Person("Marc", 40));
         people.add(new Person("David", 41));
         System.out.println(people.size());

         people.update(0, p -> p.setAge(100));
         for (Person person : people) {
            System.out.println(person);
         }
      }
   }
}//END OF MVPersistentCollection
