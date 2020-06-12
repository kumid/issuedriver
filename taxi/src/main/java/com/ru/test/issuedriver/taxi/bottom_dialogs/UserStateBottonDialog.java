package com.ru.test.issuedriver.taxi.bottom_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ru.test.issuedriver.taxi.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserStateBottonDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetListener mListener;
    View mUser_state_sheet_1, mUser_state_sheet_2;

    public UserStateBottonDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                // This is gotten directly from the source of BottomSheetDialog
                // in the wrapInBottomSheet() method
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

                // Right here!
                BottomSheetBehavior.from(bottomSheet)
                        .setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_state_bottom_sheet_layout, container, false);

        mUser_state_sheet_1 = view.findViewById(R.id.user_state_sheet_0);
        mUser_state_sheet_2 = view.findViewById(R.id.user_state_sheet_1);

        mUser_state_sheet_1.setOnClickListener(this);
        mUser_state_sheet_2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int res =  0;
        switch (v.getId()){
            case R.id.user_state_sheet_0:
                res = 0;
                break;
            case R.id.user_state_sheet_1:
                res = 1;
                break;
            case R.id.user_state_sheet_2:
                res = 2;
                break;
            default:
                break;
        }
         mListener.onButtonClicked(res);
        dismiss();
    }

    public interface BottomSheetListener{
        void onButtonClicked(int id);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException ex){
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
