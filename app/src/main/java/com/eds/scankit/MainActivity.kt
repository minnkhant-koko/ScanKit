package com.eds.scankit

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.gson.GsonBuilder
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CAMERA_REQUEST_CODE = 13001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA).toTypedArray(), CAMERA_REQUEST_CODE)

        btnDefaultView.setOnClickListener {
            jumpToDefault()
        }

        btnCustomizeView.setOnClickListener {
            jumpToCustomize()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            Toast.makeText(applicationContext, "You can start Scanning now", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
        }
    }

    private fun jumpToDefault() {
        startActivity(DefaultViewActivity.getIntent(applicationContext))
    }

    private fun jumpToCustomize() {
        startActivity(CustomizeViewActivity.getIntent(applicationContext))
    }
}