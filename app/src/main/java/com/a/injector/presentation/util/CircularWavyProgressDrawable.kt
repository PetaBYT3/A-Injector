package com.a.injector.presentation.util

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CircularWavyProgressDrawable(
    color: Int,
    private val strokeWidthPx: Float = 10f,
    private val waveCount: Int = 8,
    private val waveAmplitudePx: Float = 6f,
    durationMs: Long = 1200L
) : Drawable(), Animatable {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        this.color = color
        strokeCap = Paint.Cap.ROUND
    }

    private val path = Path()
    private var phase = 0f

    private val animator = ValueAnimator.ofFloat(0f, (2 * PI).toFloat()).apply {
        duration = durationMs
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            phase = it.animatedValue as Float
            invalidateSelf()
        }
    }

    override fun draw(canvas: Canvas) {
        val b = bounds
        if (b.isEmpty) return

        val cx = b.exactCenterX()
        val cy = b.exactCenterY()
        val baseRadius = (min(b.width(), b.height()) / 2f) - strokeWidthPx - waveAmplitudePx

        if (baseRadius <= 0) return

        path.reset()
        val steps = 120
        for (i in 0..steps) {
            val theta = (i.toFloat() / steps) * (2 * PI).toFloat()
            // Persamaan gelombang sinus mengelilingi lingkaran
            val r = baseRadius + waveAmplitudePx * sin(waveCount * theta - phase)
            val x = cx + r * cos(theta)
            val y = cy + r * sin(theta)

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        canvas.drawPath(path, paint)
    }

    override fun start() {
        if (!animator.isRunning) animator.start()
    }

    override fun stop() {
        if (animator.isRunning) animator.cancel()
    }

    override fun isRunning(): Boolean = animator.isRunning

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}