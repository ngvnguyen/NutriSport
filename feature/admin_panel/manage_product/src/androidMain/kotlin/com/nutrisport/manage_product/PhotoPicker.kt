package com.nutrisport.manage_product

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.gitlive.firebase.storage.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PhotoPicker {
    private var openPhotoPicker by mutableStateOf(false)

    actual fun open(){
        openPhotoPicker = true
    }

    @Composable
    actual fun InitializePhotoPicker(
        onImageSelected:(File?)->Unit
    ){

        val pickMediaResult = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ){uri->
            onImageSelected(uri?.let { File(it) })
            openPhotoPicker = false
        }

        LaunchedEffect(openPhotoPicker) {
            if (openPhotoPicker){
                pickMediaResult.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }



}