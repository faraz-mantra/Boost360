package com.appservice.ui.address.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.framework.utils.*
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.item_location_searches.view.*

const val RECENT_LEARNING_DATA = "RECENT_LEARNING_DATA"

class PlacesResultAdapter(
  var mContext: Context,
  val loadData: (isData: Boolean) -> Unit,
  val onClick: (prediction: AutocompletePrediction) -> Unit,
) : RecyclerView.Adapter<PlacesResultAdapter.ViewHolder>(), Filterable {

  private var mResultList: ArrayList<AutocompletePrediction>? = arrayListOf()
  private val placesClient: PlacesClient = Places.createClient(mContext)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location_searches, parent, false)
    return ViewHolder(view)
  }

  override fun getItemCount(): Int {
    return mResultList!!.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.onBind(position)
    holder.itemView.setOnClickListener {
      onClick(mResultList?.get(position)!!)
    }
  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun onBind(position: Int) {
      val res = mResultList?.get(position)
      itemView.apply {
        tv_address_title.text = res?.getPrimaryText(null)
      }
    }
  }

  override fun getFilter(): Filter {
    return object : Filter() {
      override fun performFiltering(constraint: CharSequence): FilterResults {
        val results = FilterResults()
        mResultList = getPredictions(constraint)
        if (mResultList != null) {
          results.values = mResultList
          results.count = mResultList!!.size
        }
        return results
      }

      override fun publishResults(constraint: CharSequence, results: FilterResults) {

      }
    }
  }

  private fun getPredictions(constraint: CharSequence): ArrayList<AutocompletePrediction>? {
    val result: ArrayList<AutocompletePrediction> = arrayListOf()
    val token = AutocompleteSessionToken.newInstance()
    val request = FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
      //.setLocationBias(bounds)
      .setCountries("IN")
      .setTypeFilter(TypeFilter.ADDRESS)
      .setSessionToken(token)
      .setQuery(constraint.toString())
      .build()

    placesClient.findAutocompletePredictions(request)
      .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
        result.addAll(response.autocompletePredictions)
        notifyDataSetChanged()
      }.addOnFailureListener { exception: Exception? ->
        if (exception is ApiException) {
          Log.e("TAG", "Place not found: " + exception.statusCode)
          Toast.makeText(mContext, "Place not found", Toast.LENGTH_LONG).show()
        } else {
          Toast.makeText(mContext, "${exception?.localizedMessage ?: "Error!"}", Toast.LENGTH_LONG).show()
        }
      }
    loadData(result.isNotEmpty())
    return result
  }
}

fun getRecentDataSearch(): ArrayList<AutocompletePrediction>? {
  return convertStringToList(PreferencesUtils.instance.getData(RECENT_LEARNING_DATA, "") ?: "")
}

fun saveRecentDataSearch(autocompletePrediction: AutocompletePrediction) {
  (getRecentDataSearch() ?: ArrayList()).apply {
    add(0, autocompletePrediction);
    if (size > 5) removeAt(size - 1);
    PreferencesUtils.instance.saveData(RECENT_LEARNING_DATA, convertListObjToString(this))
  }
}