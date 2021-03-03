package com.mary.arexample2.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.ArrayList

object PermissionCheckUtil {
    private const val TAG = "PermissionCheckUtil"

    var CAMERA_REQUEST_CODE = 10

    fun checkPermission(activity: Activity, permission : Array<String>) {

        var requestList : MutableList<String> = mutableListOf()

        for(i : Int in permission.indices){
            var permissionCheck = ContextCompat.checkSelfPermission(activity, permission[i])

            if(permissionCheck == PackageManager.PERMISSION_DENIED) {

                DlogUtil.d(TAG,  "권한 승인 필요")
                requestList.add(permission[i])

            }

        }

        ActivityCompat.requestPermissions(activity, requestList.toTypedArray(), CAMERA_REQUEST_CODE)

    }
}