package com.boost.upgrades.ui.features

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.AllFeatureAdaptor
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.remote.ApiInterface
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.utils.WebEngageController
import com.google.android.material.snackbar.Snackbar
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.view_all_features_fragment.*
import retrofit2.Retrofit

class ViewAllFeaturesFragment : BaseFragment() {

    lateinit var root: View

    lateinit var retrofit: Retrofit
    lateinit var ApiService: ApiInterface
    lateinit var localStorage: LocalStorage
    lateinit var allFeatureAdaptor: AllFeatureAdaptor
    lateinit var viewAllFeaturesViewModelFactory: ViewAllFeaturesViewModelFactory

    lateinit var progressDialog: ProgressDialog

    companion object {
        fun newInstance() = ViewAllFeaturesFragment()
    }

    private lateinit var viewModel: ViewAllFeaturesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.view_all_features_fragment, container, false)

        viewAllFeaturesViewModelFactory = ViewAllFeaturesViewModelFactory(requireNotNull(requireActivity().application))

        viewModel = ViewModelProviders.of(requireActivity(), viewAllFeaturesViewModelFactory).get(ViewAllFeaturesViewModel::class.java)

        progressDialog = ProgressDialog(requireContext())

        allFeatureAdaptor = AllFeatureAdaptor(activity as UpgradeActivity, ArrayList())

        localStorage = LocalStorage.getInstance(context!!)!!



        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loadData()
        initMvvM()
        initializeRecycler()

        shimmer_view_container2.startShimmer()



        back_button2.setOnClickListener{
            (activity as UpgradeActivity).onBackPressed()
        }

        WebEngageController.trackEvent("ADDONS_MARKETPLACE All_Features Loaded", "All_Features", "")
    }

    fun loadData(){
        viewModel.loadAddonsFromDB()
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvM(){
        viewModel.addonsResult().observe(this, Observer {
            if(it != null){
                initialiseRecyclerView(it)
            }
        })

        viewModel.addonsLoader().observe(this, Observer {
            if(it){
                val status = "Loading. Please wait..."
                progressDialog.setMessage(status)
                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
                progressDialog.show()
            }else{
                progressDialog.dismiss()
                if (shimmer_view_container2.isShimmerStarted) {
                    shimmer_view_container2.stopShimmer()
                    shimmer_view_container2.visibility = View.GONE
                }
            }
        })

        viewModel.addonsError().observe(this, Observer {
            Snackbar.make(root, viewModel.errorMessage, Snackbar.LENGTH_LONG).show()
            if (shimmer_view_container2.isShimmerStarted) {
                shimmer_view_container2.stopShimmer()
                shimmer_view_container2.visibility = View.GONE
            }
          Toasty.error(requireContext(), "onFailure: " + it, Toast.LENGTH_LONG).show()
        })
    }

    fun initialiseRecyclerView(upgradeList: List<FeaturesModel>){
        if(shimmer_view_container2.isShimmerStarted) {
            shimmer_view_container2.stopShimmer()
            shimmer_view_container2.visibility = View.GONE
        }
        allFeatureAdaptor.addupdates(upgradeList)
        recyclerDetails.adapter=allFeatureAdaptor
        allFeatureAdaptor.notifyDataSetChanged()
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerDetails.apply {
            layoutManager=gridLayoutManager

        }
    }

}
