package org.river.android.cache.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.river.android.cache.impl.CacheObject;
import org.river.android.cache.impl.CacheObject.State;
import org.river.android.cache.size.ISizeOf;
import org.river.android.cache.utils.CacheMetaParser;
import org.river.android.cache.utils.CacheUtils;
import org.river.android.cache.utils.CloseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;

/**
 * <p>
 * 保存在硬盘中的缓存存储对象
 * 
 * @author river
 * @date 20130918
 */
public class DiskStore extends AbstractStore {

	public static final String TAG = "DiskStore";

	private static final Logger log = LoggerFactory.getLogger(TAG);
	private static final String META_FILE_NAME = ".meta";

	protected Context ctx;
	protected File cacheDir;
	protected File cacheFile;


	/**
	 * <p>
	 * 获取新实例
	 * 
	 * @param ctx
	 * @param name
	 * @return 如果加载失败则返回null
	 */
	public static Store instance(Context ctx, String name,long limited, Policy policy) {
		DiskStore store = new DiskStore(ctx, name, policy,limited);
		try {
			store.load(store.cacheFile);
		} catch (Exception e) {
			log.error("load from cache error");

			store = null;
		}
		return store;
	}

	protected DiskStore(Context ctx, String name, Policy policy,long limited) {
		super(name,policy,limited);
		this.ctx = ctx;		
		this.cacheDir = CacheUtils.getAppCacheDir(ctx);
		this.cacheFile = new File(cacheDir, name);
	}

	/**
	 * <p>
	 * 从路径中加载已有数据,如果文件不存在 则初始化路径
	 * 
	 * @throws IOException
	 */
	protected void load(File file) throws Exception {
		if (!file.exists()) {
			this.initialize(file);
		}
		File meta = new File(cacheFile, META_FILE_NAME);
		this.cachePool=CacheMetaParser.loadMetaFile(meta);
	}




	/**
	 * <p>
	 * 
	 * 初始化硬盘缓存，创建缓存元数据对象
	 * 
	 * @param file
	 * @throws Throwable
	 */
	private void initialize(File file) throws IOException {
		file.mkdir();

		// 创建元文件
		File meta = new File(file, META_FILE_NAME);
		try {
			meta.createNewFile();
		} catch (IOException e) {
			log.error("create meta file of cache[" + name + "] error");
			throw e;
		}
	}

	/**
	 * <p>
	 * 更新元数据，并立即把缓存对象写入硬盘
	 * TODO 当缓存的大小大于limited时的逻辑
	 */
	public synchronized boolean put(CacheObject obj) {
		if (obj == null || obj.getValue() == null) {
			return false;
		}

		this.write(obj);

		obj.setState(State.PERSTED);
		obj.setSize(sizeof);
		obj.setValue(null);
		cachePool.put((String) obj.getKey(), obj);

		return true;
	}

	/**
	 * <p>
	 * 把对象写入硬盘
	 * 
	 * @param obj
	 */
	private void write(CacheObject obj) {

		ObjectOutputStream oos = null;
		try {
			File objFile = new File(this.cacheFile, (String) obj.getKey());
			oos = new ObjectOutputStream(new FileOutputStream(objFile));
			oos.writeObject(obj);
		} catch (Exception e) {
			log.error("persist CacheObject[" + obj.getKey() + "] error");
		} finally {
			CloseUtils.close(oos);
		}
	}

	/**
	 * <p>
	 * 从硬盘中读取对象
	 */
	public CacheObject get(Object key) {
		CacheObject meta = this.cachePool.get(key);
		if (meta == null) {
			return null;
		}

		CacheObject persisted = this.read((String) key);
		meta.setValue(persisted.getValue());
		return meta;
	}

	/**
	 * <p>
	 * 从硬盘中读取缓存对象
	 * 
	 * @param key
	 * @return
	 */
	private CacheObject read(String key) {
		CacheObject persisted = null;
		ObjectInputStream ois = null;

		try {
			File objFile = new File(this.cacheFile, key);
			ois = new ObjectInputStream(new FileInputStream(objFile));
			persisted = (CacheObject) ois.readObject();
		} catch (Throwable e) {
			log.error("read CacheObject[" + key + "] error:", e.getMessage());
		} finally {
			CloseUtils.close(ois);
		}
		return persisted;
	}

	public Set<String> getKeys() {
		return this.cachePool.keySet();
	}

	/**
	 * <p>
	 * 删除缓存对象文件并返回
	 */
	public synchronized CacheObject remove(Object key) {
		CacheObject oldObj = this.cachePool.remove(key);
		if (oldObj == null) {
			return null;
		}

		CacheObject persited = this.read((String) key);
		if (persited != null) {
			oldObj.setValue(persited.getValue());
		}

		File objFile = new File(this.cacheFile, (String) key);

		if (objFile.exists()) {
			objFile.delete();
		}

		return oldObj;
	}

	/**
	 * <p>
	 * 删除所有指定keys的缓存对象
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
	public void removeAll() {
		Set<String> keySet = this.cachePool.keySet();
		this.removeAll(keySet);

	}

	/**
	 * <p>
	 * 覆盖缓存对象文件，并更新元数据
	 */
	public CacheObject override(CacheObject obj) {
		if (obj == null) {
			return null;
		}

		this.write(obj);

		CacheObject meta = this.cachePool.get(obj.getKey());
		if (meta == null) {
			meta = obj;
			meta.setValue(null);
			this.cachePool.put((String) meta.getKey(), meta);
		}

		return obj;
	}

	/**
	 * <p>
	 * 把所有元数据持久化,并释放所有资源
	 */
	public void dispose() {
		try {
			this.flush();
		} catch (IOException e) {
			log.error("dispose Store["+name+"] error",e.getMessage());
		}
	}


	
	/**
	 * <p>
	 * 从硬盘存储中释放相应大小的空间
	 */
	public synchronized long free(long size) {
		List<CacheObject> freeList=super.getFreeList(size);		
		long sum=0;
		for(CacheObject tmp:freeList){
			sum=+tmp.getSize(this.sizeof);
			this.remove(tmp.getKey());
		}
		
		return sum;
	}

	/**
	 * <p>
	 * 计算存储当前占用空间大小
	 */
	public long getSize() {
		Set<String> keys=this.cachePool.keySet();
		long size=0;
		for(String tmp:keys){
			CacheObject cacheObj=this.cachePool.get(tmp);
			size=+cacheObj.getSize(this.sizeof);
		}
		
		return size;
	}

	/**
	 * <p>
	 * 硬盘存储，忽略内存空间
	 */
	public long getInMemorySize() {
		return 0;
	}

	/**
	 * <p>
	 * @see getSize()
	 */
	public long getOnDiskSize() {
		return this.getSize();
	}

	public boolean containsKey(Object key) {
		return this.cachePool.containsKey(key);
	}

	public boolean containsKeyOnDisk(Object key) {
		return this.cachePool.containsKey(key);
	}

	public boolean containsKeyInMemory(Object key) {
		return this.containsKeyInMemory(key);
	}

	public void expire() {
		
	}

	public void flush() throws IOException {
		this.persistMeta(cachePool);
	}
	
	/**
	 * <p>
	 * 持久化元数据
	 * @param cachePool
	 */
	protected void persistMeta(Map<String,CacheObject> cachePool){
		File metaFile = new File(this.cacheFile, META_FILE_NAME);
		OutputStream os = null;

		try {
			os = new FileOutputStream(metaFile);
			String metaXml = CacheMetaParser.buildMetaXml(cachePool);
			os.write(metaXml.getBytes());
			os.flush();
		} catch (Throwable e) {
			log.error("flush Store[" + name + "] error", e.getMessage());
		} finally {
			CloseUtils.close(os);
		}
	}

	public Policy getInMemEvictPolicy() {
		return this.policy;
	}

	public void setInMemEvictPolicy(Policy policy) {
		this.policy=policy;
	}


	public List<CacheObject> getAll(Collection<String> keys) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<CacheObject> getAll() {
		Set<String> keys=this.cachePool.keySet();
		
		List<CacheObject> all=new ArrayList<CacheObject>();
		for(String tmp:keys){
			all.add(this.cachePool.get(tmp));
		}
		return all;
	}


	@Override
	protected Map<String, CacheObject> getCachePool() {
		return this.cachePool;
	}
	
	public ISizeOf getSizeof() {
		return sizeof;
	}

	public void setSizeof(ISizeOf sizeof) {
		this.sizeof = sizeof;
	}

}
