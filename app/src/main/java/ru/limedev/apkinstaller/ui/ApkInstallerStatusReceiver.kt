package ru.limedev.apkinstaller.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.os.Build
import android.util.Log

class ApkInstallerStatusReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                Log.i(TAG, "Installation has begun and continues, showing dialog...")
                showConfirmationDialog(context, intent)
            }
            PackageInstaller.STATUS_SUCCESS -> {
                Log.i(TAG, "Installation successful")
            }
            PackageInstaller.STATUS_FAILURE -> {
                Log.e(TAG, "Installation failed: STATUS_FAILURE")
            }
            PackageInstaller.STATUS_FAILURE_ABORTED -> {
                Log.e(TAG, "Installation failed: STATUS_FAILURE_ABORTED")
            }
            PackageInstaller.STATUS_FAILURE_BLOCKED -> {
                Log.e(TAG, "Installation failed: STATUS_FAILURE_BLOCKED")
            }
            PackageInstaller.STATUS_FAILURE_CONFLICT -> {
                Log.e(TAG, "Installation failed: STATUS_FAILURE_CONFLICT")
            }
            PackageInstaller.STATUS_FAILURE_INCOMPATIBLE -> {
                Log.e(TAG, "Installation failed: STATUS_FAILURE_INCOMPATIBLE")
            }
            PackageInstaller.STATUS_FAILURE_INVALID -> {
                Log.e(TAG, "Installation failed: STATUS_FAILURE_INVALID")
            }
            PackageInstaller.STATUS_FAILURE_STORAGE -> {
                Log.e(TAG, "Installation failed: STATUS_FAILURE_STORAGE")
            }
            else -> {
                Log.w(TAG, "Installation failed: Unknown status")
            }
        }
    }

    private fun showConfirmationDialog(context: Context, intent: Intent) {
        val confirmationIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_INTENT, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_INTENT)
        }?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        if (confirmationIntent != null) context.startActivity(confirmationIntent)
    }

    companion object {
        private const val TAG = "apkinstaller"
    }
}