package com.mj.stalvarestatussaver


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem


open class StatusItem(_status : StatusData) : AbstractItem<StatusItem.ViewHolder>() {

    val status = _status

    override val type: Int get() = R.id.item_container
    override val layoutRes: Int get() = R.layout.status_list_item

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<StatusItem>(view) {


        var name: TextView = view.findViewById(R.id.name)
        var image: ImageView = view.findViewById(R.id.imageView)
        var save: ImageView = view.findViewById(R.id.img_save)



        override fun unbindView(item: StatusItem) {

            name.text = item.status.getDate()
            save.setOnClickListener {
                saveStatus(it.context, item.status)
            }

            if (item.status.isImage()) {
                Glide.with(image.context)
                    .load(item.status.path)
                    .centerCrop()
                    .into(image)

                return
            }

            if (item.status.isVideo()) {
                Glide.with(image.context)
                    .load(VideoThumbnailCache.getBitmap(item.status.path))
                    .centerCrop()
                    .into(image)

                return
            }


        }

        override fun bindView(item: StatusItem, payloads: List<Any>) {

        }


        private fun saveStatus(
            context: Context,
            status: StatusData
        ) {

            try {
                val f = status.save()
                Toast.makeText(context, "Status saved at ${f.absolutePath}", Toast.LENGTH_SHORT).show();

                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                val contentUri: Uri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)


            } catch (e: Exception) {
                Toast.makeText(context, "Could not save status: ${e.message}", Toast.LENGTH_SHORT).show();
            }

        }


    }
}