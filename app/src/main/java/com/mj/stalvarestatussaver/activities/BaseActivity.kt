package com.mj.stalvarestatussaver.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.mj.stalvarestatussaver.MyApp
import timber.log.Timber

@SuppressLint("Registered")
open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val now = System.currentTimeMillis()
        Timber.e("started at $now: ${now - MyApp.startedAt}ms elapsed")
    }
}