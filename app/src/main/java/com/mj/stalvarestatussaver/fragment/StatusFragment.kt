package com.mj.stalvarestatussaver.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.mj.stalvarestatussaver.*
import com.mj.stalvarestatussaver.utils.PaletteCache
import com.mj.stalvarestatussaver.utils.VideoThumbnailCache
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import timber.log.Timber

class StatusFragment : Fragment() {

    private lateinit var mImageView: ImageView
    private lateinit var mStatus: StatusData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStatus = arguments?.getParcelable<StatusData>(
            SECTION_STATUS
        ) ?: StatusData.getBlankStatus()
    }

    val vm by lazy {
        activity?.let { ViewModelProvider(it).get(SharedViewModel::class.java) } ?: throw Exception("Activity not defined")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_tabbed_status, container, false)

//        mImageView = root.findViewById<ImageView>(R.id.imageView)
//        Picasso.get().load(mStatus.path).fit().into(PicassoTarget())


        val imageView = root.findViewById<ImageView>(R.id.imageView)
        if (mStatus.isVideo()) {
            imageView.setImageBitmap(
                VideoThumbnailCache.getBitmap(
                    mStatus.path
                )
            )
        } else {
            Picasso.get().load(mStatus.path).fit().into(imageView)
        }

        vm.setCurrentStatus(mStatus)

        return root
    }


    inner class PicassoTarget: Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            TODO("Not yet implemented")
        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
            Timber.e("Failed to load bitmap: ${e?.message}")
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            mImageView.setImageBitmap(bitmap)

            if (PaletteCache.has(mStatus.path)) {
                updateColors(PaletteCache.get(mStatus.path))
            } else {
                Palette.from(BitmapFactory.decodeFile(mStatus.path)).generate {
                    if (it == null) {
                        Timber.e("Could not generate palette")
                        return@generate
                    }

                    PaletteCache.save(
                        mStatus.path,
                        it
                    ) //caching
                    updateColors(it);
                }
            }
        }
    }

    private fun updateColors(palette: Palette) {

        val titleColor: Int = palette.vibrantSwatch?.titleTextColor ?: resources.getColor(
            R.color.primaryLightColor
        )

        val c1 = Color.parseColor("#000000")
        val c2 = Color.parseColor("#ffffff")
        var c1_ = palette.getDarkVibrantColor(c1)
        val c2_ = palette.getLightVibrantColor(c2)

        Timber.i("shall update colors")

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