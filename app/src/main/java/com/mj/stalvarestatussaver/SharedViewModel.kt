package com.mj.stalvarestatussaver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import timber.log.Timber

class SharedViewModel: ViewModel() {


    val currentPalette: MutableLiveData<Palette> = MutableLiveData()

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

    fun setPalette(palette: Palette) {
        this.currentPalette?.value = palette
    }

}
