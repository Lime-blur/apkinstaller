package ru.limedev.apkinstaller

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import ru.limedev.apkinstaller.databinding.ActivityMainBinding
import java.io.File

private val separator: String = File.separator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
        createDocsDirectory(this)
        binding.button.setOnClickListener { startUpdateApk(this) }
        binding.button2.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "application/vnd.android.package-archive" }
            apkFileResultLauncher.launch(intent)
        }
    }

    private fun startUpdateApk(context: Context) {
        val uri = getApkUriFromDocuments(context) ?: return
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        startActivity(intent)
    }

    private fun getApkUriFromDocuments(context: Context?): Uri? {
        if (context == null) return null
        val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return null
        val files = docsDir.listFiles() ?: return null
        if (files.isEmpty() || files.size > 1) return null
        return FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", files[0])
    }

    private fun createDocsDirectory(context: Context?) {
        if (context == null) return
        val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath ?: return
        createFile("$docsDir$separator.temp")
    }

    private fun createFile(pathname: String, replace: Boolean = true): File {
        val file = File(pathname)
        if (replace && file.exists()) file.delete()
        return file
    }

    private fun startUpdateApk(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        startActivity(intent)
        finish()
    }
}