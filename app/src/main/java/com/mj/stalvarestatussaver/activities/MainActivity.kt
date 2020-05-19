package com.mj.stalvarestatussaver.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mj.stalvarestatussaver.R
import com.mj.stalvarestatussaver.Status
import com.mj.stalvarestatussaver.StatusItem
import com.mj.stalvarestatussaver.fragment.TabbedStatusActivity
import com.tumblr.remember.Remember
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


class MainActivity : BaseActivity() {

    private lateinit var mListView: RecyclerView
    private lateinit var mStatuses: List<StatusItem>
    private lateinit var mFastAdapter: FastAdapter<StatusItem>
    private lateinit var mItemsAdapter: ItemAdapter<StatusItem>
    private lateinit var mAdView: AdView
    private lateinit var mInterstitialAd: InterstitialAd


    override fun onResume() {
        super.onResume()
        Timber.e("resumed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.e("created")
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = " All statuses"

        //logo animation
        animate()

        mListView = recycler_view;
        setLayoutManager(Remember.getBoolean(LIST_STYLE, true))

        mItemsAdapter = ItemAdapter();
        mFastAdapter = FastAdapter.with(mItemsAdapter)

        mListView.layoutManager = StaggeredGridLayoutManager(resources.getInteger(R.integer.grid_span_count), 1)
        mListView.adapter = mFastAdapter

        mFastAdapter.onClickListener = { view, adapter, item, position ->
            TabbedStatusActivity.start(this, position, ArrayList(mStatuses.map { it.status }))
            false
        }


        mAdView = findViewById(R.id.adView)
        mAdView.loadAd(AdRequest.Builder().build())
        loadAdInterstitial()

        loadFilesWithPermission()
    }

    private fun loadFilesWithPermission() {

        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    loadFiles()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    Toast.makeText(this@MainActivity, getString(R.string.on_perm_denied), Toast.LENGTH_SHORT).show();
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@MainActivity, getString(R.string.on_perm_denied), Toast.LENGTH_SHORT).show();
                }

            }).check()
    }

    private fun loadFiles() {
        CoroutineScope(Dispatchers.IO).launch {
            val res = loadListOfFiles()

            CoroutineScope(Dispatchers.Main).launch {
                when  {
                    res.error != null -> {
                        Toast.makeText(this@MainActivity, "Could not load statuses", Toast.LENGTH_SHORT).show();
                        Timber.i("${res.error}")
                    }

                    res.statuses.isNullOrEmpty() -> {
                        Toast.makeText(this@MainActivity, "Could not load statuses.", Toast.LENGTH_SHORT).show();
                        Timber.i("Statuses null/empty")
                    }

                    else -> showStatuses(res.statuses)
                }
            }


        }
    }

    private fun animate() {
        val delayMillis: Long = 2000L

        val anim = AlphaAnimation(0.55f, 1.0f)
        anim.startOffset = 30
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        anim.duration = 500

        Timber.e("start animation: ${System.currentTimeMillis()}")
        logo.startAnimation(anim)


        val zoom = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.fade_out
        )

        zoom.duration = 800L
        zoom.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) { placeholder_view.visibility = View.GONE }
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        placeholder_view.postDelayed({placeholder_view.startAnimation(zoom)}, delayMillis);

    }

    private fun showStatuses(statuses: List<Status>) {
        mStatuses = statuses.map { StatusItem(it) }
        mItemsAdapter.add(mStatuses)
    }

    private fun setLayoutManager(listStyleGrid: Boolean) {
        Timber.e("list is grid: $listStyleGrid")
        mListView.layoutManager = if (listStyleGrid)
            StaggeredGridLayoutManager(resources.getInteger(R.integer.grid_span_count), 1)
        else
            LinearLayoutManager(this)
    }


    data class Resource(val statuses: List<Status>?, val error: String?)
    private  fun loadListOfFiles(): Resource  {

        val path = Environment.getExternalStorageDirectory()?.absolutePath + Status.STATUS_FOLDER_PATH
        val directory = File(path)

        if (!directory.isDirectory)   {
            return Resource(null, "$path is not a valid directory.")
        }

        val files = directory.listFiles()

        if (files == null) {
            Timber.e("error 2: ${directory.absolutePath}");
            return Resource(null, "Could not load statuses.")

        }

        val statuses = files
            .map { Status(it.absolutePath, it.lastModified()) }
            .filter { it.isStatus() }
            .sortedByDescending { it.modified }

        statuses.forEach { it.loadAndCachePalette() }
        return Resource(statuses, null)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val listStyleGrid = Remember.getBoolean(LIST_STYLE, true)
        menu.findItem(R.id.tabs)?.icon = resources
            .getDrawable(if (listStyleGrid) R.drawable.ic_dehaze_black_24dp else R.drawable.ic_dashboard_black_24dp)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.tabs -> {
                val listStyleGrid = Remember.getBoolean(LIST_STYLE, true)
                Remember.putBoolean(LIST_STYLE, !listStyleGrid)
                setLayoutManager(listStyleGrid)
                item.icon = resources
                    .getDrawable(if (listStyleGrid) R.drawable.ic_dehaze_black_24dp else R.drawable.ic_dashboard_black_24dp)
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

        val progress = ProgressDialog(this)
        progress.setMessage("Saving, please wait...")
        progress.isIndeterminate = true
        progress.show()


        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
            loadAdInterstitial() //load another..
        } else {
            Timber.i("The interstitial wasn't loaded yet.");
        }

        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show();

        CoroutineScope(Dispatchers.IO).launch {
            var count = 0;
            val exceptions = arrayListOf<Exception>()
            for (status in mStatuses) {
                try {
                    status.status.save()
                    count++
                } catch (e: Exception) {
                    exceptions.add(e)
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                progress.dismiss()

                var message = "$count statuses saved successfully. "
                if (exceptions.isNotEmpty()) {
                    message += "${exceptions.size} statuses could not be saved: ${exceptions[0].localizedMessage}"
                }

                Snackbar.make(content, message, Snackbar.LENGTH_LONG).show()
            }
        }


    }

    private fun loadAdInterstitial() {

        mInterstitialAd =  InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        val adRequest =  AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdLoaded() {
                super.onAdLoaded()
                Timber.d("Interstitial loaded")

                //show with probability 17% of the time
                val r = java.util.Random()
                if (r.nextInt(100) < 17) mInterstitialAd.show()

            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                Timber.e("Ad failed to load ${mInterstitialAd.adUnitId}")
            }
        }
    }

    companion object {
        const val LIST_STYLE: String = "listStyle"
        const val RQ_READ_FILES: Int = 7
    }


}
