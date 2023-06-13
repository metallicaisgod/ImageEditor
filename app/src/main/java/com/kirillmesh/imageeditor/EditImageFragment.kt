package com.kirillmesh.imageeditor

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kirillmesh.imageeditor.databinding.FragmentEditImageBinding
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class EditImageFragment : Fragment() {

    private lateinit var mPhotoEditor: PhotoEditor

    private val args by navArgs<EditImageFragmentArgs>()

    private var _binding: FragmentEditImageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditImageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPhotoEditor = PhotoEditor.Builder(requireContext(), binding.photoEditorView)
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk

        // mPhotoEditor.setOnPhotoEditorListener(this)

        binding.photoEditorView.source.setImageBitmap(args.originalBitmap)
        // binding.photoEditorView.source.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.imgNext.setOnClickListener {
            mPhotoEditor.saveAsBitmap(object : OnSaveBitmap {

                override fun onBitmapReady(saveBitmap: Bitmap?) {
                    if (saveBitmap != null) {
                        findNavController().navigate(
                            EditImageFragmentDirections.actionEditImageFragmentToSaveImageFragment(
                                saveBitmap
                            )
                        )
                    }
                }

                override fun onFailure(e: Exception?) {
                    Log.d("EditImageFragment", e?.message.toString())
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}