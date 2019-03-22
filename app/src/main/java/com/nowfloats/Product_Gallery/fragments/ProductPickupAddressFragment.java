package com.nowfloats.Product_Gallery.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.thinksity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ProductPickupAddressFragment extends DialogFragment {

    private ArrayList<String> signUpCountryList = new ArrayList<>();

    private EditText editCountry;

    public static ProductPickupAddressFragment newInstance() {

        ProductPickupAddressFragment fragment = new ProductPickupAddressFragment();

        //Bundle args = new Bundle();

        //args.putSerializable(ARG_MAP_TYPE, pick_type);
        //fragment.setArguments(args);

        //args.putSerializable(ARG_MAP_DATA, mDataMap);
        //fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new Dialog(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = com.nowfloats.riachatsdk.R.style.DialogAnimation;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_pickup_address, container, false);
        setCancelable(false);

        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnSave = view.findViewById(R.id.btn_save);

        editCountry = view.findViewById(R.id.edit_country);

        editCountry.setFocusable(false);
        editCountry.setFocusableInTouchMode(false);

        editCountry.setOnClickListener(v -> showCountryDialog(signUpCountryList));

        loadCountryCodeandCountryNameMap();

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {

        });

        return view;
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

                //etPin.requestFocus();
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
}