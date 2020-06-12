package com.ru.test.issuedriver.taxi.customer.ui.orders_list;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ru.test.issuedriver.taxi.MyActivity;
import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.data.order;
import com.ru.test.issuedriver.taxi.helpers.callBacks;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.ru.test.issuedriver.taxi.helpers.callBacks.callback4goToNavigate;

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

        callback4goToNavigate = new callBacks.goToNavigateInterface() {
            @Override
            public void callback(order currentOrder) {
                if(currentOrder.from_position == null
                        || currentOrder.to_position == null)
                    return;

                String uri = "http://maps.google.com/maps?saddr="+currentOrder.from_position.getLatitude() + "," +currentOrder.from_position.getLongitude() +
                        "&daddr="+currentOrder.to_position.getLatitude() + "," +currentOrder.to_position.getLongitude();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        };

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