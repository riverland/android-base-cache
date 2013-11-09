package org.river.android.cache.store;

import java.util.List;
import java.util.Random;

import org.river.android.cache.impl.CacheObject;

/**
 * <p>
 * A base policy class
 *
 * @author River
 * @date 20130911
 */
public abstract class AbstractPolicy implements Policy {

    /**
     * The sample size to use
     */
    static final int DEFAULT_SAMPLE_SIZE = 30;

    /**
     * Used to select random numbers
     */
    static final Random RANDOM = new Random();

    /**
     * sampleSize how many samples to take
     *
     * @param populationSize the size of the store
     * @return the smaller of the map size and the default sample size of 30
     */
    public static int calculateSampleSize(int populationSize) {
        if (populationSize < DEFAULT_SAMPLE_SIZE) {
            return populationSize;
        } else {
            return DEFAULT_SAMPLE_SIZE;
        }
    }

    public CacheObject selectByPolicy(List<CacheObject> samples){
        if(samples==null||samples.size()==0){
        	return null;
        }
        
        if (samples.size() == 1) {
            return samples.get(0);
        }
        
        CacheObject lowest = null;
        for (CacheObject tmp : samples) {
        	
            if (tmp == null ) {
                continue;
            }
            
            if (lowest != null) {
            	lowest=lower(lowest,tmp);            	
            }else{
            	lowest = tmp;
            }

        }
        
        return lowest;
    }
    
    protected abstract CacheObject lower(CacheObject l,CacheObject r);

    /**
     * Generates a random sample from a population
     *
     * @param populationSize the size to draw from
     * @return a list of random offsets
     */
    public static int[] generateRandomSample(int populationSize) {
        int sampleSize = calculateSampleSize(populationSize);
        int[] offsets = new int[sampleSize];

        if (sampleSize != 0) {
            int maxOffset = 0;
            maxOffset = populationSize / sampleSize;
            for (int i = 0; i < sampleSize; i++) {
                offsets[i] = RANDOM.nextInt(maxOffset);
            }
        }
        return offsets;
    }
    
    
}
