package org.river.android.cache;

/**
 * the interface for the Cache which offer the interface to cached object 
 * in DISC or  in MEM
 * @author river
 * @date 20130908
 */
public interface ICache {
	
	/**
	 * <p>
	 * get the Cached Object from cache pool by the cache String key
	 * @param key
	 * @return
	 */
	public Object get(String key);
	
	
	
	/**
	 * <p>
	 * 
	 * @param key
	 * @return old cache object 
	 */
	public Object put(String key,Object value);
	
	/**
	 * <p>
	 * get the Cached Object from cache pool by the cache String key quietly,
	 * without update the statistic data
	 * @param key
	 * @return
	 */
	public Object getQuiet(String key);
	
	
	/**
	 * <p>
	 * remove the cached object from cache pool by the cache String key
	 * @param key
	 * @return
	 */
	public Object remove(String key);
	
	/**
	 * <p>
	 * free specied size mem
	 * @param size unit in Byte
	 * @return
	 */
	public long free(long size);
	
	
	/**
	 * <p>
	 * flush to the underly resource
	 */
	public void flush();
	
}
