package com.mj.stalvarestatussaver.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.mj.stalvarestatussaver.R
import com.mj.stalvarestatussaver.SharedViewModel
import com.mj.stalvarestatussaver.StatusData
import com.mj.stalvarestatussaver.StatusItem
import kotlinx.android.synthetic.main.activity_tabbed_status.*
import timber.log.Timber

class TabbedStatusActivity : AppCompatActivity() {

    private lateinit var mStatuses: ArrayList<StatusData>

    //private lateinit var vm.mStatuses: List<StatusItem>
    private lateinit var context: Context

    val vm by lazy {
        ViewModelProvider(this).get(SharedViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed_status)

        context = this

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

            context.startActivity(Intent.createChooser(shareIntent, context.resources.getText(
                R.string.send_to
            )))
        }


        val position = intent.getIntExtra(KEY_POSITION, 0)


        val statuses: ArrayList<StatusData> =  intent.getParcelableArrayListExtra(KEY_STATUSES) ?: arrayListOf()

        if (statuses.isEmpty()) {
            Toast.makeText(this, "Could not load status", Toast.LENGTH_SHORT).show();
            finish()
            return
        }

        if (position < statuses.size) {
            statuses.add(0, statuses.removeAt(position))
        }

        vm.mStatuses = statuses

        Timber.d("pos: $position")
        Timber.d("statuses $mStatuses")


        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

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

    companion object {

        const val KEY_POSITION: String = "position";
        const val KEY_STATUSES: String = "statuses";

        fun start(
            context: Context,
            position: Int,
            statuses: ArrayList<StatusData>
        ) {
            val intent = Intent(context, TabbedStatusActivity::class.java)
            intent.putExtra(KEY_POSITION, position)
            intent.putExtra(KEY_STATUSES, statuses)
            context.startActivity(intent);
        }
    }

}