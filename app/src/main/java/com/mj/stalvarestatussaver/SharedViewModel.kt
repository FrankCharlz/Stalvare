package com.mj.stalvarestatussaver

import androidx.lifecycle.ViewModel
import timber.log.Timber

class SharedViewModel: ViewModel() {


    private lateinit var mCurrentStatus: StatusData

    var mStatuses: ArrayList<StatusData> = arrayListOf()

    fun getCurrentStatus(): StatusData {
        Timber.d("getting current status: ${mCurrentStatus.path}")
        return mCurrentStatus
    }

    fun setCurrentStatus(status: StatusData) {
        Timber.d("setting current status: ${status.path}")
        mCurrentStatus = status
    }

}
