package com.nowfloats.customerassistant;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.nowfloats.customerassistant.adapters.CustomerAssistantAdapter;
import com.nowfloats.customerassistant.decorators.RecyclerSectionItemDecoration;
import com.nowfloats.customerassistant.models.SuggestionsDO;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.nowfloats.customerassistant.CustomerAssistantActivity.KEY_DATA;

/**
 * Created by admin on 8/17/2017.
 */

public class CustomerAssistantListFragment extends android.app.Fragment
        implements RecyclerSectionItemDecoration.SectionCallback {


    private RecyclerView rvSAMCustomerList;

    public List<SuggestionsDO> lsSuggestionsDOs;

    private ImageView ivSort;

    private SectionType sectionType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            sectionType = SectionType.DATE;
            lsSuggestionsDOs = (List<SuggestionsDO>) getArguments().get(KEY_DATA);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_ca_customer_list, container, false);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeControls(view);

        setOnClickListeners();

    }

    private void initializeControls(View view) {

        ivSort = (ImageView) view.findViewById(R.id.ivSort);
        rvSAMCustomerList = (RecyclerView) view.findViewById(R.id.rvSAMCustomerList);

        rvSAMCustomerList.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,
                false));

        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(Methods.dpToPx(20, getActivity()),
                        true,
                        CustomerAssistantListFragment.this);
        rvSAMCustomerList.addItemDecoration(sectionItemDecoration);

        updateList();
    }

    private void updateList() {

        rvSAMCustomerList.setAdapter(null);

        rvSAMCustomerList.setAdapter(new CustomerAssistantAdapter(getActivity(),
                (ArrayList<SuggestionsDO>) lsSuggestionsDOs));
    }


    private void setOnClickListeners() {
        ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortPopup();
            }
        });
    }

    public enum SectionType {
        DATE,
        EXPIRY,
        SOURCE
    }

    @Override
    public boolean isSection(int position) {

        switch (sectionType) {
            case DATE:
                return (position == 0 ||
                        !Methods.getFormattedDate(lsSuggestionsDOs.get(position - 1).
                                getDate()).equalsIgnoreCase(Methods.getFormattedDate(lsSuggestionsDOs.get(position).
                                getDate())));
            case EXPIRY:
                return (position == 0 ||
                        !lsSuggestionsDOs.get(position - 1).
                                getExpiryTimeOfMessage().equalsIgnoreCase(lsSuggestionsDOs.get(position).
                                getExpiryTimeOfMessage()));
            case SOURCE:
                return (position == 0 ||
                        !lsSuggestionsDOs.get(position - 1).
                                getSource().equalsIgnoreCase(lsSuggestionsDOs.get(position).
                                getSource()));
        }
        return false;
    }

    @Override
    public CharSequence getSectionHeader(int position) {
        switch (sectionType) {
            case DATE:
                return Methods.getFormattedDate(lsSuggestionsDOs.get(position).getDate());
            case EXPIRY:
                return lsSuggestionsDOs.get(position).getExpiryTimeOfMessage();
            case SOURCE:
                return lsSuggestionsDOs.get(position).getSource();
        }
        return "";
    }

    public class AssistantComparator implements Comparator<SuggestionsDO> {
        public int compare(SuggestionsDO left, SuggestionsDO right) {
            switch (sectionType) {
                case DATE:
                    return Methods.getFormattedDate(right.
                            getDate()).compareTo(Methods.getFormattedDate(left.
                            getDate()));
                case EXPIRY:
                    return (left.getExpiryDate()+"")
                            .compareTo(right.getExpiryDate()+"");
            }
            return 0;
        }
    }


    public void showSortPopup() {
        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.MyPopupMenuStyle);
        PopupMenu popup = new PopupMenu(wrapper, ivSort);
        popup.inflate(R.menu.menu_sam_sort);

        switch (sectionType) {
            case DATE:
                popup.getMenu().findItem(R.id.sort_date).setChecked(true);
                break;
            case EXPIRY:
                popup.getMenu().findItem(R.id.sort_expiry).setChecked(true);
                break;
            case SOURCE:
                popup.getMenu().findItem(R.id.sort_source).setChecked(true);
                break;
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.sort_date:
                        sectionType = SectionType.DATE;
                        Collections.sort(lsSuggestionsDOs, new AssistantComparator());
                        break;
                    case R.id.sort_expiry:
                        sectionType = SectionType.EXPIRY;
                        Collections.sort(lsSuggestionsDOs, new AssistantComparator());
                        break;
                    case R.id.sort_source:
                        sectionType = SectionType.SOURCE;
                        break;
                    default:
                        break;
                }
                updateList();
                return false;
            }
        });
        popup.show();
    }
}
