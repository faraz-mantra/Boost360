package com.framework.glide

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

class GlideImageLoader(
        private val mImageView: ImageView?,
        private val mProgressBar: ProgressBar?
) { //1

    fun load(url: String?, options: RequestOptions?) { //2
        if (options == null) return

        onConnecting() //3

        DispatchingProgressManager.expect(
                url,
                object : UIonProgressListener { //4

                    override val granularityPercentage: Float //5
                        get() = 1.0f

                    override fun onProgress(bytesRead: Long, expectedLength: Long) {
                        if (mProgressBar != null) {
                            mProgressBar.progress = (100 * bytesRead / expectedLength).toInt() //6
                        }
                    }
                })

        Glide.with(mImageView!!.context) //7
                .load(url)
                .apply(options) //8
                .listener(object : RequestListener<Drawable> { //9
                    override fun onLoadFailed(
                            e: GlideException?, model: Any?, target: Target<Drawable>?,
                            isFirstResource: Boolean
                    ): Boolean {
                        DispatchingProgressManager.forget(url)
                        onFinished()
                        return false
                    }

                    override fun onResourceReady(
                            resource: Drawable?, model: Any?, target: Target<Drawable>?,
                            dataSource: DataSource?, isFirstResource: Boolean
                    ): Boolean {

                        DispatchingProgressManager.forget(url)
                        onFinished()
                        return false
                    }
                })
                .into(mImageView)
    }


    private fun onConnecting() {
        if (mProgressBar != null) mProgressBar.visibility = View.VISIBLE
    }

    private fun onFinished() {
        if (mProgressBar != null && mImageView != null) {
            mProgressBar.visibility = View.GONE
            mImageView.visibility = View.VISIBLE
        }
    }
}