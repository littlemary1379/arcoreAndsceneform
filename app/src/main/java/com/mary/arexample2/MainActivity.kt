package com.mary.arexample2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Scene
import com.mary.arexample2.util.DlogUtil
import com.mary.arexample2.util.PermissionCheckUtil

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }


    private lateinit var arSceneView: ArSceneView

    private var session: Session? = null
    private var installRequest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck()
        findView()
        checkARcore()
        setListener()
    }

    private fun findView() {
        arSceneView = findViewById(R.id.arSceneView)
    }

    private fun setListener() {
        var gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                onSingleTapUp(e)
                return true
            }

            override fun onDown(e: MotionEvent?): Boolean {
                return true
            }
        })

        arSceneView.scene.setOnTouchListener(Scene.OnTouchListener { hitTestResult, motionEvent ->
            return@OnTouchListener gestureDetector.onTouchEvent(motionEvent)
        })

    }

    //1. permission
    private fun permissionCheck() {
        PermissionCheckUtil.checkPermission(this, arrayOf(Manifest.permission.CAMERA))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var permissionSize: Int = permissions.size

        for (i: Int in 0 until permissionSize) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) == PackageManager.PERMISSION_DENIED) {
                DlogUtil.d(TAG, "권한 미승인")
                finish()
            } else {
                DlogUtil.d(TAG, "${permissions[i]} 권한 승인")
            }
        }

    }

    //2. Create Session
    //세션을 만들기 전 AR core이 지원되는지 아닌지 확인하고 세션을 생성한다.
    private fun checkARcore() {
        try {
            if (session == null) {
                when (ArCoreApk.getInstance().requestInstall(this, installRequest)) {
                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        DlogUtil.d(TAG, "AR core 설치 필요")
                        installRequest = true
                    }
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        DlogUtil.d(TAG, "AR core 설치 미필요")
                        createSession()
                    }
                }
            }
        } catch (e: UnavailableArcoreNotInstalledException) {
            DlogUtil.d(TAG, "ARCore 설치 필요")
        } catch (e: UnavailableUserDeclinedInstallationException) {
            DlogUtil.d(TAG, "ARCore 설치 필요")
        } catch (e: UnavailableApkTooOldException) {
            DlogUtil.d(TAG, "ARCore 업데이트 필요")
        } catch (e: UnavailableSdkTooOldException) {
            DlogUtil.d(TAG, "앱 업데이트 필요")
        } catch (e: UnavailableDeviceNotCompatibleException) {
            DlogUtil.d(TAG, "디바이스가 AR core을 지원하지 않음")
        } catch (e: Exception) {
            DlogUtil.d(TAG, "AR 세션 생성 실패")
        }
    }

    private fun createSession() {
        DlogUtil.d(TAG, "세션 생성")
        session = Session(this)
        val config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE

        session?.configure(config)
        arSceneView.setupSession(session!!)
        arSceneView.resume()

    }


}