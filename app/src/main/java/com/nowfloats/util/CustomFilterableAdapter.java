package com.nowfloats.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;


public class CustomFilterableAdapter extends ArrayAdapter<String> implements
		Filterable {

	private List<String> stringList;
	private Context context;
	private Filter stringFilter;
	private List<String> origStringList;

	public CustomFilterableAdapter(List<String> stringList, Context ctx) {
		super(ctx, R.layout.row_of_custom_search_dialog, stringList);
		this.stringList = stringList;
		this.context = ctx;
		this.origStringList = stringList;
	}

	public int getCount() {
		return stringList.size();
	}

	public String getItem(int position) {
		return stringList.get(position);
	}

	public long getItemId(int position) {
		return stringList.get(position).hashCode();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		StringHolder holder = new StringHolder();

		// First let's verify the convertView is not null
		if (convertView == null) {
			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			v = inflater.inflate(R.layout.row_of_custom_search_dialog, null);
			// Now we can fill the layout with the right values
			TextView tv = (TextView) v.findViewById(R.id.custom_search_dialog_textview);

			holder.stringNameView = tv;

			v.setTag(holder);
		} else
			holder = (StringHolder) v.getTag();

		String p = stringList.get(position);
		holder.stringNameView.setText(p);

		return v;
	}

	public void resetData() {
		stringList = origStringList;
	}

	private static class StringHolder {
		public TextView stringNameView;
	}

	@Override
	public Filter getFilter() {
		if (stringFilter == null)
			stringFilter = new StringFilter();

		return stringFilter;
	}

	private class StringFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			// We implement here the filter logic
			if (constraint == null || constraint.length() == 0) {
				// No filter implemented we return all the list
				results.values = origStringList;
				results.count = origStringList.size();
			} else {
				// We perform filtering operation
				List<String> nStringList = new ArrayList<String>();

				for (String p : stringList) {
					if (p.toUpperCase().startsWith(
							constraint.toString().toUpperCase()))
						nStringList.add(p);
				}

				results.values = nStringList;
				results.count = nStringList.size();

			}
			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {

			// Now we have to inform the adapter about the new list filtered
			if (results.count == 0)
				notifyDataSetInvalidated();
			else {
				stringList = (List<String>) results.values;
				notifyDataSetChanged();
			}

		}

	}
}
