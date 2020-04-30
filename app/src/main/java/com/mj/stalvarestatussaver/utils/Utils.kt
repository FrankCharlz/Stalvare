package com.mj.stalvarestatussaver.utils

import android.graphics.Color

fun Int.setTransparency(transparency: Float): Int {
    var alpha = Color.alpha(this)
    val red = Color.red(this)
    val green = Color.green(this)
    val blue = Color.blue(this)
    alpha = (alpha * transparency).toInt()
    return Color.argb(alpha, red, green, blue)
}