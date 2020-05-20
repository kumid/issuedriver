package com.ru.test.issuedriver.admin.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ru.test.issuedriver.admin.R;
import com.ru.test.issuedriver.admin.data.feedback;
import com.ru.test.issuedriver.admin.data.user;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel homeViewModel;
    RecyclerView history_rv;
    feedbackAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel.initNotificationLoad();
        initViews(root);

        return root;
    }
    boolean isFirst = true;
    private void initViews(View root) {
        history_rv = root.findViewById(R.id.user_rv);
        history_rv.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<feedback>>() {
            @Override
            public void onChanged(List<feedback> feedbacks) {
//                if (isFirst)
                {
                    adapter = new feedbackAdapter(homeViewModel, feedbacks);
                    history_rv.setAdapter(adapter);
                    isFirst = false;
//                adapter.setChangedData(users);
                }
            }
        });
    }
}
