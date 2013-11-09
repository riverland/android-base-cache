package org.river.android.cache.utils;

import static android.os.Environment.MEDIA_MOUNTED;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

/**
 * <p>
 * supply some cache utils method
 * @author river
 * @date 20130909
 */
public class CacheUtils {
	public static final String TAG="CacheUtils";
	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
	
	/**
	 * <p>
	 * get the app's cache file dir
	 * @param ctx
	 * @return
	 */
	public static File getAppCacheDir(Context ctx){
		File dir = null;
		if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(ctx)) {
			dir = getExternalCacheDir(ctx);
		}
		
		if (dir == null) {
			dir = ctx.getCacheDir();
		}
		
		if (dir == null) {
			Log.e(TAG,"Can't define system cache directory");
		}
		return dir;
	}
	
	/**
	 * <p>
	 * get external cache directory or create new one if it doesn't exist
	 * @param ctx
	 * @return
	 */
	public static File getExternalCacheDir(Context ctx){
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File cacheDir = new File(new File(dataDir, ctx.getPackageName()), "cache");
		if (!cacheDir.exists()) {
			if (!cacheDir.mkdirs()) {
				Log.e(TAG, "create external cache directory error");
				return null;
			}
			
			try {
				new File(cacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				Log.e(TAG, "create <.nomedia> file in application external cache directory error");
			}
		}
		return cacheDir;
	}
	
	/*
	 * <p>
	 * check whether app has the write permission  on external storage
	 * @param context
	 * @return
	 */
	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}
}
