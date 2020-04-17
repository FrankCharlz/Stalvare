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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mj.stalvarestatussaver.R
import com.mj.stalvarestatussaver.Status
import com.mj.stalvarestatussaver.StatusItem
import com.mj.stalvarestatussaver.fragment.TabbedStatusActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private lateinit var mListView: RecyclerView
    private var mListStyleGrid: Boolean = true
    private lateinit var mStatuses: List<StatusItem>
    private lateinit var mFastAdapter: FastAdapter<StatusItem>
    private lateinit var mItemsAdapter: ItemAdapter<StatusItem>
    private lateinit var mAdView: AdView
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mListView = recycler_view;

        mItemsAdapter = ItemAdapter();
        mFastAdapter = FastAdapter.with(mItemsAdapter)

        mListView.layoutManager = StaggeredGridLayoutManager(resources.getInteger(R.integer.grid_span_count), 1)
        mListView.adapter = mFastAdapter

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

        val path = Environment.getExternalStorageDirectory()?.absolutePath + Status.STATUS_FOLDER_PATH

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
                Status(
                    it.absolutePath,
                    it.lastModified()
                )
            }
            .filter { it.isStatus() }
            .map { StatusItem(it) }
            .sortedByDescending { it.status.modified }

        mItemsAdapter.add(mStatuses)

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
                switchListStyle()
                false
            }

            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                false
            }
            R.id.save_all -> {
                saveAll();
                false
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveAll() {
        //// TODO: 17-Apr-20 show dialog
        var count = 0;
        for (status in mStatuses) {
            try {
                status.status.save()
                count++
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show();
            }
        }

        Snackbar.make(content, "$count statuses saved successfuly", Snackbar.LENGTH_SHORT).show()
        loadAdInterstitial();

    }

    private fun loadAdInterstitial() {

        mInterstitialAd =  InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        val adRequest =  AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdLoaded() {
                super.onAdLoaded()
                mInterstitialAd.show()
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                Timber.e("Ad failed to load ${mInterstitialAd.adUnitId}")
            }
        }
    }

    private fun switchListStyle() {
        mListView.layoutManager = if (mListStyleGrid) LinearLayoutManager(this)
        else StaggeredGridLayoutManager(resources.getInteger(R.integer.grid_span_count), 1)

        mListStyleGrid = !mListStyleGrid
    }

}
