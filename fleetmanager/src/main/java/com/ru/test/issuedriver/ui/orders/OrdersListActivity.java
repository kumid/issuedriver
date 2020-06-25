package com.ru.test.issuedriver.ui.orders;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerV2Activity;
import com.ru.test.issuedriver.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.customer.ui.orders_list.notificationsCustomerAdapter;
import com.ru.test.issuedriver.customer.ui.orders_list.notificationsPerformerAdapter;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.helpers.callBacks;
import com.ru.test.issuedriver.helpers.firestoreHelper;
import com.ru.test.issuedriver.bottom_dialogs.OrderCancelBottonDialog;
import com.ru.test.issuedriver.helpers.fsm.sender;

import java.util.ArrayList;
import java.util.List;

import static com.ru.test.issuedriver.helpers.callBacks.callback4goToNavigate;

public class OrdersListActivity extends AppCompatActivity implements OrderCancelBottonDialog.CancelBottomSheetListener {

    RecyclerView notification_rv;
    notificationsCustomerAdapter adapterCustomer;
    notificationsPerformerAdapter adapterPerformer;
    private OrdersListViewModel ordersListViewModel;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        ordersListViewModel =
                ViewModelProviders.of(CustomerV2Activity.getInstance()).get(OrdersListViewModel.class);

        initViews();

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Заявки");
        }

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
    }


    private void initViews() {
        notification_rv = findViewById(R.id.notification_rv);
        notification_rv.setLayoutManager(new LinearLayoutManager(OrdersListActivity.this));

        RecyclerView.Adapter adapter;



        if(MyActivity.CurrentUser.is_performer) {
//            adapterPerformer
            adapterPerformer = new notificationsPerformerAdapter(ordersListViewModel, new ArrayList<order>());
            adapter = adapterPerformer;
            callBacks.callback4cancelOrder = new callBacks.CancelOrderInterface() {
                @Override
                public void callback(order order) {
                    OrderCancelBottonDialog dialog = new OrderCancelBottonDialog(order, MyActivity.CurrentUser.is_performer);
                    dialog.show(getSupportFragmentManager(), null);
                }
            };
        } else {
//            adapterCustomer
            adapterCustomer  =  new notificationsCustomerAdapter(ordersListViewModel, new ArrayList<order>());
            adapter = adapterCustomer;
            callBacks.callback4cancelOrder = new callBacks.CancelOrderInterface() {
                @Override
                public void callback(order order) {
                    OrderCancelBottonDialog dialog = new OrderCancelBottonDialog(order, MyActivity.CurrentUser.is_performer);
                    dialog.show(getSupportFragmentManager(), null);
                }
            };
            callBacks.callback4deleteOrder = new callBacks.DeleteOrderInterface() {
                @Override
                public void callback(boolean success) {
                    if(success)
                        OrdersListActivity.this.finish();
                }
            };
        }

        notification_rv.setAdapter(adapter);

        ordersListViewModel.getNotifications().observe(OrdersListActivity.this, new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                if(MyActivity.CurrentUser.is_performer) {
                    adapterPerformer.setChangedData(orders);
                }
                else
                    adapterCustomer.setChangedData(orders);
            }
        });
    }

    /// ActionBar Back button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                //actionBar.setDisplayHomeAsUpEnabled(false);
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onCancelButtonClicked(order item) {
        Log.e("myLogs", "");
        firestoreHelper.setOrderCancelState(item, 1,
                String.format(MyActivity.CurrentUser.is_performer ? "Отменен водителем":"Отменен заказчиком", item.cancel_reason));
        firestoreHelper.setUserState(item.performer_email, 0);
        //ordersListViewModel.setOrderDelete(item);

        sender.send(item, MyActivity.CurrentUser.is_performer ? sender.orderStateType.cancel_order_from_performer : sender.orderStateType.cancel_order_from_customer);
    }
}
