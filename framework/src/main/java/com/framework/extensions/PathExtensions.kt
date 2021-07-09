package com.framework.extensions

import android.graphics.Path
import android.graphics.Point

fun Path.cubicTo(x1: Int, y1: Int, x2: Int, y2: Int,
                 x3: Int, y3: Int) {
  cubicTo(x1.toFloat(), y1.toFloat(), x2.toFloat(), y2.toFloat(), x3.toFloat(), y3.toFloat())
}

fun Path.cubicTo(anchor1: Point, anchor2: Point, endPoint: Point) {
  cubicTo(anchor1.x, anchor1.y, anchor2.x, anchor2.y, endPoint.x, endPoint.y)
}

fun Path.lineTo(x: Int, y: Int) {
  lineTo(x.toFloat(), y.toFloat())
}

fun Path.lineTo(point: Point) {
  lineTo(point.x, point.y)
}

fun Path.moveTo(x: Int, y: Int) {
  moveTo(x.toFloat(), y.toFloat())
}

fun Path.moveTo(point: Point) {
  moveTo(point.x, point.y)
}