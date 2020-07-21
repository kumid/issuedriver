package com.ru.test.issuedriver.customer.ui.orders_list;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.bottom_dialogs.OrderCancelBottonDialog;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.place;
import com.ru.test.issuedriver.helpers.callBacks;
import com.ru.test.issuedriver.performer.PerformerActivity;
import com.ru.test.issuedriver.performer.ui.orderPerforming.OrderPerformingActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.ru.test.issuedriver.helpers.callBacks.callback4goToNavigate;

public class OrdersListFragment extends Fragment {

    RecyclerView notification_rv;
//    View mOrder_list_title;
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

        if(MyActivity.CurrentUser == null)
            return root;

        initViews(root);

        callback4goToNavigate = new callBacks.goToNavigateInterface() {
            @Override
            public void callback(order currentOrder) {
                if(currentOrder.from_position == null
                        || currentOrder.to_position == null)
                    return;

                String uri = "http://maps.google.com/maps?saddr="+currentOrder.from_position.getLatitude() + "," +currentOrder.from_position.getLongitude() +
                        "&daddr="+currentOrder.to_position.getLatitude() + "," +currentOrder.to_position.getLongitude();
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        };

        return root;
    }

    private void initViews(View root) {
        notification_rv = root.findViewById(R.id.notification_rv);
//        mOrder_list_title = root.findViewById(R.id.order_list_title);
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
                if(MyActivity.CurrentUser.is_performer) {
                    adapterPerformer.setChangedData(orders);
                    boolean isRed = false;
                    for (order one: orders){
                        if(!one.accept){
                           isRed = true;
                           break;
                        }
                    }
                    PerformerActivity.getInstance().navView.setItemIconTintList(ColorStateList.valueOf(Color.parseColor(isRed?"#ff0000":"#6200EE")));

                }
                else
                    adapterCustomer.setChangedData(orders);

                //mOrder_list_title.setVisibility(orders.size() == 0 ? View.GONE : View.VISIBLE);
            }
        });
    }
}