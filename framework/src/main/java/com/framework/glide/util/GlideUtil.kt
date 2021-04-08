package com.framework.glide.util

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.framework.R
import com.framework.views.CircularImageView
import com.framework.views.customViews.CustomImageView
import com.framework.views.roundedimageview.RoundedImageView
import jp.wasabeef.glide.transformations.BlurTransformation

fun View.getRequestOptionImage(placeholder: Int): RequestOptions {
  return RequestOptions().priority(Priority.IMMEDIATE).placeholder(placeholder).error(placeholder).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
      .override(width, height).skipMemoryCache(true).dontAnimate().dontTransform().encodeFormat(Bitmap.CompressFormat.PNG).format(DecodeFormat.DEFAULT)
}

fun Context.glideLoad(mImageView: CustomImageView?, url: String?) {
  if (mImageView == null) return
  Glide.with(this).load(url).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.DATA).into(mImageView)
}

fun Context.glideLoad(mImageView: CircularImageView, url: String?, placeholder: Int?, isCrop: Boolean = false) {
  try {
    if (url.isNullOrEmpty()) return
    val glide = Glide.with(this).load(url).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    placeholder?.let { glide.placeholder(it) }
    if (isCrop) glide.centerCrop()
    glide.into(mImageView)
  } catch (e: Exception) {
    Log.e("GlideUtil", "Error: ${e.localizedMessage}")
  }
}


fun Context.glideLoad(mImageView: RoundedImageView?, url: String?) {
  try {
    if (mImageView == null) return
    Glide.with(this).load(url).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.DATA).into(mImageView)
  } catch (e: Exception) {
    Log.e("GlideUtil", "Error: ${e.localizedMessage}")
  }
}

fun Context.glideLoad(mImageView: RoundedImageView, url: String?, placeholder: Int?, isCrop: Boolean = false) {
  try {
    if (url.isNullOrEmpty()) return
    val glide = Glide.with(this).load(url).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    placeholder?.let { glide.placeholder(it) }
    if (isCrop) glide.centerCrop()
    glide.into(mImageView)
  } catch (e: Exception) {
    Log.e("GlideUtil", "Error: ${e.localizedMessage}")
  }
}

fun Context.glideLoad(mImageView: CustomImageView, url: String?, placeholder: Int?, isCrop: Boolean = false) {
  try {
    if (url.isNullOrEmpty()) return
    val glide = Glide.with(this).load(url).skipMemoryCache(true).thumbnail(0.1f).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    placeholder?.let { glide.placeholder(it) }
    if (isCrop) glide.centerCrop()
    glide.into(mImageView)
  } catch (e: Exception) {
    Log.e("GlideUtil", "Error: ${e.localizedMessage}")
  }
}

fun Context.loadGifGlide(mImageView: CustomImageView, gif_file: Int?, placeholder: Int?) {
  try {
    val glide = Glide.with(this).load(gif_file).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    placeholder?.let { glide.placeholder(it) }
    glide.into(DrawableImageViewTarget(mImageView))
  } catch (e: Exception) {
    Log.e("GlideUtil", "Error: ${e.localizedMessage}")
  }
}

fun Context.glideLoad(mImageView: CustomImageView, imgID: Int){
  try {
    Glide.with(this).load(imgID).override(600, 600).into(mImageView);
  }catch (e:Exception){
    e.printStackTrace()
  }
}


fun Activity.glideLoad(mImageView: CustomImageView, url: String, placeholder: Int, isCenterCrop: Boolean = false, isLoadBitmap: Boolean = false) {
  try {
    val options: RequestOptions = mImageView.getRequestOptionImage(placeholder)
    val glideImage = Glide.with(this).load(url).apply(options)
    if (isCenterCrop) glideImage.centerCrop()
    if (isLoadBitmap) {
      glideImage.into(object : CustomTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
          mImageView.setImageDrawable(resource)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
        }
      })
    } else glideImage.into(mImageView)
  } catch (e: Exception) {
    Log.e("GlideUtil", "Error: ${e.localizedMessage}")
  }
}

fun Context.glideLoadCircle(mImageView: CustomImageView, url: String) {
  Glide.with(this).load(url)
      .apply(RequestOptions.circleCropTransform()).skipMemoryCache(true)
      .diskCacheStrategy(DiskCacheStrategy.DATA).into(mImageView)
}

fun Context.glideLoadBlur(mImageView: CustomImageView, url: String) {
  Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.DATA)
      .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3))).into(mImageView)
}

fun Context.glideLoadColor(mImageView: CustomImageView, url: String, view: View) {
  Glide.with(this)
      .asBitmap().load(url)
      .diskCacheStrategy(DiskCacheStrategy.ALL)
      .listener(object : RequestListener<Bitmap> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Bitmap>?,
            isFirstResource: Boolean,
        ): Boolean {
          return false
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onResourceReady(
            resource: Bitmap?,
            model: Any,
            target: Target<Bitmap>,
            dataSource: DataSource,
            isFirstResource: Boolean,
        ): Boolean {
          if (resource != null) {
            val p = Palette.from(resource).generate()
            // Use generated instance
            view.setUpInfoBackgroundColor(p)
          }
          return false
        }
      }).into(mImageView)
}


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private fun View.setUpInfoBackgroundColor(palette: Palette) {
  val swatch = getMostPopulousSwatch(palette)
  if (swatch != null) {
    val startColor = ContextCompat.getColor(context, R.color.secondary_text)
    val endColor = swatch.rgb
    backgroundTintList = ColorStateList.valueOf(endColor)
  }
}

fun getMostPopulousSwatch(palette: Palette?): Palette.Swatch? {
  var mostPopulous: Palette.Swatch? = null
  if (palette != null) {
    for (swatch in palette.swatches) {
      if (mostPopulous == null || swatch.population > mostPopulous.population) {
        mostPopulous = swatch
      }
    }
  }
  return mostPopulous
}

