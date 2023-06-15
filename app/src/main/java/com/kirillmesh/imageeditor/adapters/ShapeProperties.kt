package com.kirillmesh.imageeditor.adapters

import android.os.Parcelable
import ja.burhanrashid52.photoeditor.shape.ShapeType
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ShapeProperties (
    val colorCode: Int = Int.MAX_VALUE,
    val opacity: Int = 255,
    val shapeSize: Int = 25,
    val shapeType: @RawValue ShapeType = ShapeType.Brush
) : Parcelable
