package com.ru.test.issuedriver.performer.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.performer.PerformerActivity;
import com.ru.test.issuedriver.registration.RegistrationViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationsFragment extends Fragment {

    RecyclerView notification_rv;
    notificationsAdapter adapter;

    private NotificationsViewModel notificationsViewModel;
    private RegistrationViewModel registrationViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        registrationViewModel =
                ViewModelProviders.of(PerformerActivity.getInstance()).get(RegistrationViewModel.class);
        notificationsViewModel =
                ViewModelProviders.of(PerformerActivity.getInstance()).get(NotificationsViewModel.class);
        notificationsViewModel.initNotificationLoad(getViewLifecycleOwner(), registrationViewModel.currentUser);

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