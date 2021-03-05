package com.mary.arexample2.util

object Constant {

    var process : ARProcess = ARProcess.DETECT_PLANE

    enum class ARProcess {
        DETECT_PLANE, MEASURE_HEIGHT_HINT, MEASURE_HEIGHT, MEASURE_ROOM, SELECTED_WALL_OBJECT, DRAW_WALL_OBJECT
    }
}