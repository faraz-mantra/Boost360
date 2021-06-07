package com.nowfloats.CustomPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dashboard.utils.CodeUtilsKt;
import com.framework.BaseApplication;
import com.framework.utils.ContentSharing;
import com.nowfloats.CustomPage.Model.CustomPageLink;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.CustomPage.Model.ItemsItem;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by guru on 27-04-2015.
 */
public class CustomPageAdapter extends RecyclerView.Adapter<CustomPageAdapter.ViewHolder> {
    Activity appContext;
    View displayView;
    ArrayList<CustomPageModel> storeData;
    private final LayoutInflater mInflater;
    private final SimpleDateFormat format;
    public UserSessionManager session;
    public CustomPageInterface pageInterface;
    //    public CustomPageDeleteInterface pageDeleteInterface;
    private final View prev_view = null;
    public static boolean deleteCheck = false;
    public Bus bus;
    private final CustomPageLink customPageLink;

    //    Drawable drawableFromTheme;
    //PorterDuffColorFilter primary;
    public CustomPageAdapter(Activity appContext, ArrayList<CustomPageModel> storeData,
                             UserSessionManager session, CustomPageInterface pageInterface, Bus bus, CustomPageLink customPageLink) {
        this.appContext = appContext;
        this.storeData = storeData;
        this.session = session;
        this.pageInterface = pageInterface;
        this.bus = bus;
        this.customPageLink = customPageLink;
//        pageDeleteInterface = (CustomPageDeleteInterface)appContext;
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        format = new SimpleDateFormat("MMM dd,yyyy hh:mm aa", Locale.US);
        format.setTimeZone(TimeZone.getDefault());
//        primary = new PorterDuffColorFilter(appContext.getResources()
//                .getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView imageView, stencil, share;
        TextView titleText, dateText;
        CardView cardView;
        LinearLayout fullLayout;

        public ViewHolder(View v) {
            super(v);
            stencil = itemView.findViewById(R.id.page_stencil_icon);
            imageView = itemView.findViewById(R.id.page_menu);
            titleText = itemView.findViewById(R.id.page_name);
            dateText = itemView.findViewById(R.id.page_date);
            fullLayout = itemView.findViewById(R.id.full_layout_card);
            share = itemView.findViewById(R.id.shareData);
//            share_facebook = (ImageView) itemView.findViewById(R.id.share_facebook);
//            share_whatsapp = (ImageView) itemView.findViewById(R.id.share_whatsapp);
            cardView = itemView.findViewById(R.id.cardView);


        }

    }


    @Override
    public CustomPageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        displayView = mInflater.inflate(R.layout.custom_page_list_design, parent, false);
        ViewHolder viewHolder = new ViewHolder(displayView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        try {
            if (storeData.get(position) != null) {
                holder.fullLayout.setTag(position + "");
                holder.imageView.setTag(position + "");
                //holder.stencil.setColorFilter(primary);
                if (storeData.get(position).getSel() == 0) {
                    holder.imageView.setVisibility(View.INVISIBLE);

                    int[] attrs = new int[]{android.R.attr.selectableItemBackground /* index 0 */};
                    TypedArray ta = appContext.obtainStyledAttributes(attrs);
                    Drawable drawableFromTheme = ta.getDrawable(0 /* index */);

                    ta.recycle();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.fullLayout.setBackground(drawableFromTheme);
                    } else {
                        holder.fullLayout.setBackgroundDrawable(drawableFromTheme);
                    }
                    //holder.fullLayout.setBackgroundColor( ContextCompat.getColor(appContext,android.R.color.transparent));
//                    holder.imageView.setColorFilter(greyBg);
                } else {
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.fullLayout.setBackgroundColor(appContext.getResources().getColor(R.color.gray_transparent));
                }

//                holder.fullLayout.setBackgroundColor(android.R.attr.selectableItemBackground);
                holder.titleText.setText(storeData.get(position).DisplayName);
                try {
                    String dateString = storeData.get(position).CreatedOn;
                    holder.dateText.setText(Methods.getFormattedDate(dateString));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /*holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int POs = Integer.parseInt(v.getTag().toString());
                        showPopup(POs);
                    }
                });*/

                holder.share.setOnClickListener(v -> shareLink(storeData.get(position),"default"));
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int POs = Integer.parseInt(v.getTag().toString());
                        //Log.v("ggg",POs+"");
                        if (deleteCheck) {
                            if (CustomPageFragment.posList.contains(POs + "")) {
                                if (CustomPageFragment.posList.size() == 1) {
                                    deleteCheck = false;
                                }
//                                v.setBackgroundColor(android.R.attr.selectableItemBackground);
                                CustomPageFragment.posList.remove(POs + "");
                                bus.post(new DeletePageTriggerEvent(POs, false, v));
//                                pageDeleteInterface.DeletePageTrigger(POs,false,v);
//                                holder.imageView.setVisibility(View.INVISIBLE);
                                storeData.get(POs).setSel(0);
                            } else {
//                                v.setBackgroundColor(appContext.getResources().getColor(R.color.gray_transparent));
                                deleteCheck = true;
                                if (!(CustomPageFragment.posList.contains(POs + "")))
                                    CustomPageFragment.posList.add(POs + "");
//                                pageDeleteInterface.DeletePageTrigger(POs,true,v);
                                bus.post(new DeletePageTriggerEvent(POs, true, v));
//                                holder.imageView.setVisibility(View.VISIBLE);
                                storeData.get(POs).setSel(1);
                            }
                            if (CustomPageFragment.custompageAdapter != null)
                                CustomPageFragment.custompageAdapter.notifyDataSetChanged();
                            if (CustomPageFragment.recyclerView != null)
                                CustomPageFragment.recyclerView.invalidate();
                        } else {
                            BoostLog.d(CustomPageAdapter.class.getSimpleName(), storeData.get(POs).PageId + " " + POs);
                            editPage(storeData.get(POs).PageId, POs);
                        }
                    }
                });

              /*  holder.fullLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                        if(prev_view!=null){
//                            prev_view.setBackgroundColor(android.R.attr.selectableItemBackground);
//                        }
//                        prev_view = v;
//                        int c = v.get;
                        int POs = Integer.parseInt(v.getTag().toString());
                        //Log.v("ggg",POs+"");
                        if (CustomPageFragment.posList.contains(POs+"")){
                            if(CustomPageFragment.posList.size()==1){
                                deleteCheck = false;
                            }
//                            v.setBackgroundColor(android.R.attr.selectableItemBackground);
                            CustomPageFragment.posList.remove(POs+"");
//                            pageDeleteInterface.DeletePageTrigger(POs,false,v);
                            bus.post(new DeletePageTriggerEvent(POs,false,v));
//                            holder.imageView.setVisibility(View.INVISIBLE);
                            storeData.get(POs).setSel(0);
                        }else{
//                            v.setBackgroundColor(appContext.getResources().getColor(R.color.gray_transparent));
                            deleteCheck =true;
                            if (!(CustomPageFragment.posList.contains(POs+"")))
                                CustomPageFragment.posList.add(POs+"");
//                            pageDeleteInterface.DeletePageTrigger(POs,true,v);
                            bus.post(new DeletePageTriggerEvent(POs,true,v));
//                            holder.imageView.setVisibility(View.VISIBLE);
                            storeData.get(POs).setSel(1);
                        }
                        if (CustomPageFragment.custompageAdapter!=null)
                            CustomPageFragment.custompageAdapter.notifyDataSetChanged();
                        if (CustomPageFragment.recyclerView!=null)
                            CustomPageFragment.recyclerView.invalidate();
//                        Methods.showSnackBarPositive(appContext, "Long press...." + POs);
                        return true;
                    }
                });*/

                /*holder.fullLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction()== MotionEvent.ACTION_UP){

                        }else if (event.getAction()== MotionEvent.ACTION_DOWN){

                        }
                        return false;
                    }
                });*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSelection(int position) {
        if (position == 0) {
            for (int i = 0; i < storeData.size(); i++) {
                storeData.get(i).setSel(0);
            }
        } else {
            storeData.get(position).setSel(0);
        }
    }

    public void shareLink(CustomPageModel customPageModel, String type) {
        String productType = CodeUtilsKt.getProductType(session.getFP_AppExperienceCode());
        String subDomain = "";
        if (productType.equals("PRODUCTS")) {
            subDomain = "all-products";
        } else {
            subDomain = "all-services";
        }
        String url = null;
        Iterator<ItemsItem> it = customPageLink.getItems().iterator();
        while (it.hasNext()) {
            try{
            if (it.next().getId().equals(customPageModel.PageId)) {
                url = it.next().getUrl().getUrl();
                break;
            }}catch (Exception ignored){
          url = session.getRootAliasURI();
            }
        }
        if (url==null){
            url = session.getRootAliasURI();
        }
        switch (type) {
            case "facebook":
                ContentSharing.Companion.shareCustomPages(customPageModel.DisplayName,url,session.getUserPrimaryMobile(),session.getRootAliasURI() + "/"+subDomain,false,true);
                break;
            case "whatsapp":
                ContentSharing.Companion.shareCustomPages(customPageModel.DisplayName,url,session.getUserPrimaryMobile(),session.getRootAliasURI() + "/"+subDomain,true,false);
                break;
            default:ContentSharing.Companion.shareCustomPages(customPageModel.DisplayName,url,session.getUserPrimaryMobile(),session.getRootAliasURI() + "/"+subDomain,false,false);

        }

    }

    private void editPage(String pageId, final int position) {

        Intent intent = new Intent(appContext, CreateCustomPageActivity.class);
        intent.putExtra("pageid", "" + pageId);
        intent.putExtra("position", position);
        appContext.startActivity(intent);


    }

//    private void deletePage(String pageId,final MaterialDialog dialog, final int posi) {
//        try {
//            JSONObject map = new JSONObject();
//            map.put("PageId", pageId);
//            map.put("Tag", "" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
//            map.put("clientId", "" + Constants.clientId);
//            String url = Constants.NOW_FLOATS_API_URL + "/Discover/v1/floatingpoint/custompage/delete";
//            new PageDelete(url, map.toString(), appContext, posi).execute();
//            dialog.dismiss();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        /*pageInterface.deletePage(map, new Callback<String>() {
//            @Override
//            public void success(String s, Response response) {
//                CustomPageFragment.dataModel.remove(posi);
//                dialog.dismiss();
//                Methods.showSnackBarPositive(appContext, "Page deleted successfully");
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                Log.e("delete error",""+error.getMessage());
//                Methods.showSnackBarNegative(appContext, "Something went wrong, Try again later");
//            }
//        });*/
//    }

    @Override
    public int getItemCount() {
        return storeData.size();
    }

}