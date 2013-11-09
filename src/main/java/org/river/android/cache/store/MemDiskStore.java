package org.river.android.cache.store;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.river.android.cache.impl.CacheObject;
import org.river.android.cache.impl.CacheObject.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

/**
 * <p>
 * 支持一级缓存和二级缓存
 * 
 * @author river
 * @date 20130913
 */
public class MemDiskStore implements Store {

	public static final String TAG = "MemDiskStore";
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(TAG);

	/* 二级缓存 */
	private Store diskStore;
	/* 一级缓存 */
	private Store memStore;

	private long memLimited;
	@SuppressWarnings("unused")
	private long diskLimited;
	private Policy policy;
	private String name;

	public MemDiskStore(Context ctx, String name, Policy memPolicy,Policy diskPolicy, long memLimited, long diskLimited) {
		this.memLimited = Math.min(memLimited, diskLimited);
		this.diskLimited = Math.max(memLimited, diskLimited);
		this.diskStore = DiskStore.instance(ctx, name, diskLimited, diskPolicy);
		this.policy = memPolicy;
		this.memStore = this.loadFromDiskStore(diskStore);
		this.memStore.setInMemEvictPolicy(memPolicy);
	}

	/*
	 * <p> 从二级缓存中加载一级缓存
	 */
	private Store loadFromDiskStore(Store store) {
		memStore = MemStore.instance(store.getName(), memLimited, policy);

		Map<String, CacheObject> memPool=new HashMap<String,CacheObject>();
		((MemStore) memStore).cachePool = memPool;

		Set<String> keys = store.getKeys();
		for (String tmp : keys) {
			CacheObject obj=diskStore.get(tmp);
			obj.setState(State.PERSTED);
			memPool.put(tmp,obj);
		}

		return memStore;
	}

	public synchronized boolean put(CacheObject obj) {		
		boolean rt = this.memStore.put(obj);
		return rt;
	}

	/**
	 * <p>
	 * 先从内存中查找，找不到再从硬盘中查找
	 */
	public CacheObject get(Object key) {
		CacheObject memObj=this.memStore.get(key);
		if(memObj==null||memObj.getValue()==null){
			CacheObject diskObj=this.diskStore.get(key);
			if(diskObj!=null){
				memObj.setValue(diskObj.getValue());
			}
		}
		
		return memObj;
	}

	public Set<String> getKeys() {
		return this.memStore.getKeys();
	}

	public CacheObject remove(Object key) {
		return this.memStore.remove(key);
	}

	public void removeAll(Collection<String> keys) {
		this.memStore.removeAll(keys);
	}

	public void removeAll() {
		this.memStore.removeAll();
	}

	public CacheObject override(CacheObject obj) {
		return this.memStore.override(obj);
	}

	/**
	 * <p>
	 * 释放资源
	 */
	public void dispose() {
		this.diskStore.dispose();
	}

	public long free(long size) {
		return this.memStore.free(size);
	}

	public long getSize() {
		return this.memStore.getSize();
	}

	public long getInMemorySize() {
		return this.memStore.getInMemorySize();
	}

	public long getOnDiskSize() {
		return this.diskStore.getOnDiskSize();
	}

	public boolean containsKey(Object key) {
		return this.memStore.containsKey(key);
	}

	public boolean containsKeyOnDisk(Object key) {
		return this.diskStore.containsKeyOnDisk(key);
	}

	public boolean containsKeyInMemory(Object key) {
		return this.memStore.containsKeyInMemory(key);
	}

	/**
	 * <p>
	 * 把数据持久化到硬盘
	 * TODO 当前大小大于limited的逻辑
	 */
	public synchronized void flush() throws IOException {		
		this.flushTransient();
		this.removeDeleted();
		this.persistMeta();
	}
	
	/*
	 * <p>
	 * 获取需要持久化的缓存
	 * @return
	 */
	private void  flushTransient(){
		Set<String> keys=memStore.getKeys();		
		for(String tmp:keys){
			CacheObject obj=memStore.get(tmp);
			if(obj.getState().equals(State.PERSTED)){
				continue;
			}
			
			this.diskStore.put(obj);			
		}		
	}
	
	/*
	 * <p>
	 * 从硬盘中删除已经删除的数据
	 */
	private void removeDeleted(){
		Set<String> keys=this.diskStore.getKeys();
		for(String tmp:keys){
			if(null==memStore.get(tmp)){
				this.diskStore.remove(tmp);
			}
		}
	}
	
	/*
	 * <p>
	 * 持久化元数据
	 */
	private void persistMeta(){
		Map<String,CacheObject> cachePool=((MemStore)memStore).cachePool;
		((DiskStore)diskStore).persistMeta(cachePool);	
	}

	public Policy getInMemEvictPolicy() {
		return this.memStore.getInMemEvictPolicy();
	}

	public void setInMemEvictPolicy(Policy policy) {
		this.policy=policy;
		this.memStore.setInMemEvictPolicy(policy);
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
		for(String tmp:keys){
			CacheObject obj=this.get(tmp);
			if(obj!=null){
				all.add(obj);
			}
		}
		
		return all;
	}

	public long getLimited() {
		return this.diskStore.getLimited();
	}

	
	public void expire() {
		this.persistExpired();
		this.diskStore.expire();
	}
	
	/*
	 * <p>
	 * 持久化内存中超时的数据
	 */
	private void persistExpired(){
		Set<String> keys=this.memStore.getKeys();
		for(String tmp:keys){
			CacheObject obj=this.memStore.get(tmp);
			if(obj.isExpired()){
				this.diskStore.put(obj);
				obj.setValue(null);
				obj.setState(State.PERSTED);
			}
		}
	}

	public List<CacheObject> getAll() {
		return this.memStore.getAll();
	}

	public String getName() {
		return name;
	}

}
