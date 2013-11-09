package org.river.android.cache.utils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * 关闭用的工具类
 * @author river
 * @date 20130913
 */
public class CloseUtils {
	
	/**
	 * <p>
	 * @param ins
	 */
	public static void close(InputStream ins){
		try{
			if(ins!=null){
				ins.close();
			}
		}catch(Throwable e){
			//do nothing
		}
	}
	
	/**
	 * <p>
	 * @param os
	 */
	public static void close(OutputStream os){
		try{
			if(os!=null){
				os.close();
			}
		}catch(Throwable e){
			//do nothing
		}
	}
}
