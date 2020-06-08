package com.ru.test.issuedriver.customer.ui.map;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ru.test.issuedriver.MainViewModel;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.place;
import com.ru.test.issuedriver.ui.history.historyAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class placesAdapter extends RecyclerView.Adapter<placesAdapter.Holder> {

    List<place> cards;
    MainViewModel mainViewModel;

    public placesAdapter(MainViewModel mainViewModel, List<place> list){
        cards = list;
        this.mainViewModel = mainViewModel;
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
        holder.mPlace_item_layout.setBackgroundColor(Color.parseColor(item.isSelected ? "#CCFFEB3B": "#CCFFFFFF"));
        holder.mPlace_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (place element: cards) {
                    if(element.equals(item)) {
                        item.isSelected = !item.isSelected;
                        mainViewModel.currentPlace = item.isSelected?item:null;
                    }
                    else
                        element.isSelected = false;
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        TextView mPlace_item_title;
        View mPlace_item_layout;

        public Holder(@NonNull View itemView) {
            super(itemView);

            mPlace_item_layout = itemView.findViewById(R.id.place_item_layout);
            mPlace_item_title  = itemView.findViewById(R.id.place_item_title);

        }

    }

}
