package com.mj.stalvarestatussaver

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView

class FullDialog(
    title: String,
    data: StatusData,
    type: Type,
    finishOnDismiss: Boolean
) : DialogFragment() {

    enum class Type(val color: Int) {
        //        SUCCESS(R.color.success),
        NORMAL(R.color.primaryColor),
//        WARNING(R.color.warning),
//        ERROR(R.color.error)
    }

    private lateinit var mToolbar: Toolbar
    private lateinit var mRecyclerView: RecyclerView

    private val mData = data
    private val mTitle = title
    private val mType = type
    private val mFinishOnDismiss = finishOnDismiss

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.status_dialog, container, false)

        val imageView = view.findViewById<ImageView>(R.id.imageView);

        mData.setImage(imageView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    inner class Coo: Toolbar.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            dismiss()
            return true
        }
    }

    override fun onStart() {
        super.onStart()
        val size = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(size, size)
        dialog?.window?.setWindowAnimations(R.style.FullScreenDialog_Animations)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (mFinishOnDismiss) activity?.finish()
    }

    companion object {
        const val TAG: String = "ResultFragment"

        fun newInstance(
            activity: AppCompatActivity,
            title: String,
            data: StatusData,
            type: Type = Type.NORMAL,
            finishOnDismiss: Boolean = false
        ) {
            val dialog = FullDialog(title, data, type, finishOnDismiss)
            dialog.show(activity.supportFragmentManager, TAG)
        }
    }
}