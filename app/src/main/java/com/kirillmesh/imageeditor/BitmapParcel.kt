package com.kirillmesh.imageeditor

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BitmapParcel(
    val bitmap: Bitmap
): Parcelable