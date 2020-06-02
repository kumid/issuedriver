package com.ru.test.issuedriver.taxi.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.data.order;
import com.ru.test.issuedriver.taxi.helpers.mysettings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    RecyclerView history_rv;
    historyAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(getActivity()).get(HistoryViewModel.class);
        historyViewModel.initNotificationsHistoryLoad(mysettings.GetUser());

        View root = inflater.inflate(R.layout.fragment_history, container, false);

        initViews(root);
        return root;
    }


    private void initViews(View root) {
        history_rv = root.findViewById(R.id.history_rv);
        adapter = new historyAdapter(historyViewModel, new ArrayList<order>());
        history_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        history_rv.setAdapter(adapter);

        historyViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                adapter.setChangedData(orders);
            }
        });
    }
}
