package com.mj.stalvarestatussaver

import android.app.Application
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.*
import java.io.File

class StatusViewModel(app: Application) : AndroidViewModel(app) {

    private val _index = MutableLiveData<StatusData>()
    private val _statuses = MutableLiveData<List<StatusItem>>()

    private val context = app.applicationContext


    val text: LiveData<StatusData> = Transformations.map(_index) {
        it
    }

    fun setIndex(status: StatusData?) {
        _index.value = status
    }

    fun loadStatuses() {

        val path = Environment.getExternalStorageDirectory()?.absolutePath + MainActivity.STATUS_FOLDER_PATH

        val directory = File(path)


        if (!directory.isDirectory)   {
            Toast.makeText(context, "$path is not a valid directory.", Toast.LENGTH_SHORT).show();
            return
        }

        val files = directory.listFiles()

        if (files == null) {
            Toast.makeText(context, "Could not load statuses.", Toast.LENGTH_SHORT).show();
            return
        }

        val statuses = files
            .map { StatusData(it.absolutePath, it.lastModified()) }
            .filter { it.isStatus() }
            .map { StatusItem(it) }
            .sortedByDescending { it.status.modified }


        _statuses.value = statuses

    }
}