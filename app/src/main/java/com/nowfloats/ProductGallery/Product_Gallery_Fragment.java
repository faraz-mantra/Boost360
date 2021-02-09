package com.nowfloats.ProductGallery;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.ProductGallery.Model.ProductListModel;
import com.nowfloats.ProductGallery.Service.ProductAPIService;
import com.nowfloats.ProductGallery.Service.ProductDelete;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.accessbility.BubbleInAppDialog;
import com.nowfloats.on_boarding.OnBoardingApiCalls;
import com.nowfloats.sellerprofile.model.SellerProfile;
import com.nowfloats.sellerprofile.model.WebResponseModel;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 08-06-2015.
 */
public class Product_Gallery_Fragment extends Fragment implements ProductDelete.DeleteProductGalleryInterface {

    public static Bus bus;

    public static LinearLayout empty_layout, progressLayout;

    private GridView gridView;

    public ProductGalleryAdapter productGalleryAdapter;

    public static ArrayList<ProductListModel> productItemModelList;

    private Activity activity;

    private UserSessionManager session;

    int visibilityFlag = 1;

    private boolean userScrolled = false;

    private ProductAPIService apiService;

    private String currencyValue;

    private FROM from = FROM.DEFAULT;

    public static final String KEY_FROM = "KEY_FROM";

    private boolean isAnyProductSelected = false, mIsApEnabled = false;
    private String deliveryMethod = Constants.DeliveryMethod.ASSURED_PURCHASE.getValue();

    public enum FROM {
        BUBBLE,
        DEFAULT
    }

    private ArrayList<Integer> arrSelectedProducts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        arrSelectedProducts = new ArrayList<>();
        if (getArguments() != null)
            from = (FROM) getArguments().get(KEY_FROM);
        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        apiService = new ProductAPIService();
        currencyValue = getString(R.string.currency_text);

        new Thread(() -> {
            if (Constants.Currency_Country_Map == null) {
                Constants.Currency_Country_Map = new HashMap<String, String>();
                Constants.currencyArray = new ArrayList<String>();
            }
            if (Constants.Currency_Country_Map.size() == 0) {
                for (Locale locale : Locale.getAvailableLocales()) {
                    try {
                        if (locale != null && locale.getISO3Country() != null && Currency.getInstance(locale) != null) {
                            Currency currency = Currency.getInstance(locale);
                            String loc_currency = currency.getCurrencyCode();
                            String country = locale.getDisplayCountry();
                            if (!Constants.Currency_Country_Map.containsKey(country.toLowerCase())) {
                                Constants.Currency_Country_Map.put(country.toLowerCase(), loc_currency);
                                Constants.currencyArray.add(country + "-" + loc_currency);
                            }
                        }
                    } catch (Exception e) {
                        System.gc();
                        e.printStackTrace();
                    }
                }
            }
            try {
                currencyValue = Constants.Currency_Country_Map.get(
                        session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        Log.d("Product_Gallery", "onCreate");
    }

    private void checkIfAPEnabled() {

        /*Constants.webActionAdapter.create(ProductGalleryInterface.class)
                .getMerchantProfileData(String.format("{merchant_id:'%s'}", session.getFPID()), new Callback<WebActionModel<MerchantProfileModel>>() {
                    @Override
                    public void success(WebActionModel<MerchantProfileModel> merchantProfileModelWebActionModel, Response response) {
                        progressLayout.setVisibility(View.GONE);
                        if (merchantProfileModelWebActionModel.getData().size() > 0 && merchantProfileModelWebActionModel.getData().get(0).getPaymentType() == 0) {
                            mIsApEnabled = true;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressLayout.setVisibility(View.GONE);
                    }
                });*/

        if (!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats"))
        {
            return;
        }

        Constants.apApiAdapter.create(ProductGalleryInterface.class)
                .getSellerProfileData(session.getFpTag(), new Callback<WebResponseModel<SellerProfile>>() {
                    @Override
                    public void success(WebResponseModel<SellerProfile> webResponseModel, Response response) {
                        progressLayout.setVisibility(View.GONE);

                        if(webResponseModel != null && webResponseModel.getData() != null)
                        {
                            deliveryMethod = webResponseModel.getData().getDeliveryMethod();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressLayout.setVisibility(View.GONE);                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Product_Gallery", "onCreateView");
        getProducts("0");
        return inflater.inflate(R.layout.fragment_product__gallery, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        empty_layout = (LinearLayout) view.findViewById(R.id.emptyproductlayout);
        progressLayout = (LinearLayout) view.findViewById(R.id.progress_productlayout);
        progressLayout.setVisibility(View.VISIBLE);
        gridView = (GridView) view.findViewById(R.id.product_gridview);
        final FloatingActionButton addProduct = (FloatingActionButton) view.findViewById(R.id.fab_product);

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("-1")) {
                    Methods.showFeatureNotAvailDialog(getContext());
                }else {
                    MixPanelController.track(EventKeysWL.PRODUCT_GALLERY_ADD, null);
                    Intent intent;

                    intent = new Intent(activity, Product_Detail_Activity_V45.class);
                    intent.putExtra("new", "");
                    intent.putExtra("isApEnabled", mIsApEnabled);
                    intent.putExtra("deliveryMethod", deliveryMethod);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (from == FROM.DEFAULT) {

                    if (arrSelectedProducts.size() > 0) {
                        showOverlay(view);
                    } else {

                        Intent intent;

                        intent = new Intent(activity, Product_Detail_Activity_V45.class);
                        intent.putExtra("product", position + "");
                        intent.putExtra("isApEnabled", mIsApEnabled);
                        //intent.putExtra("deliveryMethod", deliveryMethod);

                        Methods.launchFromFragment(activity, view, intent);
                    }
                } else {
                    showOverlay(view);
                }

            }
        });

        gridView.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (from == FROM.DEFAULT) {
                showOverlay(view1);
            }
            return true;
        });
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }

                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    if (visibilityFlag == 0) {
                        visibilityFlag = 1;
                        YoYo.with(Techniques.SlideInUp).interpolate(new DecelerateInterpolator()).duration(200).playOn(addProduct);
                    }

                } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                    if (visibilityFlag == 1) {
                        YoYo.with(Techniques.SlideOutDown).interpolate(new AccelerateInterpolator()).duration(200).playOn(addProduct);
                        visibilityFlag = 0;

                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((userScrolled) && (lastInScreen == totalItemCount) && (totalItemCount % 10 == 0)) {
                    userScrolled = false;
                    getProducts("" + totalItemCount);
                }
            }
        });

        if (from == FROM.BUBBLE) {
            addProduct.setVisibility(View.GONE);
        }
    }

    private void showOverlay(View view) {
        ProductListModel productItemModel = (ProductListModel) view.getTag(R.string.key_details);
        if (productItemModel == null) {
            return;
        }
        if (isAnyProductSelected && !productItemModel.isProductSelected
                && from != FROM.DEFAULT) {
            Toast.makeText(activity, "You can select only one product", Toast.LENGTH_LONG).show();
        } else {
            productItemModel.isProductSelected = !productItemModel.isProductSelected;
            FrameLayout flMain = (FrameLayout) view.findViewById(R.id.flMain);
            FrameLayout flOverlay = (FrameLayout) view.findViewById(R.id.flOverlay);
            View vwOverlay = view.findViewById(R.id.vwOverlay);
            if (productItemModel.isProductSelected) {
                flOverlay.setVisibility(View.VISIBLE);
                setOverlay(vwOverlay, 200, flMain.getWidth(), flMain.getHeight());
                isAnyProductSelected = true;
                arrSelectedProducts.add((Integer) view.getTag(R.string.key_selected));
            } else {
                arrSelectedProducts.remove((Integer) view.getTag(R.string.key_selected));
                isAnyProductSelected = false;
                flOverlay.setVisibility(View.GONE);
            }

            if (from == FROM.DEFAULT) {
                if (arrSelectedProducts.size() > 0) {
                    ((ProductGalleryActivity) getActivity()).showActionDelete(
                            arrSelectedProducts.size());
                } else {
                    ((ProductGalleryActivity) getActivity()).hideActionDelete();
                }
            }
        }
    }

    public Uri getImageUri(Bitmap inImage, ProductListModel productItemModel) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), inImage, productItemModel.Name, productItemModel.Description);
        return Uri.parse(path);
    }


    public void setOverlay(View v, int opac, int width, int height) {
        int opacity = opac; // from 0 to 255
        v.setBackgroundColor(opacity * 0x1000000); // black with a variable alpha
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(width, height);
        params.gravity = Gravity.NO_GRAVITY;
        v.setLayoutParams(params);
        v.invalidate();
    }

    private void getProducts(String skip) {
        HashMap<String, String> values = new HashMap<>();
        values.put("clientId", Constants.clientId);
        values.put("skipBy", skip);
        values.put("fpTag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
        //invoke getProduct api
        apiService.getProductList(activity, values, bus);
    }

    @Subscribe
    public void loadMore(LoadMoreProductEvent event) {
        try {
            progressLayout.setVisibility(View.GONE);
            if (event.data != null) {
                //int addPos = productItemModelList.size();
                for (int i = 0; i < event.data.size(); i++) {
                    productItemModelList.add(event.data.get(i));
                    //addPos++;
                }
                productGalleryAdapter.refreshDetails(productItemModelList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.gc();
        }
        if (productItemModelList != null && !session.getOnBoardingStatus() && productItemModelList.size() != session.getProductsCount()){
            session.setProductsCount(productItemModelList.size());
            OnBoardingApiCalls.updateData(session.getFpTag(),String.format("add_product:%s",productItemModelList.size()>0?"true":"false"));
        }
    }

    private static final String PRODUCT_SEARCH = "PRODUCT_SEARCH";

    public void filterProducts(final String searchText) {

        if (productItemModelList != null && productItemModelList.size() > 0) {

            synchronized (PRODUCT_SEARCH) {

                try {
                    ArrayList<ProductListModel> arrModelTemp = null;
                    if (TextUtils.isEmpty(searchText)) {
                        arrModelTemp = productItemModelList;
                    } else {
                        Predicate<ProductListModel> searchItem = new Predicate<ProductListModel>() {
                            public boolean apply(ProductListModel productListModel) {
                                return (!TextUtils.isEmpty(productListModel.Description)
                                        && productListModel.Description.toLowerCase().contains(searchText.toLowerCase()))
                                        || (!TextUtils.isEmpty(productListModel.Name)
                                        && productListModel.Name.toLowerCase().contains(searchText.toLowerCase()));
                            }
                        };
                        arrModelTemp = (ArrayList<ProductListModel>)
                                filter(productItemModelList, searchItem);
                    }
                    productGalleryAdapter.refreshDetails(arrModelTemp);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public interface Predicate<T> {
        boolean apply(T type);
    }

    public static <T> Collection<T> filter(Collection<T> col, Predicate<T> predicate) {

        Collection<T> result = new ArrayList<T>();
        if (col != null) {
            for (T element : col) {
                if (predicate.apply(element)) {
                    result.add(element);
                }
            }
        }
        return result;
    }


    @Subscribe
    public void getProductList(ArrayList<ProductListModel> data) {

        if (data != null)
        {
            //Log.i("","PRoduct List Size--"+data.size());
            //Log.d("Product Id", data.get(0)._id);
            //checkIfAPEnabled();

            productItemModelList = data;
            productGalleryAdapter = new ProductGalleryAdapter(activity, currencyValue, from);
            gridView.setAdapter(productGalleryAdapter);
            gridView.invalidateViews();
            productGalleryAdapter.refreshDetails(productItemModelList);

            if (productItemModelList.size() == 0)
            {
                empty_layout.setVisibility(View.VISIBLE);
                session.setBubbleShareProducts(false);

                if (from == FROM.BUBBLE)
                {
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), BubbleInAppDialog.class));
                }
            }

            else
            {
                session.setBubbleShareProducts(true);
                empty_layout.setVisibility(View.GONE);
            }
        }

        else
        {
            progressLayout.setVisibility(View.GONE);

            if (productItemModelList == null || productItemModelList.size() == 0)
            {
                Product_Gallery_Fragment.empty_layout.setVisibility(View.VISIBLE);
            }

            else
            {
                Product_Gallery_Fragment.empty_layout.setVisibility(View.GONE);
            }

            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
        }

        if (productItemModelList != null && !session.getOnBoardingStatus() && productItemModelList.size() != session.getProductsCount())
        {
            session.setProductsCount(productItemModelList.size());
            OnBoardingApiCalls.updateData(session.getFpTag(),String.format("add_product:%s",productItemModelList.size()>0?"true":"false"));
        }
    }

//    public ArrayList<Uri> getSelectedProducts() {
//
//        ArrayList<Uri> arrayList = new ArrayList<Uri>();
//        for (ProductListModel productListModel : productItemModelList) {
//            if (productListModel.isProductSelected && productListModel.picimageURI != null) {
//                arrayList.add(productListModel.picimageURI);
//            }
//        }
//        return arrayList;
//    }

    public String getSelectedProducts() {

        String selectedProducts = "";
        if (productItemModelList == null) return null;
        for (ProductListModel productListModel : productItemModelList) {
            if (productListModel.isProductSelected) {

                try {

                    if (!TextUtils.isEmpty(session.getRootAliasURI())) {
                        selectedProducts = selectedProducts + session.getRootAliasURI() + "/";
                    } else {
                        selectedProducts = selectedProducts + "https://" + session.getFpTag() + ".nowfloats.com/";
                    }
//                    selectedProducts = selectedProducts + URLEncoder.encode(productListModel.Name, "UTF-8").replace("+","") + "/p" + productListModel.ProductIndex;
                    selectedProducts = selectedProducts + productListModel.Name.replaceAll("[^a-zA-Z0-9]+", "-") + "/p" + productListModel.ProductIndex;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return selectedProducts;
    }


    @Override
    public void onResume() {
        super.onResume();

        bus.register(this);
        if (productItemModelList != null && productItemModelList.size() == 0) {
            empty_layout.setVisibility(View.VISIBLE);
        } else {
            empty_layout.setVisibility(View.GONE);
        }

        if (productGalleryAdapter != null) {
            productGalleryAdapter.notifyDataSetChanged();
        }
        if (gridView != null) gridView.invalidateViews();
//        if (HomeActivity.plusAddButton != null)
//            HomeActivity.plusAddButton.setVisibility(View.GONE);
//        if (HomeActivity.headerText != null)
//            HomeActivity.headerText.setText(getString(R.string.product_gallery));
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    public void clearSelectedImages() {

        if (productGalleryAdapter != null) {
            for (ProductListModel productListModel : productItemModelList)
                productListModel.isProductSelected = false;
            arrSelectedProducts.clear();
            productGalleryAdapter.refreshDetails(productItemModelList);
        }
    }

    public void deleteSelectedProducts() {
        String url = Constants.NOW_FLOATS_API_URL + "/Product/v1/Delete";
        new ProductDelete(url, getActivity(), arrSelectedProducts,Product_Gallery_Fragment.this).execute();
    }

    @Override
    public void galleryProductDeleted() {
        gridView.invalidate();
        arrSelectedProducts.clear();
        productGalleryAdapter.notifyDataSetChanged();
    }
}