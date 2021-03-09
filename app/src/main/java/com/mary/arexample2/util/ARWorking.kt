package com.mary.arexample2.util

import android.content.Context
import com.google.ar.core.Anchor
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Session
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.mary.arexample2.MainActivity
import com.mary.arexample2.util.event.DeviceUtil

class ARWorking {

    companion object {
        private const val TAG = "ARWorking"
    }

    fun arRunning(arSceneView: ArSceneView, context: Context, session: Session) {

        //바닥면을 인지하면 먼저 높이를 책정한다.
        //일단은.. 실력이 모자라니까 직접입력부터 하자 ㅠ

        //방을 그릴때 , 카메라 가운데를 기점으로 점 랜더링
        //랜더링 된 점을 처음 터치하면 그 것이 시작지점
        if (Constant.process == Constant.ARProcess.MEASURE_ROOM) {
            //1. 가운데에 점 랜더링
            if (arSceneView != null) {
                //DlogUtil.d(TAG, "안비엇수")
                if (arSceneView.arFrame != null) {

                    var deviceUtil = DeviceUtil()
                    var frame: Frame = arSceneView.arFrame!!

                    var hitResult: List<HitResult> = frame.hitTest(deviceUtil.getScreenX(context), deviceUtil.getScreenY(context))

                    for(hit in hitResult) {

                        val anchor = hit.createAnchor()
                        val anchorNode = AnchorNode(anchor)
                        anchorNode.setParent(arSceneView.scene)

                        val color = Color(1f, 0f, 0f)

                        MaterialFactory.makeOpaqueWithColor(context, color)
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

//                    arSceneView.arFrame?.camera?.displayOrientedPose?.let { DlogUtil.d(TAG, it) }
//
//
//
//                    var anchor : Anchor = session.createAnchor(arSceneView.arFrame?.androidSensorPose)
//                    var anchorNode : AnchorNode = AnchorNode(anchor)
//
//                    val color = Color(1f, 0f, 0f)
//
//                    MaterialFactory.makeOpaqueWithColor(context, color)
//                            .thenAccept { it ->
//                                val sphere: Renderable = ShapeFactory.makeSphere(0.01f, Vector3.zero(), it)
//                                val indicatorModel = Node()
//
//                                indicatorModel.setParent(anchorNode)
//
//                                DlogUtil.d(TAG, anchorNode.worldPosition.x)
//                                DlogUtil.d(TAG, anchorNode.worldPosition.y)
//                                DlogUtil.d(TAG, anchorNode.worldPosition.z)
//
//
//                                indicatorModel.renderable = sphere
//
//                            }
//
//
//                }


                } else {
                    DlogUtil.d(TAG, "비엇수")
                }
            }
        }

    }
}