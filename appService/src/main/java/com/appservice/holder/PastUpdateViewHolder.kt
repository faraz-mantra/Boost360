package com.appservice.holder

import android.view.ViewGroup
import android.widget.TextView
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ListItemPastUpdateBinding
import com.appservice.model.updateBusiness.pastupdates.PastCategoryApiCode
import com.appservice.model.updateBusiness.pastupdates.PastPostItem
import com.appservice.model.updateBusiness.pastupdates.PastSocialModel
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.bumptech.glide.Glide
import com.framework.BaseApplication
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.utils.highlightHashTag

class PastUpdateViewHolder(binding: ListItemPastUpdateBinding) :
    AppBaseRecyclerViewHolder<ListItemPastUpdateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val postItem = item as? PastPostItem
        binding.apply {
            Glide.with(BaseApplication.instance).load(postItem?.imageUri)
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