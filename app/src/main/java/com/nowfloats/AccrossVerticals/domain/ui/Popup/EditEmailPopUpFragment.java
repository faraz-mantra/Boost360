package com.nowfloats.AccrossVerticals.domain.ui.Popup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thinksity.R;

@Deprecated
public class EditEmailPopUpFragment extends DialogFragment {

    private ImageView closeButton;

    public static EditEmailPopUpFragment newInstance() {
        return new EditEmailPopUpFragment();
    }


    @Override
    public void onStart() {
        super.onStart();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setLayout(width, height);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.fullscreen_color);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.editing_email_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}