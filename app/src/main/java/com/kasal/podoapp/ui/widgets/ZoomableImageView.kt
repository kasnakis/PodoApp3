package com.kasal.podoapp.ui.widgets

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

class ZoomableImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private val matrixValues = FloatArray(9)
    private val imageMatrixInternal = Matrix()
    private val last = PointF()
    private var mode = NONE

    private var minScale = 1f
    private var maxScale = 4f

    private val scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var scale = currentScale() * detector.scaleFactor
            scale = max(minScale, min(scale, maxScale))
            val factor = scale / currentScale()
            imageMatrixInternal.postScale(factor, factor, detector.focusX, detector.focusY)
            imageMatrix = imageMatrixInternal
            return true
        }
    })

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val target = if (currentScale() < 2f) 2f else 1f
            val factor = target / currentScale()
            imageMatrixInternal.postScale(factor, factor, e.x, e.y)
            imageMatrix = imageMatrixInternal
            centerImage()
            return true
        }
    })

    init {
        super.setScaleType(ScaleType.MATRIX)
        imageMatrix = imageMatrixInternal
    }

    override fun setScaleType(scaleType: ScaleType?) {
        // κλειδώνουμε σε MATRIX
        super.setScaleType(ScaleType.MATRIX)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                last.set(event.x, event.y)
                mode = DRAG
            }
            MotionEvent.ACTION_POINTER_DOWN -> mode = ZOOM
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                val dx = event.x - last.x
                val dy = event.y - last.y
                imageMatrixInternal.postTranslate(dx, dy)
                imageMatrix = imageMatrixInternal
                last.set(event.x, event.y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                centerImage()
            }
        }
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerImage()
    }

    private fun currentScale(): Float {
        imageMatrixInternal.getValues(matrixValues)
        return matrixValues[Matrix.MSCALE_X]
    }

    private fun centerImage() {
        val d = drawable ?: return
        val viewW = width.toFloat()
        val viewH = height.toFloat()
        val imgW = d.intrinsicWidth.toFloat()
        val imgH = d.intrinsicHeight.toFloat()

        val scale = maxOf(viewW / imgW, viewH / imgH)
        minScale = min(1f, scale) // τουλάχιστον ταιριάζει στην οθόνη

        // Αν είμαστε στο ελάχιστο, κεντράρουμε την εικόνα
        if (currentScale() <= minScale + 0.001f) {
            imageMatrixInternal.reset()
            val dx = (viewW - imgW * minScale) / 2f
            val dy = (viewH - imgH * minScale) / 2f
            imageMatrixInternal.postScale(minScale, minScale)
            imageMatrixInternal.postTranslate(dx, dy)
            imageMatrix = imageMatrixInternal
        }
    }

    companion object {
        private const val NONE = 0
        private const val DRAG = 1
        private const val ZOOM = 2
    }
}
