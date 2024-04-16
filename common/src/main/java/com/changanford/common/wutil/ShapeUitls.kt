package com.changanford.common.wutil

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.graphics.toColorInt

/**
 * @author: niubobo
 * @date: 2024/4/16
 * @description：
 */
typealias ColorInt = Int
typealias Px = Int
typealias FloatPx = Float

internal const val NO_GETTER = "Getter not available"

inline fun shapeDrawable(fill: GradientDrawable.() -> Unit): GradientDrawable =
    GradientDrawable().also {
        it.gradientType = GradientDrawable.LINEAR_GRADIENT
        it.fill()
    }

enum class Shape {
    RECTANGLE, OVAL, LINE, RING,
}

typealias ShapeInt = Int

fun toInt(s: Shape): ShapeInt = when (s) {
    Shape.RECTANGLE -> GradientDrawable.RECTANGLE
    Shape.OVAL -> GradientDrawable.OVAL
    Shape.LINE -> GradientDrawable.LINE
    Shape.RING -> GradientDrawable.RING
}

enum class Orientation {
    TOP_BOTTOM, TR_BL, RIGHT_LEFT, BR_TL, BOTTOM_TOP, BL_TR, LEFT_RIGHT, TL_BR,
}

private fun GradientDrawable.toOrientation(orientation: Orientation): GradientDrawable.Orientation =
    when (orientation) {
        Orientation.TOP_BOTTOM -> GradientDrawable.Orientation.TOP_BOTTOM
        Orientation.TR_BL -> GradientDrawable.Orientation.TR_BL
        Orientation.RIGHT_LEFT -> GradientDrawable.Orientation.RIGHT_LEFT
        Orientation.BR_TL -> GradientDrawable.Orientation.BR_TL
        Orientation.BOTTOM_TOP -> GradientDrawable.Orientation.BOTTOM_TOP
        Orientation.BL_TR -> GradientDrawable.Orientation.BL_TR
        Orientation.LEFT_RIGHT -> GradientDrawable.Orientation.LEFT_RIGHT
        Orientation.TL_BR -> GradientDrawable.Orientation.TL_BR
    }

var GradientDrawable.shapeEnum: Shape
    set(value) {
        shape = toInt(value)
    }
    @Deprecated(message = NO_GETTER, level = DeprecationLevel.HIDDEN) get() = error(NO_GETTER)

fun rectangleGradientShape(
    radius: FloatPx = Float.NaN,
    colors: IntArray,
    orientation: Orientation,
    fill: GradientDrawable.() -> Unit = {}
): GradientDrawable =
    shapeDrawable {
        shapeEnum = Shape.RECTANGLE
        setColors(colors)
        this.orientation = toOrientation(orientation)
        // DO NOT CHANGE
        // RADIUS AND COLOR ORDER IS IMPORTANT FOR RIPPLES!
        if (!radius.isNaN()) {
            cornerRadius = radius
        }
        fill.invoke(this)
    }

fun rectangleShape(
    radius: FloatPx = Float.NaN,
    color: ColorInt,
    size: Px? = null,
    fill: GradientDrawable.() -> Unit = {}
): GradientDrawable =
    shapeDrawable {
        shapeEnum = Shape.RECTANGLE
        solidColor = color
        size?.let {
            this.size = it
        }
        // DO NOT CHANGE
        // RADIUS AND COLOR ORDER IS IMPORTANT FOR RIPPLES!
        if (!radius.isNaN()) {
            cornerRadius = radius
        }
        fill.invoke(this)
    }

fun circleShape(color: ColorInt, size: Px? = null): GradientDrawable = shapeDrawable {
    shape = GradientDrawable.OVAL
    solidColor = color
    size?.let {
        this.size = it
    }
}

var GradientDrawable.solidColor: ColorInt
    set(value) = setColor(value)
    @Deprecated(message = NO_GETTER, level = DeprecationLevel.HIDDEN) get() = error(NO_GETTER)

var GradientDrawable.size: Px
    set(value) = setSize(value, value)
    get() = intrinsicWidth

class Stroke {
    var width: Px = -1
    var color: ColorInt = -1
    var dashWidth: FloatPx = 0F
    var dashGap: FloatPx = 0F
}

inline fun GradientDrawable.stroke(fill: Stroke.() -> Unit): Stroke = Stroke().also {
    it.fill()
    setStroke(it.width, it.color, it.dashWidth, it.dashGap)
}

class Size {
    var width: Px = -1
    var height: Px = -1
}

inline fun GradientDrawable.size(fill: Size.() -> Unit): Size = Size().also {
    fill(it)
    setSize(it.width, it.height)
}

class Corners {
    var radius: FloatPx = 0F

    var topLeft: FloatPx = Float.NaN
    var topRight: FloatPx = Float.NaN
    var bottomLeft: FloatPx = Float.NaN
    var bottomRight: FloatPx = Float.NaN

    internal fun FloatPx.orRadius(): FloatPx = takeIf { it >= 0 } ?: radius
}

fun Corners.render(): FloatArray = floatArrayOf(
    topLeft.orRadius(), topLeft.orRadius(),
    topRight.orRadius(), topRight.orRadius(),
    bottomRight.orRadius(), bottomRight.orRadius(),
    bottomLeft.orRadius(), bottomLeft.orRadius()
)

inline fun GradientDrawable.corners(fill: Corners.() -> Unit): Corners = Corners().also {
    it.fill()
    cornerRadii = it.render()
}

fun GradientDrawable.corners(
    radius: FloatPx = 0f,
    topLeft: FloatPx = Float.NaN,
    topRight: FloatPx = Float.NaN,
    bottomLeft: FloatPx = Float.NaN,
    bottomRight: FloatPx = Float.NaN
): Corners = Corners().also {
    it.radius = radius
    it.topLeft = topLeft
    it.topRight = topRight
    it.bottomLeft = bottomLeft
    it.bottomRight = bottomRight
    cornerRadii = it.render()
}

fun View.warpInWhiteShadow(radius: Float = 0f, topLeft: Float = Float.NaN, topRight: Float = Float.NaN, bottomLeft: Float = Float.NaN, bottomRight: Float = Float.NaN) {
    background = rectangleShape(color = "#1AFFFFFF".toColorInt()) {
        corners(radius, topLeft, topRight, bottomLeft, bottomRight)
    }
    translationZ = 6f
}