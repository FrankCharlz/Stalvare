package com.mj.stalvarestatussaver.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mj.stalvarestatussaver.R
import com.mj.stalvarestatussaver.SharedViewModel
import com.mj.stalvarestatussaver.Status
import kotlinx.android.synthetic.main.status_list_item.*

class StatusFragment : Fragment() {

    private lateinit var mRoot: View
    private lateinit var mImageView: ImageView
    private lateinit var mStatus: Status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStatus = arguments?.getParcelable(SECTION_STATUS) ?: Status.getBlankStatus()
    }

    private val vm by lazy {
        activity?.let { ViewModelProvider(it).get(SharedViewModel::class.java) } ?: throw Exception("Activity not defined")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRoot = inflater.inflate(R.layout.fragment_tabbed_status, container, false)

        mImageView = mRoot.findViewById<ImageView>(R.id.imageView)
        mStatus.setImage(imageView)

        vm.setCurrentStatus(mStatus)

        return mRoot
    }


    companion object {

        private const val SECTION_STATUS = "sectionStatus"

        @JvmStatic
        fun newInstance(status: Status): StatusFragment {
            return StatusFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SECTION_STATUS, status)
                }
            }
        }
    }

}