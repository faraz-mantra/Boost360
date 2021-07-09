package com.framework.views.blur

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.annotation.Nullable


/**
 * Blur Controller that handles all blur logic for the attached View.
 * It honors View size changes, View animation and Visibility changes.
 *
 *
 * The basic idea is to draw the view hierarchy on a bitmap, excluding the attached View,
 * then blur and draw it on the system Canvas.
 *
 *
 * It uses [ViewTreeObserver.OnPreDrawListener] to detect when
 * blur should be updated.
 *
 *
 * Blur is done on the main thread.
 */
class BlockingBlurController(@param:NonNull val blurView: View, @param:NonNull private val rootView: ViewGroup, @param:ColorInt private var overlayColor: Int) : BlurController {
    private val scaleFactor = BlurController.DEFAULT_SCALE_FACTOR
    private var blurRadius = BlurController.DEFAULT_BLUR_RADIUS
    private var roundingWidthScaleFactor = 1f
    private var roundingHeightScaleFactor = 1f
    private var blurAlgorithm: BlurAlgorithm?
    private var internalCanvas: Canvas? = null
    private var internalBitmap: Bitmap? = null
    private val rootLocation = IntArray(2)
    private val blurViewLocation = IntArray(2)
    private val drawListener = ViewTreeObserver.OnPreDrawListener { // Not invalidating a View here, just updating the Bitmap.
        // This relies on the HW accelerated bitmap drawing behavior in Android
        // If the bitmap was drawn on HW accelerated canvas, it holds a reference to it and on next
        // drawing pass the updated content of the bitmap will be rendered on the screen
        updateBlur()
        true
    }
    private var blurEnabled = true
    private var initialized = false

    @Nullable
    private var frameClearDrawable: Drawable? = null
    private var hasFixedTransformationMatrix = false
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private fun downScaleSize(value: Float): Int {
        return Math.ceil(value / scaleFactor.toDouble()).toInt()
    }

    /**
     * Rounds a value to the nearest divisible by [.ROUNDING_VALUE] to meet stride requirement
     */
    private fun roundSize(value: Int): Int {
        return if (value % ROUNDING_VALUE == 0) {
            value
        } else value - value % ROUNDING_VALUE + ROUNDING_VALUE
    }

    fun init(measuredWidth: Int, measuredHeight: Int) {
        if (isZeroSized(measuredWidth, measuredHeight)) {
            blurView.setWillNotDraw(true)
            return
        }
        blurView.setWillNotDraw(false)
        allocateBitmap(measuredWidth, measuredHeight)
        internalCanvas = internalBitmap?.let { Canvas(it) }
        initialized = true
        if (hasFixedTransformationMatrix) {
            setupInternalCanvasMatrix()
        }
    }

    private fun isZeroSized(measuredWidth: Int, measuredHeight: Int): Boolean {
        return downScaleSize(measuredHeight.toFloat()) == 0 || downScaleSize(measuredWidth.toFloat()) == 0
    }

    fun updateBlur() {
        if (!blurEnabled || !initialized) {
            return
        }
        if (frameClearDrawable == null) {
            internalBitmap?.eraseColor(Color.TRANSPARENT)
        } else {
            internalCanvas?.let { frameClearDrawable?.draw(it) }
        }
        if (hasFixedTransformationMatrix) {
            rootView.draw(internalCanvas)
        } else {
            internalCanvas?.save()
            setupInternalCanvasMatrix()
            rootView.draw(internalCanvas)
            internalCanvas?.restore()
        }
        blurAndSave()
    }

    /**
     * Deferring initialization until view is laid out
     */
    private fun deferBitmapCreation() {
        blurView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    blurView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    legacyRemoveOnGlobalLayoutListener()
                }
                val measuredWidth = blurView.measuredWidth
                val measuredHeight = blurView.measuredHeight
                init(measuredWidth, measuredHeight)
            }

            fun legacyRemoveOnGlobalLayoutListener() {
                blurView.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        })
    }

    private fun allocateBitmap(measuredWidth: Int, measuredHeight: Int) {
        val nonRoundedScaledWidth = downScaleSize(measuredWidth.toFloat())
        val nonRoundedScaledHeight = downScaleSize(measuredHeight.toFloat())
        val scaledWidth = roundSize(nonRoundedScaledWidth)
        val scaledHeight = roundSize(nonRoundedScaledHeight)
        roundingHeightScaleFactor = nonRoundedScaledHeight.toFloat() / scaledHeight
        roundingWidthScaleFactor = nonRoundedScaledWidth.toFloat() / scaledWidth
        internalBitmap = blurAlgorithm?.supportedBitmapConfig?.let { Bitmap.createBitmap(scaledWidth, scaledHeight, it) }
    }

    /**
     * Set up matrix to draw starting from blurView's position
     */
    private fun setupInternalCanvasMatrix() {
        rootView.getLocationOnScreen(rootLocation)
        blurView.getLocationOnScreen(blurViewLocation)
        val left = blurViewLocation[0] - rootLocation[0]
        val top = blurViewLocation[1] - rootLocation[1]
        val scaleFactorX = scaleFactor * roundingWidthScaleFactor
        val scaleFactorY = scaleFactor * roundingHeightScaleFactor
        val scaledLeftPosition = -left / scaleFactorX
        val scaledTopPosition = -top / scaleFactorY
        internalCanvas!!.translate(scaledLeftPosition, scaledTopPosition)
        internalCanvas!!.scale(1 / scaleFactorX, 1 / scaleFactorY)
    }

    override fun draw(canvas: Canvas?): Boolean {
        if (!blurEnabled || !initialized) {
            return true
        }
        // Not blurring own children
        if (canvas === internalCanvas) {
            return false
        }
        updateBlur()
        canvas!!.save()
        canvas.scale(scaleFactor * roundingWidthScaleFactor, scaleFactor * roundingHeightScaleFactor)
        internalBitmap?.let { canvas.drawBitmap(it, 0f, 0f, paint) }
        canvas.restore()
        if (overlayColor != TRANSPARENT) {
            canvas.drawColor(overlayColor)
        }
        return true
    }

    private fun blurAndSave() {
        internalBitmap = blurAlgorithm?.blur(internalBitmap, blurRadius)
        if ((blurAlgorithm?.canModifyBitmap()==true).not()) {
            internalCanvas?.setBitmap(internalBitmap)
        }
    }

    override fun updateBlurViewSize() {
        val measuredWidth = blurView.measuredWidth
        val measuredHeight = blurView.measuredHeight
        init(measuredWidth, measuredHeight)
    }

    override fun destroy() {
        setBlurAutoUpdate(false)
        blurAlgorithm!!.destroy()
        initialized = false
    }

    override fun setBlurRadius(radius: Float): BlurViewFacade? {
        blurRadius = radius
        return this
    }

    override fun setBlurAlgorithm(algorithm: BlurAlgorithm?): BlurViewFacade? {
        blurAlgorithm = algorithm
        return this
    }

    override fun setFrameClearDrawable(@Nullable frameClearDrawable: Drawable?): BlurViewFacade? {
        this.frameClearDrawable = frameClearDrawable
        return this
    }

    override fun setBlurEnabled(enabled: Boolean): BlurViewFacade? {
        blurEnabled = enabled
        setBlurAutoUpdate(enabled)
        blurView.invalidate()
        return this
    }

    override fun setBlurAutoUpdate(enabled: Boolean): BlurViewFacade? {
        blurView.viewTreeObserver.removeOnPreDrawListener(drawListener)
        if (enabled) {
            blurView.viewTreeObserver.addOnPreDrawListener(drawListener)
        }
        return this
    }

    override fun setHasFixedTransformationMatrix(hasFixedTransformationMatrix: Boolean): BlurViewFacade? {
        this.hasFixedTransformationMatrix = hasFixedTransformationMatrix
        return this
    }

    override fun setOverlayColor(overlayColor: Int): BlurViewFacade? {
        if (this.overlayColor != overlayColor) {
            this.overlayColor = overlayColor
            blurView.invalidate()
        }
        return this
    }

    companion object {
        // Bitmap size should be divisible by ROUNDING_VALUE to meet stride requirement.
        // This will help avoiding an extra bitmap allocation when passing the bitmap to RenderScript for blur.
        // Usually it's 16, but on Samsung devices it's 64 for some reason.
        private const val ROUNDING_VALUE = 64

        @ColorInt
        val TRANSPARENT = 0
    }

    /**
     * @param blurView View which will draw it's blurred underlying content
     * @param rootView Root View where blurView's underlying content starts drawing.
     * Can be Activity's root content layout (android.R.id.content)
     * or some of your custom root layouts.
     */
    init {
        blurAlgorithm = NoOpBlurAlgorithm()
        val measuredWidth = blurView.measuredWidth
        val measuredHeight = blurView.measuredHeight
        if (isZeroSized(measuredWidth, measuredHeight)) {
            deferBitmapCreation()
        } else init(measuredWidth, measuredHeight)
    }
}