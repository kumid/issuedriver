package com.ru.test.issuedriver.helpers;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.ru.test.issuedriver.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActionBottonSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetListener mListener;
    int id;

    Chronometer mOrder_chronometr_bottom;
    TextView mOrder_distance_bottom;

    String time, dist;
    public ActionBottonSheetDialog(String time, String dist) {
        this.time = time;
        this.dist = dist;
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
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        mOrder_chronometr_bottom = view.findViewById(R.id.order_chronometr_bottom);
        mOrder_distance_bottom = view.findViewById(R.id.order_distance_bottom);
        Button mBottom_sheet_btn = view.findViewById(R.id.bottom_sheet_btn);

        mOrder_chronometr_bottom.setText(time);
        mOrder_distance_bottom.setText(dist);
        mBottom_sheet_btn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom_sheet_btn:
                //PersonInfoActivity.getInstance().summaFromDialog = mPiBonusSumma.getText().toString();
                mListener.onButtonClicked(id);
                dismiss();
                break;
         }
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
