package org.river.android.cache.store;

import org.river.android.cache.impl.CacheObject;

/**
 * <p>
 * 
 * Least recent use
 * 
 * @author River
 * @date 20130911
 */
public class LRUPolicy extends AbstractPolicy {

	public static final String NAME = "LRU";

	public static Policy instance(){
		return new LRUPolicy();
	}
	
	public String getName() {
		return NAME;
	}

	@Override
	protected CacheObject lower(CacheObject l, CacheObject r) {

		return l.getLastAccessTime() > r.getLastAccessTime() ? l : r;
	}

}
