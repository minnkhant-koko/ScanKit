package com.eds.scankit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.huawei.hms.hmsscankit.OnLightVisibleCallBack
import com.huawei.hms.hmsscankit.OnResultCallback
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import kotlinx.android.synthetic.main.activity_customzie_view.*
import java.io.IOException

class CustomizeViewActivity: AppCompatActivity() {

    private lateinit var remoteView : RemoteView
    private var screenWidth : Int = 0
    private var screenHeight : Int = 0

    private val onResultCallBack = OnResultCallback {
        showResult(it)
    }

    private val onLightVisibleCallBack = OnLightVisibleCallBack {
        toggleFlashlight(it)
    }

    companion object {
        private const val SCAN_FRAME_SIZE = 300
        private const val REQUEST_CODE_ALBUM = 0x12
        private const val SCAN_RESULT = "scanResult"
        private var TAG = CustomizeViewActivity::class.java.simpleName
        fun getIntent(context : Context) = Intent(context, CustomizeViewActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customzie_view)

        // initialize toolbar/actionbar
        setSupportActionBar(toolbar)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)

        val displayMetrics = resources.displayMetrics
        val density = displayMetrics.density
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        val scanFrameSize = (SCAN_FRAME_SIZE*density).toInt()
        val rect : Rect = Rect(
            screenWidth/2 - scanFrameSize/2,
            screenWidth/2 + scanFrameSize/2,
            screenHeight/2 - scanFrameSize/2,
            screenHeight/2 + scanFrameSize/2
        )

        remoteView = RemoteView.Builder()
            .setContext(this)
            .setBoundingBox(rect)
            .setFormat(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE)
            .build()

        remoteView.onCreate(savedInstanceState)
        remoteView.setOnResultCallback(onResultCallBack)
        remoteView.setOnLightVisibleCallback(onLightVisibleCallBack)
        flCameraPreview.addView(remoteView, FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
    }

    override fun onStart() {
        super.onStart()
        remoteView.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_view_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // All menu icons are downloaded from the author named kiranshastry of flaticon.com
        // Here is the link to his works => https://www.flaticon.com/authors/kiranshastry
        // You can download more awesome icons there. <3
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.menu_flashlight -> {
                if (remoteView.lightStatus) {
                    item.setIcon(R.drawable.torch)
                } else {
                    item.setIcon(R.drawable.flashlight)
                }
                remoteView.switchLight()
                true
            }
            R.id.menu_album -> {
                scanFromPhotoAlbum()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun toggleFlashlight(visible : Boolean) {
//        Toast.makeText(applicationContext, "DimLight Listener : $visible", Toast.LENGTH_SHORT).show()
    }

    private fun scanFromPhotoAlbum() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        this.startActivityForResult(intent, REQUEST_CODE_ALBUM)
    }

    private fun showResult(hmsResults : Array<HmsScan>) {
        Log.v(TAG, "Results ${GsonBuilder().setPrettyPrinting().create().toJson(hmsResults)}")
    }

    override fun onPause() {
        super.onPause()
        remoteView.onPause()
    }

    override fun onStop() {
        super.onStop()
        remoteView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ALBUM) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, data?.data)
                val hmsScan = ScanUtil.decodeWithBitmap(
                    applicationContext,
                    bitmap,
                    HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create()
                )

                if (hmsScan != null && hmsScan.isNotEmpty() && hmsScan[0] != null && hmsScan[0].originalValue.isNotEmpty()) {
                    val intent = Intent().putExtra(SCAN_RESULT, hmsScan[0])
                    setResult(Activity.RESULT_OK, intent)
                    showResult(hmsScan)
                    this.finish()
                } else {
                    Toast.makeText(applicationContext, "Scanning from Album failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e : IOException) {
                e.printStackTrace()
            }
        }
    }
}