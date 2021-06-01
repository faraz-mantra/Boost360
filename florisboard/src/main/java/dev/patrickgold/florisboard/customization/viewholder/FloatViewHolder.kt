package dev.patrickgold.florisboard.customization.viewholder

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.framework.utils.DateUtils.KEYBOARD_DISPLAY_DATE
import com.framework.utils.DateUtils.getDate
import com.framework.utils.DateUtils.parseDate
import com.framework.views.customViews.CustomButton
import com.framework.views.customViews.CustomImageView
import com.framework.views.customViews.CustomTextView
import com.framework.views.roundedimageview.RoundedImageView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.Float
import uk.co.deanwild.flowtextview.FlowTextView
import java.text.SimpleDateFormat
import java.util.*

class FloatViewHolder(itemView: View, val listener: OnItemClickListener?) :
    BaseRecyclerViewHolder(itemView) {

    private val desc: CustomTextView = itemView.findViewById(R.id.tv_description)
    private val image: CustomImageView = itemView.findViewById(R.id.imageView)
    private val time: CustomTextView = itemView.findViewById(R.id.ctv_date_time)
    private val shareBtn: CustomButton = itemView.findViewById(R.id.btn_share)
    private val itemCardView = itemView

    override fun bindTo(position: Int, item: BaseRecyclerItem?) {
        val float = item as Float
        if (float.imageUri?.isNotEmpty() == true) {
            image.visibility = View.VISIBLE
            Glide.with(image).load(float.imageUri).placeholder(R.drawable.default_product_image)
                .into(image)
        } else {
            image.visibility = View.GONE
        }
        desc.text = float.message
        val timeString = (float.createdOn?.subSequence(float.createdOn?.indexOf("(")!!+1,float.createdOn?.length!!-2) as String).toLong()
        time.text = getDate(timeString, KEYBOARD_DISPLAY_DATE)
        shareBtn.setOnClickListener { listener?.onItemClick(position, float) }
    }
}

