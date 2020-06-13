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

public class SelectDirectionBottonDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetListener mListener;
    View mDirectionShort, mDirectionFar;

    public SelectDirectionBottonDialog() {

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
        View view = inflater.inflate(R.layout.select_direction_bs_layout, container, false);

        mDirectionShort = view.findViewById(R.id.select_direction_short);
        mDirectionFar = view.findViewById(R.id.select_direction_far);

        mDirectionShort.setOnClickListener(this);
        mDirectionFar.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int res =  0;
        switch (v.getId()){
            case R.id.select_direction_short:
                res = 0;
                break;
            case R.id.select_direction_far:
                res = 1;
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
