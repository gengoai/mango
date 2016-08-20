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
 */

package com.davidbracewell.concurrent;

import com.davidbracewell.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.davidbracewell.Validations.validateArgument;

/**
 * Class allowing a resource to have multiple {@link java.util.concurrent.locks.ReentrantLock} locks using a round robin
 * strategy.
 *
 * @author David B. Bracewell
 */
public final class MultiLock {

  private final AtomicInteger lockIdx = new AtomicInteger(0);
  private List<Lock> locks;

  /**
   * Default Constructor creates 3 locks
   */
  public MultiLock() {
    init(Config.get(MultiLock.class, "numberOfLocks").asInteger(3));
  }

  /**
   * Default constructor
   *
   * @param numberOfLocks Number of locks to use
   */
  public MultiLock(int numberOfLocks) {
    init(numberOfLocks);
  }

  private void init(int numberOfLocks) {
    locks = Collections.synchronizedList(new ArrayList<Lock>());
    for (int i = 0; i < numberOfLocks; i++) {
      locks.add(new ReentrantLock());
    }
  }

  /**
   * Lock
   *
   * @return The lock id
   */
  public int lock() {
    int lid = lockIdx.get();
    locks.get(lid).lock();
    if (lockIdx.incrementAndGet() >= locks.size()) {
      lockIdx.set(0);
    }
    return lid;
  }

  /**
   * Unlock a lock
   *
   * @param lid ID of lock to unlock
   */
  public void unlock(int lid) {
    locks.get(lid).unlock();
  }

  /**
   * <p> Gets the number of locks in the multilock </p>
   *
   * @return The number of locks
   */
  public int size() {
    return locks.size();
  }

  /**
   * <p> Resizes the multi lock. The size will only shrink if the locks are not in use. </p>
   *
   * @param newSize The new size of the multi lock
   */
  public void setSize(int newSize) {
    validateArgument(newSize > 0, "The number of locks must be greater than zero.");

    if (newSize > locks.size()) {
      // We just need to add locks
      while (locks.size() < newSize) {
        locks.add(new ReentrantLock());
      }
    } else {
      // need resize, so wait for tasks to finish
      for (int i = locks.size() - 1; i >= 0 && locks.size() > newSize; i--) {
        Lock l = locks.get(i);
        if (l.tryLock()) {
          locks.remove(i);
        }
      }
      lockIdx.set(0);
    }


  }

}// END CLASS MultiLock
