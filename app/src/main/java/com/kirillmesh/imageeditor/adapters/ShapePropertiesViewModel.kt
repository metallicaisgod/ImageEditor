package com.kirillmesh.imageeditor.adapters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kirillmesh.imageeditor.R
import ja.burhanrashid52.photoeditor.shape.ShapeType

class ShapePropertiesViewModel(application: Application) : AndroidViewModel(application) {

    private val _shapeProperties = MutableLiveData<ShapeProperties>()
    val shapeProperties: LiveData<ShapeProperties>
        get() = _shapeProperties

    private var currentShapeProperties = ShapeProperties(
        colorCode = getApplication<Application>().applicationContext.getColor(R.color.black)
    )

    fun getCurrentShapeProperties(){
        _shapeProperties.value = currentShapeProperties
    }

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