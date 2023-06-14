package com.kirillmesh.imageeditor

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.kirillmesh.imageeditor.adapters.ColorPickerAdapter
import com.kirillmesh.imageeditor.databinding.AddTextDialogBinding

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */
class TextEditorDialogFragment : DialogFragment() {

    private var _binding: AddTextDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var mInputMethodManager: InputMethodManager
    private var mColorCode = 0
    private var mTextEditorListener: TextEditorListener? = null

    interface TextEditorListener {
        fun onDone(inputText: String, colorCode: Int)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddTextDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mInputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //Setup the color picker for text color
        binding.addTextColorPickerRecyclerView.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(requireContext())

        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                mColorCode = colorCode
                binding.addTextEditText.setTextColor(colorCode)
            }
        })

        binding.addTextColorPickerRecyclerView.adapter = colorPickerAdapter

        val arguments = requireArguments()

        binding.addTextEditText.setText(arguments.getString(EXTRA_INPUT_TEXT))
        mColorCode = arguments.getInt(EXTRA_COLOR_CODE)
        binding.addTextEditText.setTextColor(mColorCode)
        mInputMethodManager.showSoftInput(binding.addTextEditText, 0)

        //Make a callback on activity when user is done with text editing
        binding.addTextDoneTextView.setOnClickListener { onClickListenerView ->
            mInputMethodManager.hideSoftInputFromWindow(onClickListenerView.windowToken, 0)
            dismiss()
            val inputText = binding.addTextEditText.text.toString()
            val textEditorListener = mTextEditorListener
            if (inputText.isNotEmpty() && textEditorListener != null) {
                textEditorListener.onDone(inputText, mColorCode)
            }
        }
    }

    //Callback to listener if user is done with text editing
    fun setOnTextEditorListener(textEditorListener: TextEditorListener) {
        mTextEditorListener = textEditorListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val TAG: String = TextEditorDialogFragment::class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"

        //Show dialog with provide text and text color
        //Show dialog with default text input as empty and text color white
        @JvmOverloads
        fun show(
            fragmentActivity: FragmentActivity,
            inputText: String = "",
            @ColorInt colorCode: Int = ContextCompat.getColor(fragmentActivity, R.color.white)
        ): TextEditorDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(fragmentActivity.supportFragmentManager, TAG)
            return fragment
        }
    }
}