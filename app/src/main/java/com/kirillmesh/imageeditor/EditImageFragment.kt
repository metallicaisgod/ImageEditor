package com.kirillmesh.imageeditor

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.canhub.cropper.CropImageView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kirillmesh.imageeditor.adapters.*
import com.kirillmesh.imageeditor.databinding.FragmentEditImageBinding
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class EditImageFragment : Fragment(), OnPhotoEditorListener, EditingToolsAdapter.OnItemSelected,
    StickerBSDFragment.StickerListener, EmojiBSDFragment.EmojiListener,
    FilterListener {

    private val args by navArgs<EditImageFragmentArgs>()

    private var _binding: FragmentEditImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var mPhotoEditor: PhotoEditor
    private lateinit var mShapeBuilder: ShapeBuilder
    private lateinit var mEmojiBSDFragment: EmojiBSDFragment
    private lateinit var mStickerBSDFragment: StickerBSDFragment
    private lateinit var mShapeDialogFragment: ShapeDialogFragment
    private val mEditingToolsAdapter = EditingToolsAdapter(this)
    private val mFilterViewAdapter = FilterViewAdapter(this)

    private val mConstraintSet = ConstraintSet()
    private var isEnvironmentViewVisible = false to 0

    private val shapePropertiesViewModel by lazy {
        ViewModelProvider(requireActivity())[ShapePropertiesViewModel::class.java]
    }
    private lateinit var shapeProperties: ShapeProperties

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditImageBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        with(binding) {

            mPhotoEditor = PhotoEditor.Builder(requireContext(), photoEditorView)
                .setPinchTextScalable(true) // set flag to make text scalable when pinch
                //.setDefaultTextTypeface(mTextRobotoTf)
                //.setDefaultEmojiTypeface(mEmojiTypeFace)
                .build() // build photo editor sdk

            mPhotoEditor.setOnPhotoEditorListener(this@EditImageFragment)

            photoEditorView.source.setImageBitmap(args.originalBitmap)
            cropImageView.scaleType = CropImageView.ScaleType.CENTER_CROP
            photoEditorView.source.scaleType = ImageView.ScaleType.CENTER_CROP


            nextImageView.setOnClickListener {
                if (isEnvironmentViewVisible.first &&
                    isEnvironmentViewVisible.second == cropButtons.id
                ) {
                    cropImageView.visibility = View.GONE
                    showEnvironment(false, cropButtons.id)
                    photoEditorView.visibility = View.VISIBLE
                }

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
                        Log.d(TAG, e?.message.toString())
                    }
                })
            }

            backImageView.setOnClickListener {

                if (isEnvironmentViewVisible.first) {
                    with(binding) {
                        showEnvironment(false, isEnvironmentViewVisible.second)
                        if (isEnvironmentViewVisible.second == cropButtons.id) {
                            cropImageView.visibility = View.GONE
                            photoEditorView.visibility = View.VISIBLE
                        }
                        currentToolTextView.setText(R.string.app_name)
                    }
//                } else if (!mPhotoEditor.isCacheEmpty) {
//                    showSaveDialog()
                } else {
                    findNavController().navigate(R.id.action_editImageFragment_to_chooseImageFragment)
                }
            }
        }

        shapePropertiesViewModel.getCurrentShapeProperties()
        shapePropertiesViewModel.shapeProperties.observe(viewLifecycleOwner){
            shapeProperties = it
        }

    }

    private fun initViews() {

        mStickerBSDFragment = StickerBSDFragment()
        mStickerBSDFragment.setStickerListener(this)
        mEmojiBSDFragment = EmojiBSDFragment()
        mEmojiBSDFragment.setEmojiListener(this)
        mShapeDialogFragment = ShapeDialogFragment()
        mShapeDialogFragment.setOnShapeDialogFragmentDone(
            object : ShapeDialogFragment.OnShapeDialogFragmentDone{
                override fun onDone() {
                    mPhotoEditor.setBrushDrawingMode(true)
                    mShapeBuilder = ShapeBuilder()
                    mPhotoEditor.setShape(
                        mShapeBuilder.withShapeType(shapeProperties.shapeType)
                            .withShapeOpacity(shapeProperties.opacity)
                            .withShapeColor(shapeProperties.colorCode)
                            .withShapeSize(shapeProperties.shapeSize.toFloat())
                    )
                }
            })

        with(binding) {
            cropButtons.setOnButtonsClickListener(object : CropToolsView.OnButtonsClickListener {
                override fun onRotate() {
                    cropImageView.rotateImage(90)
                }

                override fun onDone() {
                    cropImageView.visibility = View.GONE
                    cropImageView.croppedImageAsync()
                    showEnvironment(false, cropButtons.id)
                    photoEditorView.visibility  = View.VISIBLE
                }

            })
            cropImageView.setOnCropImageCompleteListener { _, result ->
                photoEditorView.source.setImageBitmap(result.bitmap)
            }

            toolsRecyclerView.adapter = mEditingToolsAdapter

            filtersRecyclerView.adapter = mFilterViewAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onToolSelected(toolType: ToolType) {
        with(binding) {
            mPhotoEditor.setBrushDrawingMode(false)
            cropImageView.visibility = View.GONE
            when (toolType) {
                ToolType.SHAPE -> {
                    currentToolTextView.setText(R.string.label_shape)
                    showBottomSheetDialogFragment(mShapeDialogFragment)
                }

                ToolType.TEXT -> {
                    val textEditorDialogFragment =
                        TextEditorDialogFragment.show(requireActivity())
                    textEditorDialogFragment.setOnTextEditorListener(object :
                        TextEditorDialogFragment.TextEditorListener {
                        override fun onDone(inputText: String, colorCode: Int) {
                            val styleBuilder = TextStyleBuilder()
                            styleBuilder.withTextColor(colorCode)
                            mPhotoEditor.addText(inputText, styleBuilder)
                            currentToolTextView.setText(R.string.label_text)
                        }
                    })
                }

                ToolType.ERASER -> {
                    mPhotoEditor.brushEraser()
                    currentToolTextView.setText(R.string.label_eraser_mode)
                }

                ToolType.FILTER -> {
                    currentToolTextView.setText(R.string.label_filter)
                    showEnvironment(true, filtersRecyclerView.id)
                }

                ToolType.CROP -> {
                    currentToolTextView.setText(R.string.label_crop_rotate)
                    photoEditorView.visibility = View.GONE
                    mPhotoEditor.saveAsBitmap(object : OnSaveBitmap {

                        override fun onBitmapReady(saveBitmap: Bitmap?) {
                            cropImageView.setImageBitmap(saveBitmap)
                        }

                        override fun onFailure(e: Exception?) {
                            Log.d(TAG, "Fail on save Bitmap")
                        }
                    })

                    showEnvironment(true, cropButtons.id)
                    cropImageView.visibility = View.VISIBLE
                }

                ToolType.EMOJI -> showBottomSheetDialogFragment(mEmojiBSDFragment)
                ToolType.STICKER -> showBottomSheetDialogFragment(mStickerBSDFragment)
            }
        }
    }

    private fun showBottomSheetDialogFragment(fragment: BottomSheetDialogFragment?) {
        if (fragment == null || fragment.isAdded) {
            return
        }
        fragment.show(requireActivity().supportFragmentManager, fragment.tag)
    }

    private fun showEnvironment(isVisible: Boolean, viewId: Int) {
        isEnvironmentViewVisible = isVisible to viewId
        mConstraintSet.clone(binding.rootView)

        if (isVisible) {
            mConstraintSet.clear(viewId, ConstraintSet.START)
            mConstraintSet.connect(
                viewId, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                viewId, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                viewId, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(viewId, ConstraintSet.END)
        }

        val changeBounds = ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        TransitionManager.beginDelayedTransition(binding.rootView, changeBounds)

        mConstraintSet.applyTo(binding.rootView)
    }

    override fun onEmojiClick(emojiUnicode: String) {
        mPhotoEditor.addEmoji(emojiUnicode)
        binding.currentToolTextView.setText(R.string.label_emoji)
    }

    override fun onStickerClick(bitmap: Bitmap) {
        mPhotoEditor.addImage(bitmap)
        binding.currentToolTextView.setText(R.string.label_sticker)
    }

    override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter)
    }

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
        val textEditorDialogFragment =
            TextEditorDialogFragment.show(requireActivity(), text.toString(), colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object :
            TextEditorDialogFragment.TextEditorListener {
            override fun onDone(inputText: String, colorCode: Int) {
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                mPhotoEditor.editText(binding.rootView, inputText, styleBuilder)
                binding.currentToolTextView.setText(R.string.label_text)
            }
        })
    }

    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType?) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onStopViewChangeListener(viewType: ViewType?) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onTouchSourceImage(event: MotionEvent?) {
        Log.d(TAG, "onTouchView() called with: event = [$event]")
    }

    companion object {

        private const val TAG = "EditImageFragment"
    }
}