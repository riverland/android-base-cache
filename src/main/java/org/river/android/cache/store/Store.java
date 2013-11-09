package org.river.android.cache.store;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.river.android.cache.impl.CacheObject;

/**
 * <p>
 * This is the interface for store.
 *
 * @author River
 * @date 20130918
 */
public interface Store {


    /**
     * Puts an item into the store.
     * @return true if this is a new put for the key or element is null. Returns false if it was an update.
     */
    public boolean put(CacheObject obj);
    
    
    /**
     * <p>
     * Gets cache object by key
     * @return
     */
    public CacheObject get(Object key);


    /**
     * Gets an Array of the keys  in the store.
     * @return
     */
    public Set<String> getKeys();

    /**
     * <p>
     * Removes an item from the cache.
     * @return
     */
    public CacheObject remove(Object key);

    /**
     * <p>
     * Removes a collection of elements from the cache.
     */
    public void removeAll(Collection<String> keys);

    /**
     * <p>
     * Remove all from the store.
     */
    public void removeAll();

    /**
     * replace if the same CacheObject(with same key) desn't exist in the store 
     *
     * @param cache object to be replace
     * @return the existed one
     */
    public CacheObject override(CacheObject element);
    
  

    /**
     * <p>
     * Prepares for shutdown.
     */
    public void dispose();
    
    /**
     * <p>
     * free the specify size space
     * @param size
     * @return
     */
    public long free(long size);
    
    /**
     * <p>
     * get the limit size of the store
     * @return
     */
    public long getLimited();

    /**
     * <p>
     * Returns the current local store size
     * @return the size of the local store size
     */
    public long getSize();

    /**
     * <p>
     * Returns the current local in-memory store size
     * @return 
     */
    public long getInMemorySize();

    /**
     * <p>
     * Returns the current local on-disk store size
     * @return 
     */
    public long getOnDiskSize();


    /**
     * <p>
     * A check to see if a key is in the Store.
     */
    public boolean containsKey(Object key);

    /**
     * A check to see if a key is in the Store and is currently held on disk.
     */
    public boolean containsKeyOnDisk(Object key);

    
    /**
     * A check to see if a key is in the Store and is currently held in memory.
     **/
    public boolean containsKeyInMemory(Object key);

    /**
     * Expire all.
     */
    public void expire();

    /**
     * <p>
     * Flush to persistent store.
     * @throws IOException if any IO error occurs
     */
    public void flush() throws IOException;


    /**
     * <p>
     * Gets Memory eviction policy
     */
    public Policy getInMemEvictPolicy();

    /**
     * Sets the eviction policy strategy.
     * @param policy the new policy
     */
    public void setInMemEvictPolicy(Policy policy);

    /**
     * <p>
     * get all
     * @param keys a collection of keys to look for
     * @return 
     */
    public List< CacheObject> getAll(Collection<String> keys);
    
    /**
     * <p>
     * get all
     * @param keys a collection of keys to look for
     * @return 
     */
    public List< CacheObject> getAll();
    
    /**
     * <p>
     * get the name of the Store
     * @return
     */
    public String getName();
}
