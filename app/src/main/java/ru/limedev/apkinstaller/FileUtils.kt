package ru.limedev.apkinstaller

import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

val separator: String = File.separator

fun createDirectory(pathname: String, replace: Boolean = true): File {
    return createFile(pathname, replace).apply { mkdir() }
}

fun createFile(pathname: String, replace: Boolean = true): File {
    val file = File(pathname)
    if (replace && file.exists()) file.delete()
    return file
}

fun File.fromInputStream(inputStream: InputStream) {
    inputStream.use { stream ->
        FileOutputStream(this).use { fos ->
            val bytes = ByteArray(1024)
            var b: Int
            while (stream.read(bytes, 0, 1024).also { b = it } != -1) {
                fos.write(bytes, 0, b)
            }
        }
    }
}