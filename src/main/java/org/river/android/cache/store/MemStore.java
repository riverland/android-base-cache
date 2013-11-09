package org.river.android.cache.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.river.android.cache.impl.CacheObject;

/**
 * <p>
 * 存储在内存中的存储
 * 
 * @author river
 * @date 20130913
 */
public class MemStore extends AbstractStore {
	public static final String TAG = "MemStore";

	protected Map<String, CacheObject> cachePool = new HashMap<String, CacheObject>();

	/**
	 * <p>
	 * 获取新实例
	 * 
	 * @param ctx
	 * @param name
	 * @return 如果加载失败则返回null
	 */
	public static Store instance(String name, long limited, Policy policy) {
		MemStore store = new MemStore(name, policy, limited);
		return store;
	}

	public MemStore(String name, Policy policy, long limied) {
		super(name, policy, limied);
	}

	/**
	 * <p>
	 * 完全替换对象
	 */
	public synchronized boolean put(CacheObject obj) {
		if (obj == null || obj.getKey() == null || obj.getValue() == null) {
			return false;
		}
		obj.setSize(sizeof);
		this.cachePool.put((String) obj.getKey(), obj);
		return true;
	}

	/**
	 * <p>
	 * 根据key获取缓存对象
	 */
	public CacheObject get(Object key) {
		return this.cachePool.get((String) key);
	}

	/**
	 * <p>
	 * 获取所有key
	 */
	public Set<String> getKeys() {

		return this.cachePool.keySet();
	}

	/**
	 * <p>
	 * 根据key删除缓存对象
	 */
	public synchronized CacheObject remove(Object key) {
		return this.cachePool.remove((String) key);
	}

	/**
	 * <p>
	 * 删除所有keys中的缓存对象
	 */
	public synchronized void removeAll(Collection<String> keys) {
		if (keys == null || keys.isEmpty()) {
			return;
		}

		for (String tmp : keys) {
			this.remove(tmp);
		}
	}

	/**
	 * <p>
	 * 删除所有
	 */
	public synchronized void removeAll() {
		this.cachePool = null;
	}

	/**
	 * <p>
	 * 如果CacheObject已经存在,则只是覆盖value部分
	 */
	public synchronized CacheObject override(CacheObject obj) {
		if (obj == null || obj.getKey() == null||obj.getValue()==null) {
			return null;
		}
		CacheObject oldOne = this.cachePool.get(obj.getKey());

		if (oldOne != null) {
			oldOne.setLastAccessTime(System.currentTimeMillis());
			oldOne.setLastUpdateTime(oldOne.getLastAccessTime());
			oldOne.setValue(obj.getValue());
		}else{
			this.cachePool.put((String)obj.getKey(), obj);
		}
		

		return oldOne;
	}

	/**
	 * <p>
	 * do nothing
	 */
	public void dispose() {
		//TODO some logic
	}

	/**
	 * <p>
	 * 释放指定大小的空间
	 */
	public long free(long size) {
		List<CacheObject> freeList=this.getFreeList(size);
		
		long sum=0;
		for(CacheObject tmp:freeList){
			sum=+tmp.getSize(this.sizeof);
			this.cachePool.remove(tmp.getKey());
		}
		
		return sum;
	}

	/**
	 * <p>
	 * 获取在内存中的空间
	 */
	public long getSize() {
		long sum=0;
		Set<String> keys=this.cachePool.keySet();
		for(String tmp:keys){
			CacheObject obj=this.cachePool.get(tmp);
			if(obj!=null){
				sum=+obj.getInMemSize();
			}
		}
		return sum;
	}

	public long getInMemorySize() {
		return this.getSize();
	}

	/**
	 * <p>
	 * not support
	 */
	public long getOnDiskSize() {
		return 0;
	}

	public boolean containsKey(Object key) {
		return this.cachePool.containsKey(key);
	}

	public boolean containsKeyOnDisk(Object key) {
		return false;
	}

	public boolean containsKeyInMemory(Object key) {
		return this.cachePool.containsKey(key);
	}


	/**
	 * <p>
	 * not support
	 */
	public void flush() throws IOException {
		// do nothing
	}

	public Policy getInMemEvictPolicy() {
		return this.policy;
	}

	public void setInMemEvictPolicy(Policy policy) {
		this.policy=policy;
	}

	/**
	 * <p>
	 * 获取所有keys的缓存
	 */
	public List<CacheObject> getAll(Collection<String> keys) {
		if(keys==null||keys.isEmpty()){
			return null;
		}
		
		List<CacheObject> all=new ArrayList<CacheObject>();
		for(String key:keys){
			CacheObject obj=this.cachePool.get(key);
			if(obj!=null){
				all.add(obj);
			}
		}
		
		return all;
	}
	
	
	public List<CacheObject> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, CacheObject> getCachePool() {
		return this.cachePool;
	}

}
