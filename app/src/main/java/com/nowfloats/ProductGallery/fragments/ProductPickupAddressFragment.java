package com.nowfloats.ProductGallery.fragments;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.ProductGallery.Model.AddressInformation;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ProductPickupAddressFragment extends DialogFragment {

    private ArrayList<String> signUpCountryList = new ArrayList<>();

    private EditText editCountry;
    private EditText editWarehouseName;
    private EditText editContactNumber;
    private EditText editBuildingName;
    private EditText editCity;
    private EditText editState;
    private TextView tvFileName;
    private TextView tvTitle;
    private TextView tvWarehouse;
    private Button btnFileChooser;
    private ImageButton ibRemove;
    private CheckBox checkAcceptance;
    private LinearLayout layoutFileName;

    private OnSaveAddress listener;
    private OnFileChooser fileChooser;
    private boolean isFileSelected;

    private AddressInformation address;

    public static ProductPickupAddressFragment newInstance() {

        ProductPickupAddressFragment fragment = new ProductPickupAddressFragment();
        return fragment;
    }

    public void setAddress(AddressInformation address)
    {
        this.address = address;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new Dialog(requireActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_pickup_address, container, false);
        setCancelable(false);

        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnSave = view.findViewById(R.id.btn_save);
        btnFileChooser = view.findViewById(R.id.btn_file_chooser);
        ibRemove = view.findViewById(R.id.ib_remove);

        editWarehouseName = view.findViewById(R.id.edit_warehouse_name);
        editContactNumber = view.findViewById(R.id.edit_contact_number);
        editBuildingName = view.findViewById(R.id.edit_building_name);
        editCity = view.findViewById(R.id.edit_city);
        editState = view.findViewById(R.id.edit_state);
        editCountry = view.findViewById(R.id.edit_country);
        tvFileName = view.findViewById(R.id.label_file_name);
        tvTitle = view.findViewById(R.id.label_title);
        tvWarehouse = view.findViewById(R.id.label_warehouse);
        layoutFileName = view.findViewById(R.id.layout_file_name);

        checkAcceptance = view.findViewById(R.id.check_address_acceptance);

        editCountry.setFocusable(false);
        editCountry.setFocusableInTouchMode(false);

        editCountry.setOnClickListener(v -> showCountryDialog(signUpCountryList));

        loadCountryCodeandCountryNameMap();

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {

            if(isValid())
            {
                listener.onSave(initAddressInformation());
                dismiss();
            }
        });

        btnFileChooser.setOnClickListener(v -> fileChooser.openDialog());

        ibRemove.setOnClickListener(v -> {

            removeFile();
            fileChooser.onFileRemove();
        });

        String title = address ==  null ? "Add New Address" : "Edit Address";
        tvTitle.setText(String.valueOf(title));

        String subTitle = address ==  null ? "" : (address.areaName != null ? address.areaName : "");
        tvWarehouse.setText(String.valueOf(subTitle));

        setAddressData(address);
        return view;
    }

    private void setAddressData(AddressInformation address)
    {
        if(address == null)
        {
            return;
        }

        editWarehouseName.setText(address.areaName != null ? address.areaName : "");
        editContactNumber.setText(address.contactNumber != null ? address.contactNumber : "");
        editBuildingName.setText(address.streetAddress != null ? address.streetAddress : "");
        editCity.setText(address.city != null ? address.city : "");
        editState.setText(address.state != null ? address.state : "");
        editCountry.setText(address.country != null ? address.country : "");
    }


    public void setFileName(String fileName)
    {
        tvFileName.setText(fileName);
        btnFileChooser.setVisibility(View.GONE);
        layoutFileName.setVisibility(View.VISIBLE);
    }

    public void removeFile()
    {
        tvFileName.setText("");
        btnFileChooser.setVisibility(View.VISIBLE);
        layoutFileName.setVisibility(View.GONE);
    }

    public void isFileSelected(boolean isFileSelected)
    {
        this.isFileSelected = isFileSelected;
    }

    private AddressInformation initAddressInformation()
    {
        if(address == null)
        {
            address = new AddressInformation();
        }

        address.areaName = editWarehouseName.getText().toString();
        address.contactNumber = editContactNumber.getText().toString();
        address.streetAddress = editBuildingName.getText().toString();
        address.city = editCity.getText().toString();
        address.state = editState.getText().toString();
        address.country = editCountry.getText().toString();

        return address;
    }


    public boolean isValid()
    {
        if(editWarehouseName.getText().toString().trim().length() == 0)
        {
            editWarehouseName.requestFocus();
            Toast.makeText(getContext(), "Enter warehouse name", Toast.LENGTH_LONG).show();
            return false;
        }

        if(editContactNumber.getText().toString().trim().length() == 0)
        {
            editContactNumber.requestFocus();
            Toast.makeText(getContext(), "Enter warehouse contact number", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!isFileSelected)
        {
            Toast.makeText(getContext(), "Address proof required", Toast.LENGTH_LONG).show();
            return false;
        }

        if(editBuildingName.getText().toString().trim().length() == 0)
        {
            editBuildingName.requestFocus();
            Toast.makeText(getContext(), "Enter building/plot no. and street address", Toast.LENGTH_LONG).show();
            return false;
        }

        if(editCity.getText().toString().trim().length() == 0)
        {
            editCity.requestFocus();
            Toast.makeText(getContext(), "Enter city", Toast.LENGTH_LONG).show();
            return false;
        }

        if(editState.getText().toString().trim().length() == 0)
        {
            editState.requestFocus();
            Toast.makeText(getContext(), "Enter state", Toast.LENGTH_LONG).show();
            return false;
        }

        if(editCountry.getText().toString().trim().length() == 0)
        {
            editCountry.requestFocus();
            Toast.makeText(getContext(), "Select country", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!checkAcceptance.isChecked())
        {
            Toast.makeText(getContext(), "Please confirm entered details", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    private void loadCountryCodeandCountryNameMap()
    {
        Thread thread = new Thread() {

            @Override
            public void run()
            {
                try
                {
                    String[] locales = Locale.getISOCountries();

                    for (String countryCode : locales)
                    {
                        Locale obj = new Locale("", countryCode);
                        signUpCountryList.add(obj.getDisplayCountry());
                    }

                    Collections.sort(signUpCountryList);
                }

                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }


    private void showCountryDialog(ArrayList<String> countries) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.search_list_item_layout, countries);

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select a Country");

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.search_list_layout, null);
        builderSingle.setView(view);

        EditText edtSearch = view.findViewById(R.id.edtSearch);
        ListView lvItems = view.findViewById(R.id.lvItems);

        lvItems.setAdapter(adapter);


        final Dialog dialog = builderSingle.show();

        lvItems.setOnItemClickListener((parent, v, position, id) -> {

                String strVal = adapter.getItem(position);
                dialog.dismiss();

                editCountry.setText(strVal);
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                adapter.getFilter().filter(s.toString().toLowerCase());
            }
        });

        dialog.setCancelable(false);
    }

    public void setOnClickListener(OnSaveAddress listener)
    {
        this.listener = listener;
    }

    public interface OnSaveAddress
    {
        void onSave(AddressInformation addressInformation);
    }

    public void setFileChooserListener(OnFileChooser fileChooser)
    {
        this.fileChooser = fileChooser;
    }

    public interface OnFileChooser
    {
        void openDialog();
        void onFileRemove();
    }


    @Override
    public void onStart()
    {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setLayout(width, height);
        }
    }
}