package com.kirillmesh.imageeditor.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kirillmesh.imageeditor.R
import com.kirillmesh.imageeditor.databinding.RowEditingToolsBinding

class EditingToolsAdapter(private val mOnItemSelected: OnItemSelected) :
    RecyclerView.Adapter<EditingToolsAdapter.ViewHolder>() {
    private val mToolList: MutableList<ToolModel> = ArrayList()

    interface OnItemSelected {
        fun onToolSelected(toolType: ToolType)
    }

    internal inner class ToolModel(
        val mToolName: String,
        val mToolIcon: Int,
        val mToolType: ToolType
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowEditingToolsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mToolList[position]
        with(holder.binding) {
            toolNameTextView.text = item.mToolName
            toolIconImageView.setImageResource(item.mToolIcon)
        }
    }

    override fun getItemCount(): Int {
        return mToolList.size
    }

    inner class ViewHolder(val binding: RowEditingToolsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { _: View? ->
                mOnItemSelected.onToolSelected(
                    mToolList[layoutPosition].mToolType
                )
            }
        }
    }

    init {
        mToolList.add(ToolModel("Crop", R.drawable.ic_crop, ToolType.CROP))
        mToolList.add(ToolModel("Shape", R.drawable.ic_oval, ToolType.SHAPE))
        mToolList.add(ToolModel("Text", R.drawable.ic_text, ToolType.TEXT))
        mToolList.add(ToolModel("Eraser", R.drawable.ic_eraser, ToolType.ERASER))
        mToolList.add(ToolModel("Filter", R.drawable.ic_photo_filter, ToolType.FILTER))
        mToolList.add(ToolModel("Emoji", R.drawable.ic_insert_emoticon, ToolType.EMOJI))
        mToolList.add(ToolModel("Sticker", R.drawable.ic_sticker, ToolType.STICKER))
    }
}