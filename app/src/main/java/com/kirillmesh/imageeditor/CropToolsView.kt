package com.kirillmesh.imageeditor

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.kirillmesh.imageeditor.databinding.CropToolsBinding

class CropToolsView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var onButtonsClickListener: OnButtonsClickListener? = null

    private var binding: CropToolsBinding

    interface OnButtonsClickListener {

        fun onRotate()
        fun onDone()
    }

    init {

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.crop_tools, this, true)
        binding = CropToolsBinding.bind(this)

        binding.rotateImageView.setOnClickListener {
            onButtonsClickListener?.onRotate()
        }
        binding.doneTextView.setOnClickListener {
            onButtonsClickListener?.onDone()
        }
    }

    fun setOnButtonsClickListener(listener: OnButtonsClickListener) {
        onButtonsClickListener = listener
    }
}