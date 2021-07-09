package com.onboarding.nowfloats.ui.channel

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import com.framework.base.BaseDialogFragment
import com.framework.models.BaseViewModel
import com.framework.utils.ConversionUtils
import com.framework.utils.ScreenUtils
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.DialogCategorySelectorConfirmBinding
import com.onboarding.nowfloats.extensions.fadeIn

class ChannelConfirmDialog() : BaseDialogFragment<DialogCategorySelectorConfirmBinding, BaseViewModel>(), Parcelable {

    private var count: Int = 0
    private var onConfirmClicked: () -> Unit = { }

    constructor(parcel: Parcel) : this() {
        count = parcel.readInt()
    }

    override fun getLayout(): Int {
        return R.layout.dialog_category_selector_confirm
    }

  override fun onCreateView() {
        if (count > 0) {
            val title = StringBuilder(resources.getString(R.string.you_selected) + "\n$count " + resources.getString(R.string.channel))
            if (count > 1) title.append(resources.getString(R.string.more_than_one_add_s))
            binding?.title?.text = title.toString()
        } else binding?.title?.text = ""
        setClickListeners(binding?.confirm)
        binding?.dialogRoot?.post {
          (binding?.container?.fadeIn(300L)?.mergeWith(binding?.imageRiya?.fadeIn(300L)))
                    ?.andThen(binding?.title?.fadeIn(200L)?.mergeWith(binding?.desc?.fadeIn(100L)))
                    ?.andThen(binding?.confirm?.fadeIn(50L))?.subscribe()
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.confirm -> {
                this.onConfirmClicked()
                this.dismiss()
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.MaterialDialogTheme
    }

    override fun getWidth(): Int? {
        return ScreenUtils.instance.getWidth(activity) - ConversionUtils.dp2px(32f)
    }

    fun setCount(count: Int) {
        this.count = count
    }

    fun setOnConfirmClick(closure: () -> Unit) {
        this.onConfirmClicked = closure
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChannelConfirmDialog> {
        override fun createFromParcel(parcel: Parcel): ChannelConfirmDialog {
            return ChannelConfirmDialog(parcel)
        }

        override fun newArray(size: Int): Array<ChannelConfirmDialog?> {
            return arrayOfNulls(size)
        }
    }
}