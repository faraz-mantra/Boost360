package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;


public class EndlessScrollListenerFloats implements OnScrollListener {
	private int previousTotal = 0;
	private boolean loading = true;
	String websiteName = "";

	public EndlessScrollListenerFloats() {

	}

	int handlerType = -1;
	ListView listView = null;
	Activity context = null;
	BaseAdapter adapter = null;
	Button goToTopButton = null;

	public EndlessScrollListenerFloats(int handlerType, ListView listView,
			BaseAdapter adapter, Activity context, Button goToTopButton) {
		this.handlerType = handlerType;
		this.listView = listView;
		this.context = context;
		this.adapter = adapter;
		this.goToTopButton = goToTopButton;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		if (totalItemCount > 1 && visibleItemCount > 1) {
			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
				}
			}

			if (listView.getFirstVisiblePosition() == 0) {
				goToTopButton.setVisibility(View.GONE);
			} else {
				goToTopButton.setVisibility(View.VISIBLE);
			}

//			if (!loading
//					&& ((firstVisibleItem + visibleItemCount) >= totalItemCount)
//					&& totalItemCount > 8) {
//				loading = true;
//				if (adapter instanceof BizFloatsCustomAdapter) {
//					if (Constants.moreStorebizFloatsAvailable) {
//						customToast("More messages coming up ...");
//						GetStoreFrontBizFloatsAsyncTask gsf = new GetStoreFrontBizFloatsAsyncTask(
//								context, websiteName,
//								(BizFloatsCustomAdapter) adapter, listView);
//						gsf.execute();
//					} else {
//						customToast("No more messages ......");
//					}
//				}
//
//			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	public void reset() {
		previousTotal = 0;
	}

	public void customToast(String msg) {
		try {
			// LayoutInflater inflater = context.getLayoutInflater();
			// View layout = inflater.inflate(R.layout.custom_toast_layout,
			// (ViewGroup)context.findViewById(R.id.toast_layout_root));
			//
			// TextView text = (TextView) layout.findViewById(R.id.text);
			// text.setText(msg);
			// Toast toast = new Toast(context);
			// toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			// toast.setDuration(8000);
			// toast.setView(layout);
			// toast.show();
			Util.toast(msg, context);

		} catch (Exception e) {

		}
	}

}