package com.kirillmesh.imageeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.SeekBar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kirillmesh.imageeditor.adapters.ColorPickerAdapter
import com.kirillmesh.imageeditor.databinding.FragmentBottomShapesDialogBinding
import ja.burhanrashid52.photoeditor.shape.ShapeType

class ShapeBSDFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {
    private var _binding: FragmentBottomShapesDialogBinding? = null
    private val binding get() = _binding!!

    private var mProperties: Properties? = null

    interface Properties {
        fun onColorChanged(colorCode: Int)
        fun onOpacityChanged(opacity: Int)
        fun onShapeSizeChanged(shapeSize: Int)
        fun onShapePicked(shapeType: ShapeType)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomShapesDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // shape picker
        binding.shapeRadioGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            when (checkedId) {
                R.id.lineRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Line)
                }
                R.id.arrowRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Arrow())
                }
                R.id.ovalRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Oval)
                }
                R.id.rectRadioButton -> {
                    mProperties!!.onShapePicked(ShapeType.Rectangle)
                }
                else -> {
                    mProperties!!.onShapePicked(ShapeType.Brush)
                }
            }
        }
        binding.shapeOpacity.setOnSeekBarChangeListener(this)
        binding.shapeSize.setOnSeekBarChangeListener(this)


        binding.shapeColorsRecyclerView.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(requireContext())
        colorPickerAdapter.setOnColorPickerClickListener(object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                if (mProperties != null) {
                    dismiss()
                    mProperties!!.onColorChanged(colorCode)
                }
            }
        })
        binding.shapeColorsRecyclerView.adapter = colorPickerAdapter
    }

    fun setPropertiesChangeListener(properties: Properties?) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar.id) {
            R.id.shapeOpacity -> if (mProperties != null) {
                mProperties!!.onOpacityChanged(i)
            }
            R.id.shapeSize -> if (mProperties != null) {
                mProperties!!.onShapeSizeChanged(i)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}