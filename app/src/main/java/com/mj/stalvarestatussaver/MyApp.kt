package com.mj.stalvarestatussaver

import android.app.Application
import android.os.Environment
import timber.log.Timber
import java.io.File

class MyApp: Application() {


    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
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