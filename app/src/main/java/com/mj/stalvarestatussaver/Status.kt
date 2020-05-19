package com.mj.stalvarestatussaver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.palette.graphics.Palette
import com.mj.stalvarestatussaver.utils.PaletteCache
import com.mj.stalvarestatussaver.utils.VideoThumbnailCache
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


@Parcelize
data class Status(
    val path: String,
    val modified: Long

): Parcelable {

    private fun getExtension(): String {
        //returns extension including .
        return if (path.length >= 4) path.takeLast(4) else ""
    }

    fun isStatus(): Boolean {
        with(getExtension()) {
            return this.startsWith(".") && return this.length == 4
        }
    }

    fun isVideo(): Boolean {
        return getExtension().equals(".mp4", true) ||
                getExtension().equals(".3gp", true)
    }

    fun isImage(): Boolean {
        return isStatus() && !isVideo()
    }

    fun getDate(): String {

        val diff = (System.currentTimeMillis() - modified)/1000
        return when  {
            diff < 60 ->  "${diff}s"
            diff < 3600 -> "${diff/60}m"
            else ->  "${diff/3600}h"
        }
    }

    fun save(): File {
        val folderName = MyApp.getAppFolder().absolutePath + "/"
        val target = File(folderName, getfileName())
        return File(path).copyTo(target, false, 1024)
    }

    private fun getfileName(): String {
        return path.split("/").last()
    }

    fun getUriToFile(context: Context): Uri {
        //return Uri.fromFile(File(path))
        return FileProvider
            .getUriForFile(
                context, context.
                applicationContext.packageName.toString() + ".provider", File(path)
            )
    }

    fun getMimeType(): String {
        return if (isVideo()) "video/*" else "image/*"
    }

    fun getVideoIntent(context: Context): Intent {
        //buggy in api level 24
        return when {
            Build.VERSION.SDK_INT >= 24 -> {
                val intent = Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(getUriToFile(context), "video/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent
            }

            else -> {
                val uri = Uri.fromFile(getFile())
                val i2 = Intent(Intent.ACTION_VIEW, uri)
                i2.setDataAndType(uri, "video/*")
                i2
            }
        }
    }

    private fun getFile(): File {
        return  File(path);
    }

    fun setImage(view: ImageView) {

        if (isImage()) {
            Picasso.get().load(getFile()).into(view)
            return
        }

        if (isVideo()) {
            view.setImageBitmap(VideoThumbnailCache.getBitmap(path))
            return
        }

    }

    fun setImage(target: Target) {

        if (isImage()) {
            Picasso.get().load(getFile()).into(target)
            return
        }

    }

    private fun getBitmap(): Bitmap? {
        if (isVideo()) return VideoThumbnailCache.getBitmap(path)
        else return BitmapFactory.decodeFile(path)
    }

    fun loadAndCachePalette() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val palette = getBitmap()?.let { Palette.from(it).generate() }
                palette?.let { PaletteCache.save(path, it) }
            } catch (e: Exception) {
                Timber.e("error: ${e.message}")
            }
        }
    }

    fun getShareIntent(context: Context): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, getUriToFile(context))
            type = "image/*"
        }
    }

    companion object {

        //        const val STATUS_FOLDER_PATH: String =  "/DCIM/Camera/"
        const val STATUS_FOLDER_PATH: String =  "/WhatsApp/Media/.Statuses/"

        fun getBlankStatus(): Status {
            return Status("", System.currentTimeMillis());
        }
    }
}