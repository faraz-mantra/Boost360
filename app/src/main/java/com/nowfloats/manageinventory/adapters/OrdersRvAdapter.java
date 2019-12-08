package com.nowfloats.manageinventory.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import com.nowfloats.manageinventory.models.OrderDataModel.Order;
import com.nowfloats.util.Methods;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

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

    private List<Order> mOrderModelList;
//    private Map<String, ArrayList<ProductModel>> mProductListMap;

    public OrdersRvAdapter(Context context) {
        this.mOrderModelList = new ArrayList<>();
//        this.mProductListMap = new HashMap<>();
        this.mContext = context;
        this.mCallback = (PaginationAdapterCallback) context;
        this.mOnItemClickListener = (OnRecyclerViewItemClickListener) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
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

//    public ArrayList<ProductModel> getProductsForOrder(String orderId) {
//        if (mProductListMap.containsKey(orderId)) {
//            return mProductListMap.get(orderId);
//        }
//        return null;
//    }

    @Override
    @SuppressLint("NewApi")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Order orderModel = mOrderModelList.get(position);

        switch (getItemViewType(position)) {
            case ITEM:
                final OrdersViewHolder ordersViewHolder = (OrdersViewHolder) holder;
//                List<ProductModel> products = null;
                if (orderModel == null)
                    return;
//                if (mProductListMap.containsKey(orderModel.getId())) {
//                    products = mProductListMap.get(orderModel.getId());
//                }
                ordersViewHolder.tvOrderId.setText(orderModel.getReferenceNumber());
                ordersViewHolder.tvOrderDate.setText(Methods.getParsedDate(orderModel.getCreatedOn()));

                if (orderModel.getBillingDetails() != null) {
                    ordersViewHolder.tvOrderTotalAmount.setText(TextUtils.isEmpty(orderModel.getBillingDetails().getCurrencyCode()) ? "INR" :
                            orderModel.getBillingDetails().getCurrencyCode() + " " + orderModel.getBillingDetails().getAmountPayableByBuyer() + "");
                } else {
                    ordersViewHolder.tvOrderTotalAmount.setText("N/A");
                }

                if (orderModel.getLogisticsDetails() != null) {
                    ordersViewHolder.tvExpDelivDate.setText(
                            Methods.getParsedDate(orderModel.getLogisticsDetails().getDeliveredOn()));
                } else {
                    ordersViewHolder.tvExpDelivDate.setText("N/A");
                }
                ordersViewHolder.tvHeadExp.setText("Exp. Deliv. Date:");
                if (orderModel.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.COMPLETED)) {
                    ordersViewHolder.tvOrderStatusTag.setTextColor(Color.parseColor("#4BB543"));
                    ordersViewHolder.tvOrderStatusTag.setText("Completed");
                    ordersViewHolder.tvHeadExp.setText("Deliv. Date:");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.delivered_order_text_bg));
                } else if (orderModel.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.INITIATED)) {
                    ordersViewHolder.tvOrderStatusTag.setTextColor(Color.parseColor("#808080"));
                    ordersViewHolder.tvOrderStatusTag.setText("Initiated");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.initiated_order_text_bg));
                } else if (orderModel.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.PLACED)) {
                    ordersViewHolder.tvOrderStatusTag.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
                    ordersViewHolder.tvOrderStatusTag.setText("PLACED");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.received_order_text_bg));
                } else if (orderModel.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.CONFIRMED)) {
                    ordersViewHolder.tvOrderStatusTag.setTextColor(ContextCompat.getColor(mContext, R.color.primary));
                    ordersViewHolder.tvOrderStatusTag.setText("Confirmed");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.received_order_text_bg));
                } else if (orderModel.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.CANCELLED)) {
                    ordersViewHolder.tvOrderStatusTag.setTextColor(Color.parseColor("#F80208"));
                    ordersViewHolder.tvOrderStatusTag.setText("Cancelled");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.cancelled_order_text_bg));
                } else if (orderModel.getStatus().equalsIgnoreCase(OrderListActivity.OrderStatus.ESCALATED)) {
                    ordersViewHolder.tvOrderStatusTag.setTextColor(Color.parseColor("#F80208"));
                    ordersViewHolder.tvOrderStatusTag.setText("Escalated");
                    ordersViewHolder.tvOrderStatusTag.setBackground(mContext.getDrawable(R.drawable.cancelled_order_text_bg));
                }
                if (orderModel.getOrderDetails() != null) {
                    ordersViewHolder.tvItemsCount.setText(orderModel.getOrderDetails().size() > 1 ? orderModel.getOrderDetails().size() + " Items" : orderModel.getOrderDetails().size() + " Item");

                    if (orderModel.getOrderDetails().get(0).getProduct() != null && orderModel.getOrderDetails().get(0).getProduct().getFeaturedImage() != null) {
                        Picasso.get().load(orderModel.getOrderDetails().get(0).getProduct().getFeaturedImage().getTileImageUri()).
                                into(ordersViewHolder.ivProducts);
                    }

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

    public List<Order> getOrders() {
        return mOrderModelList;
    }

    public void setOrders(List<Order> orderList) {
        this.mOrderModelList = orderList;
    }

    public void add(Order orderModel) {
        mOrderModelList.add(orderModel);
        notifyItemInserted(mOrderModelList.size() - 1);
    }

//    public void putToProductMap(String key, ProductModel val) {
//        if (mProductListMap.containsKey(key)) {
//            mProductListMap.get(key).add(val);
//        } else {
//            ArrayList<ProductModel> list = new ArrayList<>();
//            list.add(val);
//            mProductListMap.put(key, list);
//        }
//    }

    public void addAll(List<Order> orderModelList) {
        if (orderModelList != null && orderModelList.size() > 0) {
            int prevPosition = mOrderModelList.size();
            mOrderModelList.addAll(orderModelList);
            if (orderModelList.size() == 1) {
                notifyItemInserted(mOrderModelList.size() - 1);
            } else {
                notifyItemRangeInserted(prevPosition, mOrderModelList.size() - 1);
            }
        }
    }

    public void refreshList(List<Order> orderModelList) {
        this.mOrderModelList = orderModelList;
        notifyDataSetChanged();
    }

    public void remove(Order orderModel) {
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


//    public void addLoadingFooter() {
//        isLoadingAdded = true;
//        add(new Order());
//    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mOrderModelList.size() - 1;
        Order result = getItem(position);

        remove(result);
    }

    public Order getItem(int position) {
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
//        mProductListMap.clear();
        notifyDataSetChanged();
    }

    protected class OrdersViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderId, tvOrderDate, tvOrderTotalAmount, tvOrderStatusTag, tvExpDelivDate, tvItemsCount, tvHeadExp;
        ImageView ivProducts;

        public OrdersViewHolder(final View itemView) {
            super(itemView);

            tvOrderId = (TextView) itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = (TextView) itemView.findViewById(R.id.tv_order_date);
            tvOrderTotalAmount = (TextView) itemView.findViewById(R.id.tv_order_total_amount);
            tvOrderStatusTag = (TextView) itemView.findViewById(R.id.tv_order_status_tag);
            tvExpDelivDate = (TextView) itemView.findViewById(R.id.tv_exp_deliv_date);
            tvHeadExp = (TextView) itemView.findViewById(R.id.tvHeadExp);
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
