package com.mj.stalvarestatussaver

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import com.mj.stalvarestatussaver.ui.main.StatusFragment
import java.io.File

class TabbedStatusActivity : AppCompatActivity() {

    private lateinit var mStatuses: List<StatusItem>
    private lateinit var context: Context

    val vm by lazy {
        ViewModelProvider(this).get(StatusViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed_status)

        context = this

        loadStatuses();


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)



    }


    inner class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
        FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return StatusFragment.newInstance(mStatuses[position].status)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "Status $position"
        }

        override fun getCount(): Int {
            return mStatuses.size
        }
    }


    fun loadStatuses() {

        val path = Environment.getExternalStorageDirectory()?.absolutePath + MainActivity.STATUS_FOLDER_PATH

        val directory = File(path)


        if (!directory.isDirectory)   {
            Toast.makeText(context, "$path is not a valid directory.", Toast.LENGTH_SHORT).show();
            return
        }

        val files = directory.listFiles()

        if (files == null) {
            Toast.makeText(context, "Could not load statuses.", Toast.LENGTH_SHORT).show();
            return
        }

         mStatuses = files
            .map { StatusData(it.absolutePath, it.lastModified()) }
            .filter { it.isStatus() }
            .map { StatusItem(it) }
            .sortedByDescending { it.status.modified }

    }

}