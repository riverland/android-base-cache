package org.river.android.cache.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.river.android.cache.impl.CacheObject;
import org.river.android.cache.size.ISizeOf;
import org.river.android.cache.size.SerialSizeOf;

/**
 * <p>
 * 存储抽象基类
 * 
 * @author river
 * @date 20130913
 */
public abstract class AbstractStore implements Store {

	protected String name;
	protected long limited = Long.MAX_VALUE;
	protected Policy policy = LRUPolicy.instance();
	protected Map<String, CacheObject> cachePool;
	protected ISizeOf sizeof=new SerialSizeOf();
	
	public AbstractStore(String name, Policy policy, long limied) {

		this.name = name;
		this.limited = limied <= 0 ? Long.MAX_VALUE : limited;
		this.policy = policy == null ? this.policy : policy;
	}

	protected abstract Map<String, CacheObject> getCachePool();

	/**
	 * <p>
	 * 获取需要释放的资源
	 * 
	 * @param size
	 * @return
	 */
	protected List<CacheObject> getFreeList(long size) {
		List<CacheObject> freeList = new ArrayList<CacheObject>();

		List<CacheObject> cacheList = this.getCacheList();
		long sum = 0;
		while (sum < size && !cacheList.isEmpty()) {
			CacheObject freeObj = this.policy.selectByPolicy(cacheList);
			freeList.add(freeObj);
			sum = sum + freeObj.getSize(this.sizeof);

			freeList.remove(freeObj);
		}
		return freeList;
	}

	/*
	 * <p> 获取所有缓存元数据列表
	 */
	private List<CacheObject> getCacheList() {
		List<CacheObject> cacheList = new ArrayList<CacheObject>();
		Map<String, CacheObject> cachePool = this.getCachePool();

		Set<String> keys = cachePool.keySet();
		for (String tmp : keys) {
			cacheList.add(cachePool.get(tmp));
		}
		return cacheList;
	}
	

	/**
	 * <p>
	 * 删除所有过期数据
	 */
	public void expire() {		
		Map<String, CacheObject> cachePool = this.getCachePool();
		Set<String> keys=cachePool.keySet();
		
		for(String tmp:keys){
			CacheObject obj=cachePool.get(tmp);
			if(obj==null){
				continue;
			}
			
			if(obj.isExpired()){
				cachePool.remove(tmp);
			}
		}
	}
	
	public long getLimited() {
		return this.limited;
	}
	
	public String getName(){
		return this.name;
	}

}
