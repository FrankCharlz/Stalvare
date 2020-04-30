package com.mj.stalvarestatussaver


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mj.stalvarestatussaver.utils.PaletteCache
import com.mj.stalvarestatussaver.utils.setTransparency
import timber.log.Timber


open class StatusItem(_status : Status) : AbstractItem<StatusItem.ViewHolder>() {

    val status = _status

    override val type: Int get() = R.id.item_container
    override val layoutRes: Int get() = R.layout.status_list_item

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<StatusItem>(view) {

        var ribbon: RelativeLayout = view.findViewById(R.id.ribbon)
        var name: TextView = view.findViewById(R.id.name)
        var image: ImageView = view.findViewById(R.id.imageView)
        var save: ImageView = view.findViewById(R.id.img_save)
        var play: ImageView = view.findViewById(R.id.play)

        override fun bindView(item: StatusItem, payloads: List<Any>) {

            name.text = "${item.status.getDate()}"

            play.setOnClickListener {
                playStatus(it.context, item.status)
            }
            save.setOnClickListener {
                saveStatus(it.context, item.status)
            }

            item.status.setImage(image)
            play.visibility = if (item.status.isVideo()) View.VISIBLE else View.GONE;

            val palette = PaletteCache.get(item.status.path)?.let {

                it.darkVibrantSwatch?.rgb?.let { it1 -> ribbon.setBackgroundColor(it1.setTransparency(0.5f)) }
                it.darkVibrantSwatch?.bodyTextColor.let {
                        it1 ->
                    it1?.let { it2 -> name.setTextColor(it2) }
                }
            }


        }


        override fun unbindView(item: StatusItem) {
            Timber.d("unbinding")
        }

        private fun shareStatus(context: Context, status: Status) {

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                setDataAndType(status.getUriToFile(context), status.getMimeType())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, context.resources.getText(R.string.send_to)))
        }

        private fun saveStatus(context: Context, status: Status) {

            try {
                val f = status.save()
                Toast.makeText(context, "Status saved at ${f.absolutePath}", Toast.LENGTH_SHORT).show();

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = status.getUriToFile(context)
                context.sendBroadcast(mediaScanIntent)


            } catch (e: Exception) {
                Toast.makeText(context, "Could not save status: ${e.message}", Toast.LENGTH_SHORT).show();
            }

        }

        private fun playStatus(context: Context, status: Status) {
            val intent = status.getVideoIntent(context)
            context.startActivity(intent)
        }


    }
}