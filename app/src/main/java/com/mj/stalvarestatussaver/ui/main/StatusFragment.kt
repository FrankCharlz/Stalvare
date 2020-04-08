package com.mj.stalvarestatussaver.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.mj.stalvarestatussaver.R
import com.mj.stalvarestatussaver.StatusData

class StatusFragment : Fragment() {

    private lateinit var mStatus: StatusData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStatus = arguments?.getParcelable<StatusData>(SECTION_STATUS) ?: StatusData.getBlankStatus()
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
            .into(imageView)

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