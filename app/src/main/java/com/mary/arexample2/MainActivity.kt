package com.mary.arexample2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.ar.sceneform.ArSceneView
import com.mary.arexample2.util.DlogUtil
import com.mary.arexample2.util.PermissionCheckUtil

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }


    private lateinit var arSceneView: ArSceneView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck()
        findView()
    }

    private fun findView() {
        arSceneView = findViewById(R.id.arSceneView)
    }

    //1. permission
    private fun permissionCheck() {
        PermissionCheckUtil.checkPermission(this, arrayOf(Manifest.permission.CAMERA))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var permissionSize : Int = permissions.size

        for(i : Int in 0 until permissionSize) {
            if(ContextCompat.checkSelfPermission(this,permissions[i]) == PackageManager.PERMISSION_DENIED) {
                DlogUtil.d(TAG, "권한 미승인")
            } else {
                DlogUtil.d(TAG, "${permissions[i]} 권한 승인")
            }
        }

    }

    //2. Create Session



}