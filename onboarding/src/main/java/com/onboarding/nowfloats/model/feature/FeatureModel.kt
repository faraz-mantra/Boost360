package com.onboarding.nowfloats.model.feature

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

@Deprecated("old")
class FeatureModel(
        val description: String? = null,
        val image: String? = null,
        val imageTpe: String? = null,
        val title: String? = null
) : AppBaseRecyclerViewItem {

    var details = ArrayList<FeatureDetailsModel>()

    override fun getViewType(): Int {
        return RecyclerViewItemType.FEATURE_ITEM.getLayout()
    }

    enum class FeatureType {
        DIGITAL_CONTENT, DIGITAL_PAYMENT, DIGITAL_SECURITY, DIGITAL_ASSISTANT
    }

    fun getDrawable(context: Context?): Drawable? {
        if (context == null) return null
        return when (FeatureType.values().firstOrNull { it.name == imageTpe }) {
            FeatureType.DIGITAL_CONTENT -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_documentation_n,
                    context.theme
            )
            FeatureType.DIGITAL_PAYMENT -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_rupee_n,
                    context.theme
            )
            FeatureType.DIGITAL_SECURITY -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_security_n,
                    context.theme
            )
            FeatureType.DIGITAL_ASSISTANT -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_review_support_n,
                    context.theme
            )
            else -> null
        }
    }
}