package org.river.android.cache;

import java.util.List;

/**
 * <p>
 * @author river
 * @date 20130908
 */
public interface ILruCache extends ICache{
	
	/**
	 * <p>
	 * get the latest cached objects
	 * @param size
	 * @return
	 */
	public List<Object> latest(int size);
	
	
	/**
	 * <p>
	 * get the oldest cached objects
	 * @param size
	 * @return
	 */
	public List<Object> oldest(int size);
	
	
}
