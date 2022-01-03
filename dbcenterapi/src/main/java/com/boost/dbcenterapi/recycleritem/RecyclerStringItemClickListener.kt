package com.boost.dbcenterapi.recycleritem


interface RecyclerStringItemClickListener {
  fun onStringItemClick(position: Int, item: String?, actionType: Int)
}