package com.appservice.ui.address.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBusinessAddressBinding
import com.framework.models.BaseViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.AutocompletePrediction


const val ADDRESS_DATA = "ADDRESS_DATA"

class AddAddressFragment : AppBaseFragment<FragmentBusinessAddressBinding, BaseViewModel>(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener {

  private lateinit var mGoogleMap: GoogleMap
  private var locationAddress: AutocompletePrediction? = null

  val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
    if (isGranted) mapReady() else showShortToast("Permission required for showing location")
  }

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddAddressFragment {
      val fragment = AddAddressFragment()
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
    locationAddress = requireArguments().getParcelable(ADDRESS_DATA)
    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
    mapFragment?.getMapAsync(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mGoogleMap = googleMap
    if (isPermissionGiven()) {
      mapReady()
    } else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    MapsInitializer.initialize(this.requireContext())
  }

  @SuppressLint("MissingPermission")
  private fun mapReady() {
    mGoogleMap.isMyLocationEnabled = true
    mGoogleMap.uiSettings.isMyLocationButtonEnabled = true
    mGoogleMap.uiSettings.isZoomControlsEnabled = true
    moveCameraLatLong(LatLng(28.5714496, 77.2749958),true)
  }

  private fun moveCameraLatLong(latLng: LatLng, isAddLister: Boolean = false) {
    mGoogleMap.apply {
      moveCamera(CameraUpdateFactory.newLatLng(latLng))
      animateCamera(CameraUpdateFactory.zoomTo(14.0f))
      if (isAddLister) mGoogleMap.setOnCameraIdleListener(this@AddAddressFragment)
    }
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    super.onCreateMenu(menu, menuInflater)
    menuInflater.inflate(R.menu.menu_help, menu)
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    when (menuItem.itemId) {
      R.id.menu_help -> {
        showLongToast("Coming soon...")
        return true
      }
    }
    return super.onMenuItemSelected(menuItem)
  }

  private fun isPermissionGiven(): Boolean {
    return checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
  }

  override fun onCameraIdle() {
    showLongToast("Location: ${mGoogleMap.cameraPosition.target.latitude} : ${mGoogleMap.cameraPosition.target.longitude}")
    moveCameraLatLong(mGoogleMap.cameraPosition.target)
  }
}