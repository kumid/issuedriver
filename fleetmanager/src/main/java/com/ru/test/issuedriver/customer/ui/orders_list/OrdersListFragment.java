package com.ru.test.issuedriver.customer.ui.orders_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.order;

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
    notificationsCustomerAdapter adapterCustomer;
    notificationsPerformerAdapter adapterPerformer;

    private OrdersListViewModel ordersListViewModel;
//    private RegistrationViewModel registrationViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        registrationViewModel =
//                ViewModelProviders.of(getActivity()).get(RegistrationViewModel.class);
        ordersListViewModel =
                ViewModelProviders.of(getActivity()).get(OrdersListViewModel.class);

//        notificationsViewModel.initNotificationLoad(getViewLifecycleOwner(), registrationViewModel.currentUser);
//        notificationsViewModel.initNotificationLoad(MyActivity.CurrentUser);

        View root = inflater.inflate(R.layout.fragment_orders_list, container, false);

        initViews(root);

        return root;
    }

    private void initViews(View root) {
        notification_rv = root.findViewById(R.id.notification_rv);
        notification_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        if(MyActivity.CurrentUser.is_performer) {
            adapterPerformer = new notificationsPerformerAdapter(ordersListViewModel, new ArrayList<order>());
            notification_rv.setAdapter(adapterPerformer);
        } else {
            adapterCustomer = new notificationsCustomerAdapter(ordersListViewModel, new ArrayList<order>());
            notification_rv.setAdapter(adapterCustomer);
        }


        ordersListViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                if(MyActivity.CurrentUser.is_performer)
                    adapterPerformer.setChangedData(orders);
                else
                    adapterCustomer.setChangedData(orders);
            }
        });
    }
}