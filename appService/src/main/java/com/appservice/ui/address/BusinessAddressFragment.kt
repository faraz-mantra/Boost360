package com.appservice.ui.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentBusinessAddressBinding
import com.appservice.utils.getMarkerBitmapFromView
import com.framework.models.BaseViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode

class BusinessAddressFragment : AppBaseFragment<FragmentBusinessAddressBinding, BaseViewModel>(), OnMapReadyCallback {

  private lateinit var mGoogleMap: GoogleMap

  var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result != null && result.resultCode == Activity.RESULT_OK) {
      val place = Autocomplete.getPlaceFromIntent(result.data)
      showLongToast("${place.address}")
    }
  }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BusinessAddressFragment {
      val fragment = BusinessAddressFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_business_address
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mGoogleMap = googleMap
    MapsInitializer.initialize(this.requireContext())
    addCustomMarker()
  }

  private fun addCustomMarker() {
    if (this::mGoogleMap.isInitialized.not()) return
    mGoogleMap.apply {
      val latLong = LatLng(28.5714496, 77.2749958)
      addMarker(
        MarkerOptions().position(latLong).icon(
          BitmapDescriptorFactory.fromBitmap(
            baseActivity.getMarkerBitmapFromView(R.drawable.ic_location_current)
          )
        ).anchor(0.5f, 1F)
      )
      animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 14.0f))
    }
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    super.onCreateMenu(menu, menuInflater)
    menuInflater.inflate(R.menu.menu_product_info, menu)
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    when (menuItem.itemId) {
      R.id.menu_edit -> {
        if (!Places.isInitialized()) Places.initialize(requireActivity(), resources.getString(R.string.google_map_key))
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
          .setTypeFilter(TypeFilter.REGIONS)
          .setCountry("IN")
          .build(requireActivity())
        resultLauncher.launch(intent)
        return true
      }
      R.id.menu_help -> {
        showLongToast("Coming soon...")
        return true
      }
    }
    return false
  }
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu_edit -> {
        if (!Places.isInitialized()) Places.initialize(requireActivity(), resources.getString(R.string.google_map_key))
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
          .setTypeFilter(TypeFilter.REGIONS)
          .setCountry("IN")
          .build(requireActivity())
        resultLauncher.launch(intent)
      }
      R.id.menu_help -> showLongToast("Coming soon...")
    }
    return super.onOptionsItemSelected(item)
  }

  private fun onBackResult() {
    val intent = Intent()
    intent.putExtra(IntentConstant.IS_UPDATED.name, true)
    baseActivity.setResult(AppCompatActivity.RESULT_OK, intent)
    baseActivity.finish()
  }
}