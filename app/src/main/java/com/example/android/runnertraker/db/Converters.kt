package com.example.android.runnertraker.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {


    @TypeConverter
    fun fromBitmap (bm : Bitmap):ByteArray{
        val outputStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray):Bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
}