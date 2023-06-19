package com.kirillmesh.imageeditor

import android.graphics.Bitmap
import android.graphics.Matrix

object Utils {

    fun resizePhoto(bitmap: Bitmap, inSampleSize: Int): Bitmap {
        if(inSampleSize > 1){
            val w = bitmap.width
            val h = bitmap.height
            val newWidth = w / inSampleSize
            val newHeight = h / inSampleSize
            return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
        }
        return bitmap
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {

        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}