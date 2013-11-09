package org.river.android.cache.size;

/**
 * <p>
 * 对象大小测量接口
 * @author river
 * @date 20130917
 */
public interface ISizeOf {
	
	/**
	 * <p>
	 * 对象大小
	 * @return
	 */
	public long sizeof(Object obj);
}
