package com.mj.stalvarestatussaver

import androidx.lifecycle.ViewModel
import timber.log.Timber

class SharedViewModel: ViewModel() {


    private lateinit var mCurrentStatus: Status

    var mStatuses: ArrayList<Status> = arrayListOf()

    fun getCurrentStatus(): Status {
        Timber.d("getting current status: ${mCurrentStatus.path}")
        return mCurrentStatus
    }

    fun setCurrentStatus(status: Status) {
        Timber.d("setting current status: ${status.path}")
        mCurrentStatus = status
    }

}
