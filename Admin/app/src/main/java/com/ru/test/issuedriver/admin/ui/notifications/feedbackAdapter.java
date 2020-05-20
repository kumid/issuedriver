package com.ru.test.issuedriver.admin.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ru.test.issuedriver.admin.R;
import com.ru.test.issuedriver.admin.data.feedback;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class feedbackAdapter extends RecyclerView.Adapter<feedbackAdapter.VH> {

    List<feedback> cards;
    NotificationsViewModel viewModel;

    public feedbackAdapter(NotificationsViewModel historyViewModel, List<feedback> lst){
        cards = lst;
        viewModel = historyViewModel;
    }
    public void setChangedData(List<feedback> lst) {
        cards = lst;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.feedback_item, parent, false);
        VH viewHolder = new VH(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        final feedback item = cards.get(position);
        holder.mUser_item_fio.setText(item.message);
        holder.mUser_item_email.setText(item.phone);
        holder.mUser_item_accepted.setChecked(item.accept);
        holder.mUser_item_accepted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                viewModel.setFeedbackAcceptState(item, isChecked);
            }
        });
    }
    @Override
    public int getItemCount() {
        return cards.size();
    }


    class VH extends RecyclerView.ViewHolder{
        TextView mUser_item_fio, mUser_item_email;
        CheckBox mUser_item_accepted;
        CardView mHistory_item;

        public VH(@NonNull View itemView) {
            super(itemView);

            mHistory_item  = itemView.findViewById(R.id.user_item);
            mUser_item_fio = itemView.findViewById(R.id.feedback_item_msg);
            mUser_item_email = itemView.findViewById(R.id.feedback_item_phone);
            mUser_item_accepted  = itemView.findViewById(R.id.feedback_item_accepted);
         }
    }
}
