package com.ru.test.issuedriver.taxi.performer.ui.order;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.ru.test.issuedriver.taxi.MyActivity;
import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.data.order;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OrderCancelBottonDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private BottomSheetListener mListener;
    int id;
    TextView mOrder_distance_bottom, mOrder_fuel_bottom;
    View mCancel_action_standard_group, mCancel_action_other_group;
    CheckBox mCancel_action_other_check;
    TextInputEditText mCancel_sheet_reason_other_text;

    order item;

    public OrderCancelBottonDialog(order item) {
        this.item = item;
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
        View view = inflater.inflate(R.layout.cancel_order_bottom_sheet_layout, container, false);

        mCancel_action_other_check = view.findViewById(R.id.cancel_action_other_check);
        mCancel_sheet_reason_other_text = view.findViewById(R.id.cancel_sheet_reason_other_text);
        mCancel_action_other_group  = view.findViewById(R.id.cancel_action_other_group);
        mCancel_action_standard_group  = view.findViewById(R.id.cancel_action_standard_group);

        View mCancel_sheet_reason1 = view.findViewById(R.id.cancel_sheet_reason1);
        View mCancel_sheet_reason2 = view.findViewById(R.id.cancel_sheet_reason2);
        View mCancel_sheet_reason3 = view.findViewById(R.id.cancel_sheet_reason3);
        View mCancel_sheet_reason4 = view.findViewById(R.id.cancel_sheet_reason4);
        View mCancel_sheet_reason_other = view.findViewById(R.id.cancel_sheet_reason_other);

        mCancel_sheet_reason1.setOnClickListener(this);
        mCancel_sheet_reason2.setOnClickListener(this);
        mCancel_sheet_reason3.setOnClickListener(this);
        mCancel_sheet_reason4.setOnClickListener(this);
        mCancel_sheet_reason_other.setOnClickListener(this);

        mCancel_action_other_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                InputMethodManager imm =  (InputMethodManager) MyActivity.getMyInstance().getSystemService(Context.INPUT_METHOD_SERVICE);

                if(isChecked){
                    mCancel_action_other_group.setVisibility(View.VISIBLE);
                    mCancel_action_standard_group.setVisibility(View.GONE);
                    imm.showSoftInput(mCancel_sheet_reason_other_text, 0);
                } else {
                    mCancel_action_other_group.setVisibility(View.GONE);
                    mCancel_action_standard_group.setVisibility(View.VISIBLE);
                    imm.hideSoftInputFromWindow(mCancel_sheet_reason_other_text.getWindowToken(), 0);
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        mListener.onButtonClicked(item, v.getId());
        dismiss();
    }

    public interface BottomSheetListener{
        void onButtonClicked(order item, int id);
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
