package com.boost.dbcenterapi.recycleritem


interface RecyclerStringItemClickListener {
  fun onItemClick(position: Int, item: String?, actionType: Int)
}