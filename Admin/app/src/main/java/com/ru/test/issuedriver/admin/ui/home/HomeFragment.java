package com.ru.test.issuedriver.admin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ru.test.issuedriver.admin.R;
import com.ru.test.issuedriver.admin.data.user;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
     RecyclerView history_rv;
    userAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        homeViewModel.initNotificationLoad();
        initViews(root);

        return root;
    }
    boolean isFirst = true;
    private void initViews(View root) {
        history_rv = root.findViewById(R.id.user_rv);
        history_rv.setLayoutManager(new LinearLayoutManager(getContext()));

        homeViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<user>>() {
            @Override
            public void onChanged(List<user> users) {
                if (isFirst) {
                    adapter = new userAdapter(homeViewModel, users);
                    history_rv.setAdapter(adapter);
                    isFirst = false;
//                adapter.setChangedData(users);
                }
            }
        });
    }
}
