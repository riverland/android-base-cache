package org.river.android.cache;

import org.river.android.cache.store.LRUPolicy;
import org.river.android.cache.store.Policy;
import org.river.android.cache.store.Store;

/**
 * <p>
 * Config object for cache
 * 
 * @author River
 * @date 20130911
 */
public class CacheConfig implements Cloneable {

	/** Default value for clearOnFlush */
	public static final boolean DEFAULT_CLEAR_ON_FLUSH = true;

	/** The default interval between runs of the expiry thread. */
	public static final long DEFAULT_EXPIRY_THREAD_INTERVAL_SECONDS = 60;

	/** Set a buffer size for the spool of approx 30MB. */
	public static final int DEFAULT_SPOOL_BUFFER_SIZE = 10;

	/** Default number of diskAccessStripes. */
	public static final int DEFAULT_DISK_ACCESS_STRIPES = 1;

	/** Logging is off by default. */
	public static final boolean DEFAULT_LOGGING = false;

	/** The default memory store eviction policy is LRU. */
	public static final Policy DEFAULT_MEMORY_STORE_EVICTION_POLICY = LRUPolicy.instance();

	/** Default value for copyOnRead */
	public static final boolean DEFAULT_COPY_ON_READ = false;

	/** Default value for copyOnRead */
	public static final boolean DEFAULT_COPY_ON_WRITE = false;

	/** Default value for ttl */
	public static final long DEFAULT_TTL = 0;

	/** Default value for tti */
	public static final long DEFAULT_TTI = 0;

	/** Default value for maxElementsOnDisk */
	public static final int DEFAULT_MAX_ELEMENTS_ON_DISK = 0;

	/** Default value for maxEntriesInCache */
	public static final long DEFAULT_MAX_ENTRIES_IN_CACHE = 0;

	/** Default value for statistics */
	public static final boolean DEFAULT_STATISTICS = true;

	/** Default maxBytesOnHeap value */
	public static final long DEFAULT_MAX_BYTES_ON_HEAP = 0;

	/** Default maxBytesOffHeap value */
	public static final long DEFAULT_MAX_BYTES_OFF_HEAP = 0;

	/** Default maxBytesOnDisk value */
	public static final long DEFAULT_MAX_BYTES_ON_DISK = 0;

	/** Default eternal value */
	public static final boolean DEFAULT_ETERNAL_VALUE = false;

	/* the name of the cache. */
	protected volatile String name;

	/* Timeout in milliseconds for CacheLoader related calls */
	protected volatile long cacheLoaderTimeoutMillis;

	/**
	 * the maximum objects to be held in the {@link Store}. <code>0</code>
	 * translates to no-limit.
	 */
	protected volatile Integer maxEntriesLocalHeap;

	/*
	 * the maximum objects to be held in the {@link DiskStore}. <code>0</code>
	 * translates to no-limit.
	 */
	protected volatile int maxElementsOnDisk = DEFAULT_MAX_ELEMENTS_ON_DISK;

	/*
	 * the maximum entries to be held in the cache
	 */
	protected volatile long maxEntriesInCache = DEFAULT_MAX_ENTRIES_IN_CACHE;

	/*
	 * The policy used to evict elements from the {@link Store}. This can be one
	 * of: <ol> <li>LRU - least recently used <li>LFU - Less frequently used
	 * <li>FIFO - first in first out, the oldest element by creation time </ol>
	 * The default value is LRU
	 */
	protected volatile Policy evictionPolicy = DEFAULT_MEMORY_STORE_EVICTION_POLICY;

	/*
	 * Sets whether the MemoryStore should be cleared when {@link Cache#flush
	 * flush()} is called on the cache - true by default.
	 */
	protected volatile boolean clearOnFlush = DEFAULT_CLEAR_ON_FLUSH;

	/*
	 * Sets whether CacheObject are eternal. If eternal, timeouts are ignored
	 * and never expired.
	 */
	protected volatile boolean eternal = DEFAULT_ETERNAL_VALUE;

	/*
	 * the time to idle for an element before it expires. Is only used if the
	 * element is not eternal.A value of 0 means do not check for idling.
	 */
	protected volatile long timeToIdleSeconds = DEFAULT_TTI;

	/*
	 * Sets the time to idle for an element before it expires. Is only used if
	 * the element is not eternal. This attribute is optional in the
	 * configuration. A value of 0 means do not check time to live.
	 */
	protected volatile long timeToLiveSeconds = DEFAULT_TTL;

	/*
	 * whether elements can overflow to disk when the in-memory cache has
	 * reached the set limit.
	 */
	protected volatile Boolean overflowToDisk;

	/*
	 * The size of the disk spool used to buffer writes
	 */
	protected volatile int diskSpoolBufferSizeKB = DEFAULT_SPOOL_BUFFER_SIZE;

	/*
	 * The number of concurrent disk access stripes.
	 */
	protected volatile int diskAccessStripes = DEFAULT_DISK_ACCESS_STRIPES;

	/*
	 * The interval in seconds between runs of the disk expiry thread. <p/> 2
	 * minutes is the default. This is not the same thing as time to live or
	 * time to idle. When the thread runs it checks these things. So this value
	 * is how often we check for expiry.
	 */
	protected volatile long diskExpiryThreadIntervalSeconds = DEFAULT_EXPIRY_THREAD_INTERVAL_SECONDS;

	/*
	 * whether elements can overflow to off heap memory when the in-memory cache
	 * has reached the set limit.
	 */
	protected volatile Boolean overflowToOffHeap;

	private volatile boolean frozen;
	private volatile Boolean copyOnRead;
	private volatile Boolean copyOnWrite;
	private String maxBytesLocalHeapInput;
	private String maxBytesLocalOffHeapInput;
	private String maxBytesLocalDiskInput;
	private Long maxBytesLocalHeap;
	private Long maxBytesLocalOffHeap;
	private Long maxBytesLocalDisk;
	private Integer maxBytesLocalHeapPercentage;
	private Integer maxBytesLocalOffHeapPercentage;
	private Integer maxBytesLocalDiskPercentage;
	private volatile boolean maxEntriesLocalDiskExplicitlySet;
	private volatile boolean maxBytesLocalDiskExplicitlySet;
	private volatile boolean maxBytesLocalOffHeapExplicitlySet;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCacheLoaderTimeoutMillis() {
		return cacheLoaderTimeoutMillis;
	}

	public void setCacheLoaderTimeoutMillis(long cacheLoaderTimeoutMillis) {
		this.cacheLoaderTimeoutMillis = cacheLoaderTimeoutMillis;
	}

	public Integer getMaxEntriesLocalHeap() {
		return maxEntriesLocalHeap;
	}

	public void setMaxEntriesLocalHeap(Integer maxEntriesLocalHeap) {
		this.maxEntriesLocalHeap = maxEntriesLocalHeap;
	}

	public int getMaxElementsOnDisk() {
		return maxElementsOnDisk;
	}

	public void setMaxElementsOnDisk(int maxElementsOnDisk) {
		this.maxElementsOnDisk = maxElementsOnDisk;
	}

	public long getMaxEntriesInCache() {
		return maxEntriesInCache;
	}

	public void setMaxEntriesInCache(long maxEntriesInCache) {
		this.maxEntriesInCache = maxEntriesInCache;
	}

	public Policy getEvictionPolicy() {
		return evictionPolicy;
	}

	public void setEvictionPolicy(Policy evictionPolicy) {
		this.evictionPolicy = evictionPolicy;
	}

	public boolean isClearOnFlush() {
		return clearOnFlush;
	}

	public void setClearOnFlush(boolean clearOnFlush) {
		this.clearOnFlush = clearOnFlush;
	}

	public boolean isEternal() {
		return eternal;
	}

	public void setEternal(boolean eternal) {
		this.eternal = eternal;
	}

	public long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
	}

	public void setTimeToIdleSeconds(long timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	public long getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	public Boolean getOverflowToDisk() {
		return overflowToDisk;
	}

	public void setOverflowToDisk(Boolean overflowToDisk) {
		this.overflowToDisk = overflowToDisk;
	}

	public int getDiskSpoolBufferSizeKB() {
		return diskSpoolBufferSizeKB;
	}

	public void setDiskSpoolBufferSizeKB(int diskSpoolBufferSizeKB) {
		this.diskSpoolBufferSizeKB = diskSpoolBufferSizeKB;
	}

	public int getDiskAccessStripes() {
		return diskAccessStripes;
	}

	public void setDiskAccessStripes(int diskAccessStripes) {
		this.diskAccessStripes = diskAccessStripes;
	}

	public long getDiskExpiryThreadIntervalSeconds() {
		return diskExpiryThreadIntervalSeconds;
	}

	public void setDiskExpiryThreadIntervalSeconds(
			long diskExpiryThreadIntervalSeconds) {
		this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
	}

	public Boolean getOverflowToOffHeap() {
		return overflowToOffHeap;
	}

	public void setOverflowToOffHeap(Boolean overflowToOffHeap) {
		this.overflowToOffHeap = overflowToOffHeap;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	public Boolean getCopyOnRead() {
		return copyOnRead;
	}

	public void setCopyOnRead(Boolean copyOnRead) {
		this.copyOnRead = copyOnRead;
	}

	public Boolean getCopyOnWrite() {
		return copyOnWrite;
	}

	public void setCopyOnWrite(Boolean copyOnWrite) {
		this.copyOnWrite = copyOnWrite;
	}

	public String getMaxBytesLocalHeapInput() {
		return maxBytesLocalHeapInput;
	}

	public void setMaxBytesLocalHeapInput(String maxBytesLocalHeapInput) {
		this.maxBytesLocalHeapInput = maxBytesLocalHeapInput;
	}

	public String getMaxBytesLocalOffHeapInput() {
		return maxBytesLocalOffHeapInput;
	}

	public void setMaxBytesLocalOffHeapInput(String maxBytesLocalOffHeapInput) {
		this.maxBytesLocalOffHeapInput = maxBytesLocalOffHeapInput;
	}

	public String getMaxBytesLocalDiskInput() {
		return maxBytesLocalDiskInput;
	}

	public void setMaxBytesLocalDiskInput(String maxBytesLocalDiskInput) {
		this.maxBytesLocalDiskInput = maxBytesLocalDiskInput;
	}

	public Long getMaxBytesLocalHeap() {
		return maxBytesLocalHeap;
	}

	public void setMaxBytesLocalHeap(Long maxBytesLocalHeap) {
		this.maxBytesLocalHeap = maxBytesLocalHeap;
	}

	public Long getMaxBytesLocalOffHeap() {
		return maxBytesLocalOffHeap;
	}

	public void setMaxBytesLocalOffHeap(Long maxBytesLocalOffHeap) {
		this.maxBytesLocalOffHeap = maxBytesLocalOffHeap;
	}

	public Long getMaxBytesLocalDisk() {
		return maxBytesLocalDisk;
	}

	public void setMaxBytesLocalDisk(Long maxBytesLocalDisk) {
		this.maxBytesLocalDisk = maxBytesLocalDisk;
	}

	public Integer getMaxBytesLocalHeapPercentage() {
		return maxBytesLocalHeapPercentage;
	}

	public void setMaxBytesLocalHeapPercentage(
			Integer maxBytesLocalHeapPercentage) {
		this.maxBytesLocalHeapPercentage = maxBytesLocalHeapPercentage;
	}

	public Integer getMaxBytesLocalOffHeapPercentage() {
		return maxBytesLocalOffHeapPercentage;
	}

	public void setMaxBytesLocalOffHeapPercentage(
			Integer maxBytesLocalOffHeapPercentage) {
		this.maxBytesLocalOffHeapPercentage = maxBytesLocalOffHeapPercentage;
	}

	public Integer getMaxBytesLocalDiskPercentage() {
		return maxBytesLocalDiskPercentage;
	}

	public void setMaxBytesLocalDiskPercentage(
			Integer maxBytesLocalDiskPercentage) {
		this.maxBytesLocalDiskPercentage = maxBytesLocalDiskPercentage;
	}

	public boolean isMaxEntriesLocalDiskExplicitlySet() {
		return maxEntriesLocalDiskExplicitlySet;
	}

	public void setMaxEntriesLocalDiskExplicitlySet(
			boolean maxEntriesLocalDiskExplicitlySet) {
		this.maxEntriesLocalDiskExplicitlySet = maxEntriesLocalDiskExplicitlySet;
	}

	public boolean isMaxBytesLocalDiskExplicitlySet() {
		return maxBytesLocalDiskExplicitlySet;
	}

	public void setMaxBytesLocalDiskExplicitlySet(
			boolean maxBytesLocalDiskExplicitlySet) {
		this.maxBytesLocalDiskExplicitlySet = maxBytesLocalDiskExplicitlySet;
	}

	public boolean isMaxBytesLocalOffHeapExplicitlySet() {
		return maxBytesLocalOffHeapExplicitlySet;
	}

	public void setMaxBytesLocalOffHeapExplicitlySet(
			boolean maxBytesLocalOffHeapExplicitlySet) {
		this.maxBytesLocalOffHeapExplicitlySet = maxBytesLocalOffHeapExplicitlySet;
	}

}
