package com.android.cellular

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject


class NotificationHandler : FirebaseMessagingService(){

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "data"


    @SuppressLint("WrongThread")
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        try {
            val params: Map<String?, String?> = p0.getData()
            val data = JSONObject(params)

            Log.e("NOTIFICATION_MSG", data.toString())

            val firestore = FirebaseFirestore.getInstance()
            val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val isScreenAwake = powerManager.isInteractive
            val myKM = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && !myKM.isDeviceLocked() && isScreenAwake) {
                if(!sharedPref.getString("user","")!!.contentEquals("")){
                    firestore.collection("Users")
                        .document(sharedPref.getString("user","")!!)
                        .collection("data")
                        .add(Check(false, FieldValue.serverTimestamp().toString()))
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener {
                        }

                }
            }else {
                if(!sharedPref.getString("user","")!!.contentEquals("")){
                    firestore.collection("Users")
                        .document(sharedPref.getString("user","")!!)
                        .collection("data")
                        .add(Check(true, FieldValue.serverTimestamp().toString()))
                        .addOnSuccessListener {
                        }
                        .addOnFailureListener {
                        }

                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }


        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        val mView = View(this)
        mView.setBackgroundColor(Color.RED)
        val wm =  getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.addView(mView, params)

        mView.setOnTouchListener(OnTouchListener { v, event ->
            val wm =  getSystemService(Context.WINDOW_SERVICE) as WindowManager
            try {
                wm.removeView(mView)

            } catch (e: java.lang.Exception) {

            }

            return@OnTouchListener false
        })


//        try {
//            wm.removeView(mView)
//        } catch (e: java.lang.Exception) {
//        }

    }
    data class Check(val isLocked : Boolean, var time : String)




}