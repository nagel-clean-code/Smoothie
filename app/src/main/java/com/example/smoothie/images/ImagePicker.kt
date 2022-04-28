package com.example.smoothie.images

import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import com.example.smoothie.domain.usecase.LoadImageFromGallery

class ImagePicker() {
    fun setupLoadFromGallery(
        activityResultRegistry: ActivityResultRegistry,
        callback: (imageUri: Uri?) -> Unit
    ) {
        val loadImageFromGallery = LoadImageFromGallery(activityResultRegistry, callback)
        loadImageFromGallery.pickImage()
    }

}