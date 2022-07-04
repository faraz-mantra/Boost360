package com.festive.poster.models.promoModele

import android.content.Context
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

class PastUpdateModel(
    val type: String? = null,
    val tags: String? = null,
    val status: String? = null,
    val SocialChannels: ArrayList<ChannelNamesModel> = arrayListOf(),
    val _id: String? = null,
    val message: String? = null,
    val groupMessageId: String? = null,
    val imageUri: String? = null,
    val tileImageUri: String? = null,
    val url: String? = null,
    val htmlString: String? = null,
    val isHtmlString: Boolean? = null,
    val createdOn: String? = null,
    val updatedOn: String? = null

) : Serializable, AppBaseRecyclerViewItem {

    fun getData(context: Context): ArrayList<PastUpdateModel> {
        return arrayListOf(
            PastUpdateModel(
                type = "",
                tags = "",
                status = "",
                SocialChannels = arrayListOf(ChannelNamesModel("FACEBOOKPAGE"), ChannelNamesModel("TWITTER")),
                _id = "",
                message = "Black friday sale upto 50% discount on our postselected merchandise, only at Smiley Dental Clinic.\n" +
                        "#dentalcare  #book  #appointments",
                groupMessageId = "",
                imageUri = "",
                tileImageUri = "",
                url = "",
                htmlString = "",
                isHtmlString = false,
                createdOn = "16th June 2021, 3:24 pm",
                updatedOn = "",
            ),
            PastUpdateModel(
                type = "",
                tags = "",
                status = "",
                SocialChannels = arrayListOf(ChannelNamesModel("INSTAGRAM")),
                _id = "",
                message = "Purdue University has been named one of Fast Company’s “Brands That Matter,” a list honoring companies and organizations that give people.",
                groupMessageId = "",
                imageUri = "",
                tileImageUri = "",
                url = "",
                htmlString = "",
                isHtmlString = false,
                createdOn = "19th June 2022, 1:00 pm",
                updatedOn = "",
            )

        )
    }

    override fun getViewType(): Int {
        return RecyclerViewItemType.PAST_UPDATE_ITEM.getLayout()
    }
}
