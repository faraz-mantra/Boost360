package dev.patrickgold.florisboard.customization.adapter


interface RecyclerItemClickListener {
  fun onItemClick(position: Int, item: BaseRecyclerItem?, actionType: Int)
}