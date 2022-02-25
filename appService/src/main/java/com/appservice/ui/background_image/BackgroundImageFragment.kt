package com.appservice.ui.background_image

import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentActiveDomainBinding
import com.appservice.databinding.FragmentBackgroundImageBinding
import com.framework.analytics.SentryController
import com.framework.models.BaseViewModel
import com.squareup.picasso.Picasso
import java.util.ArrayList

class BackgroundImageFragment : AppBaseFragment<FragmentBackgroundImageBinding,BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_background_image
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    private val backgroundImages: Unit
        private get() {
            apiDataLoaded = false
            binding!!.pbLoading.setVisibility(View.VISIBLE)
            val imageApi = Constants.restAdapter.create(ImageApi::class.java)
            imageApi.getBackgroundImages(session!!.fpid, Constants.clientId, object : Callback<List<String?>?> {
                override fun success(strings: List<String?>?, response: Response) {
                    binding!!.pbLoading.setVisibility(View.GONE)
                    apiDataLoaded = true
                    if (strings != null && strings.size > 0) {
                        adapter!!.setData(strings)
                    }
                }

                override fun failure(error: RetrofitError) {
                    binding!!.pbLoading.setVisibility(View.GONE)
                }
            })
        }

    internal inner class ImagesRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
        var images: MutableList<String?>? = ArrayList()
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
            val view: View = LayoutInflater.from(viewGroup.context).inflate(com.thinksity.R.layout.recyclerview_background_images, viewGroup, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {
            if (holder is ImageViewHolder) {
                val url = images!![i]
                try {
                    if (!TextUtils.isEmpty(url)) {
                        Picasso.get().load(url).into(holder.imageView)
                    }
                } catch (e: Exception) {
                    SentryController.captureException(e)
                    e.printStackTrace()
                }
            }
        }

        override fun getItemCount(): Int {
            return if (images == null) 0 else images!!.size
        }

        fun setData(images: List<String?>?) {
            this.images!!.clear()
            this.images!!.addAll(images!!)
            notifyDataSetChanged()
        }

        fun addImage(url: String) {
            images!!.add(0, url)
            notifyDataSetChanged()
        }

        fun removeImage(position: Int) {
            images!!.removeAt(position)
            notifyDataSetChanged()
        }

        internal inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var imageView: ImageView

            init {
                imageView = itemView.findViewById(R.id.image)
                itemView.setOnClickListener { v: View? ->
                    val array = arrayOfNulls<String>(images!!.size)
//                    images.addAll(array)
                    val intent = Intent(requireContext(), ImageViewerActivity::class.java)
                    intent.putExtra("POSITION", adapterPosition)
                    intent.putExtra("IMAGES", array)
                    startActivityForResult(intent, IMAGE_DELETE_REQUEST_CODE)
                }
            }
        }
    }
}