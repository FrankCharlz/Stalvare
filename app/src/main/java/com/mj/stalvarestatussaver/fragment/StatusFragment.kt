package com.mj.stalvarestatussaver.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.mj.stalvarestatussaver.*
import com.mj.stalvarestatussaver.utils.PaletteCache
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import timber.log.Timber

class StatusFragment : Fragment() {

    private lateinit var mRoot: View
    private lateinit var mImageView: ImageView
    private lateinit var mStatus: Status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStatus = arguments?.getParcelable<Status>(
            SECTION_STATUS
        ) ?: Status.getBlankStatus()
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
//        Picasso.get().load(mStatus.path).fit().into(PicassoTarget())


//        val imageView = root.findViewById<ImageView>(R.id.imageView)
        mStatus.setImage(PicassoTarget())

        vm.setCurrentStatus(mStatus)

        return mRoot
    }


    inner class PicassoTarget: Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            //TODO("Not yet implemented")
        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
            Timber.e("Failed to load bitmap: ${e?.message}")
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

            mImageView.setImageBitmap(bitmap)

            if (PaletteCache.has(mStatus.path)) {
                vm.setPalette(PaletteCache.get(mStatus.path))
                Timber.i("palette loaded from chache")
            } else {
                Timber.i("palette calculating..")

                Palette.from(BitmapFactory.decodeFile(mStatus.path)).generate {
                    if (it == null) {
                        Timber.e("Could not generate palette")
                        return@generate
                    }

                    PaletteCache.save(mStatus.path, it) //caching
                    Timber.i("setting palette in vm")
                    vm.setPalette(it)
                    mRoot.setBackgroundColor(it.getDarkMutedColor(ContextCompat.getColor(mRoot.context, R.color.white)))
                }
            }
        }
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