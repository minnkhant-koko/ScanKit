package com.eds.scankit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import kotlinx.android.synthetic.main.activity_default_view.*
import kotlinx.android.synthetic.main.activity_main.*

class DefaultViewActivity : AppCompatActivity() {

    private lateinit var options : HmsScanAnalyzerOptions

    companion object {
        private var TAG : String = DefaultViewActivity::class.java.simpleName
        const val REQUEST_CODE_SCAN_ONE = 0x11

        fun getIntent(context : Context) = Intent(context, DefaultViewActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default_view)

        btnScanWithDefaultView.setOnClickListener {
            scanWithDefaultView()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || data == null) {
            Log.e(TAG, "Result Code is not Okay or data is null : $data")
            return
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            val hmsScan = data.getParcelableExtra<HmsScan>(ScanUtil.RESULT)
            Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
            Log.v(TAG, "Result : ${GsonBuilder().setPrettyPrinting().create().toJson(hmsScan)}")
        }
    }

    private fun scanWithDefaultView() {
        options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
            .create()
        ScanUtil.startScan(this@DefaultViewActivity, REQUEST_CODE_SCAN_ONE, options)
    }
}