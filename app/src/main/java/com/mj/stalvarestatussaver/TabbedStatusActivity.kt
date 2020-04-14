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
import java.io.File
import java.lang.Exception

class TabbedStatusActivity : AppCompatActivity() {

    //private lateinit var vm.mStatuses: List<StatusItem>
    private lateinit var context: Context

    val vm by lazy {
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed_status)

        context = this

        loadStatuses();


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val status = vm.getCurrentStatus()
            try {
                val f = status.save()
                Toast.makeText(it.context, "Status saved at ${f.absolutePath}", Toast.LENGTH_SHORT).show();
            } catch (e: Exception) {
                Toast.makeText(it.context, "Could not save status ${e.message}", Toast.LENGTH_SHORT).show();
            }
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
            return StatusFragment.newInstance(vm.mStatuses[position])
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "Status $position"
        }

        override fun getCount(): Int {
            return vm.mStatuses.size
        }
    }


    private fun loadStatuses() {

        val path = Environment.getExternalStorageDirectory()?.absolutePath + StatusData.STATUS_FOLDER_PATH

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

        vm.mStatuses.addAll( files
            .map { StatusData(it.absolutePath, it.lastModified()) }
            .filter { it.isStatus() }
            .sortedByDescending { it.modified }
        )

    }

}