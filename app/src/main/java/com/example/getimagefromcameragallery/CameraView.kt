package com.example.getimagefromcameragallery

//@Composable
//fun CameraView(
//    outputDirectory: File,
//    executor: Executor,
//    onImageCaptured: (Uri) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//) {
//    // 1//    val lensFacing = CameraSelector.LENS_FACING_BACK
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    val preview = Preview.Builder().build()
//    val previewView = remember { PreviewView(context) }
//    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
//    val cameraSelector = CameraSelector.Builder()
//        .requireLensFacing(lensFacing)
//        .build()
//
//    // 2
//    LaunchedEffect(lensFacing) {
//        val cameraProvider = context.getCameraProvider()
//        cameraProvider.unbindAll()
//        cameraProvider.bindToLifecycle(
//            lifecycleOwner,
//            cameraSelector,
//            preview,
//            imageCapture
//        )
//
//        preview.setSurfaceProvider(previewView.surfaceProvider)
//    }
//
//    // 3
//    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
//        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
//
//        IconButton(
//            modifier = Modifier.padding(bottom = 20.dp),
//            onClick = {
//                Log.i("kilo", "ON CLICK")
//                takePhoto(
//                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
//                    imageCapture = imageCapture,
//                    outputDirectory = outputDirectory,
//                    executor = executor,
//                    onImageCaptured = onImageCaptured,
//                    onError = onError
//                )
//            },
//            content = {
//                Icon(
//                    imageVector = Icons.Sharp.Star,
//                    contentDescription = "Take picture",
//                    tint = Color.White,
//                    modifier = Modifier
//                        .size(100.dp)
//                        .padding(1.dp)
//                        .border(1.dp, Color.White, CircleShape)
//                )
//            }
//        )
//    }
//}
//
//private fun takePhoto(
//    filenameFormat: String,
//    imageCapture: ImageCapture,
//    outputDirectory: File,
//    executor: Executor,
//    onImageCaptured: (Uri) -> Unit,
//    onError: (ImageCaptureException) -> Unit
//) {
//
//    val photoFile = File(
//        outputDirectory,
//        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
//    )
//
//    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//    imageCapture.takePicture(outputOptions, executor, object: ImageCapture.OnImageSavedCallback {
//        override fun onError(exception: ImageCaptureException) {
//            Log.e("kilo", "Take photo error:", exception)
//            onError(exception)
//        }
//
//        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//            val savedUri = Uri.fromFile(photoFile)
//            onImageCaptured(savedUri)
//        }
//    })
//}
//
//private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
//    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
//        cameraProvider.addListener({
//            continuation.resume(cameraProvider.get())
//        }, ContextCompat.getMainExecutor(this))
//    }
//}
