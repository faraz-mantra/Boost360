import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R

class HelpVideosViewPagerAdapter(private var ctx: Context) : RecyclerView.Adapter<HelpVideosViewPagerAdapter.ViewHolder>() {

  private val images = intArrayOf(
    R.drawable.website,
    R.drawable.website,
    R.drawable.website,
    R.drawable.website,)


  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view: View = LayoutInflater.from(ctx).inflate(R.layout.image_preview_item, parent, false)
    val lp = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
    )
    view.layoutParams = lp
     ctx= view.context
    return ViewHolder(view)
  }


  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    holder.images.setImageResource(images[position])
  }


  override fun getItemCount(): Int {
    return images.size
  }


  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var images: ImageView

    init {
      images = itemView.findViewById(R.id.preview_image)
    }
  }
}