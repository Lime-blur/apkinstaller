package ru.limedev.apkinstaller.installer

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import android.util.Log
import ru.limedev.apkinstaller.receiver.ApkInstallerStatusReceiver

class ApkInstaller {

    fun install(context: Context?, apkUri: Uri?) {
        if (context == null || apkUri == null) {
            Log.e(TAG, "Context or apk uri is null")
            return
        }
        val sessionParams = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
        val packageInstaller = context.packageManager.packageInstaller
        val sessionId = packageInstaller.createSession(sessionParams)
        packageInstaller.openSession(sessionId).use { session ->
            val sessionResult = writeApkToSession(context, apkUri, session)
            if (!sessionResult) return@use
            val flags = getFlagsForPendingIntent()
            val receiverIntent = Intent(context, ApkInstallerStatusReceiver::class.java)
            val receiverPendingIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, flags)
            session.commit(receiverPendingIntent.intentSender)
        }
    }

    private fun writeApkToSession(
        context: Context,
        apkUri: Uri,
        session: PackageInstaller.Session
    ): Boolean {
        context.contentResolver.openInputStream(apkUri).use { apkStream ->
            if (apkStream == null) {
                Log.e(TAG, "Apk stream is null")
                return false
            }
            val sessionStream = session.openWrite(APK_NAME, 0, -1)
            sessionStream.buffered().use { bufferedSessionStream ->
                apkStream.copyTo(bufferedSessionStream)
                bufferedSessionStream.flush()
                session.fsync(sessionStream)
            }
            return true
        }
    }

    private fun getFlagsForPendingIntent(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }

    companion object {
        private const val APK_NAME = "app-debug.apk"
        private const val TAG = "apkinstaller"
    }
}