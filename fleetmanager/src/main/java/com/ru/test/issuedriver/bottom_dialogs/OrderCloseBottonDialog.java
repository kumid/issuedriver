package com.ru.test.issuedriver.bottom_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ru.test.issuedriver.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

public class OrderCloseBottonDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private CloseBottomSheetListener mListener;
    int id;
    String distStr, fuelStr;
    Chronometer mOrder_chronometr_bottom;
    TextView mOrder_distance_bottom, mOrder_fuel_bottom;

    String time;
    double dist, fuel = 0f;
    public OrderCloseBottonDialog() {
//        this.time = time;
//        this.dist = dist;

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
        View view = inflater.inflate(R.layout.close_order_bottom_sheet_layout, container, false);

        mOrder_chronometr_bottom = view.findViewById(R.id.order_chronometr_bottom);
        mOrder_distance_bottom = view.findViewById(R.id.order_distance_bottom);
        mOrder_fuel_bottom = view.findViewById(R.id.order_fuel_bottom);
        Button mBottom_sheet_btn = view.findViewById(R.id.bottom_sheet_btn);

//        mOrder_chronometr_bottom.setText(time);

        mOrder_distance_bottom.setText(distStr);
        mOrder_fuel_bottom.setText(String.valueOf(fuelStr));

        mBottom_sheet_btn.setOnClickListener(this);

        setDistance(basetime, dist);
        mOrder_chronometr_bottom.start();
        return view;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom_sheet_btn:
                //PersonInfoActivity.getInstance().summaFromDialog = mPiBonusSumma.getText().toString();
                mListener.onCloseBottomButtonClicked(id, time, distStr, fuelStr);
                dismiss();
                break;
         }
    }

    long basetime;
    public void setDistance(long time, double distance) {
        basetime = time;
        mOrder_chronometr_bottom.setBase(time);
        this.dist = distance;
        long fuel_consumption = FirebaseRemoteConfig.getInstance().getLong("fuel_consumption");
        fuel = dist * fuel_consumption / 100f;

        if (dist < 1000) {
            distStr = String.format("%d м", Math.round(dist));

        } else {
            distStr = String.format("%.1f км", dist / 1000f);
        }
        if (fuel < 1000) {
            fuelStr = String.format("%d гр.", Math.round(fuel));

        } else {
            fuelStr = String.format("%.1f л.", fuel / 1000f);
        }
    }

    public void showDialog(FragmentManager supportFragmentManager, long time, double distance) {
        basetime = time;
        dist = distance;

        this.show(supportFragmentManager, null);
    }

    public interface CloseBottomSheetListener {
        void onCloseBottomButtonClicked(int id, String time, String distance, String fuel);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CloseBottomSheetListener) context;
        } catch (ClassCastException ex){
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
