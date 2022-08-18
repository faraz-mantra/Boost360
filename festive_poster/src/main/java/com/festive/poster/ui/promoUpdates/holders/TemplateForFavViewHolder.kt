package com.festive.poster.ui.promoUpdates.holders

import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemTemplateForFavBinding
import com.festive.poster.models.BrowseAllTemplate
import com.festive.poster.models.FavTemplate
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.ui.promoUpdates.edit_post.EditPostActivity
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.framework.webengageconstant.Promotional_Update_Edit_Click
import com.framework.webengageconstant.Promotional_Update_Post_Click
import com.framework.webengageconstant.Promotional_Update_WhatsApp_Share_Click
import java.text.SimpleDateFormat
import java.util.*


class TemplateForFavViewHolder(binding: ListItemTemplateForFavBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateForFavBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as FavTemplate


        val formattedDate  =formateDate(model.favDate)
        binding.tvFavOn.text =activity?.getString(R.string.favourited_on)+formattedDate


        if (model.isFavourite){
            binding.ivLove.setTintColor(getColor(R.color.colorEB5757)!!)
        }else{
            binding.ivLove.setTintColor(getColor(R.color.colorDBDBDB)!!)

        }
        SvgUtils.loadImage(model.primarySvgUrl, binding.ivSvg)
        binding.btnShare.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_WhatsApp_Share_Click)
            listener?.onItemClick(position,item, RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal)
        }

        binding.cardLove.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal)
        }
        binding.tvTemplateDesc.text =model.primaryText

        binding.btnPost.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Post_Click)
           // activity?.let { it1 -> PostPreviewSocialActivity.launchActivity(it1,null,model) }
           // EditPostActivity.launchActivity(binding.root.context,model)
            listener?.onItemClick(position,item, RecyclerViewActionType.POST_CLICKED.ordinal)


        }
        binding.btnEdit.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Edit_Click)

            EditPostActivity.launchActivity(binding.root.context,model)
        }
        super.bind(position, item)

    }

    fun formateDate(epoch:Long): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        dateFormat.timeZone = TimeZone.getDefault()
       return dateFormat.format(Date(epoch*1000L))
    }
}