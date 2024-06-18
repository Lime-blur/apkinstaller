package ru.limedev.apkinstaller

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File

fun createDocsDirectory(context: Context?) {
    if (context == null) return
    val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath ?: return
    createFile("$docsDir$separator.temp")
}

fun putDownloadedApkToDocs(context: Context): File? {
    val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath ?: return null
    createDirectory("$docsDir${separator}apk")
    return createFile("$docsDir${separator}apk${separator}app-debug.apk")
}

fun getApkUriFromDocs(context: Context?): Uri? {
    if (context == null) return null
    val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return null
    val files = docsDir.listFiles() ?: return null
    if (files.isEmpty()) return null
    val file = files.first { !it.isDirectory }
    return FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
}