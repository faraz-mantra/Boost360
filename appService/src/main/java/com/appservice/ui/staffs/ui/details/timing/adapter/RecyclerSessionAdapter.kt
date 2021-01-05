package com.appservice.ui.staffs.ui.details.timing.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.ui.staffs.ui.customviews.LabeledToggle
import com.appservice.ui.staffs.ui.details.timing.model.StaffTimingModel
import com.framework.views.customViews.CustomTextView

class RecyclerSessionAdapter(var recyclerItemClick: RecyclerItemClick, val context: Context) : RecyclerView.Adapter<RecyclerSessionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_session, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val staffTimingModel = StaffTimingModel.getDefaultTimings()[position]
        if (staffTimingModel.isAppliedOnAllDaysViewVisible) {
            holder.session.visibility = View.VISIBLE
            holder.toggle!!.isOn = true
        } else {
            holder.session.visibility = View.GONE
            //            holder.toggle.setColorBorder(Color.parseColor("#EC9A81"));
//            holder.toggle.setColorOff(Color.parseColor("#EC9A81"));
//            holder.toggle.setColorDisabled(Color.parseColor("#EC9A81"));
//            holder.toggle.setBackgroundColor(Color.parseColor("#EC9A81"));
            holder.toggle!!.isOn = false
        }
        holder.dayTitle.text = staffTimingModel.title
        holder.sessionAdd.setText(Html.fromHtml(context.getString(R.string.u_add_session_u)))
    }

    override fun getItemCount(): Int {
        return StaffTimingModel.getDefaultTimings().size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var session: RelativeLayout = itemView.findViewById(R.id.layout_session_create)
        var toggle: LabeledToggle? = itemView.findViewById(R.id.toggle_on_off);
        var dayTitle: CustomTextView = itemView.findViewById(R.id.ccv_title_day)
        var sessionAdd: CustomTextView = itemView.findViewById(R.id.ctv_add_session)
    }

    interface RecyclerItemClick {
        fun onToggle()
        fun onAddSession()
        fun onApplyAllDays()
    }
}