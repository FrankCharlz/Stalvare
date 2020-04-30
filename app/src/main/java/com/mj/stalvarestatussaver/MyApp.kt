package com.mj.stalvarestatussaver

import android.app.Application
import android.os.Environment
import com.tumblr.remember.Remember
import timber.log.Timber
import java.io.File

class MyApp: Application() {


    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        Remember.init(this, packageName + "_preferences")

        Timber.e("at ${System.currentTimeMillis()}")
    }


    companion object {

        const val APP_FOLDER_NAME: String = "/Stalvares"


        fun getAppFolder(): File {

            val path = Environment.getExternalStorageDirectory().absolutePath + APP_FOLDER_NAME

            val f = File(path)

            if (!f.exists() || !f.isDirectory) {
                f.mkdirs()
            }

            return f;


        }
    }
}