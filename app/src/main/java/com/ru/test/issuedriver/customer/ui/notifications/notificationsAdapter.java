package com.ru.test.issuedriver.customer.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerActivity;
import com.ru.test.issuedriver.data.order;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class notificationsAdapter extends RecyclerView.Adapter<notificationsAdapter.VH> {

    List<order> cards;
    NotificationsViewModel viewModel;

    public notificationsAdapter(NotificationsViewModel notificationsViewModel, List<order> lst){
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
        View view = inflater.inflate(R.layout.notification_item, parent, false);
        VH viewHolder = new VH(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        order item = cards.get(position);
        holder.mNotification_item_fio.setText(item.performer_fio);
        holder.mNotification_item_purpose.setText(item.purpose);
        holder.mNotification_item_from.setText(item.from);
        holder.mNotification_item_to.setText(item.to);
        holder.mNotification_item_comment.setText(item.comment);

        holder.mNotification_item_extra.setVisibility(View.GONE);

        if(item.accept) {
            holder.mNotification_item_btn_status1.setVisibility(View.VISIBLE);
            holder.mNotification_item_btn_status0.setVisibility(View.GONE);
//            holder.mNotification_item_btn_accept_ok.setVisibility(View.VISIBLE);
//            holder.mNotification_item_btn_accept.setVisibility(View.GONE);
        } else {
            holder.mNotification_item_btn_status0.setVisibility(View.VISIBLE);
            holder.mNotification_item_btn_status1.setVisibility(View.GONE);
//            holder.mNotification_item_btn_accept_ok.setVisibility(View.GONE);
//            holder.mNotification_item_btn_accept.setVisibility(View.VISIBLE);
        }


        setBtnsOnClick(holder, item);
    }

    private void setBtnsOnClick(VH holder, final order item) {
        holder.mNotification_item_btn_status0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setOrderDelete(item);
            }
        });
        holder.mNotification_item_btn_status1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewModel.setOrderAccept(item);
            }
        });
        holder.mNotification_item_btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewModel.setOrderAccept(item);
            }
        });
        holder.mNotification_item_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerActivity.getInstance().callPhone(item.performer_phone);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    class VH extends RecyclerView.ViewHolder{
        TextView mNotification_item_fio, mNotification_item_purpose, mNotification_item_from, mNotification_item_to, mNotification_item_comment;
        View mNotification_item_extra, mNotification_item_btn_accept, mNotification_item_call, mNotification_item_btn_accept_ok;
        Button mNotification_item_btn_status0, mNotification_item_btn_status1;
        CardView mNotification_item;
        public VH(@NonNull View itemView) {
            super(itemView);

            mNotification_item  = itemView.findViewById(R.id.notification_item);
            mNotification_item_extra = itemView.findViewById(R.id.notification_item_extra);
            mNotification_item_btn_status0  = itemView.findViewById(R.id.notification_item_btn_status0);
            mNotification_item_btn_status1  = itemView.findViewById(R.id.notification_item_btn_status1);
            mNotification_item_btn_accept  = itemView.findViewById(R.id.notification_item_btn_accept);
            mNotification_item_btn_accept_ok  = itemView.findViewById(R.id.notification_item_btn_accept_ok);
            mNotification_item_call  = itemView.findViewById(R.id.notification_item_call);
            mNotification_item_fio = itemView.findViewById(R.id.notification_item_fio);
            mNotification_item_purpose = itemView.findViewById(R.id.notification_item_purpose);
            mNotification_item_from  = itemView.findViewById(R.id.notification_item_from);
            mNotification_item_to  = itemView.findViewById(R.id.notification_item_to);
            mNotification_item_comment = itemView.findViewById(R.id.notification_item_comment);
            mNotification_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNotification_item_extra.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
