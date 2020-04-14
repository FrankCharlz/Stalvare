package com.mj.stalvarestatussaver

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_tabbed_status.*
import timber.log.Timber
import java.io.File

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


        save.setOnClickListener {
            val status = vm.getCurrentStatus()
            try {
                val f = status.save()
                Toast.makeText(it.context, "Status saved at ${f.absolutePath}", Toast.LENGTH_SHORT).show();
            } catch (e: Exception) {
                Toast.makeText(it.context, "Could not save status ${e.message}", Toast.LENGTH_SHORT).show();
            }
        }


        play.setOnClickListener {
            val status = vm.getCurrentStatus()
            val intent = status.getVideoIntent(it.context)
            startActivity(intent)
        }

        share.setOnClickListener {
            val status = vm.getCurrentStatus()
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                setDataAndType(status.getUriToFile(context), status.getMimeType())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, context.resources.getText(R.string.send_to)))
        }



        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

//        val tabs: TabLayout = findViewById(R.id.tabs)
//        tabs.setupWithViewPager(viewPager)

    }




    inner class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
        FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            Timber.i("pos: $position")
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
            Timber.e("error: ${directory.absolutePath}")
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