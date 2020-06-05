package com.ru.test.issuedriver.customer.ui.order;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerV2Activity;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.helpers.firestoreHelper;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class OrderActivity extends MyActivity implements View.OnClickListener {

    private static final int AUTOCOMPLETE_FROM_REQUEST_CODE = 12345;
    private static final int AUTOCOMPLETE_TO_REQUEST_CODE = 12346;
    private static final String TAG = "myLogs";
    OrderViewModel orderViewModel;
//    RegistrationViewModel registrationViewModel;
    ActionBar actionBar;
    TextInputEditText mOrder_from, mOrder_to, mOrder_purpose, mOrder_comment;
    MaterialTextView mOrder_name, mOrder_car, mOrder_carnumber;
    TextView currentDateTime;
    TimePicker mOrderTime;
    View mOrder_from_btn, mOrder_to_btn;
    Button mOrder_btn;
    RadioButton mOrder_now, mOrder_tomorrow;
    ProgressBar mProgress_circular;
    Calendar dateAndTime=Calendar.getInstance();

    /*
     *Client that exposes the Places API methods
     */
    PlacesClient placesClient;

    /*
     * Token used for sessionizing multiple instances of FindAutocompletePredictionsRequest.
     * The same token can also be used for a subsequent FetchPlaceRequest on one of the autocomplete prediction results returned.
     * */
    AutocompleteSessionToken token;
    private AutoCompleteTextView autocomplete_place_from, autocomplete_place_to;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Новая заявка");
        }
        initExtra();
        initViews();
        initVM();
        initExtra();
        setInitialDateTime();
        initAutocomplete();
    }

    private void initAutocomplete() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));
        }

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
    }

    public void onSearchCalled(int mode) {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS, Place.Field.LAT_LNG);

        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(53.5663927,34.3128526),
                new LatLng(53.617667, 34.346756));
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("RU")
                //.setLocationRestriction(bounds)
//                .setInitialQuery("Дятьково")
                .build(this);
        startActivityForResult(intent, mode == 1 ? AUTOCOMPLETE_FROM_REQUEST_CODE : AUTOCOMPLETE_TO_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE
                || requestCode == AUTOCOMPLETE_TO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());
                //Toast.makeText(AutocompleteFromIntentActivity.this, "ID: " + place.getId() + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: " + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                if(requestCode == AUTOCOMPLETE_FROM_REQUEST_CODE)
                    mOrder_from.setText(address);
                else
                    mOrder_to.setText(address);
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
//                Toast.makeText(AutocompleteFromIntentActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }







    String customer_fio, customer_phone, customer_email, performer_fio,  performer_phone, performer_email, performer_car, performer_car_numbr;
    private void initExtra() {
        customer_fio = CurrentUser.fio;
        customer_phone = CurrentUser.tel;
        customer_email = CurrentUser.email;
//        customer_fio = getIntent().getStringExtra("customer_fio");
//        customer_phone = getIntent().getStringExtra("customer_phone");
//        customer_email = getIntent().getStringExtra("customer_email");
        performer_fio = getIntent().getStringExtra("performer_fio");
        performer_phone = getIntent().getStringExtra("performer_phone");
        performer_email = getIntent().getStringExtra("performer_email");
        performer_car = getIntent().getStringExtra("performer_car");
        performer_car_numbr = getIntent().getStringExtra("performer_car_number");
    }


    private void initViews() {
        mOrder_name = findViewById(R.id.order_name);
        currentDateTime = findViewById(R.id.order_data);
        mOrder_from = findViewById(R.id.order_from);
        mOrder_to = findViewById(R.id.order_to);
        mOrder_purpose = findViewById(R.id.order_purpose);
        mOrder_comment = findViewById(R.id.order_comment);
        mOrder_btn = findViewById(R.id.order_btn);
        mProgress_circular = findViewById(R.id.progress_circular);
        mOrder_car = findViewById(R.id.order_car);
        mOrder_carnumber = findViewById(R.id.order_carnumber);
        mOrderTime = findViewById(R.id.order_time);
        mOrder_now  = findViewById(R.id.order_now);
        mOrder_tomorrow  = findViewById(R.id.order_tomorrow);
        mOrder_from_btn   = findViewById(R.id.order_from_btn);
        mOrder_to_btn   = findViewById(R.id.order_to_btn);

        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mOrderTime.setHour(mHour);
            mOrderTime.setMinute(mMinute);
        } else {
            mOrderTime.setCurrentHour(mHour);
            mOrderTime.setCurrentMinute(mMinute);
        }
        mOrderTime.setIs24HourView(true);

        mOrder_name.setText(performer_fio);
        mOrder_car.setText(performer_car);
        mOrder_carnumber.setText(performer_car_numbr);

        mOrder_btn.setOnClickListener(this);
        mOrder_from_btn.setOnClickListener(this);
        mOrder_to_btn.setOnClickListener(this);
        currentDateTime.setOnClickListener(this);

//        mOrder_from.setShowSoftInputOnFocus(false);
    }

    private void initVM() {
        orderViewModel =
                ViewModelProviders.of(CustomerV2Activity.getInstance()).get(OrderViewModel.class);
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(OrderActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(OrderActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }
    // установка начальных даты и времени
    private void setInitialDateTime() {

        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
            setTime(currentDateTime);
        }
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.order_data){
            setDate(v);
            return;
        } else if(v.getId() == R.id.order_from_btn){
            onSearchCalled(1);
            return;
        } else if(v.getId() == R.id.order_to_btn){
            onSearchCalled(2);
            return;
        }

        org.joda.time.DateTime time = new DateTime();

        if(mOrder_tomorrow.isChecked())
            time = time.plusDays(1); //calendar.add(Calendar.DAY_OF_MONTH, 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            time = time.withTime(mOrderTime.getHour(), mOrderTime.getMinute(), 0, 0);
//            calendar.set(Calendar.HOUR, mOrderTime.getHour());
//            calendar.set(Calendar.MINUTE, mOrderTime.getMinute());
        } else {
            time = time.withTime(mOrderTime.getCurrentHour(), mOrderTime.getCurrentMinute(), 0, 0);
            //            calendar.set(Calendar.HOUR, mOrderTime.getCurrentHour());
//            calendar.set(Calendar.MINUTE, mOrderTime.getCurrentMinute());
        }


        order curr = new order(
//                currentDateTime.getText().toString(),
//                calendar.getTime(),
                time.toDate(),
                mOrder_from.getText().toString(),
                mOrder_to.getText().toString(),
                mOrder_purpose.getText().toString(),
                mOrder_comment.getText().toString(),
                customer_fio,
                customer_phone,
                customer_email,
                performer_fio,
                performer_phone,
                performer_email,
                mOrder_car.getText().toString(),
                mOrder_carnumber.getText().toString()
                );

        if(curr.from.length() == 0
            || curr.to.length() == 0
            || curr.purpose.length() == 0){
            showToast("Заявка заполнена не полностью", Toast.LENGTH_LONG);
            return;
        }

        mProgress_circular.setVisibility(View.VISIBLE);
        orderViewModel.sendOrder(curr);
        orderViewModel.orderSendCompleteCalback = new OrderViewModel.orderSendComplete() {
            @Override
            public void callback(boolean pass) {
                if(pass){
                     showToast("Заявка успешно зарегистрирована", Toast.LENGTH_LONG);
                     finish();
                     //mProgress_circular.setVisibility(View.GONE);
                }
            }
        };
    }

    /// ActionBar Back button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                firestoreHelper.setUserBusy(performer_email, false);
                finish();
                actionBar.setDisplayHomeAsUpEnabled(false);
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //placesUtils.showCurrentPlace(this);
        //placesUtils.getAddressFromLocation(imHere.getLat(), imHere.getLong());
    }

    @Override
    public void onBackPressed() {
        firestoreHelper.setUserBusy(performer_email, false);
        super.onBackPressed();
    }

}
