package dev.patrickgold.florisboard.customization.viewholder

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.Float

class FloatViewHolder(itemView: View, val listener: OnItemClickListener?) : BaseRecyclerViewHolder(itemView) {

  private val desc: TextView = itemView.findViewById(R.id.textView)
  private val image: ImageView = itemView.findViewById(R.id.imageView)
  private val itemCardView = itemView

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val float = item as Float
    var line = 3
    if (float.imageUri?.isNotEmpty() == true) {
      image.visibility = View.VISIBLE
      Glide.with(image).load(float.imageUri).placeholder(R.drawable.default_product_image).into(image)
    } else {
      line = 8
      image.visibility = View.GONE
    }
    desc.apply {
      maxLines = line
      ellipsize = TextUtils.TruncateAt.END
      text = float.message
    }
    itemCardView.setOnClickListener { listener?.onItemClick(position, float) }
  }
}