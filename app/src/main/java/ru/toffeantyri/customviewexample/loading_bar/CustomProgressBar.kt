package ru.toffeantyri.customviewexample.loading_bar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import ru.toffeantyri.customviewexample.R

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    style: Int = 0
) : View(context, attrs, style) {

    @ColorInt
    private var generalBackground: Int = Color.TRANSPARENT
    private var animStarted = false
    private var tickState = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val center = Point()
    private var circleRadius = resources.getDimensionPixelSize(R.dimen.loading_circle_radius)

    init {
        val typedArray: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar)
        generalBackground = typedArray.getColor(R.styleable.CustomProgressBar_general_background, 0)
        typedArray.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d("MyLogView", "onMeasure")
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        Log.d("MyLogView", "onLayout")
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("MyLogView", "onDraw")
        initCenter(canvas)

        paint.color = context.getColor(R.color.circle_background)
        paint.style = Paint.Style.FILL
        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), circleRadius.toFloat(), paint)
        drawRectangle(canvas)
    }

    private fun initCenter(canvas: Canvas) {
        center.x = canvas.width / 2
        center.y = canvas.height / 2
    }

    private fun drawRectangle(canvas: Canvas) {
        paint.color = generalBackground
        paint.style = Paint.Style.FILL

        val mode = PorterDuff.Mode.SRC_ATOP

        paint.xfermode = PorterDuffXfermode(mode)
        val top: Float = (if (tickState in 0..1) center.y - circleRadius else center.y).toFloat()
        val right: Float = (if (tickState in 1..2) center.x + circleRadius else center.x).toFloat()
        val bottom: Float = (if (tickState in 0..1) center.y else center.y + circleRadius).toFloat()
        val left: Float = (if (tickState in 1..2) center.x else center.x - circleRadius).toFloat()
        canvas.drawRect(left, top, right, bottom, paint)

    }

    private val mRunnable: Runnable = Runnable {
        handler.postDelayed(animatorTickRunnable, 500)
    }

    private val animatorTickRunnable = Runnable {
        if (tickState < 3) tickState++ else tickState = 0
        invalidate()
        mRunnable.run()
    }


    private fun startLoading() {
        animatorTickRunnable.run()
    }

    private fun stopLoading() {
        handler.removeCallbacks(mRunnable)
        handler.removeCallbacks(animatorTickRunnable)
    }

    private var isTap = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("MyLog", "action : ${event?.action}")

        if (event?.action == MotionEvent.ACTION_DOWN) {
            isTap = true
            return true
        }

        if (event?.action == MotionEvent.ACTION_UP) {
            if (isTap) {
                isTap = false
                animStarted = !animStarted
                if (animStarted) startLoading() else stopLoading()
                return true
            }
        }
        return false
    }

}