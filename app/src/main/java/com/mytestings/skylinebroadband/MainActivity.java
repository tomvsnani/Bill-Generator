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
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Calendar;
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
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
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
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Log.d("datasnapshotchild",dataSnapshot.toString());
                    Log.d("datasnapshotchild11",dataSnapshot.getChildren().toString());

                       Entity entity= (Entity) dataSnapshot.getValue(Entity.class);
                       mainViewModel.insert(entity);

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        Entity entity= (Entity) dataSnapshot1.getValue();
                        mainViewModel.update(entity);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            settings.edit().putBoolean("my_first_time", false).apply();
        }


    }

    private void createAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar alaramcalender = Calendar.getInstance();
        alaramcalender.set(Calendar.HOUR_OF_DAY, 12);
        Intent intent = new Intent(this, Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alaramcalender.set(Calendar.MINUTE, 0);
        alaramcalender.set(Calendar.AM_PM, Calendar.AM);
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

                    if (e.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        Log.d("searchdd", s.toString());
                        searchList.add(e);

                    }

                }
                adapter.submitList(searchList);
                searchList.clear();
            }
        });
    }
}
