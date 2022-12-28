package com.nowfloats.util.base_class;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.boost.payment.base_class.OnBackPressed;

import java.util.Objects;

public abstract class BaseFragment extends Fragment implements OnBackPressed {
    private Context baseContext;
    private View layoutView;
    private TransparentProgressDialog dialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.baseContext = context;
    }

    @Override
    public Context getContext() {
        return baseContext;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutView = inflater.inflate(getContentResource(), container, false);
        init(savedInstanceState, layoutView);
        return layoutView;
    }

    /**
     * Layout resource to be inflated
     *
     * @return layout resource
     */
    @LayoutRes
    protected abstract int getContentResource();

    /**
     * Initialisations
     */
    protected abstract void init(@Nullable Bundle state, View layoutView);

    protected void showLoader() {
        try {
            if (getContext() != null && isAdded()) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                dialog = new TransparentProgressDialog(getContext());
                dialog.show();
            }

        } catch (Exception e) {
            //ignore
            //FIXME - Find a better way to do this
        }

    }

    protected void hideLoader() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            //ignore
            //FIXME - Find a better way to do this
        }
    }

    @Override
    public void onDestroy() {
        hideLoader();
        super.onDestroy();
    }

    public void toastMsg(String msg, boolean isLong) {
        if (getContext() != null && !isDetached()) {
            if (isLong)
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            else Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void showKeyboard(Context context) {
        ((InputMethodManager) Objects.requireNonNull((context).getSystemService(Context.INPUT_METHOD_SERVICE)))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        layoutView = null;
    }

    @Override
    public void onBackPressed() {

    }
}
