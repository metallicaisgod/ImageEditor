package com.kirillmesh.imageeditor

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kirillmesh.imageeditor.databinding.FragmentBottomStickerEmojiDialogBinding
import com.kirillmesh.imageeditor.databinding.RowEmojiBinding

class EmojiBSDFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomStickerEmojiDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var emojisList: ArrayList<String>
    private var behavior: BottomSheetBehavior<*>? = null

    private var mEmojiListener: EmojiListener? = null

    interface EmojiListener {
        fun onEmojiClick(emojiUnicode: String)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
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

        emojisList = getEmojis(requireContext())

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
            val gridLayoutManager = GridLayoutManager(activity, 5)
            emojiRecyclerView.layoutManager = gridLayoutManager
            val emojiAdapter = EmojiAdapter()
            emojiRecyclerView.adapter = emojiAdapter
            emojiRecyclerView.setHasFixedSize(true)
            emojiRecyclerView.setItemViewCacheSize(emojisList.size)
        }
    }

    fun setEmojiListener(emojiListener: EmojiListener?) {
        mEmojiListener = emojiListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        behavior?.removeBottomSheetCallback(mBottomSheetBehaviorCallback)
    }

    private fun getEmojis(context: Context?): ArrayList<String> {
        val convertedEmojiList = ArrayList<String>()
        val emojiList = context!!.resources.getStringArray(R.array.photo_editor_emoji)
        for (emojiUnicode in emojiList) {
            convertedEmojiList.add(convertEmoji(emojiUnicode))
        }
        return convertedEmojiList
    }

    private fun convertEmoji(emoji: String): String {
        return try {
            val convertEmojiToInt = emoji.substring(2).toInt(16)
            String(Character.toChars(convertEmojiToInt))
        } catch (e: NumberFormatException) {
            ""
        }
    }

    inner class EmojiAdapter : RecyclerView.Adapter<EmojiAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = RowEmojiBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.emojiTextView.text = emojisList[position]
        }

        override fun getItemCount(): Int {
            return emojisList.size
        }

        inner class ViewHolder(val binding: RowEmojiBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    if (mEmojiListener != null) {
                        mEmojiListener!!.onEmojiClick(emojisList[layoutPosition])
                    }
                    dismiss()
                }
            }
        }
    }
}