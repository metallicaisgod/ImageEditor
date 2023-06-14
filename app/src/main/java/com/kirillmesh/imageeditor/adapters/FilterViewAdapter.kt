package com.kirillmesh.imageeditor.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Pair
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kirillmesh.imageeditor.databinding.RowFilterViewBinding
import ja.burhanrashid52.photoeditor.PhotoFilter
import java.io.IOException

class FilterViewAdapter(private val mFilterListener: FilterListener) :
    RecyclerView.Adapter<FilterViewAdapter.ViewHolder>() {

    private val mPairList: MutableList<Pair<String, PhotoFilter>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowFilterViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filterPair = mPairList[position]
        with(holder.binding) {
            val fromAsset = getBitmapFromAsset(root.context, filterPair.first)
            filterImageView.setImageBitmap(fromAsset)
            filterNameTextView.text = filterPair.second.name.replace("_", " ")
        }

    }

    override fun getItemCount(): Int {
        return mPairList.size
    }

    inner class ViewHolder(val binding: RowFilterViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                mFilterListener.onFilterSelected(
                    mPairList[layoutPosition].second
                )
            }
        }
    }

    private fun getBitmapFromAsset(context: Context, strName: String): Bitmap? {
        val assetManager = context.assets
        return try {
            val istr = assetManager.open(strName)
            BitmapFactory.decodeStream(istr)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun setupFilters() {
        mPairList.add(Pair("filters/original.jpg", PhotoFilter.NONE))
        mPairList.add(Pair("filters/auto_fix.png", PhotoFilter.AUTO_FIX))
        mPairList.add(Pair("filters/brightness.png", PhotoFilter.BRIGHTNESS))
        mPairList.add(Pair("filters/contrast.png", PhotoFilter.CONTRAST))
        mPairList.add(Pair("filters/documentary.png", PhotoFilter.DOCUMENTARY))
        mPairList.add(Pair("filters/dual_tone.png", PhotoFilter.DUE_TONE))
        mPairList.add(Pair("filters/fill_light.png", PhotoFilter.FILL_LIGHT))
        mPairList.add(Pair("filters/fish_eye.png", PhotoFilter.FISH_EYE))
        mPairList.add(Pair("filters/grain.png", PhotoFilter.GRAIN))
        mPairList.add(Pair("filters/gray_scale.png", PhotoFilter.GRAY_SCALE))
        mPairList.add(Pair("filters/lomish.png", PhotoFilter.LOMISH))
        mPairList.add(Pair("filters/negative.png", PhotoFilter.NEGATIVE))
        mPairList.add(Pair("filters/posterize.png", PhotoFilter.POSTERIZE))
        mPairList.add(Pair("filters/saturate.png", PhotoFilter.SATURATE))
        mPairList.add(Pair("filters/sepia.png", PhotoFilter.SEPIA))
        mPairList.add(Pair("filters/sharpen.png", PhotoFilter.SHARPEN))
        mPairList.add(Pair("filters/temprature.png", PhotoFilter.TEMPERATURE))
        mPairList.add(Pair("filters/tint.png", PhotoFilter.TINT))
        mPairList.add(Pair("filters/vignette.png", PhotoFilter.VIGNETTE))
        mPairList.add(Pair("filters/cross_process.png", PhotoFilter.CROSS_PROCESS))
        mPairList.add(Pair("filters/b_n_w.png", PhotoFilter.BLACK_WHITE))
        mPairList.add(Pair("filters/flip_horizental.png", PhotoFilter.FLIP_HORIZONTAL))
        mPairList.add(Pair("filters/flip_vertical.png", PhotoFilter.FLIP_VERTICAL))
    }

    init {
        setupFilters()
    }
}