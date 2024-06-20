package ru.limedev.apkinstaller.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import java.io.File

fun Activity.startAppUpdatingByIntent(file: File?) {
    if (file == null) return
    val apkUri = file.getUriByFileProvider(this)
    startAppUpdatingByIntent(apkUri)
}

fun Activity.startAppUpdatingByIntent(apkUri: Uri?) {
    if (apkUri == null) return
    val intent = Intent(Intent.ACTION_VIEW).apply {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        setDataAndType(apkUri, "application/vnd.android.package-archive")
    }
    startActivity(intent)
    finish()
}

/**
 * Identical to the intent with mimeType "application/vnd.android.package-archive", since the
 * InstallStart activity has the same memeType.
 * @see startAppUpdatingByIntent
 */
fun Activity.startAppUpdatingByPackageInstallerIntent(apkUri: Uri?) {
    if (apkUri == null) return
    val component = ComponentName(
        "com.google.android.packageinstaller",
        "com.android.packageinstaller.InstallStart"
    )
    val intent = Intent().apply {
        data = apkUri
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        setComponent(component)
    }
    startActivity(intent)
}