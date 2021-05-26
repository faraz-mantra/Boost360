package dev.patrickgold.florisboard.customization.viewholder

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
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

    private val desc: FlowTextView = itemView.findViewById(R.id.flowing_text_view)
    private val image: RoundedImageView = itemView.findViewById(R.id.image_update)
    private val time: CustomTextView = itemView.findViewById(R.id.ctv_time)
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
        time.text = "${float.createdOn}"
        itemCardView.setOnClickListener { listener?.onItemClick(position, float) }
    }
}

fun getDate(milliSeconds: Long, dateFormat: String): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(dateFormat)
    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = milliSeconds
    return formatter.format(calendar.time)
}