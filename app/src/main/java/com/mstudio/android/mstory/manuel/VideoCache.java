package com.mstudio.android.mstory.manuel;

import android.content.Context;

import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class VideoCache {

private static SimpleCache simpleCache;

public static SimpleCache getInstance(Context context) {
    LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024);
    if (simpleCache == null) {
        simpleCache = new SimpleCache(new File(context.getCacheDir(), "media"), leastRecentlyUsedCacheEvictor, new ExoDatabaseProvider(context));


    }
    return simpleCache;
}}
