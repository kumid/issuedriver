package com.ru.test.issuedriver.taxi.performer.ui.orders_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.ru.test.issuedriver.taxi.MyActivity;
import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.data.order;
import com.ru.test.issuedriver.taxi.performer.PerformerActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrdersListFragment extends Fragment {

    RecyclerView notification_rv;
    notificationsAdapter adapter;

    private OrdersListViewModel ordersListViewModel;
//    private RegistrationViewModel registrationViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        registrationViewModel =
//                ViewModelProviders.of(PerformerActivity.getInstance()).get(RegistrationViewModel.class);
        ordersListViewModel =
                ViewModelProviders.of(PerformerActivity.getInstance()).get(OrdersListViewModel.class);
        ordersListViewModel.initNotificationLoad(MyActivity.CurrentUser);

        View root = inflater.inflate(R.layout.fragment_orders_list, container, false);

        initViews(root);

        return root;
    }

    private void initViews(View root) {
        notification_rv = root.findViewById(R.id.notification_rv);
        adapter = new notificationsAdapter(ordersListViewModel, new ArrayList<order>());
        notification_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        notification_rv.setAdapter(adapter);

        ordersListViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                adapter.setChangedData(orders);
            }
        });
    }
}