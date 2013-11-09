package org.river.android.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import org.river.android.cache.CacheConfig;
import org.river.android.cache.size.ISizeOf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A Cache Object, which should be cached in the store consisting of a key,value
 * and attributes.
 * 
 * @author River
 * @date 20130906
 */
public class CacheObject implements Serializable, Cloneable {

	private static final long serialVersionUID = 1183620559901329321L;

	private static final Logger LOG = LoggerFactory.getLogger(CacheObject.class.getName());

	private static final long ID_NOT_SET = 0l;

	private static final AtomicLongFieldUpdater<CacheObject> HIT_COUNT_UPDATER = AtomicLongFieldUpdater.newUpdater(CacheObject.class, "hitCount");

	private Object key;
	private Object value;
	private HitBean hitBean;
	private volatile long size;
	private volatile long hitCount;
	private volatile long timeToLive = Integer.MIN_VALUE;
	private volatile long timeToIdle = Integer.MIN_VALUE;
	private transient long creationTime;
	private transient long lastAccessTime;
	private volatile long lastUpdateTime;
	private volatile boolean cacheDefaultLifespan = true;
	private volatile long id = ID_NOT_SET;
	private State state=State.TRANSIENT;

	/**
	 * <p>
	 * constructor
	 */
	public CacheObject(final Object key, final Object value) {
		this.key = key;
		this.value = value;
		HIT_COUNT_UPDATER.set(this, 0);
		this.creationTime = System.currentTimeMillis();
		this.lastAccessTime = this.creationTime;
		this.lastUpdateTime = this.creationTime;
	}

	@Override
	public final boolean equals(final Object object) {
		if (object == null || !(object instanceof CacheObject)) {
			return false;
		}

		CacheObject cacheObj = (CacheObject) object;
		if (key == null || cacheObj.getKey() == null) {
			return false;
		}

		return key.equals(cacheObj.getKey());
	}

	/**
	 * <p>
	 * Gets the hashcode, based on the key.
	 */
	@Override
	public final int hashCode() {
		return key.hashCode();
	}

	/**
	 * Returns a {@link String} representation of the {@link CacheObject}.
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("[ key = ").append(key).append(", value=").append(value).append(", version=").append(", hitCount=").append(hitCount).append(", CreationTime = ").append(this.getCreationTime())
				.append(", LastAccessTime = ").append(this.getLastAccessTime()).append(" ]");

		return sb.toString();
	}

	/**
	 * Clones an this object. A completely new object is created, with no common
	 * references with the existing one.
	 * 
	 * @return a new {@link CacheObject}, with exactly the same field values as
	 *         the one it was cloned from.
	 * @throws CloneNotSupportedException
	 */
	@Override
	public final Object clone() throws CloneNotSupportedException {
		try {
			return new CacheObject(deepCopy(key), deepCopy(value));
		} catch (Exception e) {
			LOG.error("Error cloning CacheObject key[" + key + "]");
			throw new CloneNotSupportedException();
		}
	}

	/**
	 * <p>
	 * copy the key or value
	 * 
	 * @param oldValue
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Object deepCopy(final Object oldValue) throws IOException, ClassNotFoundException {
		Serializable newValue = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		try {
			oos = new ObjectOutputStream(bout);
			oos.writeObject(oldValue);
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			ois = new ObjectInputStream(bin);
			newValue = (Serializable) ois.readObject();
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				if (ois != null) {
					ois.close();
				}
			} catch (Exception e) {
				LOG.error("Error closing Stream");
			}
		}
		return newValue;
	}

	/**
	 * The size of this object in serialized form. This is not the same thing as
	 * the memory size, which is JVM dependent. Relative values should be
	 * meaningful, however.
	 * 
	 * @return The serialized size in bytes
	 */
	public final long getSerializedSize() {

		if (!isSerializable()) {
			return 0;
		}
		long size = 0;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bout);
			oos.writeObject(this);
			size = bout.size();
			return size;
		} catch (IOException e) {
			LOG.debug("measuring CacheObject key [" + key + "] error: " + e.getMessage());
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (Exception e) {
				LOG.error("Error closing ObjectOutputStream");
			}
		}

		return size;
	}

	/**
	 * Whether the Object may be Serialized.
	 */
	public final boolean isSerializable() {
		return isKeySerializable() && (value instanceof Serializable || value == null);
	}

	/**
	 * <p>
	 * Whether the CacheObject key may be Serialized.
	 */
	public final boolean isKeySerializable() {
		return key instanceof Serializable || key == null;
	}

	/**
	 * <p>
	 * check expired
	 * 
	 * @return
	 */
	public boolean isExpired() {

		long now = System.currentTimeMillis();
		long expirationTime = getExpirationTime();

		return now > expirationTime;
	}

	/**
	 * Check whether the cache object is expired
	 * 
	 * @param config
	 * 
	 * @return
	 */
	public boolean isExpired(CacheConfig config) {
		if (cacheDefaultLifespan) {
			if (config.isEternal()) {
				timeToIdle = 0;
				timeToLive = 0;
			} else {
				timeToIdle = config.getTimeToIdleSeconds();
				timeToLive = config.getTimeToLiveSeconds();
			}
		}
		return isExpired();
	}

	/**
	 * Returns the expiration time based on time to live. If this element also
	 * has a time to idle setting, the expiry time will vary depending on
	 * whether the element is accessed.
	 * 
	 * @return the time to expiration
	 */
	public long getExpirationTime() {

		long expirationTime = 0;
		long ttlExpiry = creationTime + getTimeToLive() * 1000;

		long mostRecentTime = Math.max(creationTime, lastAccessTime);
		mostRecentTime = Math.max(mostRecentTime, lastUpdateTime);
		long ttiExpiry = mostRecentTime + getTimeToIdle() * 1000;

		if (getTimeToLive() != 0 && (getTimeToIdle() == 0 || lastAccessTime == 0)) {
			expirationTime = ttlExpiry;
		} else if (getTimeToLive() == 0) {
			expirationTime = ttiExpiry;
		} else {
			expirationTime = Math.min(ttlExpiry, ttiExpiry);
		}
		return expirationTime;
	}

	/**
	 * Custom serialization write logic
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeInt((int) Math.ceil((double) creationTime / 1000));
		out.writeInt((int) lastAccessTime / 1000);
	}

	/**
	 * Custom serialization read logic
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		creationTime = in.readInt() * 1000;
		lastAccessTime = in.readInt() * 1000;
	}
	
	public long getInMemSize(){
		return this.getSize();
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.size=0;
		this.value = value;
	}

	public HitBean getHitBean() {
		return hitBean;
	}

	public void setHitBean(HitBean hitBean) {
		this.hitBean = hitBean;
	}

	public long getHitCount() {
		return hitCount;
	}

	public void setHitCount(long hitCount) {
		this.hitCount = hitCount;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public long getTimeToIdle() {
		return timeToIdle;
	}

	public void setTimeToIdle(long timeToIdle) {
		this.timeToIdle = timeToIdle;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public boolean isCacheDefaultLifespan() {
		return cacheDefaultLifespan;
	}

	public void setCacheDefaultLifespan(boolean cacheDefaultLifespan) {
		this.cacheDefaultLifespan = cacheDefaultLifespan;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

	public long getSize() {		
		return this.size;
	}

	public long getSize(ISizeOf sizeOf) {
		if(sizeOf==null){
			return this.size;
		}
		
		if(this.size==0){
			this.setSize(sizeOf);
		}
		return this.size;
	}

	public void setSize(long size){
		this.size=size;
	}
	
	public void setSize(ISizeOf sizeof){
		if(sizeof==null){
			return;
		}
		
		this.size=sizeof.sizeof(this);
	}
	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	/**
	 * <p>
	 * 状态
	 * @author river
	 * @date 20130913
	 */
	public static enum State{
		PERSTED,TRANSIENT
	}
}
