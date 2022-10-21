package com.framework.glide.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.bumptech.glide.request.transition.Transition
import com.framework.glide.GlideApp
import com.framework.glide.util.loadGifGlide
import com.framework.views.customViews.CustomImageView
import java.io.File

class ImageService constructor(val context: Context) {
  /** Compose views */
  fun loadGif(
    imageUrl: String,
    thumbnailUrl: String,
    onResourceReady: (GifDrawable?) -> Unit,
    onLoadFailed: () -> Unit,
  ) {
    loadGif(imageUrl)
      .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
      .thumbnail(loadGif(thumbnailUrl))
      .into(object : CustomTarget<GifDrawable>() {
        override fun onLoadFailed(errorDrawable: Drawable?) {
          super.onLoadFailed(errorDrawable)
          onLoadFailed.invoke()
        }

        override fun onLoadCleared(placeholder: Drawable?) {
          onLoadFailed.invoke()
        }

        override fun onResourceReady(
          resource: GifDrawable,
          transition: Transition<in GifDrawable>?,
        ) {
          onResourceReady.invoke(resource)
        }
      })
  }

  /** ImageViews */
  fun loadGif(imageUrl: String, @DrawableRes placeholder: Int?, imageView: CustomImageView, onResourceReady: () -> Unit, onLoadFailed: (GlideException?) -> Unit) {
    loadGif(imageUrl)
      .override(SIZE_ORIGINAL, SIZE_ORIGINAL)
      .error(placeholder)
      .listener(
        object : RequestListener<GifDrawable> {
          override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
            onLoadFailed.invoke(e)
            return false
          }

          override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            onResourceReady.invoke()
            return false
          }
        }
      )
      .into(imageView)
      .clearOnDetach()
  }

  private fun loadGif(imageUrl: String): RequestBuilder<GifDrawable> {
    return GlideApp.with(context).asGif().transition(withCrossFade()).load(imageUrl)
  }

  fun loadGif(imageView: CustomImageView, file: File, placeholder: Int? = null, onResourceReady: () -> Unit, onLoadFailed: (GlideException?) -> Unit) {
    context.loadGifGlide(imageView, file, placeholder)
  }

}