package com.example.cameraxtest2capstone

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import java.io.File

class ImageAnalyzer(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val file: File,
    private val imageCropPercentages: MutableLiveData<Pair<Int, Int>>
) : ImageAnalysis.Analyzer {

    var x = 0

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        // We requested a setTargetAspectRatio, but it's not guaranteed that's what the camera
        // stack is able to support, so we calculate the actual ratio from the first frame to
        // know how to appropriately crop the image we want to analyze.
        val imageWidth = mediaImage.width
        val imageHeight = mediaImage.height

        Log.d("ImageAnalyzer", "Image Width: $imageWidth")
        Log.d("ImageAnalyzer", "Image Height: $imageHeight")

        val actualAspectRatio = imageWidth / imageHeight

        val convertImageToBitmap = convertYuv420888ImageToBitmap(mediaImage)
        val cropRect = Rect(0, 0, imageWidth, imageHeight)
//        val cropRect = Rect(0, 0, 300, 300)

        // If the image has a way wider aspect ratio than expected, crop less of the height so we
        // don't end up cropping too much of the image. If the image has a way taller aspect ratio
        // than expected, we don't have to make any changes to our cropping so we don't handle it
        // here.
        val currentCropPercentages = imageCropPercentages.value ?: return
        if (actualAspectRatio > 3) {
            val originalHeightCropPercentage = currentCropPercentages.first
            val originalWidthCropPercentage = currentCropPercentages.second
            imageCropPercentages.value =
                Pair(originalHeightCropPercentage / 2, originalWidthCropPercentage)
        }

        // If the image is rotated by 90 (or 270) degrees, swap height and width when calculating
        // the crop.
        val cropPercentages = imageCropPercentages.value ?: return
        val heightCropPercent = cropPercentages.first
        val widthCropPercent = cropPercentages.second
        val (widthCrop, heightCrop) = when (rotationDegrees) {
            90, 270 -> Pair(heightCropPercent / 100f, widthCropPercent / 100f)
            else -> Pair(widthCropPercent / 100f, heightCropPercent / 100f)
        }

        Log.d("ImageAnalyzer", "Width Crop: $widthCrop")
        Log.d("ImageAnalyzer", "Height Crop: $heightCrop")

//        Log.d("ImageAnalyzer", "Inset dx: ${(imageWidth * widthCrop / 2).toInt()}")
//        Log.d("ImageAnalyzer", "Inset dy: ${(imageHeight * heightCrop / 2).toInt()}")

        cropRect.inset(
            (imageWidth * widthCrop / 2).toInt(),
            (imageHeight * heightCrop / 2).toInt()
        )

//        cropRect.inset(
//            0,
//            0
//        )


        val croppedBitmap =
            rotateAndCrop(convertImageToBitmap, rotationDegrees, cropRect)

        // TODO call recognizeText() once implemented
        if (x == 30){

            Log.d("ImageAnalyzer", "ready")
            recognizeImage(croppedBitmap)

        }else{
            x += 1
            imageProxy.close()
        }

    }

    fun recognizeImage(image: Bitmap){

        bitmapToFile(file, image, context)


    }


}