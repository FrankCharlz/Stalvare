package com.mj.stalvarestatussaver.utils;



import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.util.HashMap;

/**
 * Created by Frank on 1/11/2016.
 */
public class VideoThumbnailCache {

    private static HashMap<String, Bitmap> thumbCache = new HashMap<>();

    public static Bitmap getBitmap(String path) {

        Bitmap bitmap = thumbCache.get(path);

        if (bitmap == null) {
            bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
            thumbCache.put(path, bitmap);
        }
        return bitmap;
    }
}