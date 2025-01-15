package com.franklinndiwe.insight.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

object BitmapUtils {
    suspend fun Bitmap.saveToDisk(context: Context): Uri? {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "Quote${System.currentTimeMillis()}.png"
        )
        file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)
        return scanFilePath(context, file.path)
    }

    private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context, arrayOf(filePath), arrayOf("image/png")
            ) { _, scannedUri ->
                if (scannedUri == null) continuation.cancel(Exception("File $filePath could not be scanned")) else continuation.resume(
                    scannedUri
                )
            }
        }
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

    private fun determineTextColor(backgroundColor: Int, supposedTextColor: Int): Int {
        val textColor: Int
        val whiteColor = Color.White.toArgb()
        val blackColor = Color.Black.toArgb()

        // Calculate contrast ratios
        val supposedTextColorContrastRatio =
            ColorUtils.calculateContrast(supposedTextColor, backgroundColor)
        val whiteContrastRatio = ColorUtils.calculateContrast(whiteColor, backgroundColor)
        val blackContrastRatio = ColorUtils.calculateContrast(blackColor, backgroundColor)

        textColor = when {
            ((supposedTextColorContrastRatio >= blackContrastRatio) || (supposedTextColorContrastRatio >= whiteContrastRatio)) && (supposedTextColorContrastRatio >= 4.5) -> supposedTextColor
            ((whiteContrastRatio >= blackContrastRatio) || (whiteContrastRatio >= supposedTextColorContrastRatio)) && (whiteContrastRatio >= 4.5) -> Color.White.toArgb()
            else -> Color.Black.toArgb()
        }

        return textColor
    }

    fun loadBitmap(context: Context, imagePath: String, scope: CoroutineScope): Bitmap? {
        var result: Bitmap? = null
        if (imagePath.startsWith("file:///")) {
            result = context.assets.open(imagePath.split("android_asset/")[1])
                .use(BitmapFactory::decodeStream)
        } else if (imagePath.startsWith("https://")) {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context).data(imagePath).allowHardware(false).build()
            scope.launch(Dispatchers.IO) {
                val imageRequest = imageLoader.execute(request)
                if (imageRequest is SuccessResult) result =
                    (imageRequest.drawable.toBitmap()) else if (imageRequest is ErrorResult) cancel(
                    imageRequest.throwable.localizedMessage ?: "ErrorResult", imageRequest.throwable
                )
            }
        } else {
            result = try {
                File(imagePath).inputStream().use(BitmapFactory::decodeStream)
            } catch (e: Exception) {
                null
            }
        }
        return result
    }

    fun getBackgroundColor(bitmap: Bitmap?) =
        Color(bitmap?.let { Palette.from(bitmap).generate().darkVibrantSwatch?.rgb }
            ?: Color.Black.toArgb())

    fun getSupposedTextColor(bitmap: Bitmap?) =
        bitmap?.let { Palette.from(it).generate().darkVibrantSwatch?.bodyTextColor }
            ?: Color.White.toArgb()

}