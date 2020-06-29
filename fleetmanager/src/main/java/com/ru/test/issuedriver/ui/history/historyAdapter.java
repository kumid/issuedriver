package com.ru.test.issuedriver.ui.history;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.order;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class historyAdapter extends RecyclerView.Adapter<historyAdapter.VH> {

    List<order> cards;
    HistoryViewModel viewModel;
    Activity currentActivity ;
    boolean isPerformer;
    public historyAdapter(HistoryViewModel historyViewModel, List<order> lst, Activity activity){
        cards = lst;
        viewModel = historyViewModel;
        currentActivity = activity;
        isPerformer = MyActivity.CurrentUser.is_performer;
    }

    public void setChangedData(List<order> lst) {
        cards = lst;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.history_item, parent, false);
        VH viewHolder = new VH(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        order item = cards.get(position);

        SimpleDateFormat sfd;
        if (item.accept_timestamp != null) {
            sfd = new SimpleDateFormat("Поездка: dd-MM-yyyy HH:mm:ss");
            holder.mHistory_item_accept_date.setText(sfd.format(item.accept_timestamp.toDate()));
            holder.mHistory_item_close_date.setText("");
        }
        if (item.end_timestamp != null) {
            sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            holder.mHistory_item_close_date.setText(sfd.format(item.end_timestamp.toDate()));
        }

        holder.mHistory_item_purpose.setText(item.purpose);
        holder.mHistory_item_from.setText(item.from);
        holder.mHistory_item_to.setText(item.to);
        holder.mHistory_item_comment.setText(item.comment);

        holder.mHistory_time.setText(item.spent_time);
        holder.mHistory_distance.setText(String.valueOf(item.distance));
        holder.mHistory_fuel.setText(String.valueOf(item.fuel));

        holder.mHistory_item_extra.setVisibility(View.GONE);

        holder.mHistory_item_btn_status_completed.setVisibility(View.GONE);


        String photo = "";
        if(!isPerformer){
            photo = item.performer_photo;
            holder.mHistory_item_fio.setText(item.performer_fio);
            holder.mHistory_item_car.setText(item.car);
            holder.mHistory_item_carnumber.setText(item.car_number);
        } else {
            photo = item.customer_photo;
            holder.mHistory_item_fio.setText(item.customer_fio);
        }

        if(photo != null && photo.length() > 0) {
            Picasso.get().load(photo)
                    .placeholder(R.drawable.avatar)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.mHistory_item_item_photo);
        }

        if (item.state == 0) {
            holder.mHistory_item_state.setImageDrawable(currentActivity.getResources().getDrawable(R.drawable.green_triangle));
            holder.mHistory_item_close_state_title.setText(currentActivity.getResources().getString(R.string.history_item_close_date));
            //            holder.mHistory_item_btn_status_completed.setText("Закрыта");
            holder.mHistory_item_distance_calc_group.setVisibility(View.VISIBLE);
//            holder.mHistory_item_cancel_reason.setVisibility(View.GONE);
//            holder.mHistory_item_cancel_reason.setText("");
            holder.mHistory_item_cancel_line.setVisibility(View.GONE);
        } else {
            holder.mHistory_item_state.setImageDrawable(currentActivity.getResources().getDrawable(R.drawable.red_triangle));
            holder.mHistory_item_close_state_title.setText(currentActivity.getResources().getString(R.string.history_item_cancel_date));
//            holder.mHistory_item_btn_status_completed.setText("Отменена");
            holder.mHistory_item_distance_calc_group.setVisibility(View.GONE);
            holder.mHistory_item_cancel_line.setVisibility(View.VISIBLE);
//            holder.mHistory_item_cancel_reason.setVisibility(View.VISIBLE);
            holder.mHistory_item_cancel_reason.setText(item.cancel_reason);
        }
    }
    @Override
    public int getItemCount() {
        return cards.size();
    }


    class VH extends RecyclerView.ViewHolder{
        TextView mHistory_item_fio, mHistory_item_purpose, mHistory_item_from, mHistory_item_to, mHistory_item_comment, mHistory_time, mHistory_distance, mHistory_fuel,
                mHistory_item_accept_date, mHistory_item_cancel_reason, mHistory_item_close_date, mHistory_item_close_state_title,
                mHistory_item_car, mHistory_item_carnumber;
        View mHistory_item_extra, mHistory_item_call, mHistory_item_cancel_line, mHistory_item_distance_calc_group, mHistory_item_car_group;
        ImageView mHistory_item_state, mHistory_item_item_photo;
        Button mHistory_item_btn_status_completed;
        CardView mHistory_item;

        public VH(@NonNull View itemView) {
            super(itemView);

            mHistory_item  = itemView.findViewById(R.id.history_item);
            mHistory_item_extra = itemView.findViewById(R.id.history_item_extra);
            mHistory_item_btn_status_completed = itemView.findViewById(R.id.history_item_btn_status_completed);
            mHistory_item_call  = itemView.findViewById(R.id.history_item_call);
            mHistory_item_accept_date = itemView.findViewById(R.id.history_item_accept_date);
            mHistory_item_close_date = itemView.findViewById(R.id.history_item_close_date);
            mHistory_item_close_state_title = itemView.findViewById(R.id.history_item_close_state_title);
            mHistory_item_fio = itemView.findViewById(R.id.history_item_fio);
            mHistory_item_purpose = itemView.findViewById(R.id.history_item_purpose);
            mHistory_item_from  = itemView.findViewById(R.id.history_item_from);
            mHistory_item_to  = itemView.findViewById(R.id.history_item_to);
            mHistory_item_comment = itemView.findViewById(R.id.history_item_comment);

            mHistory_item_car_group = itemView.findViewById(R.id.history_item_car_group);
            mHistory_item_car = itemView.findViewById(R.id.history_item_car);
            mHistory_item_carnumber = itemView.findViewById(R.id.history_item_carnumber);
            mHistory_item_item_photo = itemView.findViewById(R.id.history_item_photo);
            if(isPerformer) mHistory_item_car_group.setVisibility(View.GONE);

            mHistory_item_cancel_reason = itemView.findViewById(R.id.history_item_cancel_reason);
            mHistory_item_cancel_line = itemView.findViewById(R.id.history_item_cancel_line);
            mHistory_item_distance_calc_group = itemView.findViewById(R.id.history_item_distance_calc_group);

            mHistory_time = itemView.findViewById(R.id.history_time);
            mHistory_distance = itemView.findViewById(R.id.history_distance);
            mHistory_fuel = itemView.findViewById(R.id.history_fuel);

            mHistory_item_state = itemView.findViewById(R.id.history_item_state);

            mHistory_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mHistory_item_extra.getVisibility() == View.GONE) {
                        mHistory_item_extra.setVisibility(View.VISIBLE);
                    } else {
                        mHistory_item_extra.setVisibility(View.GONE);
                    }

                }
            });
        }
    }
}
