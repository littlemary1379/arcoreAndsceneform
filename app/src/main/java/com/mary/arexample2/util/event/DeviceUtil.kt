package com.mary.arexample2.util.event

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.mary.arexample2.util.DlogUtil

class DeviceUtil {

    companion object {
        private const val TAG = "DeviceUtil"
    }

    fun getScreenX(context: Context): Float {

        var displayMetrics = DisplayMetrics()

        context.display?.getRealMetrics(displayMetrics)
        DlogUtil.d(TAG, displayMetrics.widthPixels)

        return displayMetrics.widthPixels / 2f
    }

    fun getScreenY(context: Context): Float {

        var displayMetrics = DisplayMetrics()

        context.display?.getRealMetrics(displayMetrics)
        DlogUtil.d(TAG, displayMetrics.widthPixels)

        return displayMetrics.heightPixels / 2f
    }
}