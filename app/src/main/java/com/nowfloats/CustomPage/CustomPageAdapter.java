package com.nowfloats.CustomPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 27-04-2015.
 */
public class CustomPageAdapter extends RecyclerView.Adapter<CustomPageAdapter.ViewHolder>{
    Activity appContext;
    View displayView;
    ArrayList<CustomPageModel> storeData;
    private LayoutInflater mInflater;
    private SimpleDateFormat format;
    public UserSessionManager session;
    public CustomPageInterface pageInterface;
    //    public CustomPageDeleteInterface pageDeleteInterface;
    private View prev_view = null;
    public static boolean deleteCheck = false;
    public Bus bus;
//    Drawable drawableFromTheme;
    PorterDuffColorFilter primary;
    public CustomPageAdapter(Activity appContext, ArrayList<CustomPageModel> storeData,
                             UserSessionManager session, CustomPageInterface pageInterface, Bus bus) {
        this.appContext = appContext;
        this.storeData = storeData;
        this.session = session;
        this.pageInterface = pageInterface;
        this.bus = bus;
//        pageDeleteInterface = (CustomPageDeleteInterface)appContext;
        mInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        format = new SimpleDateFormat("MMM dd,yyyy hh:mm aa", Locale.US);
        format.setTimeZone(TimeZone.getDefault());
        primary = new PorterDuffColorFilter(appContext.getResources()
                .getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ImageView imageView,stencil;
        TextView titleText,dateText;
        LinearLayout fullLayout;
        public ViewHolder(View v) {
            super(v);
            stencil = (ImageView)itemView.findViewById(R.id.page_stencil_icon);
            imageView = (ImageView)itemView.findViewById(R.id.page_menu);
            titleText = (TextView)itemView.findViewById(R.id.page_name);
            dateText = (TextView)itemView.findViewById(R.id.page_date);
            fullLayout = (LinearLayout)itemView.findViewById(R.id.full_layout_card);
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
        try{
            if (storeData.get(position)!=null){
                holder.fullLayout.setTag(position+"");
                holder.imageView.setTag(position+"");
                holder.stencil.setColorFilter(primary);
                if (storeData.get(position).getSel()==0){
                    holder.imageView.setVisibility(View.INVISIBLE);

                    int[] attrs = new int[] { android.R.attr.selectableItemBackground /* index 0 */};
                    TypedArray ta = appContext.obtainStyledAttributes(attrs);
                    Drawable drawableFromTheme = ta.getDrawable(0 /* index */);

                    ta.recycle();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.fullLayout.setBackground(drawableFromTheme);
                    }else{
                        holder.fullLayout.setBackgroundDrawable(drawableFromTheme);
                    }
                    //holder.fullLayout.setBackgroundColor( ContextCompat.getColor(appContext,android.R.color.transparent));
//                    holder.imageView.setColorFilter(greyBg);
                }else{
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.fullLayout.setBackgroundColor(appContext.getResources().getColor(R.color.gray_transparent));
                }

//                holder.fullLayout.setBackgroundColor(android.R.attr.selectableItemBackground);
                holder.titleText.setText(storeData.get(position).DisplayName);
                try {
                    String dateString = storeData.get(position).CreatedOn;
                    dateString = dateString.replace("/Date(", "").replace(")/", "");
                    Long epochTime = Long.parseLong(dateString);
                    Date date = new Date(epochTime);
                    if (date != null)
                        holder.dateText.setText(format.format(date));
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

                holder.fullLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int POs = Integer.parseInt(v.getTag().toString());
                        //Log.v("ggg",POs+"");
                        if (deleteCheck){
                            if (CustomPageActivity.posList.contains(POs+"")){
                                if(CustomPageActivity.posList.size()==1){
                                    deleteCheck = false;
                                }
//                                v.setBackgroundColor(android.R.attr.selectableItemBackground);
                                CustomPageActivity.posList.remove(POs+"");
                                bus.post(new DeletePageTriggerEvent(POs,false,v));
//                                pageDeleteInterface.DeletePageTrigger(POs,false,v);
//                                holder.imageView.setVisibility(View.INVISIBLE);
                                storeData.get(POs).setSel(0);
                            }else{
//                                v.setBackgroundColor(appContext.getResources().getColor(R.color.gray_transparent));
                                deleteCheck =true;
                                if (!(CustomPageActivity.posList.contains(POs+"")))
                                    CustomPageActivity.posList.add(POs+"");
//                                pageDeleteInterface.DeletePageTrigger(POs,true,v);
                                bus.post(new DeletePageTriggerEvent(POs,true,v));
//                                holder.imageView.setVisibility(View.VISIBLE);
                                storeData.get(POs).setSel(1);
                            }
                            if (CustomPageActivity.custompageAdapter!=null)
                                CustomPageActivity.custompageAdapter.notifyDataSetChanged();
                            if (CustomPageActivity.recyclerView!=null)
                                CustomPageActivity.recyclerView.invalidate();
                        }else {
                            editPage(storeData.get(POs).PageId, POs);
                        }
                    }
                });

                holder.fullLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
//                        if(prev_view!=null){
//                            prev_view.setBackgroundColor(android.R.attr.selectableItemBackground);
//                        }
//                        prev_view = v;
//                        int c = v.get;
                        int POs = Integer.parseInt(v.getTag().toString());
                        //Log.v("ggg",POs+"");
                        if (CustomPageActivity.posList.contains(POs+"")){
                            if(CustomPageActivity.posList.size()==1){
                                deleteCheck = false;
                            }
//                            v.setBackgroundColor(android.R.attr.selectableItemBackground);
                            CustomPageActivity.posList.remove(POs+"");
//                            pageDeleteInterface.DeletePageTrigger(POs,false,v);
                            bus.post(new DeletePageTriggerEvent(POs,false,v));
//                            holder.imageView.setVisibility(View.INVISIBLE);
                            storeData.get(POs).setSel(0);
                        }else{
//                            v.setBackgroundColor(appContext.getResources().getColor(R.color.gray_transparent));
                            deleteCheck =true;
                            if (!(CustomPageActivity.posList.contains(POs+"")))
                                CustomPageActivity.posList.add(POs+"");
//                            pageDeleteInterface.DeletePageTrigger(POs,true,v);
                            bus.post(new DeletePageTriggerEvent(POs,true,v));
//                            holder.imageView.setVisibility(View.VISIBLE);
                            storeData.get(POs).setSel(1);
                        }
                        if (CustomPageActivity.custompageAdapter!=null)
                            CustomPageActivity.custompageAdapter.notifyDataSetChanged();
                        if (CustomPageActivity.recyclerView!=null)
                            CustomPageActivity.recyclerView.invalidate();
//                        Methods.showSnackBarPositive(appContext, "Long press...." + POs);
                        return true;
                    }
                });

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
        }catch(Exception e){e.printStackTrace();}
    }
    public void updateSelection(int position){
        if (position==0){
            for (int i = 0; i < storeData.size(); i++) {
                storeData.get(i).setSel(0);
            }
        }else{
            storeData.get(position).setSel(0);
        }
    }

//    private void showPopup(final int pOs) {
//        final MaterialDialog dialog = new MaterialDialog.Builder(appContext)
//                .customView(R.layout.page_edit_popup,true)
//                .show();
//        View view = dialog.getCustomView();
//        TextView title = (TextView) view.findViewById(R.id.title);
//        TextView edit = (TextView) view.findViewById(R.id.edit);
//        TextView delete = (TextView) view.findViewById(R.id.delete);
//        title.setText(storeData.get(pOs).DisplayName);
//        edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                editPage(storeData.get(pOs).PageId,pOs);
//            }
//        });
//
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deletePage(storeData.get(pOs).PageId,dialog,pOs);
//            }
//        });
//    }

    private void editPage(String pageId, final int pOs) {

        Intent intent = new Intent(appContext, CreateCustomPageActivity.class);
        intent.putExtra("pageid", "" + pageId);
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