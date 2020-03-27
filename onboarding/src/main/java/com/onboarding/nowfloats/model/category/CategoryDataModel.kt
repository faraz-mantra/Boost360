package com.onboarding.nowfloats.model.category

import SectionsFeature
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import androidx.core.content.res.ResourcesCompat
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.ChannelType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

class CategoryDataModel(
        val category_Key: String? = null,
        val category_Name: String? = null,
        val icon: String? = null,
        val channels: ArrayList<String>? = null,
        val sections: ArrayList<SectionsFeature>? = null
) : BaseResponse(), AppBaseRecyclerViewItem, Parcelable {
    val sectionType: Boolean = false
    var isSelected = false

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList(),
            parcel.createTypedArrayList(SectionsFeature.CREATOR)
    ) {
        isSelected = parcel.readByte() != 0.toByte()
    }

    override fun getViewType(): Int {
        return if (sectionType) RecyclerViewItemType.SECTION_HEADER_ITEM.getLayout() else RecyclerViewItemType.CATEGORY_ITEM.getLayout()
    }

    fun getChannelList(): ArrayList<ChannelModel> {
        val list = ArrayList<ChannelModel>()
        channels?.forEach {
            list.add(when (ChannelType.from(it)) {
                ChannelType.G_SEARCH -> ChannelModel("", "important", ChannelType.G_SEARCH.name, true)
                ChannelType.FB_PAGE -> ChannelModel("", "recommended", ChannelType.FB_PAGE.name)
                ChannelType.G_MAPS -> ChannelModel("", "recommended", ChannelType.G_MAPS.name)
                ChannelType.FB_SHOP -> ChannelModel("", "learn_more", ChannelType.FB_SHOP.name)
                ChannelType.WAB -> ChannelModel("", "learn_more", ChannelType.WAB.name)
                ChannelType.T_FEED -> ChannelModel("", "learn_more", ChannelType.T_FEED.name)
                ChannelType.G_BUSINESS -> ChannelModel("", "learn_more", ChannelType.G_BUSINESS.name)
            })
        }
        return list
    }

    fun getImage(context: Context?): Drawable? {
        if (context == null) return null
        return when (icon?.let { CategoryTypeNew.from(it) }) {
            CategoryTypeNew.DOCTORS_CLINICS -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_doctor,
                    context.theme
            )
            CategoryTypeNew.EDUCATION_COACHING -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_education_pro,
                    context.theme
            )
            CategoryTypeNew.HOTELS_MOTELS -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_group_mall,
                    context.theme
            )
            CategoryTypeNew.MANUFACTURING_EQUIPMENT -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_group_industry,
                    context.theme
            )
            CategoryTypeNew.SPAS_SALONS -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_group_spas,
                    context.theme
            )
            CategoryTypeNew.RESTAURANT_CAFES -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_group_food,
                    context.theme
            )
            CategoryTypeNew.RETAIL_BUSINESS -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_other_retails,
                    context.theme
            )
            CategoryTypeNew.SERVICES_BUSINESS -> ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_business_services_pro,
                    context.theme
            )
            else -> null
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(category_Key)
        parcel.writeString(category_Name)
        parcel.writeString(icon)
        parcel.writeStringList(channels)
        parcel.writeTypedList(sections)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryDataModel> {
        override fun createFromParcel(parcel: Parcel): CategoryDataModel {
            return CategoryDataModel(parcel)
        }

        override fun newArray(size: Int): Array<CategoryDataModel?> {
            return arrayOfNulls(size)
        }
    }
}
