package com.mj.stalvarestatussaver

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.mj.stalvarestatussaver.utils.VideoThumbnailCache
import com.squareup.picasso.Picasso
import kotlinx.android.parcel.Parcelize
import java.io.File
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

    @SuppressLint("SimpleDateFormat")
    fun getDate(): String {
        val date = Date(modified)
        val sdf = SimpleDateFormat("dd MMM, yyyy HH:mm")
        return sdf.format(date) ?: "-/-"
    }

    fun save(): File {
        val folderName = MyApp.getAppFolder().absolutePath + "/"
        val targetPath = path.replace(STATUS_FOLDER_PATH, folderName) +"/"
        val target = File(targetPath)
        return File(path).copyTo(target, false, 1024)
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

    fun getImage(): Any {
        return if (isVideo()) VideoThumbnailCache.getBitmap(path) else path
    }

    fun getVideoIntent(context: Context): Intent {
        //buggy in api level 21

        val uri = getUriToFile(context);
        val intent = Intent(Intent.ACTION_VIEW, uri )
        intent.setDataAndType(uri, "video/mp4")
        return intent
    }

    fun getFile(): File {
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

    companion object {

        const val STATUS_FOLDER_PATH: String =  "/DCIM/Camera/"
//        const val STATUS_FOLDER_PATH: String =  "/WhatsApp/Media/.Statuses/"

        fun getBlankStatus(): Status {
            return Status("", System.currentTimeMillis());
        }
    }
}