package com.nowfloats.ProductGallery;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Mobile_Site_Activity;
import com.nowfloats.NavigationDrawer.floating_view.ImagePickerBottomSheetDialog;
import com.nowfloats.ProductGallery.Adapter.SpinnerAdapter;
import com.nowfloats.ProductGallery.Model.AddressInformation;
import com.nowfloats.ProductGallery.Model.AssuredPurchase;
import com.nowfloats.ProductGallery.Model.BankInformation;
import com.nowfloats.ProductGallery.Model.Product;
import com.nowfloats.ProductGallery.Model.ProductImageResponseModel;
import com.nowfloats.ProductGallery.Model.Product_Gallery_Update_Model;
import com.nowfloats.ProductGallery.Model.Tag;
import com.nowfloats.ProductGallery.Model.UpdateValue;
import com.nowfloats.ProductGallery.Service.FileUpload;
import com.nowfloats.ProductGallery.Service.MultipleFileUpload;
import com.nowfloats.ProductGallery.Service.ProductGalleryInterface;
import com.nowfloats.ProductGallery.Service.UploadImage;
import com.nowfloats.ProductGallery.fragments.ProductPickupAddressFragment;
import com.nowfloats.helper.Helper;
import com.nowfloats.helper.ui.ImageLoader;
import com.nowfloats.manageinventory.models.WAAddDataModel;
import com.nowfloats.manageinventory.models.WaUpdateDataModel;
import com.nowfloats.manageinventory.models.WebActionModel;
import com.nowfloats.sellerprofile.model.WebResponseModel;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.WebEngageController;
import com.nowfloats.webactions.WebAction;
import com.nowfloats.webactions.WebActionsFilter;
import com.nowfloats.webactions.models.ProductImage;
import com.nowfloats.webactions.models.WebActionError;
import com.nowfloats.webactions.webactioninterfaces.IFilter;
import com.squareup.picasso.Picasso;
import com.thinksity.R;
import com.thinksity.databinding.FragmentManageProductBinding;

import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.app.Activity.RESULT_OK;
import static com.framework.webengageconstant.EventLabelKt.MANAGE_CONTENT;
import static com.framework.webengageconstant.EventLabelKt.PRODUCT_CATALOGUE_ADD_UPDATE;
import static com.framework.webengageconstant.EventNameKt.ADD_UPDATE_PRODUCT_CATALOGUE_LOADED;
import static com.framework.webengageconstant.EventNameKt.ERROR_UPDATE_PRODUCT_CATALOGUE;
import static com.framework.webengageconstant.EventNameKt.UPDATE_PRODUCT_CATALOGUE;
import static com.framework.webengageconstant.EventValueKt.NO_EVENT_VALUE;
import static com.nowfloats.util.Constants.DEV_ASSURED_PURCHASE_URL;


public class ManageProductFragment extends Fragment implements AdapterView.OnItemClickListener,
    UploadImage.ImageUploadListener, FileUpload.OnFileUpload {

  private String TAG = ManageProductFragment.class.getSimpleName();

  private String currencyType = "";
  private String productType = "PRODUCTS";
  private int MAX_IMAGE_ALLOWED = 8;

  private List<ProductImageResponseModel> imageList = new ArrayList<>();

  private ProductSpecificationRecyclerAdapter adapter;
  private ProductImageRecyclerAdapter adapterImage;
  private ProductPickupAddressRecyclerAdapter adapterAddress;

  private final int CAMERA_PERMISSION_REQUEST_CODE = 1;

  private final int CAMERA_PRIMARY_IMAGE_REQUEST_CODE = 101;
  private final int CAMERA_SECONDARY_IMAGE_REQUEST_CODE = 102;
  private final int CAMERA_PROOF_IMAGE_REQUEST_CODE = 103;

  private final int GALLERY_PRIMARY_IMAGE_REQUEST_CODE = 201;
  private final int GALLERY_SECONDARY_IMAGE_REQUEST_CODE = 202;
  private final int GALLERY_PROOF_IMAGE_REQUEST_CODE = 203;

  private final int DIALOG_REQUEST_CODE_PRIMARY = 1;
  private final int DIALOG_REQUEST_CODE_SECONDARY = 2;

  public static final String[] FILE_EXTENSIONS = new String[]{"doc", "docx", "xls", "xlsx", "pdf"};

  private Uri primaryUri, secondaryUri, proofUri;
  private File file;

  private String CATEGORY;
  private UserSessionManager session;
  private MaterialDialog materialDialog;

  private Constants.PaymentAndDeliveryMode paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE;

  private com.nowfloats.ProductGallery.Model.Product product;
  private AssuredPurchase assuredPurchase;
  private BankInformation bankInformation;
  private AddressInformation addressInformation;
  private List<AddressInformation> addressInformationList;

  private BottomSheetBehavior sheetBehavior;
  private BottomSheetBehavior sheetBehaviorAddress;

  private ProductPickupAddressFragment pickupAddressFragment;
  private String[] paymentOptionTitles;
  private WebAction mWebAction;
  private List<Tag> tags = new ArrayList<>();

  private boolean isService = false;

  public static ManageProductFragment newInstance(com.nowfloats.ProductGallery.Model.Product product) {
    ManageProductFragment fragment = new ManageProductFragment();

    Bundle args = new Bundle();
    args.putSerializable("PRODUCT", product);
    fragment.setArguments(args);

    return fragment;
  }

  private FragmentManageProductBinding binding;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle bundle = getArguments();

    if (bundle != null) {
      this.product = (com.nowfloats.ProductGallery.Model.Product) bundle.getSerializable("PRODUCT");

      if (product != null && product.otherSpecification == null) {
        product.otherSpecification = new ArrayList<>();
      }

      if (product != null && product.otherSpecification.size() == 0) {
        product.otherSpecification.add(new Product.Specification());
      }
    }

    mWebAction = getWebAction();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manage_product, container, false);
    sheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet.getRoot());
    sheetBehaviorAddress = BottomSheetBehavior.from(binding.layoutBottomSheetAddress.getRoot());
    WebEngageController.trackEvent(ADD_UPDATE_PRODUCT_CATALOGUE_LOADED, PRODUCT_CATALOGUE_ADD_UPDATE, NO_EVENT_VALUE);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    session = new UserSessionManager(getContext(), getActivity());
    this.paymentOptionTitles = getResources().getStringArray(R.array.payment_method_titles);

    initProductSpecificationRecyclerView(binding.layoutProductSpecification.productSpecificationList);
    initProductImageRecyclerView(binding.productImageList);
    initProductPickupAddressRecyclerView(binding.layoutBottomSheetAddress.pickupAddressList);

    addSpinnerListener();

    initCurrencyList();
    addPropertyListener();
    addQuantityListener();
    addSwitchVariantListener();
    addImagePickerListener();
    addPaymentConfigListener();
    initPaymentAdapter();
    spinnerAddressListener();
    addBottomSheetListener();
    addTextChangeListener();
    addInfoButtonListener();
    addTagViewListener();

    /**
     * Add key listener for restrict only one dot to these fields
     */
    binding.layoutShippingMatrixDetails.editWeight.setKeyListener(DigitsKeyListener.getInstance(false, true));
    binding.layoutShippingMatrixDetails.editHeight.setKeyListener(DigitsKeyListener.getInstance(false, true));
    binding.layoutShippingMatrixDetails.editLength.setKeyListener(DigitsKeyListener.getInstance(false, true));
    binding.layoutShippingMatrixDetails.editThickness.setKeyListener(DigitsKeyListener.getInstance(false, true));
    binding.editBasePrice.setKeyListener(DigitsKeyListener.getInstance(false, true));
    binding.editDiscount.setKeyListener(DigitsKeyListener.getInstance(false, true));
    binding.editGst.setKeyListener(DigitsKeyListener.getInstance(false, true));

    binding.layoutInventory.labelInventoryHint.setText("Inventory availability");
    binding.layoutInventory.labelInventoryQuantityHint.setText("Available qty.");
    binding.layoutInventoryOnline.labelInventoryHint.setText("Accept online payment");
    binding.layoutInventoryOnline.labelInventoryQuantityHint.setText("Max qty. per order");
    binding.layoutInventoryCod.labelInventoryHint.setText("Accept COD payment");
    binding.layoutInventoryCod.labelInventoryQuantityHint.setText("Max qty. per order");

    binding.btnPublish.setOnClickListener(view -> saveProduct());
    binding.btnDelete.setOnClickListener(view -> deleteConfirmation());

    displayPaymentAcceptanceMessage();

    Bundle bundle = getArguments();

    if (bundle != null) {
//            CATEGORY = bundle.getString("CATEGORY");
//            productType = bundle.getString("PRODUCT_TYPE");

      placeholder();

      StringBuilder title = new StringBuilder();

      if (product != null && product.productId != null) {
        setProductData();

        getAssuredPurchase(product.productId);
        displayImagesForProduct(product.productId);

        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.layoutBottomSheet.btnChange.setText(R.string.button_change);
      } else {
        binding.btnDelete.setVisibility(View.GONE);
        binding.layoutBottomSheet.btnChange.setText(R.string.button_add);
      }

//            isService = productType.equalsIgnoreCase("products") ? false : true;
      isService = session.isNonPhysicalProductExperienceCode();

      title.append(isService ? getString(R.string.service_details) : getString(R.string.product_details));
      ((ManageProductActivity) getActivity()).setTitle(title.toString());

      if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE.getValue())) {
        if (isService) {
          binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.GONE);
        } else {
          binding.layoutBottomSheet.layoutAddress.setVisibility(View.VISIBLE);
          binding.layoutBottomSheet.layoutPickupAddressInfo.setVisibility(View.VISIBLE);

          binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.VISIBLE);
        }

        binding.layoutAssuredPurchaseTax.setVisibility(View.VISIBLE);
        binding.layoutInventoryRoot.setVisibility(View.VISIBLE);
      } else {
        binding.layoutBottomSheet.layoutAddress.setVisibility(View.GONE);
        binding.layoutBottomSheet.layoutPickupAddressInfo.setVisibility(View.GONE);

        binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.GONE);

        binding.layoutAssuredPurchaseTax.setVisibility(View.GONE);
        binding.layoutInventoryRoot.setVisibility(View.GONE);
      }
    }

    this.makeLinkClickable(binding.layoutBottomSheet.tvAssuredPurchaseMessage);

    if (!Methods.isOnline(getActivity())) {
      return;
    }

    this.getBankInformation();
    this.getAddressInformation();
    this.getTagList();
  }

  protected void makeLinkClickable(TextView view) {
    SpannableStringBuilder spanTxt = new SpannableStringBuilder("*Assured Purchase can only have a single account number/branch code and tax number as part of periodic payment config. Make sure that above account details are correct. Read ");
    spanTxt.append(Methods.fromHtml("<u><b>T&C HERE</b></u>"));

    spanTxt.setSpan(new ClickableSpan() {

      @Override
      public void onClick(View widget) {
        Intent i = new Intent(getActivity(), Mobile_Site_Activity.class);
        i.putExtra("WEBSITE_NAME", getString(R.string.assured_purchase_link));
        startActivity(i);
      }
    }, spanTxt.length() - Methods.fromHtml("<u><b>T&C HERE</b></u>").length(), spanTxt.length(), 0);

    spanTxt.append(Methods.fromHtml("<a href=\"" + getString(R.string.assured_purchase_link) + "\"></a>"));

    view.setMovementMethod(LinkMovementMethod.getInstance());
    view.setText(spanTxt, TextView.BufferType.SPANNABLE);
  }

  private void changePickupAddressText(AddressInformation information) {
    binding.layoutBottomSheet.tvAddressType.setText(information.areaName != null ? information.areaName : "");
    binding.layoutBottomSheet.tvPickAddress.setText(information.toString());
    binding.layoutBottomSheet.tvMobileNumber.setText(information.contactNumber != null ? information.contactNumber : "");

    binding.layoutBottomSheet.tvPickAddress.setVisibility(View.VISIBLE);
    binding.layoutBottomSheet.tvMobileNumber.setVisibility(View.VISIBLE);
  }

  /**
   * If edit product/service set data ro fields
   */
  private void setProductData() {
    if (product == null) {
      return;
    }

    binding.editBrand.setText(product.brandName != null ? product.brandName : "");
    binding.editProductName.setText(product.Name != null ? product.Name : "");
    binding.editProductDescription.setText(product.Description != null ? product.Description : "");
    binding.editCategory.setText(product.category != null ? product.category : "");
    binding.editBasePrice.setText(product.Price > 0 ? new DecimalFormat("#.##").format(product.Price) : "");
    binding.editDiscount.setText(product.DiscountAmount > 0 ? new DecimalFormat("#.##").format(product.DiscountAmount) : "");

    this.setFinalPrice();

    /**
     * Product availability and quantity
     */
    if (!product.IsAvailable) {
      binding.layoutInventory.spinnerStockAvailability.setSelection(2);
    } else if (product.availableUnits > 0) {
      binding.layoutInventory.spinnerStockAvailability.setSelection(0);
      binding.layoutInventory.quantityValue.setText(String.valueOf(product.availableUnits));
    } else {
      binding.layoutInventory.spinnerStockAvailability.setSelection(1);
    }


    /**
     * COD product availability and quantity
     */
    if (product.codAvailable) {
      binding.layoutInventoryCod.spinnerStockAvailability.setSelection(0);
    } else {
      binding.layoutInventoryCod.spinnerStockAvailability.setSelection(1);
      binding.layoutInventoryCod.quantityValue.setText(String.valueOf(product.maxCodOrders));
    }

    /**
     * Prepaid product availability and quantity
     */
    if (product.prepaidOnlineAvailable) {
      binding.layoutInventoryOnline.spinnerStockAvailability.setSelection(0);
    } else {
      binding.layoutInventoryOnline.spinnerStockAvailability.setSelection(1);
      binding.layoutInventoryOnline.quantityValue.setText(String.valueOf(product.maxPrepaidOnlineAvailable));
    }

    if (product.keySpecification != null) {
      binding.layoutProductSpecification.layoutKeySpecification.editKey.setText(product.keySpecification.key != null ? product.keySpecification.key : "");
      binding.layoutProductSpecification.layoutKeySpecification.editValue.setText(product.keySpecification.value != null ? product.keySpecification.value : "");
    }

    if (product.paymentType != null) {
      //If payment type is assured purchase
      if (product.paymentType.equalsIgnoreCase(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE.getValue())) {
        paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE;
        binding.layoutPaymentMethod.tvPaymentConfiguration.setText(paymentOptionTitles[0]);

        binding.layoutBottomSheet.spinnerPaymentOption.setSelection(0);

        binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setVisibility(View.VISIBLE);
        binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setText(getString(R.string.payment_methud_message));
        binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);

        binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.GONE);

        if (isService) {
          binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.GONE);
        } else {
          binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.VISIBLE);
        }

        binding.layoutAssuredPurchaseTax.setVisibility(View.VISIBLE);
        binding.layoutInventoryRoot.setVisibility(View.VISIBLE);
      }

      //If payment type is unique payment url
      else if (product.paymentType.equalsIgnoreCase(Constants.PaymentAndDeliveryMode.UNIQUE_PAYMENT_URL.getValue())) {
        paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.UNIQUE_PAYMENT_URL;
        binding.layoutPaymentMethod.tvPaymentConfiguration.setText(paymentOptionTitles[1]);

        binding.layoutBottomSheet.spinnerPaymentOption.setSelection(1);
        binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setVisibility(View.GONE);

        binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.VISIBLE);

        binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.GONE);

        binding.layoutAssuredPurchaseTax.setVisibility(View.GONE);
        binding.layoutInventoryRoot.setVisibility(View.GONE);

        if (product.BuyOnlineLink != null) {
          binding.layoutPaymentMethod.editWebsite.setText(product.BuyOnlineLink.description != null ? product.BuyOnlineLink.description : "");
          binding.layoutPaymentMethod.editPurchaseUrlLink.setText(product.BuyOnlineLink.url != null ? product.BuyOnlineLink.url : "");
        }
      }

      //If payment type is dont want to sell
      else if (product.paymentType.equalsIgnoreCase(Constants.PaymentAndDeliveryMode.DONT_WANT_TO_SELL.getValue())) {
        paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.DONT_WANT_TO_SELL;
        binding.layoutPaymentMethod.tvPaymentConfiguration.setText(paymentOptionTitles[2]);

        binding.layoutBottomSheet.spinnerPaymentOption.setSelection(2);

        binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setVisibility(View.VISIBLE);
        binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setText(getString(R.string.payment_method_dont_want_to_sell));
        binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.GONE);

        binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.GONE);

        binding.layoutAssuredPurchaseTax.setVisibility(View.GONE);
        binding.layoutInventoryRoot.setVisibility(View.GONE);
      }
    }

    if (product.tags != null) {
      for (int i = 0; i < product.tags.size(); i++) {
        tags.add(new Tag(product.tags.get(i), String.valueOf(i)));
      }

      binding.tvProductKeyword.addTags(tags);
    }

    try {
      String image_url = product.TileImageUri;

      if (image_url != null && image_url.length() > 0 && !image_url.equals("null")) {
        if (!image_url.contains("http")) {
          image_url = Constants.BASE_IMAGE_URL + product.TileImageUri;
        }

        Picasso.get().load(image_url).placeholder(R.drawable.default_product_image).into(binding.ivPrimaryImage);
        binding.ibRemoveProductImage.setVisibility(View.VISIBLE);
      } else {
        Picasso.get().load(R.drawable.default_product_image).into(binding.ivPrimaryImage);
        binding.ibRemoveProductImage.setVisibility(View.GONE);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Set assured purchase filed data
   */
  private void setAssuredPurchaseData() {
    if (assuredPurchase == null) {
      return;
    }

    binding.layoutShippingMatrixDetails.editHeight.setText(assuredPurchase.height > 0 ? String.valueOf(assuredPurchase.height) : "");
    binding.layoutShippingMatrixDetails.editWeight.setText(assuredPurchase.weight > 0 ? String.valueOf(assuredPurchase.weight) : "");
    binding.layoutShippingMatrixDetails.editLength.setText(assuredPurchase.length > 0 ? String.valueOf(assuredPurchase.length) : "");
    binding.layoutShippingMatrixDetails.editThickness.setText(assuredPurchase.width > 0 ? String.valueOf(assuredPurchase.width) : "");
    binding.editGst.setText(assuredPurchase.gstCharge > 0 ? String.valueOf(assuredPurchase.gstCharge) : "");
  }

  /**
   * Set seller information filed data
   */
  private void setBankInformationData() {
    if (bankInformation == null) {
      return;
    }

    binding.layoutBottomSheet.editGst.setText(bankInformation.gstn != null ? bankInformation.gstn : "");

    if (bankInformation.bankAccount == null) {
      return;
    }

    binding.layoutBottomSheet.editIfscCode.setText(bankInformation.bankAccount.ifsc != null ? bankInformation.bankAccount.ifsc : "");
    binding.layoutBottomSheet.editBankAccount.setText(bankInformation.bankAccount.number != null ? bankInformation.bankAccount.number : "");
  }


  private void addTagViewListener() {
    binding.tvProductKeyword.setOnTagDeleteListener((view, tag, position) -> {

      tags.remove(position);
      binding.tvProductKeyword.remove(position);

      product.tags.remove(position);
    });

    binding.btnAddTag.setOnClickListener(v -> addTag(binding.editProductTags.getText().toString()));
  }


  private void addTag(String tag) {
    if (tag.trim().length() > 0) {
      if (product.tags == null) {
        product.tags = new ArrayList<>();
      }

      Tag obj = new Tag(tag, String.valueOf(product.tags.size()));
      binding.tvProductKeyword.addTag(obj);
      tags.add(obj);

      product.tags.add(tag);

      binding.editProductTags.setText("");
    }
  }

  private void addTextChangeListener() {
    binding.editBasePrice.addTextChangedListener(new TextWatcher() {

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        setFinalPrice();
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    binding.editDiscount.addTextChangedListener(new TextWatcher() {

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        setFinalPrice();
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });
  }

  /**
   * Calculate final price by subtracting base price - discount
   */
  private void setFinalPrice() {
    try {
      if (binding.editBasePrice.getText().toString().isEmpty()) {
        binding.editDiscount.setEnabled(false);
      } else {
        binding.editDiscount.setEnabled(true);
      }

      String basePrice = binding.editBasePrice.getText().toString().isEmpty() ? "0" : binding.editBasePrice.getText().toString();
      String discount = binding.editDiscount.getText().toString().isEmpty() ? "0" : binding.editDiscount.getText().toString();

      double finalPrice = Double.valueOf(basePrice) - Double.valueOf(discount);
      binding.labelFinalPrice.setText(currencyType.concat(" ").concat(Helper.getCurrencyFormatter().format(finalPrice)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Set placeholders based on product/service selection
   */
  private void placeholder() {
    String category;

    if (isService) {
      category = "Service";
    } else {
      category = "Product";
    }

    binding.labelProductName.setText(String.format(getString(R.string.label_product_name), category));
    binding.editProductName.setHint(String.format(getString(R.string.hint_product_name), category.toLowerCase()));

    binding.labelProductDescription.setText(String.format(getString(R.string.label_product_description), category));
    binding.editProductDescription.setHint(String.format(getString(R.string.hint_product_description), category.toLowerCase()));
  }

  /**
   * Add listener based on add property button click
   */
  private void addPropertyListener() {
    binding.layoutProductSpecification.buttonAddProperty.setOnClickListener(view -> {

      if (product.otherSpecification == null) {
        product.otherSpecification = new ArrayList<>();
      }

      product.otherSpecification.add(new com.nowfloats.ProductGallery.Model.Product.Specification());
      adapter.notifyItemInserted(product.otherSpecification.size());
    });
  }


  /**
   * Display payment acceptance message
   */
  private void displayPaymentAcceptanceMessage() {
    try {
      //String transactionFees = WidgetKey.getPropertyValue(WidgetKey.WIDGET_TRANSACTION_FEES, WidgetKey.WIDGET_PROPERTY_TRANSACTION_FEES);

      String transactionFees = "3";

      if (Double.valueOf(transactionFees) > 0) {
        transactionFees = transactionFees.contains("%") ? transactionFees : transactionFees.concat("%");
        binding.layoutBottomSheet.tvPaymentConfigurationAcceptanceMessage.setText(String.format(getString(R.string.payment_config_acceptance_message), transactionFees));

        binding.layoutBottomSheet.layoutPaymentMethodAcceptance.setVisibility(View.VISIBLE);
      } else {
        binding.layoutBottomSheet.layoutPaymentMethodAcceptance.setVisibility(View.GONE);
      }
    } catch (Exception e) {
      binding.layoutBottomSheet.layoutPaymentMethodAcceptance.setVisibility(View.GONE);
      e.printStackTrace();
    }
  }

  /**
   * Initialize payment option spinner adapter
   */
  private void initPaymentAdapter() {
    binding.layoutBottomSheet.layoutAssuredPurchase.setVisibility(View.GONE);

    SpinnerAdapter spinnerAdapter = new SpinnerAdapter(getActivity());
    binding.layoutBottomSheet.spinnerPaymentOption.setAdapter(spinnerAdapter);

    binding.layoutBottomSheet.spinnerPaymentOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        spinnerAdapter.setSelection(position);

        switch (position) {
          case 0:

            if (isService) {
              binding.layoutBottomSheet.layoutAddress.setVisibility(View.GONE);
              binding.layoutBottomSheet.layoutPickupAddressInfo.setVisibility(View.GONE);
            } else {
              binding.layoutBottomSheet.layoutAddress.setVisibility(View.VISIBLE);
              binding.layoutBottomSheet.layoutPickupAddressInfo.setVisibility(View.VISIBLE);
            }

            binding.layoutBottomSheet.layoutAssuredPurchase.setVisibility(View.VISIBLE);
            break;

          default:

            binding.layoutBottomSheet.layoutAssuredPurchase.setVisibility(View.GONE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    binding.layoutBottomSheet.btnCancel.setOnClickListener(v -> toggleBottomSheet());
    binding.layoutBottomSheet.btnSaveInfo.setOnClickListener(v -> {

      switch (binding.layoutBottomSheet.spinnerPaymentOption.getSelectedItemPosition()) {
        case 0:

          paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE;
          binding.layoutAssuredPurchaseTax.setVisibility(View.VISIBLE);
          binding.layoutInventoryRoot.setVisibility(View.VISIBLE);

          displayPaymentAcceptanceMessage();

          if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE.getValue())
              && !isService) {
            binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.VISIBLE);
          } else {
            binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.GONE);
          }

          if (binding.layoutBottomSheet.layoutPaymentMethodAcceptance.getVisibility() == View.VISIBLE
              && !binding.layoutBottomSheet.checkPaymentConfiguration.isChecked()) {
            Toast.makeText(getContext(), "Please accept terms and condition", Toast.LENGTH_SHORT).show();
            return;
          }

          if (isValidBankInformation()) {
            saveBankInformation();
          }

          break;

        case 1:

          paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.UNIQUE_PAYMENT_URL;

          binding.layoutAssuredPurchaseTax.setVisibility(View.GONE);
          binding.layoutInventoryRoot.setVisibility(View.GONE);

          binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.GONE);

          binding.layoutBottomSheet.layoutPaymentMethodAcceptance.setVisibility(View.GONE);

          binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setVisibility(View.GONE);
          binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.VISIBLE);

          binding.layoutPaymentMethod.tvPaymentConfiguration.setText(paymentOptionTitles[1]);

          toggleBottomSheet();
          break;

        case 2:

          paymentAndDeliveryMode = Constants.PaymentAndDeliveryMode.DONT_WANT_TO_SELL;
          binding.layoutAssuredPurchaseTax.setVisibility(View.GONE);
          binding.layoutInventoryRoot.setVisibility(View.GONE);

          binding.layoutShippingMatrixDetails.layoutShippingMatrix.setVisibility(View.GONE);

          binding.layoutBottomSheet.layoutPaymentMethodAcceptance.setVisibility(View.GONE);

          binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setVisibility(View.VISIBLE);
          binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.GONE);

          binding.layoutPaymentMethod.tvPaymentConfiguration.setText(paymentOptionTitles[2]);
          binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setText(getString(R.string.payment_method_dont_want_to_sell));
          binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

          toggleBottomSheet();
          break;
      }
    });
  }


  private void addSwitchVariantListener() {
    binding.switchVariants.setOnToggledListener((labeledSwitch, isOn) -> {

    });
  }

  /**
   * Pickup address button listener
   */
  private void spinnerAddressListener() {
    binding.layoutBottomSheetAddress.buttonAddNew.setOnClickListener(v -> {

      sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
      openAddressDialog(null);
    });

    binding.layoutBottomSheet.btnChange.setOnClickListener(view -> {

      Intent intent = new Intent(getActivity(), PickupAddressActivity.class);
      intent.putExtra("ADDRESS_ID", product.pickupAddressReferenceId);
      startActivityForResult(intent, 10);
    });

    binding.layoutBottomSheetAddress.ibClose.setOnClickListener(view -> toggleAddressBottomSheet());
  }


  private void addBottomSheetListener() {
    sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

      @Override
      public void onStateChanged(@NonNull View view, int i) {

        sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
      }

      @Override
      public void onSlide(@NonNull View view, float v) {

      }
    });
  }


  /**
   * Initialize product specification adapter
   *
   * @param recyclerView
   */
  private void initProductSpecificationRecyclerView(RecyclerView recyclerView) {
    adapter = new ProductSpecificationRecyclerAdapter();
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
  }


  /**
   * Initialize product secondary image adapter
   *
   * @param recyclerView
   */
  private void initProductImageRecyclerView(RecyclerView recyclerView) {
    adapterImage = new ProductImageRecyclerAdapter();
    recyclerView.setAdapter(adapterImage);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
  }


  /**
   * Initialize pickup address list adapter
   *
   * @param recyclerView
   */
  private void initProductPickupAddressRecyclerView(RecyclerView recyclerView) {
    adapterAddress = new ProductPickupAddressRecyclerAdapter();
    recyclerView.setAdapter(adapterAddress);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
  }

  /**
   * open add/edit address dialog
   *
   * @param addressInformation
   */
  private void openAddressDialog(AddressInformation addressInformation) {
    if (addressInformation == null) {
      pickupAddressFragment = ProductPickupAddressFragment.newInstance();
    } else if (pickupAddressFragment == null) {
      pickupAddressFragment = ProductPickupAddressFragment.newInstance();
    }

    pickupAddressFragment.setAddress(addressInformation);
    pickupAddressFragment.isFileSelected(false);

    pickupAddressFragment.show(getFragmentManager(), "Address");

    pickupAddressFragment.setOnClickListener(information -> saveAddressInformation(information));

    pickupAddressFragment.setFileChooserListener(new ProductPickupAddressFragment.OnFileChooser() {

      @Override
      public void openDialog() {

      }

      @Override
      public void onFileRemove() {

        file = null;
        pickupAddressFragment.isFileSelected(false);
      }
    });
  }


  /**
   * Add listener to payment configuration click
   * start payment configuration bottom sheet
   */
  public void addPaymentConfigListener() {
    binding.layoutPaymentMethod.tvPaymentConfiguration.setOnClickListener(view -> toggleBottomSheet());
  }

  /**
   * Add listener for primary and secondary image picker
   */
  private void addImagePickerListener() {
    binding.cardPrimaryImage.setOnClickListener(v -> choosePicture(DIALOG_REQUEST_CODE_PRIMARY));
    binding.btnSecondaryImage.setOnClickListener(v -> choosePicture(DIALOG_REQUEST_CODE_SECONDARY));
    binding.ibRemoveProductImage.setOnClickListener(v -> {

      binding.ivPrimaryImage.setImageDrawable(null);
      binding.ibRemoveProductImage.setVisibility(View.GONE);
      primaryUri = null;

      if (product != null && product.TileImageUri != null) {
        product.TileImageUri = null;
      }
    });
  }


  /**
   * Image picker dialog
   *
   * @param requestCode
   */
  private void choosePicture(int requestCode) {

    final ImagePickerBottomSheetDialog imagePickerBottomSheetDialog = new ImagePickerBottomSheetDialog(image_click_type -> {
      if (image_click_type.name().equals(ImagePickerBottomSheetDialog.IMAGE_CLICK_TYPE.CAMERA.name())) {
        switch (requestCode) {
          case DIALOG_REQUEST_CODE_PRIMARY:
            cameraIntent(CAMERA_PRIMARY_IMAGE_REQUEST_CODE);
            break;

          case DIALOG_REQUEST_CODE_SECONDARY:
            cameraIntent(CAMERA_SECONDARY_IMAGE_REQUEST_CODE);
            break;
        }
      } else {
        switch (requestCode) {
          case DIALOG_REQUEST_CODE_PRIMARY:
            openImagePicker(GALLERY_PRIMARY_IMAGE_REQUEST_CODE, 1);
            break;

          case DIALOG_REQUEST_CODE_SECONDARY:
            int max = MAX_IMAGE_ALLOWED - adapterImage.getItemCount();
            max = max > 0 ? max : 1;
            openImagePicker(GALLERY_SECONDARY_IMAGE_REQUEST_CODE, max);
            break;
        }
      }
    });
    imagePickerBottomSheetDialog.show(getParentFragmentManager(), ImagePickerBottomSheetDialog.class.getName());

//        final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
//                .customView(R.layout.featuredimage_popup, true)
//                .show();
//
//        final PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
//
//        View view = dialog.getCustomView();
//
//        LinearLayout takeCamera = view.findViewById(R.id.cameraimage);
//        LinearLayout takeGallery = view.findViewById(R.id.galleryimage);
//        ImageView cameraImg = view.findViewById(R.id.pop_up_camera_imag);
//        ImageView galleryImg = view.findViewById(R.id.pop_up_gallery_img);
//        cameraImg.setColorFilter(whiteLabelFilter_pop_ip);
//        galleryImg.setColorFilter(whiteLabelFilter_pop_ip);

//        takeCamera.setOnClickListener(v -> {
//
//            switch (requestCode) {
//                case DIALOG_REQUEST_CODE_PRIMARY:
//
//                    cameraIntent(CAMERA_PRIMARY_IMAGE_REQUEST_CODE);
//                    break;
//
//                case DIALOG_REQUEST_CODE_SECONDARY:
//
//                    cameraIntent(CAMERA_SECONDARY_IMAGE_REQUEST_CODE);
//                    break;
//            }
//
//            dialog.dismiss();
//        });
//
//        takeGallery.setOnClickListener(v -> {
//
//            switch (requestCode) {
//                case DIALOG_REQUEST_CODE_PRIMARY:
//
//                    openImagePicker(GALLERY_PRIMARY_IMAGE_REQUEST_CODE, 1);
//                    break;
//
//                case DIALOG_REQUEST_CODE_SECONDARY:
//
//                    int max = MAX_IMAGE_ALLOWED - adapterImage.getItemCount();
//                    max = max > 0 ? max : 1;
//                    openImagePicker(GALLERY_SECONDARY_IMAGE_REQUEST_CODE, max);
//                    break;
//            }
//
//            dialog.dismiss();
//        });
  }

  /**
   * Initialize currency list
   */
  private void initCurrencyList() {
    currencyType = getString(R.string.currency_text);

    if (product != null && !TextUtils.isEmpty(product.CurrencyCode)) {
      currencyType = product.CurrencyCode;
    } else {
      try {
        currencyType = Constants.Currency_Country_Map.get(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY).toLowerCase());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    binding.editCurrency.setText(currencyType);

    Helper.loadCurrency();

    binding.editCurrency.setOnClickListener(view -> {

      String[] array = Constants.currencyArray.toArray(new String[Constants.currencyArray.size()]);
      Arrays.sort(array);

      showCurrencyList(array);
    });
  }


  private void addQuantityListener() {
    binding.layoutInventory.quantityValue.setText(String.valueOf(product.availableUnits));
    binding.layoutInventoryOnline.quantityValue.setText(String.valueOf(product.maxPrepaidOnlineAvailable));
    binding.layoutInventoryCod.quantityValue.setText(String.valueOf(product.maxCodOrders));

    binding.layoutInventory.quantityValue.addTextChangedListener(new TextWatcher() {

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        product.availableUnits = binding.layoutInventory.quantityValue.getText().toString().length() == 0 ? 0 : Integer.valueOf(binding.layoutInventory.quantityValue.getText().toString());
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    binding.layoutInventoryCod.quantityValue.addTextChangedListener(new TextWatcher() {

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        product.maxCodOrders = binding.layoutInventoryCod.quantityValue.getText().toString().length() == 0 ? 0 : Integer.valueOf(binding.layoutInventoryCod.quantityValue.getText().toString());
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    binding.layoutInventoryOnline.quantityValue.addTextChangedListener(new TextWatcher() {

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

        product.maxPrepaidOnlineAvailable = binding.layoutInventoryOnline.quantityValue.getText().toString().length() == 0 ? 0 : Integer.valueOf(binding.layoutInventoryOnline.quantityValue.getText().toString());
      }

      @Override
      public void afterTextChanged(Editable s) {


      }
    });

    binding.layoutInventory.addQuantity.setOnClickListener(view -> {

      try {
        if (product.availableUnits <= 0) {
          product.availableUnits = 1;
        }

        product.availableUnits++;
        binding.layoutInventory.quantityValue.setText(String.valueOf(product.availableUnits));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    binding.layoutInventory.removeQuantity.setOnClickListener(view -> {

      try {
        if (product.availableUnits > 0) {
          product.availableUnits--;
        }

        binding.layoutInventory.quantityValue.setText(String.valueOf(product.availableUnits));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    binding.layoutInventoryOnline.addQuantity.setOnClickListener(view -> {

      try {
        if (product.maxPrepaidOnlineAvailable < 0) {
          product.maxPrepaidOnlineAvailable = 0;
        }

        product.maxPrepaidOnlineAvailable++;
        binding.layoutInventoryOnline.quantityValue.setText(String.valueOf(product.maxPrepaidOnlineAvailable));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    binding.layoutInventoryOnline.removeQuantity.setOnClickListener(view -> {

      try {
        if (product.maxPrepaidOnlineAvailable > 0) {
          product.maxPrepaidOnlineAvailable--;
        }

        binding.layoutInventoryOnline.quantityValue.setText(String.valueOf(product.maxPrepaidOnlineAvailable));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    binding.layoutInventoryCod.addQuantity.setOnClickListener(view -> {

      try {
        if (product.maxCodOrders < 0) {
          product.maxCodOrders = 0;
        }

        product.maxCodOrders++;
        binding.layoutInventoryCod.quantityValue.setText(String.valueOf(product.maxCodOrders));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    binding.layoutInventoryCod.removeQuantity.setOnClickListener(view -> {

      try {
        if (product.maxCodOrders > 0) {
          product.maxCodOrders--;
        }

        binding.layoutInventoryCod.quantityValue.setText(String.valueOf(product.maxCodOrders));
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }


  private void addSpinnerListener() {
    String[] stockOptions = getResources().getStringArray(R.array.stock_options);
    String[] stockAvailability = getResources().getStringArray(R.array.stock_availability);

    ArrayAdapter<String> spinner1 = new ArrayAdapter<>(getActivity(), R.layout.customized_spinner_item, stockAvailability);
    ArrayAdapter<String> spinner2 = new ArrayAdapter<>(getActivity(), R.layout.customized_spinner_item, stockOptions);

    binding.layoutInventory.spinnerStockAvailability.setAdapter(spinner1);
    binding.layoutInventoryOnline.spinnerStockAvailability.setAdapter(spinner2);
    binding.layoutInventoryCod.spinnerStockAvailability.setAdapter(spinner2);

    binding.layoutInventory.spinnerStockAvailability.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
          case 0:

            binding.layoutInventory.ivStockIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_availble_indicator));
            binding.layoutInventory.layoutQuantityMain.setVisibility(View.VISIBLE);

            if (product != null && product.availableUnits > 0) {
              binding.layoutInventory.quantityValue.setText(String.valueOf(product.availableUnits));
            } else {
              binding.layoutInventory.quantityValue.setText("1");
            }

            break;

          case 1:

            binding.layoutInventory.ivStockIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_unlimited_indicator));
            binding.layoutInventory.layoutQuantityMain.setVisibility(View.INVISIBLE);
            break;

          case 2:

            binding.layoutInventory.ivStockIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_unavailble_indicator));
            binding.layoutInventory.layoutQuantityMain.setVisibility(View.INVISIBLE);
            break;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    binding.layoutInventoryOnline.spinnerStockAvailability.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
          case 0:

            binding.layoutInventoryOnline.ivStockIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_availble_indicator));
            binding.layoutInventoryOnline.layoutQuantityMain.setVisibility(View.VISIBLE);
            break;

          case 1:

            binding.layoutInventoryOnline.ivStockIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_unavailble_indicator));
            binding.layoutInventoryOnline.layoutQuantityMain.setVisibility(View.INVISIBLE);
            break;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    binding.layoutInventoryCod.spinnerStockAvailability.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
          case 0:

            binding.layoutInventoryCod.ivStockIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_availble_indicator));
            binding.layoutInventoryCod.layoutQuantityMain.setVisibility(View.VISIBLE);
            break;

          case 1:

            binding.layoutInventoryCod.ivStockIndicator.setImageDrawable(getResources().getDrawable(R.drawable.ic_unavailble_indicator));
            binding.layoutInventoryCod.layoutQuantityMain.setVisibility(View.INVISIBLE);
            break;
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });
  }


  /**
   * Show currency dialog on click
   *
   * @param currencyList
   * @return
   */
  public String showCurrencyList(final String[] currencyList) {
    String currencyVal = binding.editCurrency.getText().toString().trim();
    int index = 0;

    if (!Util.isNullOrEmpty(currencyVal)) {
      index = Arrays.asList(currencyList).indexOf(currencyVal);
    }

    new MaterialDialog.Builder(getActivity())
        .title(getString(R.string.select_currency))
        .items(currencyList)
        .widgetColorRes(R.color.primaryColor)
        .itemsCallbackSingleChoice(index, (dialog, view, position, text) -> {

          try {
            currencyType = currencyList[position];
            currencyType = currencyType.split("-")[1];
            binding.editCurrency.setText(currencyType);
            setFinalPrice();
          } catch (Exception e) {
            e.printStackTrace();
          }

          dialog.dismiss();
          return true;
        }).show();

    return currencyType;
  }

  /**
   * Open image picker activity
   *
   * @param requestCode
   * @param max
   */
  private void openImagePicker(int requestCode, int max) {
    boolean folderMode = true;
    boolean multipleMode = true;

    ImagePicker.with(getActivity())
        .setFolderMode(folderMode)
        .setShowCamera(false)
        .setFolderTitle("Album")
        .setMultipleMode(multipleMode)
        .setMaxSize(max)
        .setBackgroundColor("#212121")
        .setAlwaysShowDoneButton(true)
        .setRequestCode(requestCode)
        .setKeepScreenOn(true)
        .start();
  }

  /**
   * display/hide secondary image add button based on max upload limit
   */
  private void displayImageAddButton() {
    if (MAX_IMAGE_ALLOWED > adapterImage.getItemCount()) {
      binding.layoutAddImage.setVisibility(View.VISIBLE);
    } else {
      binding.layoutAddImage.setVisibility(View.GONE);
    }

    if (adapterImage.getItemCount() > 0) {
      binding.btnSecondaryImage.setText(R.string.add_more);
    } else {
      binding.btnSecondaryImage.setText(R.string.browse_secondary_image);
    }
  }

  /**
   * Check camera permission
   *
   * @param requestCode
   */
  private void cameraIntent(int requestCode) {
    try {
      if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
          PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) !=
          PackageManager.PERMISSION_GRANTED) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
            ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {

          Methods.showApplicationPermissions(getString(R.string.camera_and_storage_permission), getString(R.string.we_need_this_permission_to_capture), getActivity());
        } else {
          ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
      } else {
        startCamera(requestCode);
      }
    } catch (ActivityNotFoundException e) {
      String errorMessage = getString(R.string.device_does_not_support_capturing_image);
      Methods.showSnackBarNegative(getActivity(), errorMessage);
    }
  }

  /**
   * Start camera intent
   *
   * @param requestCode
   */
  private void startCamera(int requestCode) {
    File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "boost");
    Uri tempUri;

    if (!mediaStorageDir.exists()) {
      mediaStorageDir.mkdir();
    }

    /**
     * Check if we're running on Android 5.0 or higher
     */
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      tempUri = FileProvider.getUriForFile(getActivity(),
          Constants.PACKAGE_NAME + ".provider",
          new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
    } else {
      tempUri = Uri.fromFile(new File(mediaStorageDir + "/" + System.currentTimeMillis() + ".jpg"));
    }

    switch (requestCode) {
      case CAMERA_PRIMARY_IMAGE_REQUEST_CODE:

        primaryUri = tempUri;
        break;

      case CAMERA_SECONDARY_IMAGE_REQUEST_CODE:

        secondaryUri = tempUri;
        break;

      case CAMERA_PROOF_IMAGE_REQUEST_CODE:

        proofUri = tempUri;
        break;
    }

    try {
      Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
      startActivityForResult(intent, requestCode);
    } catch (Exception e) {
      Toast.makeText(getContext(), "Failed to Open Camera", Toast.LENGTH_LONG).show();
    }
  }

  /**
   * Toggle payment bottom sheet
   */
  private void toggleBottomSheet() {
    if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
      sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    } else {
      sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
  }

  /**
   * Toggle pickup address list bottom sheet
   */
  private void toggleAddressBottomSheet() {
    if (sheetBehaviorAddress.getState() != BottomSheetBehavior.STATE_EXPANDED) {
      sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_EXPANDED);
    } else {
      sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
  }

  /**
   * Activity result for CAMERA, IMAGE GALLERY AND FILE PICKER
   *
   * @param requestCode
   * @param resultCode
   * @param data
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK && /*requestCode == Config.RC_PICK_IMAGES*/ (requestCode == GALLERY_PRIMARY_IMAGE_REQUEST_CODE ||
        requestCode == GALLERY_SECONDARY_IMAGE_REQUEST_CODE || requestCode == GALLERY_PROOF_IMAGE_REQUEST_CODE) && data != null) {
      ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);

      if (images.size() > 0 && requestCode == GALLERY_PRIMARY_IMAGE_REQUEST_CODE) {
        primaryUri = Uri.fromFile(new File(images.get(0).getPath()));
        display_image(primaryUri.getPath(), GALLERY_PRIMARY_IMAGE_REQUEST_CODE);
      }

      if (images.size() > 0 && requestCode == GALLERY_PROOF_IMAGE_REQUEST_CODE) {
        file = new File(images.get(0).getPath());

        pickupAddressFragment.setFileName(file.getName());
        pickupAddressFragment.isFileSelected(true);
      } else if (requestCode == GALLERY_SECONDARY_IMAGE_REQUEST_CODE) {
        for (Image image : images) {
          display_image(image.getPath(), GALLERY_SECONDARY_IMAGE_REQUEST_CODE);
        }
      }
    } else if (resultCode == RESULT_OK && (requestCode == CAMERA_PRIMARY_IMAGE_REQUEST_CODE ||
        requestCode == CAMERA_SECONDARY_IMAGE_REQUEST_CODE || requestCode == CAMERA_PROOF_IMAGE_REQUEST_CODE)) {

      switch (requestCode) {
        case CAMERA_PRIMARY_IMAGE_REQUEST_CODE:

          display_image(primaryUri.getPath(), requestCode);
          break;

        case CAMERA_SECONDARY_IMAGE_REQUEST_CODE:

          display_image(secondaryUri.getPath(), requestCode);
          break;

        case CAMERA_PROOF_IMAGE_REQUEST_CODE:

          file = new File(proofUri.getPath());

          pickupAddressFragment.setFileName(file.getName());
          pickupAddressFragment.isFileSelected(true);
          break;
      }
    } else if (resultCode == RESULT_OK && data != null) {
      AddressInformation information = (AddressInformation) data.getSerializableExtra("ADDRESS");
      product.pickupAddressReferenceId = information.id;
      changePickupAddressText(information);
    }
  }

  /**
   * Display image when image is picked up/capture
   *
   * @param path
   * @param requestCode
   */
  private void display_image(String path, int requestCode) {
    if (Helper.fileExist(path)) {
      try {

        File file = new File(path);

        // bitmap factory
        // BitmapFactory.Options options = new BitmapFactory.Options();
        // downsizing image as it throws OutOfMemory Exception for larger
        // images
        // options.inSampleSize = 4;
        // final Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(), options);
        // binding.ivPrimaryImage.setImageBitmap(bitmap);

        if (requestCode == CAMERA_PRIMARY_IMAGE_REQUEST_CODE || requestCode == GALLERY_PRIMARY_IMAGE_REQUEST_CODE) {
          ImageLoader.load(getContext(), file, binding.ivPrimaryImage);
          binding.ibRemoveProductImage.setVisibility(View.VISIBLE);
        }

        if (requestCode == CAMERA_SECONDARY_IMAGE_REQUEST_CODE || requestCode == GALLERY_SECONDARY_IMAGE_REQUEST_CODE) {
          List<ProductImageResponseModel> imageList = new ArrayList<>();

          ProductImageResponseModel responseModel = new ProductImageResponseModel();
          responseModel.setImage(new ProductImage(path, file.getName()));
          imageList.add(responseModel);

          adapterImage.setData(imageList);
        }
      } catch (Exception e) {
        Toast.makeText(getContext(), getString(R.string.failed_to_set_image), Toast.LENGTH_LONG).show();
        e.printStackTrace();
      } finally {
        displayImageAddButton();
      }
    } else {
      Toast.makeText(getContext(), getString(R.string.file_not_found), Toast.LENGTH_LONG).show();
    }
  }

  /**
   * Product Validation
   *
   * @return
   */
  private boolean isValidProduct() {
    if (product == null) {
      return false;
    }

    if (product.productId == null && primaryUri == null) {
      Toast.makeText(getContext(), getString(R.string.file_not_found), Toast.LENGTH_LONG).show();
      return false;
    }

    if (product.TileImageUri == null && primaryUri == null) {
      Toast.makeText(getContext(), getString(R.string.file_not_found), Toast.LENGTH_LONG).show();
      return false;
    }

    if (binding.editProductName.getText().toString().trim().length() == 0) {
      Toast.makeText(getContext(), getString(R.string.enter_product_name), Toast.LENGTH_LONG).show();
      binding.editProductName.requestFocus();
      return false;
    }

    if (binding.editProductDescription.getText().toString().trim().length() == 0) {
      Toast.makeText(getContext(), getString(R.string.enter_product_desc), Toast.LENGTH_LONG).show();
      binding.editProductDescription.requestFocus();
      return false;
    }

    if (binding.editBasePrice.getText().toString().trim().length() > 0) {

      try {
        Double.valueOf(binding.editBasePrice.getText().toString().trim());
      } catch (Exception e) {
        Toast.makeText(getContext(), getString(R.string.enter_valid_price), Toast.LENGTH_LONG).show();
        binding.editBasePrice.requestFocus();
        return false;
      }
    }

    if (binding.editDiscount.getText().toString().trim().length() > 0) {
      try {
        Double.valueOf(binding.editDiscount.getText().toString().trim());
      } catch (Exception e) {
        Toast.makeText(getContext(), getString(R.string.enter_valid_discount), Toast.LENGTH_LONG).show();
        binding.editDiscount.requestFocus();
        return false;
      }
    }

    try {
      double price = binding.editBasePrice.getText().toString().trim().length() > 0 ? Double.valueOf(binding.editBasePrice.getText().toString().trim()) : 0;
      double discount = binding.editDiscount.getText().toString().trim().length() > 0 ? Double.valueOf(binding.editDiscount.getText().toString().trim()) : 0;

      if (discount > price) {
        Toast.makeText(getContext(), getString(R.string.discount_amount_cant_be_grater_than_price), Toast.LENGTH_LONG).show();
        binding.editDiscount.requestFocus();
        return false;
      }
    } catch (Exception e) {
      Toast.makeText(getContext(), getString(R.string.invalid_input), Toast.LENGTH_LONG).show();
      binding.editDiscount.requestFocus();
      return false;
    }

        /*if(binding.layoutProductSpecification.layoutKeySpecification.editKey.getText().toString().trim().length() == 0 ||
                binding.layoutProductSpecification.layoutKeySpecification.editValue.getText().toString().trim().length() == 0)
        {
            Toast.makeText(getContext(), "Enter product specification", Toast.LENGTH_LONG).show();
            return false;
        }*/

//        if (!adapter.isValid()) {
//            Toast.makeText(getContext(), "Enter all specification values", Toast.LENGTH_LONG).show();
//            return false;
//        }

    if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE.getValue())
        && !isService) {
      if (product.pickupAddressReferenceId == null || product.pickupAddressReferenceId.isEmpty()) {
        Toast.makeText(getContext(), getString(R.string.pickup_address_required), Toast.LENGTH_LONG).show();
        return false;
      }

      return isValidAssuredPurchase();
    }

    if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE.getValue()) && bankInformation == null) {
      Toast.makeText(getContext(), getString(R.string.please_update_payment_information), Toast.LENGTH_LONG).show();
      return false;
    }

    if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.UNIQUE_PAYMENT_URL.getValue())
        && binding.layoutPaymentMethod.editPurchaseUrlLink.getText().toString().trim().length() == 0) {
      Toast.makeText(getContext(), getString(R.string.purchase_url_required), Toast.LENGTH_LONG).show();
      binding.layoutPaymentMethod.editPurchaseUrlLink.requestFocus();
      return false;
    }

    return true;
  }

  /**
   * Assured Purchase Validation
   *
   * @return
   */
  private boolean isValidAssuredPurchase() {
    if (binding.layoutShippingMatrixDetails.editWeight.getText().toString().trim().length() == 0) {
      binding.layoutShippingMatrixDetails.editWeight.requestFocus();
      Toast.makeText(getContext(), getString(R.string.enter_product_weight), Toast.LENGTH_LONG).show();
      return false;
    }

    if (binding.layoutShippingMatrixDetails.editLength.getText().toString().trim().length() == 0) {
      binding.layoutShippingMatrixDetails.editLength.requestFocus();
      Toast.makeText(getContext(), getString(R.string.enter_product_length), Toast.LENGTH_LONG).show();
      return false;
    }

    if (binding.layoutShippingMatrixDetails.editHeight.getText().toString().trim().length() == 0) {
      binding.layoutShippingMatrixDetails.editHeight.requestFocus();
      Toast.makeText(getContext(), getString(R.string.enter_product_height), Toast.LENGTH_LONG).show();
      return false;
    }

    if (binding.layoutShippingMatrixDetails.editThickness.getText().toString().trim().length() == 0) {
      binding.layoutShippingMatrixDetails.editThickness.requestFocus();
      Toast.makeText(getContext(), getString(R.string.enter_product_thickness), Toast.LENGTH_LONG).show();
      return false;
    }

    try {
      Double.valueOf(binding.layoutShippingMatrixDetails.editWeight.getText().toString().trim());
      Double.valueOf(binding.layoutShippingMatrixDetails.editLength.getText().toString().trim());
      Double.valueOf(binding.layoutShippingMatrixDetails.editHeight.getText().toString().trim());
      Double.valueOf(binding.layoutShippingMatrixDetails.editThickness.getText().toString().trim());
    } catch (Exception e) {
      Toast.makeText(getContext(), getString(R.string.enter_valid_package_dimensions), Toast.LENGTH_LONG).show();
      return false;
    }

    return true;
  }

  /**
   * Check for valid seller information
   *
   * @return
   */
  private boolean isValidBankInformation() {
    if (binding.layoutBottomSheet.editBankAccount.getText().toString().trim().length() == 0) {
      binding.layoutBottomSheet.editBankAccount.requestFocus();
      Toast.makeText(getContext(), getString(R.string.enter_bank_account_number), Toast.LENGTH_LONG).show();
      return false;
    }

    if (binding.layoutBottomSheet.editIfscCode.getText().toString().trim().length() == 0) {
      binding.layoutBottomSheet.editIfscCode.requestFocus();
      Toast.makeText(getContext(), getString(R.string.enter_ifsc), Toast.LENGTH_LONG).show();
      return false;
    }

    if (binding.layoutBottomSheet.editGst.toString().trim().length() == 0) {
      binding.layoutBottomSheet.editGst.requestFocus();
      Toast.makeText(getContext(), getString(R.string.enter_gst_tax_id), Toast.LENGTH_LONG).show();
      return false;
    }

    return true;
  }

  /**
   * check if pickup address form is valid or not
   *
   * @return
   */
  private boolean isValidAddress() {
    if (file == null) {
      Toast.makeText(getContext(), getString(R.string.address_proof_required), Toast.LENGTH_LONG).show();
      return false;
    }

    return pickupAddressFragment.isValid();
  }

  /**
   * Save pickup adderess
   */
  private void saveAddress() {
    if (!Methods.isOnline(getActivity())) {
      return;
    }

    Constants.assuredPurchaseRestAdapterDev.create(ProductGalleryInterface.class)
        .savePickupAddress(addressInformation, new Callback<WebResponseModel<AddressInformation>>() {

          @Override
          public void success(WebResponseModel<AddressInformation> webResponseModel, Response response) {

            hideDialog();

            if (webResponseModel != null && webResponseModel.getData() != null) {
              AddressInformation addressResponse = webResponseModel.getData();

              if (TextUtils.isEmpty(addressInformation.id)) {
                //If new address added then add it locally to address list
                adapterAddress.addData(addressResponse);
                Toast.makeText(getContext(), getString(R.string.address_added_successfully), Toast.LENGTH_LONG).show();
              } else {
                //If address updated the update it locally to address list
                for (int i = 0; i < addressInformationList.size(); i++) {
                  if (addressInformation.id.equals(addressInformationList.get(i).id)) {
                    addressInformationList.add(i, addressResponse);
                    adapterAddress.notifyItemChanged(i);
                    break;
                  }
                }

                Toast.makeText(getContext(), getString(R.string.address_updated_successfully), Toast.LENGTH_LONG).show();
              }

              product.pickupAddressReferenceId = webResponseModel.getData().id;
              changePickupAddressText(addressResponse);
              addressInformation.id = webResponseModel.getData().id;
            }

            Log.d("PRODUCT_JSON", getString(R.string.address_successfully_added_updated));
          }

          @Override
          public void failure(RetrofitError error) {
            hideDialog();
            Toast.makeText(getContext(), getString(R.string.failed_to_save_address), Toast.LENGTH_LONG).show();
            Log.d("PRODUCT_JSON", "FAIL " + error.getMessage() + " CODE " + error.getSuccessType());
          }
        });
  }

  /**
   * Save pickup address information
   *
   * @param information
   */
  private void saveAddressInformation(AddressInformation information) {
    if (!isValidAddress()) {
      return;
    }

    addressInformation = information;
    addressInformation.websiteId = session.getFPID();

    this.uploadFile(file);
  }

  /**
   * Get address object from address list based on product pickup address id
   *
   * @return
   */
  private AddressInformation getAddress() {
    AddressInformation addressInformation = null;

    if (product != null && product.pickupAddressReferenceId != null) {
      for (AddressInformation information : addressInformationList) {
        if (information.id != null && information.id.equals(product.pickupAddressReferenceId)) {
          addressInformation = information;
        }
      }
    }

    return addressInformation;
  }

  /**
   * Fetch pickup address information
   */
  private void getAddressInformation() {
    Constants.assuredPurchaseRestAdapterDev.create(ProductGalleryInterface.class)
        .getPickupAddress(session.getFPID(), new Callback<WebResponseModel<List<AddressInformation>>>() {

          @Override
          public void success(WebResponseModel<List<AddressInformation>> webResponseModel, Response response) {

            if (webResponseModel != null && webResponseModel.getData() != null) {
              adapterAddress.setData(webResponseModel.getData());

              AddressInformation information = getAddress();

              if (information != null) {
                changePickupAddressText(information);
              } else {
                binding.layoutBottomSheet.tvPickAddress.setText("");
              }
            }

            Log.d("PRODUCT_JSON", "GET ADDRESS");
          }

          @Override
          public void failure(RetrofitError error) {
            Log.d("PRODUCT_JSON", "GET ADDRESS FAIL");
          }
        });
  }

  /**
   * Initialize Product Object
   *
   * @return
   */
  private void initProduct() {

    try {
      product.CurrencyCode = binding.editCurrency.getText().toString();
    } catch (Exception e) {
      e.printStackTrace();
    }

    product.Name = binding.editProductName.getText().toString();
    product.Description = binding.editProductDescription.getText().toString();
    product.brandName = binding.editBrand.getText().toString().trim().length() > 0 ? binding.editBrand.getText().toString() : null;
    product.Price = binding.editBasePrice.getText().toString().trim().length() > 0 ? Double.valueOf(binding.editBasePrice.getText().toString().trim()) : 0;
    product.DiscountAmount = binding.editDiscount.getText().toString().trim().length() > 0 ? Double.valueOf(binding.editDiscount.getText().toString().trim()) : 0;
    CATEGORY = binding.editCategory.getText().toString().trim();

    product.category = TextUtils.isEmpty(CATEGORY) ? null : CATEGORY;
    product.paymentType = paymentAndDeliveryMode.getValue();

    if (product.keySpecification == null) {
      product.keySpecification = new com.nowfloats.ProductGallery.Model.Product.Specification();
    }

    product.keySpecification.key = binding.layoutProductSpecification.layoutKeySpecification.editKey.getText().toString();
    product.keySpecification.value = binding.layoutProductSpecification.layoutKeySpecification.editValue.getText().toString();

    //If assured purchase and product
    if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE.getValue())) {
      //If limited stock selected
      if (binding.layoutInventory.spinnerStockAvailability.getSelectedItemPosition() == 0) {
        product.IsAvailable = true;
        product.availableUnits = binding.layoutInventory.quantityValue.getText().toString().length() == 0 ? 1 : Integer.valueOf(binding.layoutInventory.quantityValue.getText().toString().trim());
      }

      //If unlimited stock selected
      if (binding.layoutInventory.spinnerStockAvailability.getSelectedItemPosition() == 1) {
        product.IsAvailable = true;
        product.availableUnits = -1;
      }

      //If out of stock selected
      if (binding.layoutInventory.spinnerStockAvailability.getSelectedItemPosition() == 2) {
        product.IsAvailable = false;
        product.availableUnits = 0;
      }

      product.codAvailable = (binding.layoutInventoryCod.spinnerStockAvailability.getSelectedItemPosition() == 0);
      product.prepaidOnlineAvailable = (binding.layoutInventoryOnline.spinnerStockAvailability.getSelectedItemPosition() == 0);

      product.maxCodOrders = product.codAvailable ? (binding.layoutInventoryCod.quantityValue.getText().toString().length() == 0 ? 0 : Integer.valueOf(binding.layoutInventoryCod.quantityValue.getText().toString().trim())) : 0;
      product.maxPrepaidOnlineAvailable = product.prepaidOnlineAvailable ? (binding.layoutInventoryOnline.quantityValue.getText().toString().length() == 0 ? 0 : Integer.valueOf(binding.layoutInventoryOnline.quantityValue.getText().toString().trim())) : 0;
    } else if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.DONT_WANT_TO_SELL.getValue())) {
      product.IsAvailable = false;
      product.codAvailable = false;
      product.prepaidOnlineAvailable = false;

      product.availableUnits = 0;
      product.maxCodOrders = 0;
      product.maxPrepaidOnlineAvailable = 0;
    } else if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.UNIQUE_PAYMENT_URL.getValue())) {
      product.IsAvailable = true;
      product.codAvailable = false;
      product.prepaidOnlineAvailable = false;

      product.availableUnits = -1;
      product.maxCodOrders = 0;
      product.maxPrepaidOnlineAvailable = 0;
    }

    //If unique payment url
    if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.UNIQUE_PAYMENT_URL.getValue())) {
      if (product.BuyOnlineLink == null) {
        product.BuyOnlineLink = new com.nowfloats.ProductGallery.Model.Product.BuyOnlineLink();
      }

      product.BuyOnlineLink.description = binding.layoutPaymentMethod.editWebsite.getText().toString();
      product.BuyOnlineLink.url = binding.layoutPaymentMethod.editPurchaseUrlLink.getText().toString();
    }
  }

  /**
   * Initialize Assured Purchase
   *
   * @param productId
   * @return
   */
  private AssuredPurchase initAssuredPurchase(String productId) {
    if (assuredPurchase == null) {
      assuredPurchase = new AssuredPurchase();
    }

    try {
      assuredPurchase.productId = productId;
      assuredPurchase.merchantId = session.getFPID();

      assuredPurchase.height = binding.layoutShippingMatrixDetails.editHeight.getText().toString().trim().length() > 0 ? Double.valueOf(binding.layoutShippingMatrixDetails.editHeight.getText().toString().trim()) : 0;
      assuredPurchase.weight = binding.layoutShippingMatrixDetails.editWeight.getText().toString().trim().length() > 0 ? Double.valueOf(binding.layoutShippingMatrixDetails.editWeight.getText().toString().trim()) : 0;
      assuredPurchase.length = binding.layoutShippingMatrixDetails.editLength.getText().toString().trim().length() > 0 ? Double.valueOf(binding.layoutShippingMatrixDetails.editLength.getText().toString().trim()) : 0;
      assuredPurchase.width = binding.layoutShippingMatrixDetails.editThickness.getText().toString().trim().length() > 0 ? Double.valueOf(binding.layoutShippingMatrixDetails.editThickness.getText().toString().trim()) : 0;
      assuredPurchase.gstCharge = binding.editGst.getText().toString().trim().length() > 0 ? Double.valueOf(binding.editGst.getText().toString().trim()) : 0;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return assuredPurchase;
  }

  /**
   * Initialize seller information object
   *
   * @return
   */
  private BankInformation initBankInformation() {
    if (bankInformation == null) {
      bankInformation = new BankInformation();
    }

    if (bankInformation.bankAccount == null) {
      bankInformation.bankAccount = new BankInformation.BankAccount();
    }

    bankInformation.bankAccount.number = binding.layoutBottomSheet.editBankAccount.getText().toString();
    bankInformation.bankAccount.ifsc = binding.layoutBottomSheet.editIfscCode.getText().toString();

    bankInformation.gstn = binding.layoutBottomSheet.editGst.getText().toString();

    return bankInformation;
  }

  /**
   * Call to upload product image
   *
   * @param productId
   */
  private void uploadProductImage(String productId) {
    try {
      String valuesStr = "clientId=" + Constants.clientId
          + "&requestType=sequential&requestId=" + Constants.deviceId
          + "&totalChunks=1&currentChunkNumber=1&productId=" + productId;

      String url = Constants.NOW_FLOATS_API_URL + "/Product/v1/AddImage?" + valuesStr;

      byte[] imageBytes = Methods.compressToByte(primaryUri.getPath(), getActivity());

      UploadImage upload = new UploadImage(url, imageBytes, productId);
      upload.setImageUploadListener(this);
      upload.execute();
    } catch (Exception e) {
      e.printStackTrace();
      Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
    }
  }

  /**
   * Update assured purchase
   *
   * @param productId
   */
  private void updateAssuredPurchase(String productId) {
    if (!Methods.isOnline(getActivity())) {
      return;
    }

    WaUpdateDataModel update = new WaUpdateDataModel();
    final AssuredPurchase assuredPurchase = initAssuredPurchase(productId);

    update.setQuery(String.format("{product_id:'%s'}", assuredPurchase.productId));

    update.setUpdateValue(String.format("{$set:{length:'%s', width:'%s', weight:'%s', height:'%s', gst_slab:'%s'}}",
        assuredPurchase.length,
        assuredPurchase.width,
        assuredPurchase.weight,
        assuredPurchase.height,
        assuredPurchase.gstCharge));

    update.setMulti(true);

    /**
     * Update API call
     */
    Constants.webActionAdapter.create(ProductGalleryInterface.class)
        .updateAssuredPurchaseDetails(update, new Callback<String>() {

          @Override
          public void success(String s, Response response) {

          }

          @Override
          public void failure(RetrofitError error) {

          }
        });
  }

  /**
   * Save seller information
   */
  private void saveBankInformation() {
    if (!Methods.isOnline(getActivity())) {
      return;
    }

    showDialog("Updating Seller Profile ...");

    BankInformation bankInformation = initBankInformation();
    bankInformation.sellerId = session.getFpTag();

    Log.d("PRODUCT_JSON", "BANK INFO JSON: " + new Gson().toJson(bankInformation));

    Constants.assuredPurchaseRestAdapterDev.create(ProductGalleryInterface.class)
        .saveBankInformation(bankInformation, new Callback<WebResponseModel<Object>>() {

          @Override
          public void success(WebResponseModel<Object> webResponseModel, Response response) {

            Log.d("PRODUCT_JSON", "Bank Information Saved");

            Toast.makeText(getContext(), getString(R.string.seller_profile_updated), Toast.LENGTH_SHORT).show();
            hideDialog();

            binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setVisibility(View.VISIBLE);
            binding.layoutPaymentMethod.layoutPaymentExternalPurchaseUrl.setVisibility(View.GONE);

            binding.layoutPaymentMethod.tvPaymentConfiguration.setText(paymentOptionTitles[0]);
            binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setText(getString(R.string.payment_methud_message));
            binding.layoutPaymentMethod.tvPaymentConfigurationMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);

            toggleBottomSheet();
          }

          @Override
          public void failure(RetrofitError error) {
            Toast.makeText(getContext(), getString(R.string.failed_to_update_seller_profile), Toast.LENGTH_SHORT).show();
            hideDialog();
            Log.d("PRODUCT_JSON", getString(R.string.failed_to_save_bank_information));
          }
        });
  }

  /**
   * Fetch seller information
   */
  private void getBankInformation() {
    Constants.assuredPurchaseRestAdapterDev.create(ProductGalleryInterface.class)
        .getBankInformation(session.getFpTag(), new Callback<WebResponseModel<BankInformation>>() {

          @Override
          public void success(WebResponseModel<BankInformation> webResponseModel, Response response) {

            if (webResponseModel != null) {
              bankInformation = webResponseModel.getData();
              setBankInformationData();
            }

            Log.d("PRODUCT_JSON", "SUCCESS");
          }

          @Override
          public void failure(RetrofitError error) {
            Log.d("PRODUCT_JSON", "FAIL " + error.getMessage() + " CODE " + error.getSuccessType());
          }
        });
  }

  /**
   * Save assured purchase
   *
   * @param productId
   */
  private void saveAssuredPurchase(String productId) {
    if (!Methods.isOnline(getActivity())) {
      return;
    }

    AssuredPurchase assuredPurchase = initAssuredPurchase(productId);

    WAAddDataModel<AssuredPurchase> waModel = new WAAddDataModel<>();
    waModel.setWebsiteId(session.getFPID());
    waModel.setActionData(assuredPurchase);

    Log.d("PRODUCT_JSON", "JSON: " + new Gson().toJson(waModel));

    Constants.webActionAdapter.create(ProductGalleryInterface.class)
        .addAssuredPurchaseDetails(waModel, new Callback<String>() {

          @Override
          public void success(String s, Response response) {

          }

          @Override
          public void failure(RetrofitError error) {

          }
        });
  }

  /**
   * Update product
   */
  private void updateProduct() {
    ProductGalleryInterface productInterface = Constants.restAdapterDev.create(ProductGalleryInterface.class);

    try {
      if (isValidProduct()) {
        initProduct();

        ArrayList<UpdateValue> updates = new ArrayList<>();

        JSONObject json = new JSONObject(new Gson().toJson(product));
        Iterator<String> keys = json.keys();

        Log.d("PRODUCT_JSON", "" + json.toString());

        while (keys.hasNext()) {
          String key = keys.next();
          updates.add(new UpdateValue(key, json.get(key).toString()));
        }

        Product_Gallery_Update_Model model = new Product_Gallery_Update_Model(Constants.clientId, product.productId, product.productType, updates);

        Log.d("PRODUCT_JSON", "" + new Gson().toJson(model));

        showDialog(getString(R.string.please_wait_));
        WebEngageController.trackEvent(UPDATE_PRODUCT_CATALOGUE, PRODUCT_CATALOGUE_ADD_UPDATE, NO_EVENT_VALUE);
        productInterface.put_UpdateGalleryUpdate(model, new Callback<ArrayList<String>>() {

          @Override
          public void success(ArrayList<String> strings, Response response) {

            Log.d("PRODUCT_JSON", "Product Updated Successfully");

            if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE.getValue())) {
              if (assuredPurchase == null) {
                saveAssuredPurchase(product.productId);
              } else {
                updateAssuredPurchase(product.productId);
              }
            }

            for (ProductImageResponseModel image : imageList) {
              if (TextUtils.isEmpty(image.getId())) {
                new MultipleFileUpload(product.productId, session, mWebAction, image.getImage()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
              }
            }

            if (primaryUri != null) {
              uploadProductImage(product.productId);
            } else {
              Toast.makeText(getContext(), getString(R.string.product_updated_successfully), Toast.LENGTH_SHORT).show();
              hideDialog();

              if (getActivity() != null) {
                Intent data = new Intent();
                data.putExtra("LOAD", true);
                getActivity().setResult(RESULT_OK, data);
                getActivity().finish();
              }
            }
          }

          @Override
          public void failure(RetrofitError error) {

            Log.d("PRODUCT_JSON", "Failed to Save Product");

            hideDialog();
            Toast.makeText(getContext(), getString(R.string.failed_to_update_product), Toast.LENGTH_LONG).show();
            Log.d("PRODUCT_JSON", "FAIL " + error.getMessage());
          }
        });

        Log.d("JSON_PRODUCT_UPDATE", "" + new Gson().toJson(model));
      }
    } catch (Exception e) {
      Log.d("JSON_PRODUCT_UPDATE", "ERROR " + e.getMessage());
      WebEngageController.trackEvent(ERROR_UPDATE_PRODUCT_CATALOGUE, PRODUCT_CATALOGUE_ADD_UPDATE, NO_EVENT_VALUE);
    }
  }

  /**
   * Save product information
   */
  private void saveProduct() {
    if (!Methods.isOnline(getActivity())) {
      return;
    }
    if (product != null && product.productId != null) {
      updateProduct();
      return;
    }

    ProductGalleryInterface productInterface = Constants.restAdapterDev.create(ProductGalleryInterface.class);
    try {
      if (isValidProduct()) {
        initProduct();
        product.ClientId = Constants.clientId;
        product.FPTag = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toUpperCase();
        product.productType = isService ? "SERVICES" : productType;

        Log.d("PRODUCT_JSON", "JSON: " + new Gson().toJson(product));

        showDialog(getString(R.string.please_wait_));
//        WebEngageController.trackEvent(ADD_PRODUCT_CATALOGUE, PRODUCT_CATALOGUE_ADD_UPDATE, NO_EVENT_VALUE);
        productInterface.addProduct(product, new Callback<String>() {

          @Override
          public void success(String productId, Response response) {

            Log.d("PRODUCT_JSON", getString(R.string.product_saved_successfully) + productId);

            product.productId = productId;

            if (paymentAndDeliveryMode.getValue().equalsIgnoreCase(Constants.PaymentAndDeliveryMode.ASSURED_PURCHASE.getValue())) {
              saveAssuredPurchase(productId);
            }

            for (ProductImageResponseModel image : imageList) {
              new MultipleFileUpload(productId, session, mWebAction, image.getImage()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            if (primaryUri != null) {
              uploadProductImage(productId);
            } else {
              Toast.makeText(getContext(), getString(R.string.product_saved_successfully), Toast.LENGTH_SHORT).show();
//              WebEngageController.trackEvent(PRODUCT_ADDED_TO_CATALOGUE, MANAGE_CONTENT, NO_EVENT_VALUE);
              hideDialog();

              if (getActivity() != null) {
                Intent data = new Intent();
                data.putExtra("LOAD", true);
                getActivity().setResult(RESULT_OK, data);
                getActivity().finish();
              }
            }
          }

          @Override
          public void failure(RetrofitError error) {

            Log.d("PRODUCT_JSON", "Failed to Save Product");

            hideDialog();
            Toast.makeText(getContext(), getString(R.string.failed_to_save_product), Toast.LENGTH_LONG).show();
            Log.d("PRODUCT_JSON", "FAIL " + error.getMessage());
          }
        });
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Delete product
   */
  private void deleteProduct() {
    if (!Methods.isOnline(getActivity())) {
      return;
    }

    ProductGalleryInterface productInterface = Constants.restAdapterDev.create(ProductGalleryInterface.class);

    try {
      HashMap<String, String> map = new HashMap<>();

      map.put("clientId", Constants.clientId);
      map.put("productId", product.productId);
      map.put("productType", product.productType);
      map.put("identifierType", "SINGLE");

      showDialog(getString(R.string.please_wait_));
//      WebEngageController.trackEvent(DELETE_PRODUCT_CATALOGUE, PRODUCT_CATALOGUE_ADD_UPDATE, NO_EVENT_VALUE);
      productInterface.removeProduct(map, new Callback<String>() {

        @Override
        public void success(String productId, Response response) {

          Log.d("PRODUCT_JSON", "SUCCESS : Product Deleted Successfully");

          Toast.makeText(getContext(), R.string.deleted_successfully, Toast.LENGTH_SHORT).show();
          hideDialog();

          if (getActivity() != null) {
            Intent data = new Intent();
            data.putExtra("LOAD", true);
            getActivity().setResult(RESULT_OK, data);
            getActivity().finish();
          }
        }

        @Override
        public void failure(RetrofitError error) {

          hideDialog();
          Toast.makeText(getContext(), R.string.failed_to_delete, Toast.LENGTH_LONG).show();
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Fetch suggested tag list
   */
  private void getTagList() {
    if (!Methods.isOnline(getActivity())) {
      return;
    }

    HashMap<String, String> values = new HashMap<>();
    values.put("clientId", Constants.clientId);
    values.put("fpId", session.getFPID());

    ProductGalleryInterface galleryInterface = Constants.restAdapterDev.create(ProductGalleryInterface.class);

    galleryInterface.getAllTags(values, new Callback<List<String>>() {

      @Override
      public void success(List<String> data, Response response) {

        if (data != null && response.getStatus() == 200) {
          addAutoCompleteListener(data);
        }
      }

      @Override
      public void failure(RetrofitError error) {

      }
    });
  }

  /**
   * Fetch assured purchase details for a product
   *
   * @param productId
   */
  private void getAssuredPurchase(String productId) {
    if (!Methods.isOnline(getActivity())) {
      return;
    }

    Constants.webActionAdapter.create(ProductGalleryInterface.class)
        .getAssuredPurchaseDetails(String.format("{product_id:'%s'}", productId), new Callback<WebActionModel<AssuredPurchase>>() {

          @Override
          public void success(WebActionModel<AssuredPurchase> webActionModel, Response response) {

            Log.d("ASSURED_PURCHASE", "SUCCESS");

            if (webActionModel.getData().size() > 0) {
              Log.d("ASSURED_PURCHASE", "SUCCESS " + webActionModel.getData().size());
              assuredPurchase = webActionModel.getData().get(0);
              setAssuredPurchaseData();
            }
          }

          @Override
          public void failure(RetrofitError error) {
            Log.d("ASSURED_PURCHASE", "FAIL");
          }
        });
  }

  /**
   * Initialize progress bar
   *
   * @param content
   */
  private void initProgressDialog(String content) {
    if (materialDialog != null) {
      materialDialog.setContent(content);
      return;
    }

    materialDialog = new MaterialDialog.Builder(getActivity())
        .widgetColorRes(R.color.accentColor)
        .content(content)
        .progress(true, 0).build();

    materialDialog.setCancelable(false);
  }

  /**
   * Hide progress bar
   */
  private void hideDialog() {
    if (materialDialog != null && materialDialog.isShowing()) {
      materialDialog.dismiss();
    }
  }

  /**
   * Show progress bar
   *
   * @param content
   */
  private void showDialog(String content) {
    initProgressDialog(content);

    if (!materialDialog.isShowing()) {
      materialDialog.show();
    }
  }

  @Override
  public void onPreExecute() {

  }

  @Override
  public void onPostExecute(String result, int responseCode) {
    hideDialog();

    if (responseCode == 200 || responseCode == 202) {
      Toast.makeText(getContext(), getString(R.string.product_saved_successfully), Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(getContext(), getString(R.string.failed_to_save_product), Toast.LENGTH_LONG).show();
    }

    /**
     * If information saved reload product/service list
     */
    if (getActivity() != null) {
      Intent data = new Intent();
      data.putExtra("LOAD", true);
      getActivity().setResult(RESULT_OK, data);
      getActivity().finish();
    }
  }

  /**
   * Delete product confirmation dialog
   */
  private void deleteConfirmation() {
    new MaterialDialog.Builder(getActivity())
        .title(getString(R.string.are_you_sure_want_to_delete))
        .content(R.string.delete_record_not_undone)
        .positiveText(getString(R.string.delete_))
        .positiveColorRes(R.color.primaryColor)
        .negativeText(getString(R.string.cancel))
        .negativeColorRes(R.color.light_gray)
        .callback(new MaterialDialog.ButtonCallback() {

          @Override
          public void onPositive(MaterialDialog dialog) {

            super.onPositive(dialog);
            deleteProduct();
            dialog.dismiss();
          }

          @Override
          public void onNegative(MaterialDialog dialog) {

            super.onNegative(dialog);
            dialog.dismiss();
          }
        }).show();

  }

  /**
   * Web action for upload product image
   *
   * @return
   */
  private WebAction getWebAction() {
    if (mWebAction != null) {
      return mWebAction;
    }

    mWebAction = new WebAction.WebActionBuilder()
        .setAuthHeader(Constants.WA_KEY)
        .build();

    mWebAction.setWebActionName("product_images");

    return mWebAction;
  }

  /**
   * Display secondary images for product
   *
   * @param productId
   */
  private void displayImagesForProduct(String productId) {
    if (TextUtils.isEmpty(product.productId)) {
      return;
    }

    IFilter filter = new WebActionsFilter();
    filter = filter.eq("_pid", productId);

    mWebAction.findProductImages(filter, new WebAction.WebActionCallback<List<ProductImageResponseModel>>() {

      @Override
      public void onSuccess(List<ProductImageResponseModel> result) {
        if (result != null) {
          adapterImage.setData(result);
          displayImageAddButton();
        }
      }

      @Override
      public void onFailure(WebActionError error) {
        Log.d("IMAGE_UPLOAD_RESPONSE", "GET IMAGE FAIL");
      }
    });
  }

  /**
   * Delete secondary images for product
   *
   * @param image
   */
  private void deleteImage(ProductImageResponseModel image) {
    IFilter filter = new WebActionsFilter();
    filter = filter.eq("_id", image.getId());

    mWebAction.delete(filter, false, new WebAction.WebActionCallback<Boolean>() {

      @Override
      public void onSuccess(Boolean result) {

        Toast.makeText(getContext(), R.string.image_removed_successfully, Toast.LENGTH_LONG).show();
        Log.d(TAG, "" + true);
      }

      @Override
      public void onFailure(WebActionError error) {
        Toast.makeText(getContext(), R.string.failed_to_remove_image, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Fail");
      }
    });
  }

  /**
   * Upload pickup  address proof
   *
   * @param file
   */
  private void uploadFile(File file) {
    if (!Methods.isOnline(getActivity())) {
      return;
    }

    String valuesStr;

    //If not proof not exists for address
    if (TextUtils.isEmpty(addressInformation.addressProof)) {
      valuesStr = "clientId=" + Constants.clientId
          + "&requestType=sequential"
          + "&totalChunks=1&currentChunkNumber=1&fileName=" + file.getName();
    }

    //If proof already exists replace address proof
    else {
      String url = addressInformation.addressProof;
      String fileName = url.substring(url.lastIndexOf('/') + 1);

      valuesStr = "clientId=" + Constants.clientId
          + "&requestType=sequential"
          + "&totalChunks=1&currentChunkNumber=1&fileName=" + file.getName() + "&proofFileId=" + fileName;
    }

    String url = DEV_ASSURED_PURCHASE_URL + "/api/seller/UploadOrReplaceFile?" + valuesStr;

    //Call file upload
    FileUpload upload = new FileUpload(file);
    upload.setFileUploadListener(this);
    upload.execute(url);
  }

  /**
   * Tool tip for information/hint
   *
   * @param position
   * @param message
   * @param view
   */
  private void toolTip(ViewTooltip.Position position, String message, View view) {
    ViewTooltip
        .on(getActivity(), view)
        .autoHide(true, 3500)
        .clickToHide(true)
        .corner(30)
        .textColor(Color.WHITE)
        .color(R.color.accentColor)
        .position(position)
        .text(message)
        .show();
  }

  /**
   * Add tooltip button listener
   */
  private void addInfoButtonListener() {
    binding.ibInfoProductImageIcon.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.the_primary_image_appears_on_your_webpage), binding.ibInfoProductImageIcon));
    binding.ibInfoGst.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.enter_the_text_rate_), binding.ibInfoGst));
    binding.ibInfoBrand.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.enter_the_product_if_any_), binding.ibInfoBrand));
    binding.ibInfoProductName.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.the_name_of_your_base_product), binding.ibInfoProductName));
    binding.ibInfoProductDescription.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.describe_your_product_in_details), binding.ibInfoProductDescription));
    binding.ibInfoProductCurrency.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.select_the_currency_you_want_to_accept), binding.ibInfoProductCurrency));
    binding.ibInfoProductBasePriceHelp.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.the_maximum_price_for_this_item_Including), binding.ibInfoProductBasePriceHelp));

    binding.layoutProductSpecification.ibInfoProductProperty.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.enter_the_key_specification_which_helps) +
        "E.g Size - Small or Color - Blue", binding.layoutProductSpecification.ibInfoProductProperty));
    binding.layoutProductSpecification.ibInfoProductSpecification.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.mention_other_specifications_of_the_product_you_are_of), binding.layoutProductSpecification.ibInfoProductSpecification));

    binding.layoutInventory.ibInfoProductInventory.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.you_can_use_this_to_manage), binding.layoutInventory.ibInfoProductInventory));
    binding.layoutInventory.ibInfoProductQuantity.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.the_total_number_of_units), binding.layoutInventory.ibInfoProductQuantity));

    binding.layoutInventoryCod.ibInfoProductInventory.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.allows_customer_to_pay_by_cash_at_the_time), binding.layoutInventoryCod.ibInfoProductInventory));
    binding.layoutInventoryCod.ibInfoProductQuantity.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.the_maximum_number_of_units_that_a_buyer_can_purchase), binding.layoutInventoryCod.ibInfoProductQuantity));

    binding.layoutInventoryOnline.ibInfoProductInventory.setVisibility(View.INVISIBLE);
    binding.layoutInventoryOnline.ibInfoProductQuantity.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.the_maximum_number_of_units_that_a_buyer_can_purchase), binding.layoutInventoryOnline.ibInfoProductQuantity));

    binding.layoutShippingMatrixDetails.ibInfoProductDimension.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.package_dimension_need_to_be_accurate_because_they_are), binding.layoutShippingMatrixDetails.ibInfoProductDimension));
    binding.layoutShippingMatrixDetails.ibInfoProductWeight.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.enter_the_weight_of_your_package), binding.layoutShippingMatrixDetails.ibInfoProductWeight));
    binding.layoutShippingMatrixDetails.ibInfoProductLength.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.enter_the_length_of_your_package), binding.layoutShippingMatrixDetails.ibInfoProductLength));
    binding.layoutShippingMatrixDetails.ibInfoProductHeight.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.enter_the_height_of_your_package), binding.layoutShippingMatrixDetails.ibInfoProductHeight));
    binding.layoutShippingMatrixDetails.ibInfoProductThickness.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.enter_the_thickness_of_your_package), binding.layoutShippingMatrixDetails.ibInfoProductThickness));

    binding.layoutPaymentMethod.ibInfoPaymentConfiguration.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.choose_the_best_way_for_customers_to_pay_you), binding.layoutPaymentMethod.ibInfoPaymentConfiguration));
    binding.ibInfoProductTags.setOnClickListener(v -> toolTip(ViewTooltip.Position.TOP, getString(R.string.tags_help_site_visitors_to_find_a_desire_item), binding.ibInfoProductTags));
  }

  /**
   * Add listener to autocomplete suggestion
   *
   * @param categories
   */
  private void addAutoCompleteListener(List<String> categories) {
    try {
      ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.customized_spinner_item, categories);

      binding.editProductTags.setAdapter(adapter);
      binding.editProductTags.setOnItemClickListener(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Override
  public void onSuccess(String url) {

    //save address once address proof uploaded
    addressInformation.addressProof = url;
    saveAddress();
  }

  @Override
  public void onFailure() {

    Log.d("PRODUCT_JSON", "FAILURE");
    Toast.makeText(getContext(), R.string.failed_to_upload_address_proof, Toast.LENGTH_LONG).show();
    hideDialog();
  }

  @Override
  public void onPreUpload() {

    Log.d("PRODUCT_JSON", "PREUPLOAD");
    showDialog(getString(R.string.please_wait_));
  }

  /**
   * Product Specification Dynamic Input Filed Adapter Class
   */
  class ProductSpecificationRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_product_specification_input, viewGroup, false);

      if (getActivity() != null) {
        View currentFocus = getActivity().getCurrentFocus();

        if (currentFocus != null) {
          currentFocus.clearFocus();
        }
      }

      return new ProductSpecificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
      if (holder instanceof ProductSpecificationViewHolder) {
        final ProductSpecificationViewHolder viewHolder = (ProductSpecificationViewHolder) holder;

        viewHolder.ibRemove.setVisibility(View.VISIBLE);

        viewHolder.ibRemove.setOnClickListener(view -> {

          if (getActivity() != null) {
            View currentFocus = getActivity().getCurrentFocus();

            if (currentFocus != null) {
              currentFocus.clearFocus();
            }
          }

          product.otherSpecification.remove(viewHolder.getAdapterPosition());
          notifyItemRemoved(viewHolder.getAdapterPosition());
        });

        com.nowfloats.ProductGallery.Model.Product.Specification specification = product.otherSpecification.get(i);

        viewHolder.editKey.setText(specification.key != null ? specification.key : "");
        viewHolder.editValue.setText(specification.value != null ? specification.value : "");

        viewHolder.editKey.addTextChangedListener(new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {

            product.otherSpecification.get(viewHolder.getAdapterPosition()).key = s.toString();
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
        });

        viewHolder.editValue.addTextChangedListener(new TextWatcher() {

          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {

            product.otherSpecification.get(viewHolder.getAdapterPosition()).value = s.toString();
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
        });
      }
    }

    @Override
    public int getItemCount() {
      return product.otherSpecification == null ? 0 : product.otherSpecification.size();
    }

    public void setData(List<com.nowfloats.ProductGallery.Model.Product.Specification> specificationList) {
      product.otherSpecification.addAll(specificationList);
      notifyDataSetChanged();
    }

    public boolean isValid() {
      boolean isValid = true;

      if (product.otherSpecification == null) {
        return isValid;
      }

      for (com.nowfloats.ProductGallery.Model.Product.Specification specification : product.otherSpecification) {
        if (TextUtils.isEmpty(specification.key) || TextUtils.isEmpty(specification.value)) {
          isValid = false;
          break;
        }
      }

      return isValid;
    }

    class ProductSpecificationViewHolder extends RecyclerView.ViewHolder {
      ImageButton ibRemove;
      EditText editKey;
      EditText editValue;

      private ProductSpecificationViewHolder(View itemView) {
        super(itemView);

        ibRemove = itemView.findViewById(R.id.ib_remove);
        editKey = itemView.findViewById(R.id.editKey);
        editValue = itemView.findViewById(R.id.editValue);
      }
    }
  }

  /**
   * Product Image Dynamic Input Filed Adapter Class
   */
  class ProductImageRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_product_secondary_image, viewGroup, false);
      return new ProductImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
      if (holder instanceof ProductImageViewHolder) {
        final ProductImageViewHolder viewHolder = (ProductImageViewHolder) holder;

        viewHolder.ib_remove.setOnClickListener(view -> {

          if (!TextUtils.isEmpty(imageList.get(viewHolder.getAdapterPosition()).getId())) {
            deleteImage(imageList.get(viewHolder.getAdapterPosition()));
          }

          imageList.remove(viewHolder.getAdapterPosition());
          notifyItemRemoved(viewHolder.getAdapterPosition());
          displayImageAddButton();
        });

        ProductImageResponseModel image = imageList.get(i);
        ImageLoader.load(getContext(), image.getImage().url, viewHolder.iv_image);
        viewHolder.tv_image_name.setText(image.getImage().description != null ? image.getImage().description : "");
      }
    }

    @Override
    public int getItemCount() {
      return imageList.size();
    }


    public void setData(List<ProductImageResponseModel> images) {
      imageList.addAll(images);
      notifyDataSetChanged();
      binding.btnSecondaryImage.setText(R.string.add_more);
    }

    class ProductImageViewHolder extends RecyclerView.ViewHolder {
      Button btn_change;
      ImageButton ib_remove;
      ImageView iv_image;
      TextView tv_image_name;

      private ProductImageViewHolder(View itemView) {
        super(itemView);

        btn_change = itemView.findViewById(R.id.btn_change);
        ib_remove = itemView.findViewById(R.id.ib_remove);
        iv_image = itemView.findViewById(R.id.iv_image);
        tv_image_name = itemView.findViewById(R.id.label_image_name);
      }
    }
  }

  /**
   * Product Pickup Address Dynamic Input Filed
   */
  class ProductPickupAddressRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_item_pickup_address, viewGroup, false);
      return new ProductPickupAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
      if (holder instanceof ProductPickupAddressViewHolder) {
        final ProductPickupAddressViewHolder viewHolder = (ProductPickupAddressViewHolder) holder;

        AddressInformation addressInformation = addressInformationList.get(i);

        viewHolder.tvType.setText(addressInformation.areaName != null ? addressInformation.areaName : "");
        viewHolder.tvAddress.setText(addressInformation.toString());
      }
    }

    @Override
    public int getItemCount() {
      return addressInformationList == null ? 0 : addressInformationList.size();
    }

    public void setData(List<AddressInformation> informationList) {
      if (addressInformationList == null) {
        addressInformationList = new ArrayList<>();
      }

      addressInformationList.clear();
      addressInformationList.addAll(informationList);
      notifyDataSetChanged();
    }

    public void addData(AddressInformation information) {
      if (addressInformationList == null) {
        addressInformationList = new ArrayList<>();
      }

      addressInformationList.add(information);
      notifyItemInserted(addressInformationList.size());
    }

    class ProductPickupAddressViewHolder extends RecyclerView.ViewHolder {
      TextView tvType;
      TextView tvAddress;

      private ProductPickupAddressViewHolder(View itemView) {
        super(itemView);

        tvType = itemView.findViewById(R.id.label_type);
        tvAddress = itemView.findViewById(R.id.label_address);

        tvType.setPaintFlags(tvType.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        tvType.setOnClickListener(v -> {

          sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
          openAddressDialog(addressInformationList.get(getAdapterPosition()));
        });

        itemView.setOnClickListener(v -> {

          AddressInformation information = addressInformationList.get(getAdapterPosition());
          product.pickupAddressReferenceId = information.id;
          changePickupAddressText(information);
          sheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
      }
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    addTag(parent.getItemAtPosition(position).toString());
  }
}