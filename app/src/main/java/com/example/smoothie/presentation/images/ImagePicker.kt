package com.example.smoothie.presentation.images

import android.net.Uri
import androidx.activity.result.ActivityResultRegistry

class ImagePicker() {
    fun setupLoadFromGallery(
        activityResultRegistry: ActivityResultRegistry,
        callback: (imageUri: Uri?) -> Unit
    ) = LoadImageFromGallery(activityResultRegistry, callback).pickImage()
}