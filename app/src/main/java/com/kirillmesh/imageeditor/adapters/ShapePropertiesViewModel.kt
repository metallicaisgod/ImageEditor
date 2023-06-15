package com.kirillmesh.imageeditor.adapters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ja.burhanrashid52.photoeditor.shape.ShapeType

class ShapePropertiesViewModel : ViewModel() {

    private val _shapeProperties = MutableLiveData(ShapeProperties())
    val shapeProperties: LiveData<ShapeProperties>
        get() = _shapeProperties

    private var currentShapeProperties = ShapeProperties()

    fun setColor(colorCode: Int){
        currentShapeProperties =
            currentShapeProperties.copy(colorCode = colorCode)
        _shapeProperties.value = currentShapeProperties
    }

    fun setOpacity(opacity: Int){
        currentShapeProperties =
            currentShapeProperties.copy(opacity = opacity)
        _shapeProperties.value = currentShapeProperties
    }

    fun setShapeSize(shapeSize: Int){
        currentShapeProperties =
            currentShapeProperties.copy(shapeSize = shapeSize)
        _shapeProperties.value = currentShapeProperties
    }

    fun setShapeType(shapeType: ShapeType){
       currentShapeProperties =
            currentShapeProperties.copy(shapeType = shapeType)
        _shapeProperties.value = currentShapeProperties
    }
}