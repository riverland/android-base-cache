/**
 *  Copyright Terracotta, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.river.android.cache.store;

import java.util.List;

import org.river.android.cache.impl.CacheObject;

/**
 * Cache eviction policy interface.
 * 
 * @see FIFOPolicy,LFUPolicy,LRUPolicy
 * @author River
 * @date 20130911
 */
public interface Policy {

	/**
	 * @return the name of the Policy. Inbuilt implements are LRU, LFU and FIFO.
	 */
	public String getName();

	/**
	 * <p>
	 * 
	 * Finds the best eviction candidate based on the samples.
	 * 
	 * @param samples
	 * @return the selected CacheObject
	 */
	public CacheObject selectByPolicy(List<CacheObject> samples);

}
