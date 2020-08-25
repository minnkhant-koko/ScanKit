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

class MainActivity : AppCompatActivity() {


    private lateinit var options : HmsScanAnalyzerOptions

    companion object {
        private const val CAMERA_REQUEST_CODE = 13001
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ActivityCompat.requestPermissions(this, listOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA).toTypedArray(), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.v("Main", "${GsonBuilder().setPrettyPrinting().create().toJson(permissions)}")
        Log.v("Main", "${GsonBuilder().setPrettyPrinting().create().toJson(grantResults)}")
        if (requestCode == CAMERA_REQUEST_CODE) {
            startActivity(DefaultViewActivity.getIntent(applicationContext))
        } else {
            Toast.makeText(applicationContext, "Failure", Toast.LENGTH_LONG).show()
        }
    }
}