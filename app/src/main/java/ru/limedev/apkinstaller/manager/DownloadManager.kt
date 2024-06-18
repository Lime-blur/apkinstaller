package ru.limedev.apkinstaller.manager

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.limedev.apkinstaller.utils.fromInputStream
import ru.limedev.apkinstaller.utils.putDownloadedApkToDocs
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class DownloadManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun getFileFromUrl(context: Context?, link: String, onStart: () -> Unit, onEnd: (File?) -> Unit) {
        val file = putDownloadedApkToDocs(context) ?: return
        onStart.invoke()
        getFileFromUrl(file, URL(link)) { downloadResult ->
            when {
                downloadResult.isSuccess -> onEnd.invoke(file)
                downloadResult.isError -> onEnd(null)
            }
        }
    }

    fun getFileFromUrl(file: File, url: URL, onResult: (DownloadResult) -> Unit) {
        scope.launch {
            try {
                val connect = url.openConnection() as HttpURLConnection
                if (connect.responseCode != HttpURLConnection.HTTP_OK) onDownloadError(onResult)
                file.fromInputStream(connect.inputStream)
                onDownloadSuccess(onResult)
            } catch (e: Exception) {
                onDownloadError(onResult)
            }
        }
    }

    private suspend fun onDownloadSuccess(onResult: (DownloadResult) -> Unit) {
        withContext(Dispatchers.Main) {
            onResult.invoke(
                DownloadResult(isSuccess = true)
            )
        }
    }

    private suspend fun onDownloadError(onResult: (DownloadResult) -> Unit) {
        withContext(Dispatchers.Main) {
            onResult.invoke(
                DownloadResult(isError = true)
            )
        }
    }
}