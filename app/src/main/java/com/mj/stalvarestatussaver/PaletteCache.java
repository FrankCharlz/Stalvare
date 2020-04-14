package com.mj.stalvarestatussaver;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import androidx.palette.graphics.Palette;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Created by Frank on 4/14/2020.
 */
public class PaletteCache {

    private static HashMap<String, Palette> cache = new HashMap<>();

    public static void save(@NotNull String path, @NotNull Palette palette) {
        cache.put(path, palette);
    }
    public static Palette get(@NotNull String path) {
        return cache.get(path);
    }

    public static boolean has(@NotNull String path) {
        return cache.containsKey(path);
    }
}