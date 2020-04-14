package com.mj.stalvarestatussaver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import java.lang.Exception

class StatusFragment : Fragment() {

    private lateinit var mStatus: StatusData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStatus = arguments?.getParcelable<StatusData>(SECTION_STATUS) ?: StatusData.getBlankStatus()
    }


    val vm by lazy {
        activity?.let { ViewModelProvider(it).get(SharedViewModel::class.java) } ?: throw Exception("Activity not defined")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_status, container, false)

        val imageView = root.findViewById<ImageView>(R.id.imageView)

        Glide
            .with(imageView.context)
            .load(mStatus.path)
            .fitCenter()
            //.centerCrop()
            .into(imageView)

        vm.setCurrentStatus(mStatus)

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