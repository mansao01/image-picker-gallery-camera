package com.example.getimagefromcameragallery

import android.Manifest
import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.getimagefromcameragallery.ui.theme.GetImageFromCameraGalleryTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GetImageFromCameraGalleryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageCaptureAndGallery()
                }
            }
        }
    }
}

private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
private const val IMAGE_MIME_TYPE = "image/*"
var file:File? = null

@Composable
fun ImageCaptureAndGallery() {
    val context = LocalContext.current

    val file = context.createImageFile()
    val cameraUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var galleryImageUri by remember { mutableStateOf<Uri?>(null) }
    var isImageSelected by remember { mutableStateOf(false) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                capturedImageUri = cameraUri
                isImageSelected = true
            }
        }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            galleryImageUri = uri
            isImageSelected = true
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(cameraUri)
            } else {
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplaySelectedImage(galleryImageUri ?: capturedImageUri, context)
        Row {

            Button(onClick = {
                capturedImageUri = null
                galleryImageUri = null
                isImageSelected = false
                cameraPermissionLauncher.launch(CAMERA_PERMISSION)
            }) {
                Text(text = stringResource(R.string.capture_image))
            }
            ImagePickerButton(galleryLauncher)
        }
        if (isImageSelected) {
            Button(onClick = {
                capturedImageUri = null
                galleryImageUri = null
                isImageSelected = false
            }) {
                Text(text = "Remove Image")
            }
        }
    }
}


@Composable
fun ImagePickerButton(launcher: ActivityResultLauncher<String>) {
    Button(onClick = {
        launcher.launch(IMAGE_MIME_TYPE)
    }) {
        Text(text = stringResource(R.string.pick_image))
    }
}

@Composable
fun DisplaySelectedImage(imageUri: Uri?, context: Context) {

    if (imageUri != null) {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source)
        }

        file = CameraUtils.uriToFile(imageUri, context)
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(400.dp)
            )
        }
    } else {
        // Display a placeholder image or text when no image is selected
        Image(
            painter = painterResource(id = R.drawable.ic_image),
            contentDescription = null,
            modifier = Modifier.size(400.dp)
        )
    }

}



fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(Date())
    val imageFileName = "JPEG_$timeStamp"
    return File(externalCacheDir, "$imageFileName.jpg")
}

