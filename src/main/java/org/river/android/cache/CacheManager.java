package org.river.android.cache;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * {@link ICache} management
 * @author river
 * @date 20130918
 */
public class CacheManager {
	public static String TAG="CacheManager";
	public static Logger LOG=LoggerFactory.getLogger(CacheManager.class);
	
	public static final String DEFAULT_NAME = "__DEFAULT__";
	public static final double ON_HEAP_THRESHOLD = 0.8;
	
	private static Map<String,ICache> cacheInMem=new HashMap<String,ICache>();
	
	/**
	 * <p>
	 * 获取指定名称的缓存池,如果CacheManager中已存在则返回，否则新建
	 * @param name
	 * @return
	 */
	public static ICache getMemDiskCache(String name){
		return null;
	}
	
	/**
	 * <p>
	 * 获取指定名称的缓存池,该缓存只存储在内存中，销毁时不存储到硬盘
	 * 如果CacheManager中已存在则返回，否则新建
	 * @param name
	 * @return
	 */
	public static ICache getMemOnlyCache(String name){
		return null;
	}
	
	/**
	 * <p>
	 * 从硬盘中加载缓存
	 * @param name
	 * @return
	 */
	public static ICache loadMemDiskCache(String name){
		return null;
	}
	
	/**
	 * <p>
	 * 从硬盘中加载缓存,该缓存不修改硬盘中的内容
	 * @param name
	 * @return
	 */
	public static ICache loadMemOnlyCache(String name){
		return null;
	}
}
