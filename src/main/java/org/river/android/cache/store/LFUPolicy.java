package org.river.android.cache.store;

import org.river.android.cache.impl.CacheObject;

/**
 * <p>
 * Least frequency use
 * 
 * @author River
 * @date 20130911
 */
public class LFUPolicy extends AbstractPolicy {

	public static final String NAME = "LFU";

	public static Policy instance(){
		return new LFUPolicy();
	}
	
	public String getName() {
		return NAME;
	}

	@Override
	protected CacheObject lower(CacheObject l, CacheObject r) {

		return l.getHitCount() > r.getHitCount() ? l : r;
	}

}
