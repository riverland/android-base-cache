package org.river.android.cache.size;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.river.android.cache.utils.CloseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 序列化后大小实现
 * @author river
 * @date 20130917
 */
public class SerialSizeOf implements ISizeOf {
	public static final String TAG="SerialSizeOf";
	private static Logger log=LoggerFactory.getLogger(TAG);

	public long sizeof(Object obj) {
		if(!(obj instanceof Serializable)){
			return 0;
		}
		
		long size=0;
		
		ByteArrayOutputStream baos=new ByteArrayOutputStream();		
		ObjectOutputStream oos=null;
		try{
			oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			size=baos.size();
		}catch(Exception e){
			log.error("size of error");
		}finally{
			CloseUtils.close(baos);
		}
		
		return size;
	}

}
