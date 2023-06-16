package com.kirillmesh.imageeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kirillmesh.imageeditor.adapters.ColorPickerAdapter
import com.kirillmesh.imageeditor.adapters.ShapeProperties
import com.kirillmesh.imageeditor.adapters.ShapePropertiesViewModel
import com.kirillmesh.imageeditor.databinding.FragmentBottomShapesDialogBinding
import ja.burhanrashid52.photoeditor.shape.ShapeType

class ShapeDialogFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {

    private var _binding: FragmentBottomShapesDialogBinding? = null
    private val binding get() = _binding!!

    private val shapePropertiesViewModel by lazy {
        ViewModelProvider(requireActivity())[ShapePropertiesViewModel::class.java]
    }

    private var mShapePropertiesListener: ShapePropertiesListener? = null


    private var currentProperties = ShapeProperties()

    interface ShapePropertiesListener {
        fun done(exitCode: ExitCode)
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

        shapePropertiesViewModel.getCurrentShapeProperties()
        currentProperties = shapePropertiesViewModel.shapeProperties.value ?: ShapeProperties()

        with(binding) {
            when(currentProperties.shapeType) {
                is ShapeType.Brush -> shapeRadioGroup.check(R.id.brushRadioButton)
                is ShapeType.Line -> shapeRadioGroup.check(R.id.lineRadioButton)
                is ShapeType.Arrow -> shapeRadioGroup.check(R.id.arrowRadioButton)
                is ShapeType.Oval -> shapeRadioGroup.check(R.id.ovalRadioButton)
                is ShapeType.Rectangle -> shapeRadioGroup.check(R.id.rectRadioButton)
            }
            shapeRadioGroup.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
                when (checkedId) {
                    R.id.lineRadioButton -> {
                        shapePropertiesViewModel.setShapeType(ShapeType.Line)
                    }
                    R.id.arrowRadioButton -> {
                        shapePropertiesViewModel.setShapeType(ShapeType.Arrow())
                    }
                    R.id.ovalRadioButton -> {
                        shapePropertiesViewModel.setShapeType(ShapeType.Oval)
                    }
                    R.id.rectRadioButton -> {
                        shapePropertiesViewModel.setShapeType(ShapeType.Rectangle)
                    }
                    else -> {
                        shapePropertiesViewModel.setShapeType(ShapeType.Brush)
                    }
                }
            }

            shapeOpacity.progress = currentProperties.opacity
            shapeOpacity.setOnSeekBarChangeListener(this@ShapeDialogFragment)

            shapeSize.progress = currentProperties.shapeSize
            shapeSize.setOnSeekBarChangeListener(this@ShapeDialogFragment)

            shapeColorsRecyclerView.setHasFixedSize(true)

            val colorPickerAdapter = ColorPickerAdapter(requireContext())
            colorPickerAdapter.setCurrentColor(currentProperties.colorCode)

            colorPickerAdapter.setOnColorPickerClickListener(object :
                ColorPickerAdapter.OnColorPickerClickListener {
                override fun onColorPickerClickListener(colorCode: Int) {
                    shapePropertiesViewModel.setColor(colorCode)
                }
            })
            shapeColorsRecyclerView.adapter = colorPickerAdapter

            okImageView.setOnClickListener {
                mShapePropertiesListener?.done(ExitCode.EXIT_OK)
                dismiss()
            }
            closeImageView.setOnClickListener {
                mShapePropertiesListener?.done(ExitCode.EXIT_FAIL)
                dismiss()
            }
        }
    }

    fun setShapePropertiesListener(propertiesListener: ShapePropertiesListener?) {
        mShapePropertiesListener = propertiesListener
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        when (seekBar.id) {
            R.id.shapeOpacity -> shapePropertiesViewModel.setOpacity(i)
            R.id.shapeSize -> shapePropertiesViewModel.setShapeSize(i)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    enum class ExitCode {
        EXIT_OK, EXIT_FAIL
    }

    companion object {

        private val TAG: String = ShapeDialogFragment::class.java.simpleName

        fun show(
            fragmentActivity: FragmentActivity
        ): ShapeDialogFragment {
            val fragment = ShapeDialogFragment()
            fragment.show(fragmentActivity.supportFragmentManager, TAG)
            return fragment
        }
    }
}