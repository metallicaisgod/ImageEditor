package com.kirillmesh.imageeditor

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kirillmesh.imageeditor.databinding.FragmentBottomStickerEmojiDialogBinding
import com.kirillmesh.imageeditor.databinding.RowStickerBinding

class StickerBSDFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomStickerEmojiDialogBinding? = null
    private val binding get() = _binding!!

    private var behavior: BottomSheetBehavior<*>? = null

    private var mStickerListener: StickerListener? = null
    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    interface StickerListener {
        fun onStickerClick(bitmap: Bitmap)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_bottom_sticker_emoji_dialog, null)
        _binding = FragmentBottomStickerEmojiDialogBinding.bind(contentView)
        dialog.setContentView(contentView)

        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        behavior = params.behavior as BottomSheetBehavior<*>
        behavior?.addBottomSheetCallback(mBottomSheetBehaviorCallback)

        (contentView.parent as View).setBackgroundColor(
            getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        with(binding) {
            val gridLayoutManager = GridLayoutManager(activity, 3)
            emojiRecyclerView.layoutManager = gridLayoutManager
            val stickerAdapter = StickerAdapter()
            emojiRecyclerView.adapter = stickerAdapter
            emojiRecyclerView.setHasFixedSize(true)
            emojiRecyclerView.setItemViewCacheSize(stickerPathList.size)
        }
    }

    inner class StickerAdapter : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RowStickerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // Load sticker image from remote url
            Glide.with(requireContext())
                .asBitmap()
                .load(stickerPathList[position])
                .into(holder.binding.stickerImageView)
        }

        override fun getItemCount(): Int {
            return stickerPathList.size
        }

        inner class ViewHolder(val binding: RowStickerBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    if (mStickerListener != null) {
                        Glide.with(requireContext())
                            .asBitmap()
                            .load(stickerPathList[layoutPosition])
                            .into(object : CustomTarget<Bitmap?>(256, 256) {
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    transition: Transition<in Bitmap?>?
                                ) {
                                    mStickerListener!!.onStickerClick(resource)
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {}
                            })
                    }
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        behavior?.removeBottomSheetCallback(mBottomSheetBehaviorCallback)
    }

    companion object {
        // Image Urls from flaticon(https://www.flaticon.com/stickers-pack/food-289)
        private val stickerPathList = arrayOf(
            "https://cdn-icons-png.flaticon.com/256/4392/4392452.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392455.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392459.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392462.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392465.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392467.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392469.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392471.png",
            "https://cdn-icons-png.flaticon.com/256/4392/4392522.png",
        )
    }
}