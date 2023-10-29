package ru.toffeantyri.customviewexample.custom_views.slider_error

import android.graphics.Point
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt

fun isNearTouch(
    touchX: Int,
    touchY: Int,
    rad: Int,
    customCenter: Point,
    customRadius: Float
): Boolean {
    return customRadius + rad >= sqrt(
        (((touchX - customCenter.x).toDouble().pow(2.0) +
                (touchY - customCenter.y).toDouble().pow(2.0))),
    )
}

fun pointToAngle(x: Int, y: Int, center: Point): Int {
    return when {
        x >= center.x && y < center.y -> {
            val opp = center.x.toDouble()
            val adj = (center.y - y).toDouble()
            return 270 + Math.toDegrees(atan(opp / adj)).toInt()
        }

        x > center.x -> {
            val opp = center.y.toDouble()
            val adj = center.x.toDouble()
            return Math.toDegrees(atan(opp / adj)).toInt()
        }

        y > center.y -> {
            val opp = (center.x - x).toDouble()
            val adj = center.y.toDouble()
            return Math.toDegrees(atan(opp / adj)).toInt()
        }

        x < center.x -> {
            val opp = (center.y - y).toDouble()
            val adj = (center.x - x).toDouble()
            return Math.toDegrees(atan(opp / adj)).toInt()
        }

        else -> throw IllegalArgumentException()
    }
}