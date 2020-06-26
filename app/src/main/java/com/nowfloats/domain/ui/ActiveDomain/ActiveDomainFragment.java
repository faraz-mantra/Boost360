package com.nowfloats.domain.ui.ActiveDomain;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.domain.DomainEmailActivity;
import com.nowfloats.domain.interfaces.ActiveDomainListener;
import com.nowfloats.domain.ui.Popup.AddEmailPopUpFragment;
import com.nowfloats.domain.ui.Popup.EditEmailPopUpFragment;
import com.thinksity.R;

import java.util.ArrayList;


public class ActiveDomainFragment extends Fragment implements ActiveDomainListener {

    private ActiveDomainViewModel mViewModel;
    private UserSessionManager session;
    private EmailAdapter emailAdapter;
    private RecyclerView recyclerView;
    private ImageView addNewEmailButton;
    private AddEmailPopUpFragment addEmailPopUpFragment = new AddEmailPopUpFragment();
    private EditEmailPopUpFragment editEmailPopUpFragment = new EditEmailPopUpFragment();

    public static ActiveDomainFragment newInstance() {
        return new ActiveDomainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        session = new UserSessionManager(requireContext(), requireActivity());
        mViewModel = ViewModelProviders.of(this).get(ActiveDomainViewModel.class);

        return inflater.inflate(R.layout.active_domain_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addNewEmailButton = view.findViewById(R.id.add_new_email_button);
        addNewEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailPopUpFragment.show(requireActivity().getSupportFragmentManager(), "ADD_EMAIL_POPUP_FRAGMENT");
            }
        });
        emailAdapter = new EmailAdapter(new ArrayList(), this);
        recyclerView = (RecyclerView) view.findViewById(R.id.email_recycler);
        initializeRecycler();

    }

    private void initializeRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(emailAdapter);
    }

    @Override
    public void onEmailItemClicked(String value) {
        Toast.makeText(requireContext(),value, Toast.LENGTH_LONG).show();
        editEmailPopUpFragment.show(requireActivity().getSupportFragmentManager(), "EDIT_EMAIL_POPUP_FRAGMENT");
    }
}