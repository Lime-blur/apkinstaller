package ru.limedev.apkinstaller.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import ru.limedev.apkinstaller.databinding.ActivityMainBinding
import ru.limedev.apkinstaller.download.DownloadManager
import ru.limedev.apkinstaller.installer.ApkInstaller
import ru.limedev.apkinstaller.utils.getApkUriFromDocs
import ru.limedev.apkinstaller.utils.initStorageDocs
import ru.limedev.apkinstaller.utils.startAppUpdatingByIntent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val downloadManager = DownloadManager()
    private val apkInstaller = ApkInstaller()

    private var apkFileResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) launcher@ { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data ?: return@launcher
            startUpdatingByIntentViaLauncher(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initStorageDocs(this)
        initClicks()
    }

    private fun initClicks() {
        binding.button.setOnClickListener { startUpdatingByIntentFromStorage() }
        binding.button2.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "application/vnd.android.package-archive" }
            apkFileResultLauncher.launch(intent)
        }
        binding.button3.setOnClickListener { startUpdatingByIntentFromGitHub() }
        binding.button4.setOnClickListener { startUpdatingByPackageInstallerFromStorage() }
    }

    private fun startUpdatingByIntentFromStorage() {
        val uri = getApkUriFromDocs(this)
        startAppUpdatingByIntent(uri)
    }

    private fun startUpdatingByIntentViaLauncher(uri: Uri) {
        startAppUpdatingByIntent(uri)
    }

    private fun startUpdatingByIntentFromGitHub() {
        val link = "https://github.com/Lime-blur/apkinstaller/raw/main/apk/app-debug.apk"
        downloadManager.getFileFromUrl(
            context = this,
            link = link,
            onStart = { binding.progressBar.isVisible = true },
            onEnd = { file ->
                startAppUpdatingByIntent(file)
                binding.progressBar.isVisible = false
            }
        )
    }

    private fun startUpdatingByPackageInstallerFromStorage() {
        val uri = getApkUriFromDocs(this)
        apkInstaller.install(this, uri)
    }
}