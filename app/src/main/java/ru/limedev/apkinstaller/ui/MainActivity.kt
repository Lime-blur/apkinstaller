package ru.limedev.apkinstaller.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import ru.limedev.apkinstaller.createDocsDirectory
import ru.limedev.apkinstaller.databinding.ActivityMainBinding
import ru.limedev.apkinstaller.getApkUriFromDocs
import ru.limedev.apkinstaller.manager.DownloadManager
import ru.limedev.apkinstaller.putDownloadedApkToDocs
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val downloadManager = DownloadManager()

    private var apkFileResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) launcher@ { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@launcher
            startUpdateApk(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initStorage()
        initClicks()
    }

    private fun initStorage() {
        createDocsDirectory(this)
    }

    private fun initClicks() {
        binding.button.setOnClickListener {
            startUpdateApk(getApkUriFromDocs(this))
        }
        binding.button2.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/vnd.android.package-archive"
            }
            apkFileResultLauncher.launch(intent)
        }
        binding.button3.setOnClickListener {
            download("https://github.com/Lime-blur/apkinstaller/raw/main/apk/app-debug.apk")
        }
    }

    private fun startUpdateApk(uri: Uri?) {
        if (uri == null) return
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setDataAndType(uri, "application/vnd.android.package-archive")
        }
        startActivity(intent)
        finish()
    }

    private fun download(link: String) {
        val file = putDownloadedApkToDocs(this) ?: return
        binding.progressBar.isVisible = true
        downloadManager.getFileFromUrl(file, URL(link)) { downloadResult ->
            if (downloadResult.isSuccess) {
                val uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)
                startUpdateApk(uri)
            }
            binding.progressBar.isVisible = false
        }
    }
}