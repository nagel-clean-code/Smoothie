package com.example.smoothie.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.core.graphics.drawable.toDrawable
import com.example.smoothie.presentation.BaseFragment
import java.io.ByteArrayOutputStream

fun BaseFragment.decodeFromBase64IntoDrawable(imageString: String): Drawable {
    val decodedString: ByteArray = Base64.decode(imageString, Base64.DEFAULT)
    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    return decodedByte.toDrawable(this.resources)
}

fun BaseFragment.encodeToBase64(imageDrawable: Drawable): String {
    val b = convertDrawableToByteArray(imageDrawable)
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun BaseFragment.convertDrawableToByteArray(imageDrawable: Drawable?): ByteArray{
    if(imageDrawable == null) return "".toByteArray()
    val image: Bitmap = (imageDrawable as BitmapDrawable).bitmap
    val baos = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 50, baos)    //FIXME попробовать ниже качество
    return baos.toByteArray()
}