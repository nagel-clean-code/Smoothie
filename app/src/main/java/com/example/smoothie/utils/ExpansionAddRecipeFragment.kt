package com.example.smoothie.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import androidx.core.graphics.drawable.toDrawable
import com.example.smoothie.presentation.AddRecipeFragment
import java.io.ByteArrayOutputStream

fun AddRecipeFragment.decodeFromBase64IntoDrawable(imageString: String): Drawable {
    val decodedString: ByteArray = Base64.decode(imageString, Base64.DEFAULT)
    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    return decodedByte.toDrawable(this.resources)
}

fun AddRecipeFragment.encodeToBase64(imageDrawable: Drawable): String {
    val image: Bitmap = (imageDrawable as BitmapDrawable).bitmap
    val baos = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.PNG, 100, baos)
    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}