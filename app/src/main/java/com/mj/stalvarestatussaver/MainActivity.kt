package com.mj.stalvarestatussaver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mFastAdapter: FastAdapter<StatusItem>
    private lateinit var mItemsAdapter: ItemAdapter<StatusItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mItemsAdapter = ItemAdapter<StatusItem>();
        mFastAdapter = FastAdapter.with(mItemsAdapter)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = mFastAdapter


        loadListOfFiles()

    }

    private fun loadListOfFiles() {


        val path = Environment.getExternalStorageDirectory()?.absolutePath + STATUS_FOLDER_PATH

        val directory = File(path)

        Timber.i("abs path: ${directory.absolutePath}")

        if (!directory.isDirectory)   {
            Toast.makeText(this, "$path is not a valid directory.", Toast.LENGTH_SHORT).show();
            return
        }

        val files = directory.listFiles()

        if (files == null) {
            Toast.makeText(this, "Could not load statuses.", Toast.LENGTH_SHORT).show();
            return
        }

        val statuses = files
            .map { StatusData(it.absolutePath, it.lastModified())}
            .filter { it.isStatus() }
            .map { StatusItem(it) }
            .sortedByDescending { it.status.modified }

        Timber.i("statuses: $statuses")
        mItemsAdapter.add(statuses)

    }

    companion object {
        const val STATUS_FOLDER_PATH: String = "/WhatsApp/Media/.Statuses/"

        @BindingAdapter("profileImage")
        fun loadImage(view: ImageView, profileImage: String) {
            Glide.with(view.context)
                .load(profileImage)
                .into(view)
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.tabs -> {
                //startActivity(Intent(this, OfflineAuthorizationActivity::class.java))
                false
            }

            R.id.list -> {
                //synchronizeEvents()
                true
            }

            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                false
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
