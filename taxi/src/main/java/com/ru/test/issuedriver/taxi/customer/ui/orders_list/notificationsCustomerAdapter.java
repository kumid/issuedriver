package com.ru.test.issuedriver.taxi.customer.ui.orders_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.customer.CustomerV2Activity;
import com.ru.test.issuedriver.taxi.data.order;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class notificationsCustomerAdapter extends RecyclerView.Adapter<notificationsCustomerAdapter.VH> {

    List<order> cards;
    OrdersListViewModel viewModel;

    public notificationsCustomerAdapter(OrdersListViewModel ordersListViewModel, List<order> lst){
        cards = lst;
        viewModel = ordersListViewModel;
    }
    public void setChangedData(List<order> lst) {
        cards = lst;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.notification_item_new, parent, false);
        VH viewHolder = new VH(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        order item = cards.get(position);
        holder.mNotification_item_fio.setText(item.performer_fio);
        if(item.order_timestamp != null) {
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            holder.mNotification_item_data.setText(sfd.format(item.order_timestamp.toDate()));
        }
        holder.mNotification_item_purpose.setText(item.purpose);
        holder.mNotification_item_from.setText(item.from);
        holder.mNotification_item_to.setText(item.to);
        holder.mNotification_item_comment.setText(item.comment);

        holder.mNotification_item_extra.setVisibility(View.GONE);
        holder.mNotification_item_extra_btns.setVisibility(View.GONE);


        if(item.accept) {
            if (item.completed) {
                holder.mNotification_item_btn_status_completed.setVisibility(View.VISIBLE);
                holder.mNotification_item_btn_status_in_process.setVisibility(View.GONE);
                holder.mNotification_item_btn_status_wait.setVisibility(View.GONE);
//                holder.mNotification_item_btn_cancel.setVisibility(View.GONE);
            } else {
                holder.mNotification_item_btn_status_in_process.setVisibility(View.VISIBLE);
                holder.mNotification_item_btn_status_wait.setVisibility(View.GONE);
//                holder.mNotification_item_btn_cancel.setVisibility(View.GONE);
                holder.mNotification_item_btn_status_completed.setVisibility(View.GONE);
            }
        } else {
            holder.mNotification_item_btn_status_wait.setVisibility(View.VISIBLE);
//            holder.mNotification_item_btn_cancel.setVisibility(View.VISIBLE);
            holder.mNotification_item_btn_status_in_process.setVisibility(View.GONE);
            holder.mNotification_item_btn_status_completed.setVisibility(View.GONE);
        }
        setBtnsOnClick(holder, item);
    }

    private void setBtnsOnClick(VH holder, final order item) {
        holder.mNotification_item_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.accept) {
                    if (viewModel.callback4cancelOrder != null)
                        viewModel.callback4cancelOrder.callback(item);
                } else
                    viewModel.setOrderDelete(item);
            }
        });
        holder.mNotification_item_btn_status_in_process.setOnClickListener(new View.OnClickListener() {
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
                CustomerV2Activity.getInstance().callPhone(item.performer_phone);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }


    class VH extends RecyclerView.ViewHolder{
        TextView mNotification_item_fio, mNotification_item_purpose, mNotification_item_from, mNotification_item_to, mNotification_item_comment, mNotification_item_data;
        View mNotification_item_extra, mNotification_item_btn_accept, mNotification_item_call, mNotification_item_btn_accept_ok, mNotification_item_extra_btns;
        Button mNotification_item_btn_status_wait, mNotification_item_btn_status_in_process, mNotification_item_btn_status_completed, mNotification_item_btn_cancel;
        CardView mNotification_item;
        public VH(@NonNull View itemView) {
            super(itemView);

            mNotification_item  = itemView.findViewById(R.id.notification_item);
            mNotification_item_extra = itemView.findViewById(R.id.notification_item_extra);
            mNotification_item_extra_btns = itemView.findViewById(R.id.notification_item_extra_btns);
            mNotification_item_btn_status_wait = itemView.findViewById(R.id.notification_item_btn_status_wait);
            mNotification_item_btn_status_in_process = itemView.findViewById(R.id.notification_item_btn_status_in_process);
            mNotification_item_btn_status_completed = itemView.findViewById(R.id.notification_item_btn_status_completed);
            mNotification_item_btn_cancel  = itemView.findViewById(R.id.notification_item_btn_cancel);
            mNotification_item_btn_accept  = itemView.findViewById(R.id.notification_item_btn_accept);
            mNotification_item_btn_accept_ok  = itemView.findViewById(R.id.notification_item_btn_accept_ok);
            mNotification_item_call  = itemView.findViewById(R.id.notification_item_call);
            mNotification_item_fio = itemView.findViewById(R.id.notification_item_fio);
            mNotification_item_data  = itemView.findViewById(R.id.notification_item_data);
            mNotification_item_purpose = itemView.findViewById(R.id.notification_item_purpose);
            mNotification_item_from  = itemView.findViewById(R.id.notification_item_from);
            mNotification_item_to  = itemView.findViewById(R.id.notification_item_to);
            mNotification_item_comment = itemView.findViewById(R.id.notification_item_comment);
            mNotification_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mNotification_item_extra.getVisibility() == View.GONE) {
                        mNotification_item_extra.setVisibility(View.VISIBLE);
                        mNotification_item_extra_btns.setVisibility(View.VISIBLE);
                    } else {
                        mNotification_item_extra.setVisibility(View.GONE);
                        mNotification_item_extra_btns.setVisibility(View.GONE);
                    }
                }
            });
        }
    }


}
