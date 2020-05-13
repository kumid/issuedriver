package com.ru.test.issuedriver.performer.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.order;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsFragment extends Fragment {

    RecyclerView notification_rv;
    notificationsAdapter adapter;

    private NotificationsViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        initViews(root);

        return root;
    }

    private void initViews(View root) {
        notification_rv = root.findViewById(R.id.notification_rv);
        adapter = new notificationsAdapter(notificationsViewModel, new ArrayList<order>());
        notification_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        notification_rv.setAdapter(adapter);

        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                adapter.setChangedData(orders);
            }
        });
    }
}