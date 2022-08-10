package com.festive.poster.ui.promoUpdates.holders

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.marginStart
import com.bumptech.glide.Glide
import com.festive.poster.FestivePosterApplication
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPastUpdateBinding
import com.festive.poster.models.promoModele.PastCategoryApiCode
import com.festive.poster.models.promoModele.PastPostItem
import com.festive.poster.models.promoModele.PastSocialModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.utils.highlightHashTag

class PastUpdateViewHolder(binding: ListItemPastUpdateBinding) :
    AppBaseRecyclerViewHolder<ListItemPastUpdateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val postItem = item as? PastPostItem
        binding.apply {
            Glide.with(FestivePosterApplication.instance).load(postItem?.imageUri)
                .placeholder(R.drawable.placeholder_image).into(ivSocialIcon)
            tvSocialTitle.setText(
                highlightHashTag(postItem?.message,
                    R.color.black_4a4a4a, R.font.bold), TextView.BufferType.SPANNABLE)

            reuseWrapper.apply {
                if (postItem?.type == PastCategoryApiCode.PROMOTIONAL_UPDATES.postCode) visible() else gone() //1: Promotional Update type

                if (postItem?.type == PastCategoryApiCode.TEXT_ONLY.postCode) { //3: Text only type
                    iconCard.gone()
                    val marginLayoutParams =
                        tvSocialTitle.layoutParams as ViewGroup.MarginLayoutParams
                    marginLayoutParams.marginStart = 0
                    tvSocialTitle.layoutParams = marginLayoutParams
                } else {
                    iconCard.visible()
                }

                if (postItem?.category == null) {
                    tvOfferTag.gone()
                } else {
                    tvOfferTag.text = postItem.category?.name
                    tvOfferTag.visible()
                }
                if (postItem?.createdOn?.contains("/Date(") == true) {
                    tvPostedDate.text = DateUtils.parseDate(
                        postItem.createdOn.replace("/Date(", "").replace(")/", ""),
                        DateUtils.FORMAT_SERVER_TO_LOCAL_7
                    )
                }

                val listSocial = resolveSocialList(postItem?.extradetails?.socialparameter)
                val socialLogoAdapter = activity?.let { AppBaseRecyclerViewAdapter(it, listSocial) }
                rvSocialLogo.adapter = socialLogoAdapter

                shareWrapper.setOnClickListener {
                    listener?.onItemClick(
                        position,
                        postItem,
                        RecyclerViewActionType.PAST_SHARE_BUTTON_CLICKED.ordinal
                    )
                }
                reuseWrapper.setOnClickListener {
                    listener?.onItemClick(
                        position,
                        postItem,
                        RecyclerViewActionType.PAST_REUSE_BUTTON_CLICKED.ordinal
                    )
                }
            }
            super.bind(position, item)
        }
    }

        private fun resolveSocialList(socialparameter: String?): ArrayList<PastSocialModel> {
            val socialArray: ArrayList<PastSocialModel> = arrayListOf()
            if (socialparameter.isNullOrBlank().not()) {
                val socialFinal = socialparameter?.dropLast(1)
                val split = socialFinal?.split(".")
                split?.forEach {
                    socialArray.add(PastSocialModel(it))
                }
            }
            return socialArray
        }
    }