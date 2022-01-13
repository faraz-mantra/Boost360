package com.framework.views.blur

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.renderscript.Element;

/**
 * Blur using RenderScript, processed on GPU.
 * Uses Renderscript from support library
 */
class SupportRenderScriptBlur(context: Context?) : BlurAlgorithm {
  private val renderScript: RenderScript = RenderScript.create(context)
  private val blurScript: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
  private var outAllocation: Allocation? = null
  private var lastBitmapWidth = -1
  private var lastBitmapHeight = -1
  private fun canReuseAllocation(bitmap: Bitmap?): Boolean {
    return bitmap!!.height == lastBitmapHeight && bitmap.width == lastBitmapWidth
  }

  /**
   * @param bitmap     bitmap to blur
   * @param blurRadius blur radius (1..25)
   * @return blurred bitmap
   */
  override fun blur(bitmap: Bitmap?, blurRadius: Float): Bitmap? {
    //Allocation will use the same backing array of pixels as bitmap if created with USAGE_SHARED flag
    val inAllocation: Allocation = Allocation.createFromBitmap(renderScript, bitmap)
    if (!canReuseAllocation(bitmap)) {
      if (outAllocation != null) {
        outAllocation?.destroy()
      }
      outAllocation = Allocation.createTyped(renderScript, inAllocation.getType())
      lastBitmapWidth = bitmap!!.width
      lastBitmapHeight = bitmap.height
    }
    blurScript.setRadius(blurRadius)
    blurScript.setInput(inAllocation)
    //do not use inAllocation in forEach. it will cause visual artifacts on blurred Bitmap
    blurScript.forEach(outAllocation)
    outAllocation?.copyTo(bitmap)
    inAllocation.destroy()
    return bitmap
  }

  override fun destroy() {
    blurScript.destroy()
    renderScript.destroy()
    if (outAllocation != null) {
      outAllocation!!.destroy()
    }
  }

  override fun canModifyBitmap(): Boolean {
    return true
  }

  override val supportedBitmapConfig: Bitmap.Config
    get() = Bitmap.Config.ARGB_8888

}