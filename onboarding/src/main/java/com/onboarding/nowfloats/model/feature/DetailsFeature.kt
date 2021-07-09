import android.os.Parcel
import android.os.Parcelable
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

data class DetailsFeature(
    val title: String? = null,
    val desc: String? = null,
) : AppBaseRecyclerViewItem, Parcelable {

  constructor(parcel: Parcel) : this(
      parcel.readString(),
      parcel.readString())

  override fun getViewType(): Int {
    return RecyclerViewItemType.FEATURE_DETAILS_BOTTOM_SHEET_ITEM.getLayout()
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(title)
    parcel.writeString(desc)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<DetailsFeature> {
    override fun createFromParcel(parcel: Parcel): DetailsFeature {
      return DetailsFeature(parcel)
    }

    override fun newArray(size: Int): Array<DetailsFeature?> {
      return arrayOfNulls(size)
    }
  }
}