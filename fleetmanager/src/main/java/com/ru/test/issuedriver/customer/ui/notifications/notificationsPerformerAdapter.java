package com.ru.test.issuedriver.customer.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.performer.PerformerActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class notificationsPerformerAdapter extends RecyclerView.Adapter<notificationsPerformerAdapter.VH> {

    List<order> cards;
    NotificationsViewModel viewModel;

    public notificationsPerformerAdapter(NotificationsViewModel notificationsViewModel, List<order> lst){
        cards = lst;
        viewModel = notificationsViewModel;
    }
    public void setChangedData(List<order> lst) {
        cards = lst;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notification_item_performer, parent, false);
        VH viewHolder = new VH(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        order item = cards.get(position);
        holder.mNotification_item_fio.setText(item.customer_fio);
        holder.mNotification_item_purpose.setText(item.purpose);
        holder.mNotification_item_from.setText(item.from);
        holder.mNotification_item_to.setText(item.to);
        holder.mNotification_item_comment.setText(item.comment);

        holder.mNotification_item_extra.setVisibility(View.GONE);

        if(item.accept) {
            if (item.completed) {
                holder.mNotification_item_btn_status_completed.setVisibility(View.VISIBLE);
                holder.mNotification_item_btn_status_in_process.setVisibility(View.GONE);
                holder.mNotification_item_btn_status_wait.setVisibility(View.GONE);
                holder.mNotification_item_btn_start.setVisibility(View.GONE);
            } else {
                holder.mNotification_item_btn_status_in_process.setVisibility(View.VISIBLE);
                holder.mNotification_item_btn_status_wait.setVisibility(View.GONE);
                holder.mNotification_item_btn_start.setVisibility(View.VISIBLE);
                holder.mNotification_item_btn_status_completed.setVisibility(View.GONE);
//            holder.mNotification_item_btn_accept_ok.setVisibility(View.VISIBLE);
//            holder.mNotification_item_btn_accept.setVisibility(View.GONE);
            }
        } else {
            holder.mNotification_item_btn_status_wait.setVisibility(View.VISIBLE);
            holder.mNotification_item_btn_start.setVisibility(View.GONE);
            holder.mNotification_item_btn_status_in_process.setVisibility(View.GONE);
            holder.mNotification_item_btn_status_completed.setVisibility(View.GONE);

//            holder.mNotification_item_btn_accept_ok.setVisibility(View.GONE);
//            holder.mNotification_item_btn_accept.setVisibility(View.VISIBLE);
        }

        setBtnsOnClick(holder, item);
    }


    private void setBtnsOnClick(VH holder, final order item) {
        holder.mNotification_item_btn_status_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setOrderAccept(item);
            }
        });
         holder.mNotification_item_btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setOrderAccept(item);
            }
        });
        holder.mNotification_item_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformerActivity.getInstance().callPhone(item.customer_phone);
            }
        });

        holder.mNotification_item_btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformerActivity.getInstance().startOrderPerforme(item);
            }
        });


    }

    @Override
    public int getItemCount() {
        return cards.size();
    }



    class VH extends RecyclerView.ViewHolder{
        TextView mNotification_item_fio, mNotification_item_purpose, mNotification_item_from, mNotification_item_to, mNotification_item_comment;
        View mNotification_item_extra, mNotification_item_extra_btns1, mNotification_item_btn_accept, mNotification_item_call, mNotification_item_btn_accept_ok;
        Button mNotification_item_btn_status_wait, mNotification_item_btn_status_in_process, mNotification_item_btn_status_completed, mNotification_item_btn_start;
        CardView mNotification_item;
        public VH(@NonNull View itemView) {
            super(itemView);

            mNotification_item  = itemView.findViewById(R.id.notification_item);
            mNotification_item_extra = itemView.findViewById(R.id.notification_item_extra);
            mNotification_item_btn_status_wait = itemView.findViewById(R.id.notification_item_btn_status_wait);
            mNotification_item_btn_status_in_process = itemView.findViewById(R.id.notification_item_btn_status_in_process);
            mNotification_item_btn_status_completed = itemView.findViewById(R.id.notification_item_btn_status_completed);
            mNotification_item_btn_accept  = itemView.findViewById(R.id.notification_item_btn_accept);
            mNotification_item_btn_accept_ok  = itemView.findViewById(R.id.notification_item_btn_accept_ok);
            mNotification_item_call  = itemView.findViewById(R.id.notification_item_call);
            mNotification_item_fio = itemView.findViewById(R.id.notification_item_fio);
            mNotification_item_purpose = itemView.findViewById(R.id.notification_item_purpose);
            mNotification_item_from  = itemView.findViewById(R.id.notification_item_from);
            mNotification_item_to  = itemView.findViewById(R.id.notification_item_to);
            mNotification_item_comment = itemView.findViewById(R.id.notification_item_comment);

            mNotification_item_extra_btns1 = itemView.findViewById(R.id.notification_item_extra_btns1);
            mNotification_item_btn_start = itemView.findViewById(R.id.notification_item_btn_start);

            mNotification_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNotification_item_extra.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
