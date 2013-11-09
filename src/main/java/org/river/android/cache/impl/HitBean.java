package org.river.android.cache.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 能从Cache中获取次数
 * @author river
 * @date 2130913
 */
public class HitBean implements Serializable{

	private static final long serialVersionUID = 2943822362789004152L;

	private Object key;
	
	private int hitCount;
	
	private List<Long> hitTime=new ArrayList<Long>();
	
	
	public HitBean(Object key) {
		this.key = key;
	}

	public int add(){
		synchronized(this){
			hitCount++;
			hitTime.add(System.currentTimeMillis());
		}
		
		return hitCount;
	}
	
	public String getTimeSerial(){
		StringBuffer sb=new StringBuffer();
		for(Long time:hitTime){
			sb.append(time.toString()).append(",");
		}
		
		String serial=sb.toString();
		if(serial.endsWith(",")){
			serial=serial.substring(0,serial.length()-1);
		}
		return serial;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public int getHitCount() {
		return hitCount;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public List<Long> getHitTime() {
		return hitTime;
	}

	public void setHitTime(List<Long> hitTime) {
		this.hitTime = hitTime;
	}
	
	
}
