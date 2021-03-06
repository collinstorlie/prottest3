/*
Copyright (C) 2009  Diego Darriba

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package es.uvigo.darwin.prottest.facade.thread;

import java.util.Hashtable;

import es.uvigo.darwin.prottest.util.exception.ProtTestInternalException;

/**
 * Synchronization of all threads in a thread pool. This class
 * maps every thread to an internal unique thread identifier, which
 * can be used to manage temporary files. This manager is a singleton
 * class.
 * 
 * @author Diego Darriba
 * 
 * @since 3.0
 */
public class ThreadPoolSynchronizer {

    /** Internal mapping of threads ids with sequential identifiers */
    private Hashtable<Long, Integer> threadIds;
    /** The value of the next thread in the pool */
    private int nextValue;
    /** The size of the thread pool */
    private int poolSize;
    /** The unique instance of the class */
    private static ThreadPoolSynchronizer instance;

    /** 
     * Instantiates the synchronizer
     * 
     * @param poolSize the size of the thread pool
     */
    private ThreadPoolSynchronizer(int poolSize) {
        this.poolSize = poolSize;
        threadIds = new Hashtable<Long, Integer>(poolSize);
        nextValue = 0;
    }

    /**
     * Gets the internal identifier of a thread
     * 
     * @param thread the thread
     * 
     * @return the unique internal identifier
     */
    public synchronized int getThreadId(Thread thread) {
        long threadNumber = thread.getId();
        Integer value = threadIds.get(threadNumber);

        if (value == null) {
            if (nextValue >= poolSize) {
                throw new ProtTestInternalException("Thread out of sync on ThreadPool");
            }
            // next value
            value = nextValue++;
            threadIds.put(threadNumber, value);
        }

        return value;
    }

    /** 
     * Synchronizes a new manager. This method instantiates the unique
     * instance of the class.
     * 
     * @param poolSize the size of the thread pool
     */
    public static void synchronize(int poolSize) {
        instance = new ThreadPoolSynchronizer(poolSize);
    }

    /**
     * Gets the unique instance of the class. Before the first call of
     * this method, the class thread pool shoud be synchronized.
     * 
     * @return the unique instance
     * 
     * @throws ProtTestInternalException Signals that the thread pool is not synchronized.
     */
    public synchronized static ThreadPoolSynchronizer getInstance() 
    throws ProtTestInternalException {
        if (instance == null) {
            throw new ProtTestInternalException("ThreadPool out of sync");
        }
        return instance;
    }
}
