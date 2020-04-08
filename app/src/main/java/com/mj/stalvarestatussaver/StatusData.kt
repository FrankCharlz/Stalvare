package com.mj.stalvarestatussaver

import android.annotation.SuppressLint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class StatusData(
    val path: String,
    val modified: Long

) {

    private fun getExtension(): String {
        //returns extension including .
        if (path.length >= 4) return path.takeLast(4)
        else return ""
    }

    fun isStatus(): Boolean {
        with(getExtension()) {
            return this.length == 4 && this.startsWith(".")
        }
    }

    fun isVideo(): Boolean {
        return getExtension().equals(".mp4", true)
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
        val filename = "${System.currentTimeMillis()}${getExtension()}"
        val target = File(MyApp.getAppFolder(),  filename)
        val savedFile = File(path).copyTo(target, true, 1024)
        return savedFile
    }
}