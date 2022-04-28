package com.example.smoothie.domain.usecase

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import com.example.smoothie.Constants


class LoadImageFromGallery(
    activityResultRegistry: ActivityResultRegistry,
    callback: (imageUri: Uri?) -> Unit
) {

    // Объект подписки полученный от регистрации на получения результата активити
    private val getContent: ActivityResultLauncher<String> = activityResultRegistry.register(
        REGISTRY_KEY,
        ActivityResultContracts.GetContent(),
        callback
    )

    fun pickImage() {
        getContent.launch(Constants.MIMETYPE_IMAGE)
    }

    private companion object {
        private const val REGISTRY_KEY = "LoadImageFromGallery"
    }
}

