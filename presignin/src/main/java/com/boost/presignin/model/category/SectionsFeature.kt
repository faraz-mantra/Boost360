package com.boost.presignin.model.category

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.boost.presignin.R
import java.io.Serializable


data class SectionsFeature(
        val title: String? = null,
        val desc: String? = "",
        val icon: String? = null,
        val boost_widget_key: String? = null,
        val details: ArrayList<DetailsFeature>? = null,
) : Serializable {

    fun getWidList(): List<String> {
        return if (boost_widget_key.isNullOrEmpty()) arrayListOf() else boost_widget_key.split(",")
    }


    fun getDrawable(context: Context?): Drawable? {
        if (context == null) return null
        return when (icon?.let { FeatureTypeNew.from(it) }) {
            FeatureTypeNew.DIGITAL_CONTENT -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_documentation_n, context.theme)
            FeatureTypeNew.DIGITAL_PAYMENT -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_rupee_n, context.theme)
            FeatureTypeNew.DIGITAL_SECURITY -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_security_n, context.theme)
            FeatureTypeNew.DIGITAL_ASSISTANT -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_review_support_n, context.theme)
            FeatureTypeNew.DIGITAL_APPOINTMENT -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_digital_appointment, context.theme)
            FeatureTypeNew.DIGITAL_COMLIANCE -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_digital_comliance, context.theme)
            FeatureTypeNew.DIGITAL_CLINIC -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_digital_clinic, context.theme)
            FeatureTypeNew.DIGITAL_PROFILES -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_digital_profiles, context.theme)
            FeatureTypeNew.HOTEL_RESERVATION -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_hotel_reservation, context.theme)
            FeatureTypeNew.CUSTOMER_REVIEWS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_customer_reviews, context.theme)
            FeatureTypeNew.DIGITAL_FOOD_ORDER -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_digital_food_order, context.theme)
            FeatureTypeNew.DIGITAL_QUOTATIONS -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_digital_quotations, context.theme)
            FeatureTypeNew.DIGITAL_STOREFRONT -> ResourcesCompat.getDrawable(context.resources, R.drawable.ic_digital_storefront, context.theme)
            else -> null
        }
    }


}
