package com.nowfloats.education.helper

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.nowfloats.education.helper.Constants.DATE_FORMAT
import com.nowfloats.education.helper.Constants.getDateFromString
import com.thinksity.R


private val requestOption = RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .skipMemoryCache(true)
        .priority(Priority.IMMEDIATE)
        .encodeFormat(Bitmap.CompressFormat.PNG)
        .format(DecodeFormat.DEFAULT)

@BindingAdapter("android:src")
fun setImageUrl(view: ImageView, url: String) {
    when (view.id) {
        R.id.faculty_profile_image -> loadProfileImage(view, url)
        R.id.toppers_profile_image -> loadProfileImage(view, url)
        R.id.topper_testimonial_image -> loadProfileImage(view, url)
        else -> {
            Glide.with(view.context).load(url).circleCrop()
                    .placeholder(R.drawable.profile_icon)
                    .apply(requestOption)
                    .into(view)
        }
    }
}

private fun loadProfileImage(view: ImageView, url: String) {
    Glide.with(view.context).load(url).apply(requestOption)
            .into(view)
}

@BindingAdapter("android:src")
fun setImageResource(view: ImageView, src: Int) {
    Glide.with(view.context).load(src).into(view)
}

@BindingAdapter("android:text")
fun setText(view: TextView, text: String) {
    when (view.id) {
        R.id.date_value -> view.text = getDateMonthYearFormat(text)
        R.id.commencement_date -> view.text = getDateMonthYearFormat(text)
        else -> view.text = text
    }
}

private fun getDateMonthYearFormat(dateString: String): String = getDateFromString(dateString, DATE_FORMAT)