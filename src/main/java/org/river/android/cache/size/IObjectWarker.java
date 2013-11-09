package org.river.android.cache.size;

/**
 * <p>
 * walker 接口
 * @author river
 * @date 20130916
 */
public interface IObjectWarker<E> {
	/**
	 * <p>
	 * 遍历obj对象,并返回遍历结果
	 * @param obj
	 * @return
	 */
	public E walk(Object obj);
	
	/**
	 * <p>
	 * 设置ignore对象
	 * @param ignore
	 */
	public void setIgnore(Ignore ignore);

	/**
	 * <p>
	 * 确认是否walk
	 * @author river
	 * @date 20130916
	 */
	public static interface Ignore{
		public boolean isIgnore(Object obj);
	}
}
