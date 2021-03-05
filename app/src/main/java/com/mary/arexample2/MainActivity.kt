package com.mary.arexample2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.*
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.mary.arexample2.util.Constant
import com.mary.arexample2.util.DlogUtil
import com.mary.arexample2.util.PermissionCheckUtil
import com.mary.arexample2.util.event.ESSArrow
import com.mary.arexample2.util.event.EventCenter
import com.mary.arexample2.viewholder.ArCognizePlainViewHolder
import com.mary.arexample2.viewholder.ArMeasureHeightViewHolder
import java.util.HashMap


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }


    private lateinit var arSceneView: ArSceneView
    private lateinit var frameLayoutCognize : FrameLayout

    private var session: Session? = null
    private var installRequest: Boolean = false

    private lateinit var arCognizePlainViewHolder : ArCognizePlainViewHolder
    private lateinit var arMeasureHeightViewHolder: ArMeasureHeightViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionCheck()
        findView()
        initESS()
        checkARcore()
        setListener()

        //create 할때 바닥 인식을 해야하나?
        initAR()
    }

    override fun onResume() {
        super.onResume()

        if(arSceneView == null) {
            return
        }

        if(arSceneView.session == null) {
            createSession()
        }

        try {
            arSceneView.resume()
        } catch (e: CameraNotAvailableException) {
            DlogUtil.d(TAG, e)
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        if(arSceneView != null) {
            DlogUtil.d(TAG, "pause")
            arSceneView.pause()
        }
    }

    private fun findView() {
        arSceneView = findViewById(R.id.arSceneView)
        frameLayoutCognize = findViewById(R.id.frameLayoutCognize)
    }

    private fun initESS() {
        EventCenter.addEventObserver(ESSArrow.ENTER_HEIGHT_METER, this, object : EventCenter.EventRunnable{
            override fun run(arrow: String?, poster: Any?, data: HashMap<String?, Any?>?) {
                if(!data.isNullOrEmpty()) {
                    var height = data?.get("height").toString().toInt()
                    DlogUtil.d(TAG, "data 전송, $height")
                    Constant.process = Constant.ARProcess.MEASURE_ROOM
                } else {
                    DlogUtil.d(TAG, "안되는디;")
                }
            }

        })
    }

    private fun setListener() {
        var gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                DlogUtil.d(TAG, "singleTapUp")
                onSingleTapUp(e)
                return true
            }

            override fun onDown(e: MotionEvent?): Boolean {
                DlogUtil.d(TAG, "down")
                return true
            }
        })

        arSceneView.scene.setOnTouchListener { hitTestResult, motionEvent ->
            onSingleTap(motionEvent)
            return@setOnTouchListener false
        }

        arSceneView.scene.addOnUpdateListener(Scene.OnUpdateListener { frameTime: FrameTime? ->
            var frame = arSceneView.arFrame

            if(frame?.camera?.trackingState==TrackingState.TRACKING) {



                //바닥 감지중일때는.. 힌트를 띄우고, 바닥감지중이 아닐때는 움직일때 선.. 선을 그리게 해야할거 같은데
                if(Constant.process == Constant.ARProcess.DETECT_PLANE) {
                    DlogUtil.d(TAG, "바닥 감지 완료 ${Constant.process}")
                    Constant.process = Constant.ARProcess.MEASURE_HEIGHT_HINT
                    frameLayoutCognize.visibility= View.GONE
                    frameLayoutCognize.removeAllViews()
                    initMeasureHint()
                } else {
//                    DlogUtil.d(TAG, "바닥 감지 이미 되어 있음 ${Constant.process}")
                    if(Constant.process == Constant.ARProcess.MEASURE_HEIGHT_HINT) {

                    }
                }

            } else {
                frameLayoutCognize.visibility= View.VISIBLE
                frameLayoutCognize.removeAllViews()
                initAR()
            }
        })

    }

    private fun initAR() {
        arCognizePlainViewHolder = ArCognizePlainViewHolder(this)
        frameLayoutCognize.addView(arCognizePlainViewHolder.view)
        Constant.process = Constant.ARProcess.DETECT_PLANE
    }

    private fun initMeasureHint() {
        arMeasureHeightViewHolder = ArMeasureHeightViewHolder(this)
        arMeasureHeightViewHolder.arMeasureHeightViewHolderDelegate = object : ArMeasureHeightViewHolder.ArMeasureHeightViewHolderDelegate{
            override fun confirm() {
                DlogUtil.d(TAG, "ㅇㅁㅇ ㅠㅜ")
                closeMeasure()
            }

        }
        frameLayoutCognize.addView(arMeasureHeightViewHolder.view)
        frameLayoutCognize.visibility = View.VISIBLE
    }

    private fun closeMeasure() {
        frameLayoutCognize.removeAllViews()
        frameLayoutCognize.visibility = View.GONE
    }

    private fun onSingleTap(tap: MotionEvent){
        var frame : Frame? = arSceneView.arFrame

        if (tap != null && frame?.camera?.trackingState == TrackingState.TRACKING) {
            for (hit in frame.hitTest(tap)) {
                val frameTrack = hit.trackable
                if (frameTrack is Plane && frameTrack.isPoseInPolygon(hit.hitPose)) {
                    // Create the Anchor.
                    val anchor = hit.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arSceneView.scene)

                    val color = Color(1f, 0f, 0f)

                    MaterialFactory.makeOpaqueWithColor(this, color)
                            .thenAccept { material: Material? ->
                                // The sphere is in local coordinate space, so make the center 0,0,0
                                val sphere: Renderable = ShapeFactory.makeSphere(0.01f, Vector3.zero(),
                                        material)
                                val indicatorModel = Node()
                                indicatorModel.setParent(anchorNode)
                                DlogUtil.d(TAG, anchorNode.worldPosition.x)
                                DlogUtil.d(TAG, anchorNode.worldPosition.y)
                                DlogUtil.d(TAG, anchorNode.worldPosition.z)
                                indicatorModel.renderable = sphere
                            }
                }
            }
        }
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

        if(session == null){
            session = Session(this)
        }

        //necessary : config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        val config = Config(session)
        config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
        config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

        session?.configure(config)
        arSceneView.setupSession(session!!)

    }

    override fun onDestroy() {
        super.onDestroy()

        if(arSceneView != null) {
            DlogUtil.d(TAG, "destroy")
            session?.close()
            arSceneView.destroy()
        }
    }

}