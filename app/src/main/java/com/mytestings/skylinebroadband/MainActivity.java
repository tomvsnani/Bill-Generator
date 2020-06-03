package com.mytestings.skylinebroadband;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Database.Entity;
import Database.SkyDatabase;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Adapter adapter;
    FloatingActionButton floatingActionButton;
    MainViewModel mainViewModel;

    EditText editText;
    Toolbar toolbar;
    AppCompatRadioButton totalusersRadioButton;
    AppCompatRadioButton dueUsersRadioButton;
    RadioGroup radioGroup;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    List<Entity> searchList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        floatingActionButton = findViewById(R.id.fab);
        editText = findViewById(R.id.editTextSearch);
        toolbar = findViewById(R.id.toolbar);
        totalusersRadioButton = findViewById(R.id.totalUsers);
        dueUsersRadioButton = findViewById(R.id.userswithDue);
        radioGroup = findViewById(R.id.radioGroup);
        setSupportActionBar(toolbar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false);
        adapter = new Adapter(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);


        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (settings.getBoolean("my_first_time", true)) {
            createAlarm();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.d("entityiddd", String.valueOf(dataSnapshot1.getValue()));
                        Entity entity = dataSnapshot1.getValue(Entity.class);
                        mainViewModel.insert(entity);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            settings.edit().putBoolean("my_first_time", false).apply();
        }


    }

    public void createAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar alaramcalender = Calendar.getInstance();
        alaramcalender.setTimeInMillis(System.currentTimeMillis());
        alaramcalender.set(Calendar.HOUR_OF_DAY, 24);
        Intent intent = new Intent(this, Receiver.class);
        intent.putExtra("hello", "start");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alaramcalender.set(Calendar.MINUTE, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alaramcalender.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNewUser.class);
                startActivity(intent);
            }
        });
        totalusersRadioButton.setChecked(true);
        populateWithTotalUsers();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == totalusersRadioButton.getId()) {
                    if (totalusersRadioButton.isChecked()) {
                        populateWithTotalUsers();
                    }
                }
                if (checkedId == dueUsersRadioButton.getId()) {

                    mainViewModel.getFetchOnlyListDataWithDue().observe(MainActivity.this, new Observer<List<Entity>>() {
                        @Override
                        public void onChanged(List<Entity> entities) {
                            getTextFromEditTextToSearch(entities);
                        }
                    });
                }
            }
        });
    }


    private void populateWithTotalUsers() {
        mainViewModel.getFullData().observe(MainActivity.this, new Observer<List<Entity>>() {
            @Override
            public void onChanged(final List<Entity> entities) {
                getTextFromEditTextToSearch(entities);
            }
        });
    }


    private void getTextFromEditTextToSearch(final List<Entity> entities) {
        adapter.submitList(entities);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                for (Entity e : entities) {

                    //     Log.d("phonenn", String.valueOf(e.getPhone_number().compareTo(Long.valueOf(s.toString()))==0));
                    if (e.getName().toLowerCase().contains(s.toString().toLowerCase()) ||
                            e.getPhone_number().toString().contains(s.toString())
                    ) {
                        Log.d("searchdd", s.toString());
                        searchList.add(e);

                    }

                }
                adapter.submitList(searchList);
                searchList.clear();
            }
        });
    }

    public static class Rebootreceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("hello", "yes1");
            if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {


                Log.d("helloinboot", "yes");
                Calendar alaramcalender = Calendar.getInstance();
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

                alaramcalender.setTimeInMillis(System.currentTimeMillis());
                alaramcalender.set(Calendar.HOUR_OF_DAY, 24);

                Intent intent1 = new Intent(context, Receiver.class);
                intent1.putExtra("hello", "bootc");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 5, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                alaramcalender.set(Calendar.MINUTE, 0);


                SharedPreferences sharedPreferences = context.getSharedPreferences("alarams", MODE_PRIVATE);


                String string = new SimpleDateFormat("dd:MM:yyyy").format(Calendar.getInstance().getTimeInMillis());
                Log.d("hello", String.valueOf(sharedPreferences.contains(string)));
                Log.d("hello", String.valueOf(sharedPreferences.getAll()));
                if (!sharedPreferences.getBoolean(string, false)) {
                    Intent intent2 = new Intent(context, Receiver.class);
                    intent2.putExtra("hello", "boot");
                    context.sendBroadcast(intent2);
                }
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, alaramcalender.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }
}
