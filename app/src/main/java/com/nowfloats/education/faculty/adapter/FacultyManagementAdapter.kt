package com.nowfloats.education.faculty.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nowfloats.education.faculty.model.Data
import com.nowfloats.education.helper.ItemClickEventListener
import com.thinksity.databinding.FacultyManagementBinding

/**
 * Created by NowFloats on 02-08-2016.
 */
class FacultyManagementAdapter(private val eventListener: ItemClickEventListener) :
  RecyclerView.Adapter<FacultyManagementAdapter.ViewHolder>() {
  private var menuPosition = -1
  private var menuStatus = false
  var items: List<Data> = emptyList()
  private lateinit var binding: FacultyManagementBinding

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    binding = FacultyManagementBinding.inflate(inflater, parent, false)
    return ViewHolder(binding)
  }

  fun menuOption(pos: Int, status: Boolean) {
    menuPosition = pos
    menuStatus = status
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bindView(items[position], position)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  inner class ViewHolder(private val binding: FacultyManagementBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindView(facultyManagementData: Data, position: Int) {
      binding.facultyManagementData = facultyManagementData

      binding.menuOptions.visibility = View.GONE
      if (menuPosition == position) {
        if (menuStatus) {
          binding.menuOptions.visibility = View.VISIBLE
        } else {
          binding.menuOptions.visibility = View.GONE
        }
      }
      binding.mainLayout.setOnClickListener { eventListener.itemMenuOptionStatus(position, false) }

      binding.singleItemMenuButton.setOnClickListener {
        if (binding.menuOptions.visibility == View.GONE) {
          eventListener.itemMenuOptionStatus(position, true)
        } else {
          eventListener.itemMenuOptionStatus(position, false)
        }
      }

      binding.editFaculty.setOnClickListener {
        eventListener.onEditClick(facultyManagementData, position)
      }

      binding.deleteFaculty.setOnClickListener {
        eventListener.onDeleteClick(facultyManagementData, position)
      }
    }
  }
}