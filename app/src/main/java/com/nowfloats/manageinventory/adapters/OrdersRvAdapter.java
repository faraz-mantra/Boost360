package com.nowfloats.manageinventory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.manageinventory.OrderListActivity;
import com.nowfloats.manageinventory.models.OrderModel;
import com.nowfloats.manageinventory.models.ProductModel;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by NowFloats on 29-08-2017.
 */

public class OrdersRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;
    private OnRecyclerViewItemClickListener mOnItemClickListener;
    private Context mContext;

    private String mErrorMsg;

    private List<OrderModel> mOrderModelList;
    private Map<String, ArrayList<ProductModel>> mProductListMap;

    public OrdersRvAdapter(Context context){
        this.mOrderModelList = new ArrayList<>();
        this.mProductListMap = new HashMap<>();
        this.mContext = context;
        this.mCallback = (PaginationAdapterCallback) context;
        this.mOnItemClickListener = (OnRecyclerViewItemClickListener) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType){
            case ITEM:
                View viewItem = inflater.inflate(R.layout.orders_row_layout, parent, false);
                viewHolder = new OrdersViewHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    public ArrayList<ProductModel> getProductsForOrder(String orderId){
        if(mProductListMap.containsKey(orderId)) {
            return mProductListMap.get(orderId);
        }
        return null;
    }

    @Override
    @SuppressLint("NewApi")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderModel orderModel = mOrderModelList.get(position);

        switch (getItemViewType(position)){
            case ITEM:
                final OrdersViewHolder ordersViewHolder = (OrdersViewHolder) holder;
                List<ProductModel> products = null;
                if(orderModel==null)
                    return;
                if(mProductListMap.containsKey(orderModel.getId())) {
                     products = mProductListMap.get(orderModel.getId());
                }
                ordersViewHolder.tvOrderId.setText(orderModel.getOrderId());
                ordersViewHolder.tvOrderDate.setText(getParsedDate(orderModel.getCreatedOn()));
                ordersViewHolder.tvOrderTotalAmount.setText(TextUtils.isEmpty(orderModel.getCurrency())? "INR" :
                        orderModel.getCurrency() + " " + orderModel.getTotalAmount());
                ordersViewHolder.tvExpDelivDate.setText(orderModel.getExpectedDeliveryDate()==null?"N/A"
                        :getParsedDate(orderModel.getExpectedDeliveryDate()));
                if(orderModel.getOrderStatusDelivery() == OrderListActivity.DeliveryStatus.ORDER_RECEIVED.ordinal()){
                    ordersViewHolder.tvOrderStatusTag.setTextColor(Color.parseColor("#339112"));
                    ordersViewHolder.tvOrderStatusTag.setText("Fresh");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.fresh_order_text_bg));
                }else if(orderModel.getOrderStatus() == OrderListActivity.OrderStatus.DELIVERED.ordinal()){
                    ordersViewHolder.tvOrderStatusTag.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
                    ordersViewHolder.tvOrderStatusTag.setText("Delivered");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.delivered_order_text_bg));
                }else if(orderModel.getOrderStatus() == OrderListActivity.OrderStatus.CANCELLED.ordinal()){
                    ordersViewHolder.tvOrderStatusTag.setTextColor(Color.parseColor("#808080"));
                    ordersViewHolder.tvOrderStatusTag.setText("Cancelled");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.cancelled_order_text_bg));
                }else if(orderModel.getOrderStatus() == OrderListActivity.OrderStatus.RETURNED.ordinal()){
                    ordersViewHolder.tvOrderStatusTag.setTextColor(Color.parseColor("#F80208"));
                    ordersViewHolder.tvOrderStatusTag.setText("Returned");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.returned_order_text_bg));
                }else if(orderModel.getOrderStatus() == OrderListActivity.OrderStatus.NOT_INITIATED.ordinal()){
                    ordersViewHolder.tvOrderStatusTag.setTextColor(Color.parseColor("#6DA5FF"));
                    ordersViewHolder.tvOrderStatusTag.setText("Abandoned");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.abandoned_order_text_bg));
                }
                if(products!=null) {
                    ordersViewHolder.tvItemsCount.setText(products.size()>1?products.size() + " Items": products.size() + " Item");
                    Picasso.with(mContext).load(products.get(0).getProductImgUrl()).into(ordersViewHolder.ivProducts);
                }
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            mErrorMsg != null ?
                                    mErrorMsg :
                                    mContext.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }


    }

    private String getParsedDate(String createdOn) {
        if(createdOn==null){
            return "N/A";
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String parsedDate;
        try {
            Date date = format.parse(createdOn);
            parsedDate = new SimpleDateFormat("HH.mm a dd/MM/yyyy").format(date);
        }catch (ParseException e){
            parsedDate = createdOn;
        }

        return parsedDate;
    }

    @Override
    public int getItemCount() {
        return mOrderModelList == null ? 0 : mOrderModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM;
        } else {
            return (position == mOrderModelList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
        }
    }

    public List<OrderModel> getOrders() {
        return mOrderModelList;
    }

    public void setOrders(List<OrderModel> orderList) {
        this.mOrderModelList = orderList;
    }

    public void add(OrderModel orderModel) {
        mOrderModelList.add(orderModel);
        notifyItemInserted(mOrderModelList.size() - 1);
    }

    public void putToProductMap(String key, ProductModel val){
        if(mProductListMap.containsKey(key)){
            mProductListMap.get(key).add(val);
        }else {
            ArrayList<ProductModel> list = new ArrayList<>();
            list.add(val);
            mProductListMap.put(key, list);
        }
    }

    public void addAll(List<OrderModel> orderModelList) {
        if(orderModelList!=null && orderModelList.size()>0) {
            int prevPosition = mOrderModelList.size();
            mOrderModelList.addAll(orderModelList);
            if(orderModelList.size()==1){
                notifyItemInserted(mOrderModelList.size()-1);
            }else {
                notifyItemRangeInserted(prevPosition, mOrderModelList.size() - 1);
            }
        }
    }

    public void remove(OrderModel orderModel) {
        int position = mOrderModelList.indexOf(orderModel);
        if (position > -1) {
            mOrderModelList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new OrderModel());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mOrderModelList.size() - 1;
        OrderModel result = getItem(position);

        remove(result);
    }

    public OrderModel getItem(int position) {
        return mOrderModelList.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(mOrderModelList.size() - 1);

        if (errorMsg != null) this.mErrorMsg = errorMsg;
    }

    public void clearAdapter() {
        mOrderModelList.clear();
        mProductListMap.clear();
        notifyDataSetChanged();
    }

    protected class OrdersViewHolder extends RecyclerView.ViewHolder{

        TextView tvOrderId, tvOrderDate, tvOrderTotalAmount, tvOrderStatusTag, tvExpDelivDate, tvItemsCount;
        ImageView ivProducts;

        public OrdersViewHolder(final View itemView) {
            super(itemView);

            tvOrderId = (TextView) itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = (TextView) itemView.findViewById(R.id.tv_order_date);
            tvOrderTotalAmount = (TextView) itemView.findViewById(R.id.tv_order_total_amount);
            tvOrderStatusTag = (TextView) itemView.findViewById(R.id.tv_order_status_tag);
            tvExpDelivDate = (TextView) itemView.findViewById(R.id.tv_exp_deliv_date);
            tvItemsCount = (TextView) itemView.findViewById(R.id.tv_num_of_items);
            ivProducts = (ImageView) itemView.findViewById(R.id.iv_products);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }


    public interface PaginationAdapterCallback {

        void retryPageLoad();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View v, int position);
    }
}
