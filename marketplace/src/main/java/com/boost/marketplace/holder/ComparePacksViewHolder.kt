package com.boost.marketplace.holder

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType
import com.boost.marketplace.R
import com.boost.marketplace.databinding.ItemComparePacksBinding
import com.boost.marketplace.infra.api.models.test.Packs_Data
import com.boost.marketplace.infra.api.models.test.getDatas2
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewAdapter

class ComparePacksViewHolder(binding: ItemComparePacksBinding)  :
    AppBaseRecyclerViewHolder<ItemComparePacksBinding>(binding), RecyclerItemClickListener {

    private var adapterPacks: AppBaseRecyclerViewAdapter<Packs_Data>? = null
    // var list: ArrayList<Packs_Data>? = null
    //lateinit var list: java.util.ArrayList<Bundles>
//    private var list = ArrayList<Bundles>()


    override fun bind(
        position: Int,
        item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
    ) {
        super.bind(position, item)
    //    val data = item as? Bundles ?: return
      //  binding.packageTitle.text = data.type?.title


//        val parentItem = list[position]
//
//        // For the created instance,
//        // get the title and set it
//        // as the text for the TextView
//        binding.packageTitle.text = parentItem.name
//        val data = parentItem.name
//        //        val items = data!!.split(" ".toRegex()).toTypedArray()
//        val items = data!!.split(" ".toRegex())
//        if(items.size == 1){
//            binding.packageTitle.text = items[0]
//        }else if(items.size == 2){
//            binding.packageTitle.text = items[0] + " \n" + items[1]
//        }else if(items.size == 3){
//            binding.packageTitle.text = items[0] + " \n" + items[1] + " " + items[2]
//        }else if(items.size == 4){
//            binding.packageTitle.text = items[0] + " " + items[1]  + " \n"  +items[2] + " "  + items[3]
//        }else if(items.size == 5){
//            binding.packageTitle.text = items[0] + " " + items[1]  + " \n"  +items[2] + " "  + items[3] + " " +items[4]
//        }




        if (adapterPacks == null) {
            binding.childRecyclerview.apply {
                adapterPacks = activity?.let {
                    AppBaseRecyclerViewAdapter(
                        it,
                        getDatas2(RecyclerViewItemType.TOP_FEATURES.ordinal),
                        this@ComparePacksViewHolder
                    )

                }
                adapter = adapterPacks
            }
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        listener?.onItemClick(position, item, actionType)

        binding.packageAddCartNew.setOnClickListener {

          //  Toast.makeText(getApplicationContext(), "Added to cart", Toast.LENGTH_LONG).show()
            binding.packageAddCartNew.background = getApplicationContext()?.let { it1 ->
                ContextCompat.getDrawable(
                    it1,
                    R.drawable.button_added_to_cart
                )
            }
            binding.packageAddCartNew.setTextColor(getResources()!!.getColor(R.color.tv_color_BB))
            binding.packageAddCartNew.setText((R.string.added_to_carts))
        }

        }
    }




