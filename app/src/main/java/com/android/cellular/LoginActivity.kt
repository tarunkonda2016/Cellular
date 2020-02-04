package com.android.cellular

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

class LoginActivity : AppCompatActivity() {


    companion object{
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 9003
        private const val REQUEST_ID_MULTIPLE_PERMISSIONS = 9002
    }


    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private var token : String = "x`"

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var firestore: FirebaseFirestore

    private var arrayData = HashMap<String,Any>()


    private lateinit var signInButton : SignInButton

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "data"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)





        signInButton = findViewById(R.id.signInButton)

        getDeviceToken()

       // if(checkAndRequestPermissions()){

              main()


//        }else{
//
//
//
//
//        }





    }
    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                ActivityCompat.startActivityForResult(this,intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE,null)
            }
        }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // [START_EXCLUDE]
                updateUI(null)
                // [END_EXCLUDE]
            }
        }
    }

    fun main(){
        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]


        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true)

        firestore = FirebaseFirestore.getInstance()

//        callRecorder = CallRecord.Builder(this)
//            .setLogEnable(false)
//            .setRecordFileName("RecordFileName")
//            .setRecordDirName("RecordDirName")
//            .build()
//        callRecorder.startCallRecordService()
//        callRecorder.changeReceiver(MyCallRecordReceiver(callRecorder))

        signInButton.setOnClickListener {
            signInButton()
        }

        signInButton()
    }


    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }

            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            arrayData.put("user",user.uid)
            arrayData.put("name", user.displayName!!)
            arrayData.put("email",user.email!!)
            arrayData.put("deviceToken",token)
            arrayData.put("created",  FieldValue.serverTimestamp())

            val sharedPref: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)


            firestore.collection("Users")
                .document(user.uid)
                .set(arrayData)
                .addOnSuccessListener {

                    val editor = sharedPref.edit()
                    editor.putString("user", user.uid)
                    editor.apply()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    // There was an error while writing
                    Log.e(TAG,"Error")
                }

        }
    }



    fun signInButton(){
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun  getDeviceToken(){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task: Task<InstanceIdResult?> ->
                if (!task.isSuccessful) {
                    return@addOnCompleteListener
                }

               token =  Objects.requireNonNull(task.result)!!.token
            }
    }
    private fun checkAndRequestPermissions(): Boolean {
        val camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
        val permissionRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)


        val listPermissionsNeeded = ArrayList<String>()

        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.PROCESS_OUTGOING_CALLS)
        }
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

//    override fun onRequestPermissionsResult(requestCode: Int,
//                                            permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
//
//                val perms = HashMap<String, Int>()
//                // Initialize the map with both permissions
//                perms[Manifest.permission.RECORD_AUDIO] = PackageManager.PERMISSION_GRANTED
//                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED
//                perms[Manifest.permission.PROCESS_OUTGOING_CALLS] = PackageManager.PERMISSION_GRANTED
//                perms[Manifest.permission.READ_PHONE_STATE] = PackageManager.PERMISSION_GRANTED
//                // Fill with actual results from user
//                if (grantResults.size > 0) {
//                    for (i in permissions.indices)
//                        perms[permissions[i]] = grantResults[i]
//                    if (perms[Manifest.permission.RECORD_AUDIO] == PackageManager.PERMISSION_GRANTED
//                        && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
//                        && perms[Manifest.permission.PROCESS_OUTGOING_CALLS] == PackageManager.PERMISSION_GRANTED
//                        && perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED) {
//
//                        main()
//
//
//
//                    } else {
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
//                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
//                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
//
//                               checkAndRequestPermissions()
//
//                        } else {
//
//                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                            val uri = Uri.fromParts("package", getPackageName(), null)
//                            intent.setData(uri)
//                            startActivity(intent)
//
//                            Toast.makeText(this,"Enable all permissions",Toast.LENGTH_LONG).show()
//
//
//                        }
//
//                    }
//                }
//            }
//        }
//
//    }
    fun unhide(){
        val p = getPackageManager()
        val componentName = ComponentName(this, LoginActivity::class.java)
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
    }

    fun hide(){
        val p = getPackageManager()
        val componentName = ComponentName(this, LoginActivity::class.java)
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
    }
}
