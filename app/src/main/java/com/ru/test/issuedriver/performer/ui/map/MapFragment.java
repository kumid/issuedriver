package com.ru.test.issuedriver.performer.ui.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.performer.PerformerActivity;
import com.ru.test.issuedriver.performer.ui.order.OrderActivity;
import com.ru.test.issuedriver.performer.ui.registration.RegistrationViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MapFragment extends Fragment {

    private MapViewModel homeViewModel;
    private RegistrationViewModel registrationViewModel;
//    private MapView mMapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);
        registrationViewModel =
                ViewModelProviders.of(PerformerActivity.getInstance()).get(RegistrationViewModel.class);

        View root = inflater.inflate(R.layout.fragment_map, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("fio", registrationViewModel.currentUser.getValue().fio);
                intent.putExtra("customer", registrationViewModel.currentUser.getValue().email);
                intent.putExtra("performer", "performer@gmail.com");
                startActivity(intent);
            }
        });
//        mMapView = (MapView) root.findViewById(R.id.mapView);
//        mMapView.onCreate(savedInstanceState);
//        mMapView.onResume(); // needed to get the map to display immediately


        return root;
    }

}
