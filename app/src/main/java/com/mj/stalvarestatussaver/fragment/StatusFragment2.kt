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
import timber.log.Timber
import java.text.FieldPosition

class StatusFragment2(position: Int) : Fragment() {

    private lateinit var mStatus: Status
    private lateinit var mRoot: View
    private lateinit var mImageView: ImageView

    private var mPosition: Int = position

    init {
        Timber.e("received pos: $position")
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

        return mRoot
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mStatus = vm.mStatuses[mPosition]
        mStatus.setImage(imageView)
        vm.setCurrentStatus(mStatus)
    }


}