package ir.amirroid.qrcodescanner.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
class ScannerAnalyzer(
    private val scannerUtils: ScannerUtils,
    private val previewView: PreviewView,
    private val onCallback: (Barcode) -> Unit,
    private val onErrorCallback: () -> Unit
) : ImageAnalysis.Analyzer {
    //    private val matrix = context.resources.displayMetrics
//    private val widthS = matrix.widthPixels
//    private val heightS = matrix.heightPixels
    override fun analyze(image: ImageProxy) {
        val bitmap = previewView.bitmap
        if (bitmap != null)
            scannerUtils.scan(
                bitmap.toCroppedBitmap(),
                onCallback,
                onErrorCallback
            ) {
                image.close()
            }

    }

    private fun Bitmap.toCroppedBitmap(): Bitmap {
        val startY = height * 0.15f
        val startX = width * .15f
        val endX = width * .85f
        val endY = (startY + (endX - startX))
        val widthBitmap = endX - startX
        val heightBitmap = endY - startY
        return Bitmap.createBitmap(
            this,
            startX.toInt(),
            startY.toInt(),
            widthBitmap.toInt(),
            heightBitmap.toInt()
        )
    }
}