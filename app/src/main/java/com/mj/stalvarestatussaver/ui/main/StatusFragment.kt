package com.mj.stalvarestatussaver.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mj.stalvarestatussaver.R
import com.mj.stalvarestatussaver.StatusData
import com.mj.stalvarestatussaver.StatusViewModel
import timber.log.Timber
import java.lang.Exception

class StatusFragment : Fragment() {

    val vm by lazy {
        activity?.let {
            ViewModelProvider(it).get(StatusViewModel::class.java).apply {
                setIndex(arguments?.getParcelable<StatusData>(SECTION_STATUS))
            }
        } ?: throw Exception("Activity context not found...")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_status, container, false)

        val imageView = root.findViewById<ImageView>(R.id.imageView)

        vm.text.observe(activity!!, Observer<StatusData> {
            Timber.e("observed: $it")

            Glide
                .with(imageView.context)
                .load(it.path)
                .into(imageView)

        })

        return root
    }

    companion object {

        private const val SECTION_STATUS = "sectionStatus"

        @JvmStatic
        fun newInstance(status: StatusData): StatusFragment {
            return StatusFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SECTION_STATUS, status)
                }
            }
        }
    }
}