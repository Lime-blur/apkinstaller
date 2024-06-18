package ru.limedev.apkinstaller.manager

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.limedev.apkinstaller.fromInputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class DownloadManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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