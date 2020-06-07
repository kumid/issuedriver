package com.ru.test.issuedriver.customer.ui.map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.place;
import com.ru.test.issuedriver.ui.history.historyAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class placesAdapter extends RecyclerView.Adapter<placesAdapter.Holder> {

    List<place> cards;

    public placesAdapter(List<place> list){
        cards = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.place_item, parent, false);
        Holder viewHolder = new Holder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        place item = cards.get(position);
        holder.mPlace_item_title.setText(item.address);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView mPlace_item_title;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mPlace_item_title  = itemView.findViewById(R.id.place_item_title);

        }

    }

}
