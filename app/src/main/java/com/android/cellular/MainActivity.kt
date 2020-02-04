package com.android.cellular

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 9003


    }
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                ActivityCompat.startActivityForResult(this,intent,
                    ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE,null)
            }else{
                val LAYOUT_FLAG: Int
                LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                }

               val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                )

                val mView = View(this)
                mView.setBackgroundColor(Color.TRANSPARENT)
                val wm =  getSystemService(Context.WINDOW_SERVICE) as WindowManager
                wm.addView(mView, params)

//                try {
//                    wm.removeView(mView)
//
//                } catch (e: Exception) {
//
//                }

                mView.setOnTouchListener(View.OnTouchListener { v, event ->
                    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    try {
                        wm.removeView(mView)

                    } catch (e: Exception) {

                    }

                    return@OnTouchListener false
                })

            }
        }else{

        }





    }
    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                ActivityCompat.startActivityForResult(this,intent,
                    ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE,null)
            }
        }
    }

}
