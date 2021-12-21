package dev.patrickgold.florisboard.customization.adapter

interface OnItemClickListener {
    fun onItemClick(pos: Int, item: BaseRecyclerItem,actionType:Int)
}