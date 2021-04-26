package com.boost.presignin.model.category

import android.os.Parcel
import android.os.Parcelable

data class DetailsFeature(
        val title: String? = null,
        val desc: String? = null,
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

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