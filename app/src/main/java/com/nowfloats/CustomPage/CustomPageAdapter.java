package com.nowfloats.CustomPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.CustomPage.Model.CustomPageModel;
import com.nowfloats.CustomPage.Model.PageDetail;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        format = new SimpleDateFormat("MMM dd,yyyy hh:mm aa");
        format.setTimeZone(TimeZone.getDefault());
        primary = new PorterDuffColorFilter(appContext.getResources()
                .getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView imageView,stencil;
        public TextView titleText,dateText;
        public LinearLayout fullLayout;
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
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        try{
            if (storeData.get(position)!=null){
                holder.fullLayout.setTag(position+"");
                holder.imageView.setTag(position+"");
                holder.stencil.setColorFilter(primary);
                if (storeData.get(position).getSel()==0){
                    holder.imageView.setVisibility(View.INVISIBLE);
                    holder.fullLayout.setBackgroundColor( ContextCompat.getColor(appContext,android.R.attr.selectableItemBackground));
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
                                storeData.get(position).setSel(0);
                            }else{
//                                v.setBackgroundColor(appContext.getResources().getColor(R.color.gray_transparent));
                                deleteCheck =true;
                                if (!(CustomPageActivity.posList.contains(POs+"")))
                                    CustomPageActivity.posList.add(POs+"");
//                                pageDeleteInterface.DeletePageTrigger(POs,true,v);
                                bus.post(new DeletePageTriggerEvent(POs,true,v));
//                                holder.imageView.setVisibility(View.VISIBLE);
                                storeData.get(position).setSel(1);
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
                        if (CustomPageActivity.posList.contains(POs+"")){
                            if(CustomPageActivity.posList.size()==1){
                                deleteCheck = false;
                            }
//                            v.setBackgroundColor(android.R.attr.selectableItemBackground);
                            CustomPageActivity.posList.remove(POs+"");
//                            pageDeleteInterface.DeletePageTrigger(POs,false,v);
                            bus.post(new DeletePageTriggerEvent(POs,false,v));
//                            holder.imageView.setVisibility(View.INVISIBLE);
                            storeData.get(position).setSel(0);
                        }else{
//                            v.setBackgroundColor(appContext.getResources().getColor(R.color.gray_transparent));
                            deleteCheck =true;
                            if (!(CustomPageActivity.posList.contains(POs+"")))
                                CustomPageActivity.posList.add(POs+"");
//                            pageDeleteInterface.DeletePageTrigger(POs,true,v);
                            bus.post(new DeletePageTriggerEvent(POs,true,v));
//                            holder.imageView.setVisibility(View.VISIBLE);
                            storeData.get(position).setSel(1);
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


    private void editPage(String pageId, final int pOs) {

        Intent intent = new Intent(appContext, CreateCustomPageActivity.class);
        intent.putExtra("pageid", "" + pageId);
        appContext.startActivity(intent);


    }


    @Override
    public int getItemCount() {
        return storeData.size();
    }
}