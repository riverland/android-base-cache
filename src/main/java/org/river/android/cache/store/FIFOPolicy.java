package org.river.android.cache.store;

import org.river.android.cache.impl.CacheObject;

/**
 * <p>
 * 
 * first in the cache first timeout
 * 
 * @author River
 * @date 20130911
 */
public class FIFOPolicy extends AbstractPolicy {

	public static final String NAME = "FIFO";
	
	public static Policy instance(){
		return new FIFOPolicy();
	}

	public String getName() {
		return NAME;
	}

	@Override
	protected CacheObject lower(CacheObject l, CacheObject r) {

		return l.getLastAccessTime() > r.getLastAccessTime() ? l : r;
	}

}
