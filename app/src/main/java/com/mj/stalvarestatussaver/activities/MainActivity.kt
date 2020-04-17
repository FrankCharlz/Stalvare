package com.mj.stalvarestatussaver.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mj.stalvarestatussaver.R
import com.mj.stalvarestatussaver.StatusData
import com.mj.stalvarestatussaver.StatusItem
import com.mj.stalvarestatussaver.fragment.TabbedStatusActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var mStatuses: List<StatusItem>
    private lateinit var mFastAdapter: FastAdapter<StatusItem>
    private lateinit var mItemsAdapter: ItemAdapter<StatusItem>
    private lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mItemsAdapter = ItemAdapter<StatusItem>();
        mFastAdapter = FastAdapter.with(mItemsAdapter)

        recycler_view.layoutManager = StaggeredGridLayoutManager(2, 1)
        recycler_view.adapter = mFastAdapter

        mFastAdapter.onClickListener = { view, adapter, item, position ->

            TabbedStatusActivity.start(this, position, ArrayList(mStatuses.map { it.status }))
            false
        }

        loadListOfFiles()

        mAdView = findViewById(R.id.adView)
        mAdView.loadAd(AdRequest.Builder().build())

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = " All statuses"

    }

    private fun loadListOfFiles() {


        val path = Environment.getExternalStorageDirectory()?.absolutePath + StatusData.STATUS_FOLDER_PATH

        val directory = File(path)


        if (!directory.isDirectory)   {
            Toast.makeText(this, "$path is not a valid directory.", Toast.LENGTH_SHORT).show();
            return
        }

        val files = directory.listFiles()

        if (files == null) {
            Timber.e("error: ${directory.absolutePath}")
            Toast.makeText(this, "Could not load statuses.", Toast.LENGTH_LONG).show();
            return
        }

        mStatuses = files
            .map {
                StatusData(
                    it.absolutePath,
                    it.lastModified()
                )
            }
            .filter { it.isStatus() }
            .map { StatusItem(it) }
            .sortedByDescending { it.status.modified }

        mItemsAdapter.add(mStatuses)
//        mItemsAdapter.add(mStatuses)
//        mItemsAdapter.add(mStatuses)

    }

    companion object {

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
