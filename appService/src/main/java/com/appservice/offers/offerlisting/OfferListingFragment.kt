package com.appservice.offers.offerlisting
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentOfferListingBinding
import com.appservice.offers.models.DataItem
import com.appservice.offers.models.OfferListingRequest
import com.appservice.offers.models.OfferListingResponse
import com.appservice.offers.models.Result
import com.appservice.offers.startOfferFragmentActivity
import com.appservice.offers.viewmodel.OfferViewModel
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.ui.UserSession
import com.appservice.ui.catalog.catalogService.listing.ServiceListingFragment
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.model.ItemsItem
import com.framework.extensions.gone
import com.framework.extensions.invisible
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.NetworkUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.util.*

class OfferListingFragment : AppBaseFragment<FragmentOfferListingBinding, OfferViewModel>(), RecyclerItemClickListener {
    private val list: ArrayList<DataItem> = arrayListOf()
    private val finalList: ArrayList<DataItem> = arrayListOf()
    private var adapterOffers: AppBaseRecyclerViewAdapter<DataItem>? = null
    private var layoutManagerN: LinearLayoutManager? = null
    private var targetMap: Target? = null
    private var isLoadingD = false
    private var totalElements = 0
    private var offSet: Int = PaginationScrollListener.PAGE_START
    private var limit: Int = PaginationScrollListener.PAGE_SIZE
    private var isLastPageD = false
    override fun getLayout(): Int {
        return R.layout.fragment_offer_listing
    }

    override fun getViewModelClass(): Class<OfferViewModel> {
        return OfferViewModel::class.java
    }

    companion object {
        fun newInstance(): OfferListingFragment {
            return OfferListingFragment()
        }

        private const val STORAGE_CODE = 120
        var defaultShareGlobal = true
        var shareType = 2
        var offerItem: DataItem? = null

    }

    override fun onCreateView() {
        super.onCreateView()
        layoutManagerN = LinearLayoutManager(baseActivity)
        fetchOfferListing(isFirst = true, offSet = offSet, limit = limit)
        layoutManagerN?.let { scrollPagingListener(it) }
        swipeRefreshListener()
        setOnClickListener(binding?.fbAddOffer, binding?.offerListingEmptyView?.cbCreateOffers, binding?.offerListingEmptyView?.cbWatchVideo)
    }

    private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
        binding?.rvListing?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                if (!isLastPageD) {
                    isLoadingD = true
                    adapterOffers?.addLoadingFooter(DataItem().getLoaderItem())
                    offSet += limit
                    fetchOfferListing(offSet = offSet, limit = limit)
                }
            }

            override val totalPageCount: Int
                get() = totalElements
            override val isLastPage: Boolean
                get() = isLastPageD
            override val isLoading: Boolean
                get() = isLoadingD
        })
    }

    private fun swipeRefreshListener() {
        binding?.offerListSwipeRefresh?.setOnRefreshListener {
            binding?.offerListSwipeRefresh?.isRefreshing = true
            this.offSet = PaginationScrollListener.PAGE_START
            this.limit = PaginationScrollListener.PAGE_SIZE
            fetchOfferListing(isProgress = false, isFirst = true, offSet = offSet, limit = limit)
        }
    }

    private fun fetchOfferListing(isProgress: Boolean = true, isFirst: Boolean = false, offSet: Int, limit: Int) {
        if (isFirst && isProgress) showProgress()
        viewModel?.getOfferListing(OfferListingRequest(floatingPointTag = UserSession.fpTag, limit = limit, offset = offSet))?.observeOnce(viewLifecycleOwner, {
            if (it.isSuccess()) {
                setOffersDataItem((it as? OfferListingResponse)?.result, isFirst)
            } else if (isFirst) showShortToast(it.errorMessage())
            if (isFirst) hideProgress()

        })
    }

    private fun setOffersDataItem(resultOffer: Result?, isFirstLoad: Boolean) {
        val listOffer = resultOffer?.data
        if (isFirstLoad) finalList.clear()
        if (listOffer.isNullOrEmpty().not()) {
            removeLoader()
            setEmptyView(false)
            totalElements = resultOffer?.paging?.count ?: 0
            finalList.addAll(listOffer!!)
            list.clear()
            list.addAll(finalList)
            isLastPageD = (finalList.size == totalElements)
            setAdapterNotify()
        } else if (isFirstLoad) setEmptyView(true)
        if (listOffer.isNullOrEmpty().not()) {
            list.clear()
            list.addAll(listOffer!!)
            setAdapterNotify()
        }

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.fbAddOffer, binding?.offerListingEmptyView?.cbCreateOffers -> startOfferFragmentActivity(requireActivity(), FragmentType.OFFER_DETAILS_FRAGMENT)
            binding?.offerListingEmptyView?.cbWatchVideo -> {
                //todo watch video
            }
        }
    }

    private fun setAdapterNotify() {
        if (adapterOffers == null) {
            adapterOffers = AppBaseRecyclerViewAdapter(baseActivity, list, this@OfferListingFragment)
            binding?.rvListing?.layoutManager = layoutManagerN
            binding?.rvListing?.adapter = adapterOffers
            adapterOffers?.runLayoutAnimation(binding?.rvListing)
        } else adapterOffers?.notifyDataSetChanged()
    }

    private fun setEmptyView(isEmpty: Boolean) {
        binding?.progress?.invisible()
        binding?.offerListingEmptyView?.root?.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding?.rvListing?.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding?.offerListSwipeRefresh?.isEnabled = isEmpty
    }


    private fun removeLoader() {
        if (isLoadingD) {
            isLoadingD = false
            adapterOffers?.removeLoadingFooter()
        }
    }

    private fun checkStoragePermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            showDialog(requireActivity(), getString(R.string.storage_permission), getString(R.string.to_share_the_image_we_need_storage_permission)
            ) { _: DialogInterface?, _: Int -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_CODE) }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (!defaultShareGlobal && shareType != 2 && offerItem != null) {
                share(defaultShareGlobal, shareType, offerItem)
            }
        }
    }

    fun showDialog(mContext: Context?, title: String?, msg: String?, listener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(mContext!!)
        builder.setTitle(title).setMessage(msg).setPositiveButton(getString(R.string.ok)) { dialog, which ->
            dialog.dismiss()
            listener.onClick(dialog, which)
        }
        builder.create().show()
    }

    fun share(defaultShare: Boolean, type: Int, offer: DataItem?) {
        showProgress(getString(R.string.sharing))
        if (NetworkUtils.isNetworkConnected()) {
            val shareText = String.format("*%s* %s\n*%s* %s\n\n-------------\n%s\n\nfor more details visit: %s",
                    offer?.name?.trim { it <= ' ' }, offer?.description, "${offer?.currencyCode}${offer?.price}",
                    "${offer?.currencyCode}${offer?.price}", offer?.description?.trim { it <= ' ' }, offer?.category?.trim { it <= ' ' })
            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    targetMap = null
                    try {
                        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                        val view = View(activity)
                        view.draw(Canvas(mutableBitmap))
                        val path = MediaStore.Images.Media.insertImage(requireActivity().contentResolver, mutableBitmap, "boost_360", "")
                        val uri = Uri.parse(path)
                        shareTextService(defaultShare, type, uri, shareText)
                    } catch (e: OutOfMemoryError) {
                        showShortToast(getString(R.string.image_size_is_large))
                    } catch (e: Exception) {
                        showShortToast(getString(R.string.image_not_able_to_share))
                    }
                    hideProgress()
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                    hideProgress()
                    targetMap = null
                    showShortToast(getString(R.string.failed_to_download_image))
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            }
            if (offer?.featuredImage?.tileImage.isNullOrEmpty().not()) {
                targetMap = target
                Picasso.get().load(offer?.featuredImage?.tileImage ?: "").into(target)
            } else {
                shareTextService(defaultShare, type, null, shareText)
                hideProgress()
            }
        } else {
            hideProgress()
            showShortToast(getString(R.string.can_not_share_image_offline))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val isRefresh = data?.getBooleanExtra(IntentConstant.IS_UPDATED.name, false) ?: false
            if (isRefresh) {
                this.offSet = PaginationScrollListener.PAGE_START
                this.limit = PaginationScrollListener.PAGE_SIZE
                fetchOfferListing(isFirst = true, offSet = offSet, limit = limit)
            }
        }
    }

    override fun showProgress(title: String?, cancelable: Boolean?) {
        binding?.progress?.visible()
    }

    override fun hideProgress() {
        binding?.progress?.gone()
    }

    private fun shareTextService(defaultShare: Boolean, type: Int, uri: Uri?, shareText: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        share.putExtra(Intent.EXTRA_TEXT, shareText)
        uri?.let { share.putExtra(Intent.EXTRA_STREAM, uri) }
        share.type = if (uri != null) "image/*" else "text/plain"
        if (share.resolveActivity(requireActivity().packageManager) != null) {
            if (!defaultShare) {
                if (type == 0) {
                    share.setPackage(getString(R.string.facebook_package))
                } else if (type == 1) {
                    share.setPackage(getString(R.string.whats_app_package))
                }
            }
            startActivityForResult(Intent.createChooser(share, resources.getString(R.string.share_updates)), 1)
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        offerItem = item as DataItem
        if (actionType == RecyclerViewActionType.SERVICE_ITEM_CLICK.ordinal) {
            val bundle = Bundle()
            bundle.putSerializable(IntentConstant.OFFER_DATA.name, offerItem)
            startFragmentActivity(FragmentType.OFFER_DETAILS_FRAGMENT, bundle, false, isResult = true)
        }
        if (actionType == RecyclerViewActionType.OFFER_WHATS_APP_SHARE.ordinal) {
            ServiceListingFragment.shareProduct = item as ItemsItem
            if (checkStoragePermission())
                share(ServiceListingFragment.defaultShareGlobal, 1, offerItem)
        }
        if (actionType == RecyclerViewActionType.OFFER_DATA_SHARE_CLICK.ordinal) {
            ServiceListingFragment.shareProduct = item as ItemsItem
            if (checkStoragePermission())
                share(defaultShareGlobal, 0, offerItem)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_stafflisting, menu)
    }

}