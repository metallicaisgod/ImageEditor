package com.kirillmesh.imageeditor.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.kirillmesh.imageeditor.R
import com.kirillmesh.imageeditor.databinding.ColorPickerItemListBinding
import com.kirillmesh.imageeditor.databinding.ColorPickerItemListCheckedBinding

/**
 * Created by Ahmed Adel on 5/8/17.
 */
class ColorPickerAdapter internal constructor(
    private var context: Context,
    colorPickerColors: List<Int>
) : RecyclerView.Adapter<ColorPickerAdapter.ColorViewHolder>() {

    private val colorPickerColors: List<Int>
    private var currentColor = ContextCompat.getColor((context), R.color.black)

    private lateinit var onColorPickerClickListener: OnColorPickerClickListener

    internal constructor(context: Context) : this(context, getDefaultColors(context)) {
        this.context = context
    }

    init {
        this.colorPickerColors = colorPickerColors
    }

    class ColorViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {

        val layoutId = when(viewType) {
            CHECKED_ITEM -> R.layout.color_picker_item_list_checked
            UNCHECKED_ITEM -> R.layout.color_picker_item_list
            else -> R.layout.color_picker_item_list
        }

        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {

        val colorId = colorPickerColors[position]
        val binding = holder.binding
        binding.root.setOnClickListener{
            onColorPickerClickListener.onColorPickerClickListener(
                    colorPickerColors[position]
                )
            currentColor = colorPickerColors[position]
            notifyDataSetChanged()

        }
        when(binding){
            is ColorPickerItemListBinding -> {
                binding.colorPickerView.setBackgroundColor(colorId)
            }
            is ColorPickerItemListCheckedBinding -> {
                binding.colorPickerView.setBackgroundColor(colorId)
            }
        }
    }

    override fun getItemCount(): Int {
        return colorPickerColors.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (colorPickerColors[position] == currentColor) {
            CHECKED_ITEM
        } else {
            UNCHECKED_ITEM
        }
    }

    fun setOnColorPickerClickListener(onColorPickerClickListener: OnColorPickerClickListener) {
        this.onColorPickerClickListener = onColorPickerClickListener
    }

    fun setCurrentColor(colorId: Int){
        if(colorId != Int.MAX_VALUE) {
            currentColor = colorId
        }
    }

    interface OnColorPickerClickListener {
        fun onColorPickerClickListener(colorCode: Int)
    }

    companion object {

        private const val CHECKED_ITEM = 1001
        private const val UNCHECKED_ITEM = 1002

        fun getDefaultColors(context: Context): List<Int> {
            val colorPickerColors = ArrayList<Int>()
            colorPickerColors.add(ContextCompat.getColor((context), R.color.blue_color_picker))
            colorPickerColors.add(ContextCompat.getColor((context), R.color.brown_color_picker))
            colorPickerColors.add(ContextCompat.getColor((context), R.color.green_color_picker))
            colorPickerColors.add(ContextCompat.getColor((context), R.color.orange_color_picker))
            colorPickerColors.add(ContextCompat.getColor((context), R.color.red_color_picker))
            colorPickerColors.add(ContextCompat.getColor((context), R.color.black))
            colorPickerColors.add(
                ContextCompat.getColor(
                    (context),
                    R.color.red_orange_color_picker
                )
            )
            colorPickerColors.add(
                ContextCompat.getColor(
                    (context),
                    R.color.sky_blue_color_picker
                )
            )
            colorPickerColors.add(ContextCompat.getColor((context), R.color.violet_color_picker))
            colorPickerColors.add(ContextCompat.getColor((context), R.color.white))
            colorPickerColors.add(ContextCompat.getColor((context), R.color.yellow_color_picker))
            colorPickerColors.add(
                ContextCompat.getColor(
                    (context),
                    R.color.yellow_green_color_picker
                )
            )
            return colorPickerColors
        }
    }


}