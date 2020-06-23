package com.ru.test.issuedriver.ui.history;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.helpers.mysettings;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends MyActivity {
    private HistoryViewModel historyViewModel;
    RecyclerView history_rv;
    historyAdapter adapter;
    private ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyViewModel =
                ViewModelProviders.of(HistoryActivity.this).get(HistoryViewModel.class);
        historyViewModel.initNotificationsHistoryLoad(CurrentUser);

        initViews();

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("История заказов");
        }
    }

    private void initViews() {
        history_rv = findViewById(R.id.history_rv);
        adapter = new historyAdapter(historyViewModel, new ArrayList<order>(), this);
        history_rv.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        history_rv.setAdapter(adapter);

        historyViewModel.getNotifications().observe(HistoryActivity.this, new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                adapter.setChangedData(orders);
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
}
