import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.model.feature.FeatureTypeNew
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

data class SectionsFeature(
    val title: String? = null,
    val desc: String? = "",
    val icon: String? = null,
    val boost_widget_key: String? = null,
    val details: ArrayList<DetailsFeature>? = null,
) : AppBaseRecyclerViewItem, Parcelable {

  fun getWidList(): List<String> {
    return if (boost_widget_key.isNullOrEmpty()) arrayListOf() else boost_widget_key.split(",")
  }

  constructor(parcel: Parcel) : this(
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.readString(),
      parcel.createTypedArrayList(DetailsFeature.CREATOR))

  override fun getViewType(): Int {
    return RecyclerViewItemType.FEATURE_ITEM.getLayout()
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

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(title)
    parcel.writeString(desc)
    parcel.writeString(icon)
    parcel.writeString(boost_widget_key)
    parcel.writeTypedList(details)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<SectionsFeature> {
    override fun createFromParcel(parcel: Parcel): SectionsFeature {
      return SectionsFeature(parcel)
    }

    override fun newArray(size: Int): Array<SectionsFeature?> {
      return arrayOfNulls(size)
    }
  }
}