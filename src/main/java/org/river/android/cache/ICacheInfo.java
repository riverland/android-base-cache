package org.river.android.cache;

/**
 * <p>
 * interface for Cache pool information
 * @author river
 * @date 20130908
 */
public interface ICacheInfo {
	
	/**
	 * <p>
	 * get the Cached limited 
	 * <li>-1:not limited</li>
	 * <li>greater than zero: the cache pool limit</li>
	 * @return
	 */
	public long getLimited();
	
	
	/**
	 * <p>
	 * get the current size of cache pool
	 * @return
	 */
	public long getSize();
}
