package ru.toffeantyri.customviewexample.custom_view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ru.toffeantyri.customviewexample.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.properties.Delegates

class CustomSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val START_ANGLE = 135f
        private const val END_ANGLE = START_ANGLE + 135f

    }

    private var minValue by Delegates.notNull<Int>()
    private var maxValue by Delegates.notNull<Int>()
    private var currentValue by Delegates.notNull<Int>()
    private var customRadius: Float = resources.getDimension(R.dimen.slider_custom_radius)
    private var roundRadius by Delegates.notNull<Float>()
    private var anyRadius: Float = resources.getDimension(R.dimen.slider_min_radius)

    val center = Point()
    val customCenter = Point()
    val frame = RectF()

    private val paint = Paint().apply {
        color = Color.parseColor("#75FFFFFF")
        style = Paint.Style.FILL
        strokeWidth = 6f
        flags = Paint.ANTI_ALIAS_FLAG
        textAlign = Paint.Align.CENTER
    }

    private var isTouch = false

    init {
        val typedArray : TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSlider)
        maxValue = typedArray.getInt(R.styleable.CustomSlider_max_value, 100)
        minValue = typedArray.getInt(R.styleable.CustomSlider_min_value, 0)
        currentValue = typedArray.getInt(R.styleable.CustomSlider_current_value, 50)
        customRadius = typedArray.getFloat(R.styleable.CustomSlider_touch_radius, 30F)
        typedArray.recycle()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        initSize(canvas)
        drawCircleFrame(canvas)
        drawTouchSlider(canvas)

    }

    private fun initSize(canvas: Canvas) {
        center.x = canvas.width / 2
        center.y = canvas.height / 2
        roundRadius = canvas.width / 2 - customRadius
        frame.set(
            center.x - roundRadius,
            center.y - roundRadius,
            center.x + roundRadius,
            center.y + roundRadius
        )
    }

    private fun drawTouchSlider(canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        canvas.drawArc(frame, START_ANGLE, END_ANGLE, true, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#BFFFFFFF")
        canvas.drawCircle(
            cos(Math.toRadians(END_ANGLE.toDouble()) * roundRadius + center.x).toFloat(),
            sin(Math.toRadians(END_ANGLE.toDouble()) * roundRadius + center.y).toFloat(),
            anyRadius,
            paint
        )

        canvas.drawCircle(
            cos(Math.toRadians(START_ANGLE.toDouble()) * roundRadius + center.x).toFloat(),
            sin(Math.toRadians(START_ANGLE.toDouble()) * roundRadius + center.y).toFloat(),
            anyRadius,
            paint
        )
    }

    private fun drawCircleFrame(canvas: Canvas) {
        val angle =
            ((currentValue - minValue) * (END_ANGLE - START_ANGLE) /
                    (maxValue - minValue) + START_ANGLE)

        customCenter.x = (cos(Math.toRadians(angle.toDouble())) * roundRadius + center.x).toInt()
        customCenter.y = (sin(Math.toRadians(angle.toDouble())) * roundRadius + center.y).toInt()

        paint.color = Color.parseColor("#BFFFFFFF")
        canvas.drawCircle(customCenter.x.toFloat(), customCenter.y.toFloat(), customRadius, paint)

        paint.textSize = resources.getDimensionPixelSize(R.dimen.text_size).toFloat()
        canvas.drawText(
            currentValue.toString(),
            center.x.toFloat(),
            center.y + customRadius * 3 / 2,
            paint
        )
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x?.let { it.roundToInt() } ?: 0
        val touchY = event?.y?.let { it.roundToInt() } ?: 0

        when (event?.action) {
            MotionEvent.ACTION_UP -> return false
            MotionEvent.ACTION_DOWN -> {
                isTouch = isNearTouch(touchX, touchX, 20, customCenter, customRadius)
            }

            MotionEvent.ACTION_MOVE -> {
                if (isTouch) {
                    var angle = (pointToAngle(touchX, touchY, center) - START_ANGLE).toInt()
                    if (angle < 0) {
                        angle += 360
                    }
                    var value: Int =
                        (minValue + angle * (maxValue - minValue) / (END_ANGLE - START_ANGLE)).toInt()
                    if (value != currentValue && value <= maxValue && value >= minValue
                        && abs(currentValue - value) < (maxValue - minValue) / 10 + 1
                    ) {
                        currentValue = value
                        invalidate()
                    }
                }


            }
        }
        return true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = resources.getDimensionPixelSize(R.dimen.slider_default_size)
        val desiredHeight = resources.getDimensionPixelSize(R.dimen.slider_default_size)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getMode(heightMeasureSpec)

        var width: Int = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            desiredWidth.coerceAtMost(widthSize)
        } else {
            desiredWidth
        }

        var height: Int = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            desiredWidth.coerceAtMost(heightSize)
        } else {
            desiredHeight
        }
        setMeasuredDimension(width, height)
    }


}