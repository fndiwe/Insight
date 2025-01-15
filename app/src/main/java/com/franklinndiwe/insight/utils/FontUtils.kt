package com.franklinndiwe.insight.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toFile
import com.franklinndiwe.insight.data.AppFont
import okio.use
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.UUID


object FontUtils {
    fun getFontInfo(inputStream: InputStream, path: String): AppFont? {
        var font: AppFont? = null
        try {
            val fontName = fontName(inputStream)
            font = fontName?.let {
                AppFont(
                    name = it,
                    path = path,
                    unlocked = it == "Playfair Display",
                )
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("FontUtils", it) }
        }
        return font
    }

    fun getFileFromUri(context: Context, uri: Uri): File {
        // Create a directory to store the extracted files
        val dir = File(context.filesDir, "images")
        if (!dir.exists()) dir.mkdirs()
        return getUri(uri, context, dir, "Image${UUID.randomUUID()}").toFile()
    }


    fun getFontFromPhone(
        context: Context, uri: Uri,
    ): AppFont? {
        // Create a directory to store the extracted files
        val fontsDir = File(context.filesDir, "fonts")
        if (!fontsDir.exists()) fontsDir.mkdirs()
        val contentResolver = context.contentResolver
        val uri1 =
            contentResolver.openInputStream(uri)?.let { inputStream ->
                fontName(inputStream)?.let {
                    getUri(
                        uri, context, fontsDir,
                        it
                    )
                }
            }
        return uri1?.toFile()?.inputStream()
            ?.let { getFontInfo(it, uri1.toString())?.copy(unlocked = true, shipped = false) }
    }

    private fun getUri(uri: Uri, context: Context, dir: File, fileName: String): Uri {
        var resultURI = uri
        if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr: ContentResolver = context.contentResolver
            val mimeTypeMap = MimeTypeMap.getSingleton()
            val extensionFile = mimeTypeMap.getExtensionFromMimeType(cr.getType(uri))
            val file = File(
                dir,
                "$fileName.$extensionFile"
            )
            val input = cr.openInputStream(uri)
            file.outputStream().use { stream ->
                input?.copyTo(stream)
            }
            input?.close()
            resultURI = Uri.fromFile(file)
        }
        return resultURI
    }

    private fun fontName(inputStream: InputStream): String? {
        return try {
            val ttfFile = inputStream.let { FontFileReader.readTTF(it) }
            ttfFile.fullName
        } catch (e: IOException) {
            null
        }
    }
}