package tz.or.nhif.nhifauth.views

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mj.stalvarestatussaver.R

class CustomToast(context: Context): Toast(context) {

    enum class Type(val color: Int) {
        SUCCESS(R.color.primaryColor),
        ALERT(R.color.primaryColor),
        WARNING(R.color.brown),
        ERROR(R.color.red)
    }

    private val activity: Context = context
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    val layout: View = inflater.inflate(R.layout.custom_toast, null)
    private val container: LinearLayout = layout.findViewById(R.id.custom_toast_container)
    private val tv: TextView = container.findViewById(R.id.text)


    init {
        this.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        this.view = layout
        type(Type.SUCCESS)
    }

    fun text(text: String): CustomToast {
        tv.text = text
        return this
    }

    fun text(resId: Int): CustomToast {
        tv.text = activity.getString(resId)
        return this
    }

    fun type(type: Type): CustomToast {
        container.setBackgroundColor(ContextCompat.getColor(this.container.context, type.color))
        return this
    }

    fun show(durationLong: Boolean = false) {
        this.duration = if (durationLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        this.show()
    }
}