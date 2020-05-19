package com.mj.stalvarestatussaver

import android.app.Application
import android.os.Environment
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tumblr.remember.Remember
import timber.log.Timber
import java.io.File

class MyApp: Application() {


    override fun onCreate() {
        super.onCreate()

        FirebaseAnalytics.getInstance(this);

        Remember.init(this, packageName + "_preferences")

        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else CrashReportingTree())

        startedAt = System.currentTimeMillis()

    }


    class CrashReportingTree: Timber.Tree() {

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            t?.let { FirebaseCrashlytics.getInstance().recordException(it) }
            FirebaseCrashlytics.getInstance().log("$priority -- $tag -- $message")
        }

    }


    companion object {

        var startedAt: Long = 0

        const val APP_FOLDER_NAME: String = "/Status Saver"


        fun getAppFolder(): File {

            val path = Environment.getExternalStorageDirectory().absolutePath + APP_FOLDER_NAME + "/"

            val f = File(path)

            if (!f.exists() || !f.isDirectory) {
                f.mkdirs()
            }

            return f;


        }
    }
}