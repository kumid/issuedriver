package com.ru.test.issuedriver.taxi.bottom_dialogs;

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

    private CancelBottomSheetListener mListener;
    boolean isPerformer;
    TextView mOrder_distance_bottom, mOrder_fuel_bottom;
    View mCancel_action_standard_group, mCancel_action_other_group;
    CheckBox mCancel_action_other_check;
    TextInputEditText mCancel_sheet_reason_other_input_value;

    String cancel_sheet_reason1, cancel_sheet_reason2, cancel_sheet_reason3, cancel_sheet_reason4;

    order item;

    public OrderCancelBottonDialog(order item, boolean isPerformer) {
        this.item = item;
        this.isPerformer = isPerformer;
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
        mCancel_sheet_reason_other_input_value = view.findViewById(R.id.cancel_sheet_reason_other_text);
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

        if(isPerformer){
            cancel_sheet_reason1 = getActivity().getResources().getString(R.string.cancel_sheet_reason_performer1);
            cancel_sheet_reason2 = getActivity().getResources().getString(R.string.cancel_sheet_reason_performer2);
            mCancel_sheet_reason3.setVisibility(View.GONE);
            mCancel_sheet_reason4.setVisibility(View.GONE);
        } else {
            cancel_sheet_reason1 = getActivity().getResources().getString(R.string.cancel_sheet_reason1);
                    cancel_sheet_reason2 = getActivity().getResources().getString(R.string.cancel_sheet_reason2);
            cancel_sheet_reason3 = getActivity().getResources().getString(R.string.cancel_sheet_reason3);
                    cancel_sheet_reason4 = getActivity().getResources().getString(R.string.cancel_sheet_reason4);
        }

        mCancel_action_other_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                InputMethodManager imm =  (InputMethodManager) MyActivity.getMyInstance().getSystemService(Context.INPUT_METHOD_SERVICE);

                if(isChecked){
                    mCancel_action_other_group.setVisibility(View.VISIBLE);
                    mCancel_action_standard_group.setVisibility(View.GONE);
                    imm.showSoftInput(mCancel_sheet_reason_other_input_value, 0);
                } else {
                    mCancel_action_other_group.setVisibility(View.GONE);
                    mCancel_action_standard_group.setVisibility(View.VISIBLE);
                    imm.hideSoftInputFromWindow(mCancel_sheet_reason_other_input_value.getWindowToken(), 0);
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        String reason = "";
        switch (v.getId()){
            case R.id.cancel_sheet_reason1:
                reason = cancel_sheet_reason1;
                break;
            case R.id.cancel_sheet_reason2:
                reason = cancel_sheet_reason2;
                break;
            case R.id.cancel_sheet_reason3:
                reason = cancel_sheet_reason3;
                break;
            case R.id.cancel_sheet_reason4:
                reason = cancel_sheet_reason4;
                break;
            case R.id.cancel_sheet_reason_other:
                reason = mCancel_sheet_reason_other_input_value.getText().toString();
                break;

        }
        item.state = 1;
        item.cancel_reason = reason;
        mListener.onCancelButtonClicked(item);
        dismiss();
    }

 public interface CancelBottomSheetListener {
        void onCancelButtonClicked(order item);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (CancelBottomSheetListener) context;
        } catch (ClassCastException ex){
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }
}
