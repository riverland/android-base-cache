package org.river.android.cache.impl;

import java.io.IOException;
import java.util.Map;

import org.river.android.cache.CacheConfig;
import org.river.android.cache.ICache;
import org.river.android.cache.ICacheInfo;
import org.river.android.cache.store.Policy;
import org.river.android.cache.store.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 缓存池的抽象实现
 * 
 * @author river
 * @date 20130912
 */
public abstract class AbstractCache implements ICacheInfo, ICache {

	private static final Logger log = LoggerFactory.getLogger(AbstractCache.class);
	
	protected String name;
	
	protected Store store;
	
	protected Policy policy;

	protected CacheConfig cacheConfig;	


	/**
	 * <p>
	 * 获取指定Key的缓存对象,并更新该缓存对象的统计数据
	 */
	public Object get(String key) {
		CacheObject cachedObj = store.get(key);
		Object retObj=null;
		if (cachedObj != null) {
			HitBean hit=cachedObj.getHitBean();
			hit=hit!=null?hit:new HitBean(key);
			hit.add();
			cachedObj.setHitBean(hit);
			cachedObj.setLastAccessTime(System.currentTimeMillis());
			
			retObj=cachedObj.getValue();
			cachedObj.setValue(null);
			
			store.put(cachedObj);
		}
		
		return retObj;
	}

	/**
	 * <p>
	 * 把要缓存的对象存入缓存池,如果相同key的缓存对象已经存在，
	 * 则覆盖并把旧的对象返回
	 */
	public Object put(String key, Object value) {		
		Object oldValue=this.getQuiet(key);
				
		CacheObject cacheObject=new CacheObject(key,value);
		HitBean hit=new HitBean(key);
		cacheObject.setHitBean(hit);
		
		store.put(cacheObject);
		return oldValue;
	}

	/**
	 * <p>
	 * 删除指定key的缓存对象
	 */
	public Object remove(String key) {
		return store.remove(key);
	}

	
	/**
	 * <p>
	 * 释放指定大小的空间
	 * @return 实际释放的空间
	 */
	public long free(long size) {
		return store.free(size);
	}

	/**
	 * <p>
	 * 把缓存数据往更低级别的存储保存
	 */
	public void flush() {
		try {
			this.store.flush();
		} catch (IOException e) {
			log.error("flush Cache["+name+"] error");
		}
	}

	public long getLimited() {
		return this.store.getLimited();
	}

	public long getSize() {
		return this.store.getSize();
	}
	
	
	public Object getQuiet(String key){
		return store.get(key);		
	}
}
