package com.mytestings.skylinebroadband;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Database.Entity;
import Database.SkyDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements DatesetInterface {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Adapter adapter;
    FloatingActionButton floatingActionButton;
    MainViewModel mainViewModel;
    int count = 0;

    Toolbar toolbar;
    AppCompatRadioButton totalusersRadioButton;
    AppCompatRadioButton dueUsersRadioButton;
    RadioGroup radioGroup;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView lastFiredTextView;

    DatabaseReference transactionReference;
    String fromdate;
    String todate;
    EditText to;
    EditText from;
    AlertDialog alertDialog;
    LiveData<List<String>> transactionEntityListNoLiveData;
    Boolean childlisteneradded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler);
        floatingActionButton = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar);
        totalusersRadioButton = findViewById(R.id.totalUsers);
        dueUsersRadioButton = findViewById(R.id.userswithDue);
        radioGroup = findViewById(R.id.radioGroup);
        setSupportActionBar(toolbar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        transactionReference = FirebaseDatabase.getInstance().getReference("transactions");
        databaseReference = firebaseDatabase.getReference("Users");
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                RecyclerView.VERTICAL, false);
        adapter = new Adapter(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        lastFiredTextView = findViewById(R.id.lastFired);
        String lastFired = getSharedPreferences("alarams", MODE_PRIVATE).getString("lastFired",
                "0");
        if (!lastFired.equals("0"))
            lastFiredTextView.setText("Dues last updated on :  " + lastFired);
        else
            lastFiredTextView.setText("not yet updated today");
//        FirebaseDatabase.getInstance().getReference().child("last").setValue(lastFired);
        databaseReference.keepSynced(true);
        //  transactionReference.keepSynced(true);
        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (settings.getBoolean("my_first_time", true)) {
            createAlarm();

        }
        settings.edit().putBoolean("my_first_time", false).apply();

        // transactionReference.keepSynced(true);
        retrieveFirebaseEntityData();


        transactionReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                List<String> testlivedata = SkyDatabase.getInstance(MainActivity.this).dao().getTransactionidNoLiveData();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().keepSynced(true);

                    // dataSnapshot.getRef().keepSynced(true);
                    TransactionEntity transactionEntity = dataSnapshot1.getValue(TransactionEntity.class);
                    Log.d("updatedduly", "id  " + testlivedata + "  " + transactionEntity.getTransactionId());
                    Log.d("updatedduly", "check  " + testlivedata.contains(transactionEntity.getTransactionId()));

                    if (!testlivedata.contains(transactionEntity.getTransactionId())) {
                        TransactionEntityIdsModel transactionEntityIdsModel = new TransactionEntityIdsModel();
                        transactionEntityIdsModel.setTransactionId(transactionEntity.getTransactionId());
                        SkyDatabase.getInstance(MainActivity.this).dao().insertTransactionid(transactionEntityIdsModel);

                        mainViewModel.insertinTransactionEntity(transactionEntity);
                    }
                    childlisteneradded = true;
                }
            }


            @Override
            public void onChildChanged(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {


                List<String> testlivedata = SkyDatabase.getInstance(MainActivity.this).dao().getTransactionidNoLiveData();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    dataSnapshot1.getRef().keepSynced(true);

                    // dataSnapshot.getRef().keepSynced(true);
                    TransactionEntity transactionEntity = dataSnapshot1.getValue(TransactionEntity.class);
                    Log.d("updatedduly", "id  " + testlivedata + "  " + transactionEntity.getTransactionId());
                    Log.d("updatedduly", "check  " + testlivedata.contains(transactionEntity.getTransactionId()));

                    if (!testlivedata.contains(transactionEntity.getTransactionId())) {
                        TransactionEntityIdsModel transactionEntityIdsModel = new TransactionEntityIdsModel();
                        transactionEntityIdsModel.setTransactionId(transactionEntity.getTransactionId());
                        SkyDatabase.getInstance(MainActivity.this).dao().insertTransactionid(transactionEntityIdsModel);

                        mainViewModel.insertinTransactionEntity(transactionEntity);
                    }
                    childlisteneradded = true;
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


//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://localhost:3000");
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl("http://localhost:3000/")
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//                    RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
//                    Call<List<Entity>> listCall = retrofitInterface.listRepos();
//                    listCall.enqueue(new Callback<List<Entity>>() {
//                        @Override
//                        public void onResponse(Call<List<Entity>> call, Response<List<Entity>> response) {
//                            Log.d("iddddheresize", String.valueOf(response.body().size()));
//
//                            for (Entity entity : response.body()) {
//                                try {
//                                    Thread.sleep(300);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                Log.d("iddddherecount", String.valueOf(++count));
//                                mainViewModel.insert(entity);
//                                Long id = mainViewModel.getEntityid();
//                                Log.d("iddddhere3", String.valueOf(id));
//                                entity.setId(id);
//
//                                if (id != null) {
//                                    mainViewModel.setVlueinFirebase(entity, "insert");
//
//                                }
//                            }
//                            //  adapter.submitList(response.body());
//                        }
//
//                        @Override
//                        public void onFailure(Call<List<Entity>> call, Throwable t) {
//                            Log.d("responsesi", String.valueOf(t.getMessage()));
//                        }
//                    });
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        thread.start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        Log.d("query", "tt");
        MenuItem menuItem = menu.findItem(R.id.searchview);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("query", newText);
                adapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.todaycollection) {


            Intent intent = new Intent(this, DisplayTransactionsActivity.class);
            intent.putExtra("extra", "today");
            startActivity(intent);

            return true;
        }


        if (item.getItemId() == R.id.searchbydatemenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.fromtodateresource, null);
            builder.setView(v);
            from = v.findViewById(R.id.fromdateedittext);
            to = v.findViewById(R.id.todateedittext);
            builder.create();
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    fromdate = null;
                    todate = null;
                }
            });
            from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        new DatePicker(MainActivity.this).show(getSupportFragmentManager(), "collectionsFrom");

                }
            });
            to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    new DatePicker(MainActivity.this).show(getSupportFragmentManager(), "collectionsTo");


                }
            });
          alertDialog=  builder.create();
          alertDialog.show();

            return true;
        }


        if (item.getItemId() == R.id.installationcharges) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.fromtodateresource, null);
            builder.setView(v);
            from = v.findViewById(R.id.fromdateedittext);
            to = v.findViewById(R.id.todateedittext);
            builder.create();
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    fromdate = null;
                    todate = null;
                }
            });
            from.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        new DatePicker(MainActivity.this).show(getSupportFragmentManager(), "installationFrom");

                }
            });
            to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    new DatePicker(MainActivity.this).show(getSupportFragmentManager(), "installationTo");


                }
            });
            alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return false;
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
//
//                    mainViewModel.getFetchOnlyListDataWithDue().observe(MainActivity.this, new Observer<List<Entity>>() {
//                        @Override
//                        public void onChanged(List<Entity> entities) {
//                            getTextFromEditTextToSearch(entities);
//                        }
//                    });
                    databaseReference.orderByChild("amountDue").addChildEventListener(new ChildEventListener() {
                        List<Entity> list = new ArrayList<>();

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.d("lissize", "k");

                            if (dataSnapshot.getChildrenCount() == 0)
                                adapter.submit(list);
                            Entity entity = dataSnapshot.getValue(Entity.class);
                            Log.d("lissize", "k" + list.size());
                            if (entity.getAmountDue() > 0) {
                                list.add(entity);

                            }
                            adapter.submit(list);


                            //   mainViewModel.insert(entity);

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Entity dbEntity = dataSnapshot.getValue(Entity.class);
                            List<Entity> list = new ArrayList<>(adapter.getCurrentList());
                            List<Entity> list1 = new ArrayList<>();
                            int index = -1;

                            for (Entity entity : list) {
                                if (entity.getId().compareTo(dbEntity.getId()) == 0) {

                                    index = list.indexOf(entity);
                                    Log.d("found", String.valueOf(index));
                                }
                            }
                            if (index >= 0) {
                                Log.d("foundd", String.valueOf(index));
                                list.remove(index);
                                list.add(index, dbEntity);
                            }
                            adapter.submit(list);
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            Entity dbEntity = dataSnapshot.getValue(Entity.class);
                            List<Entity> list = new ArrayList<>(adapter.getCurrentList());
                            List<Entity> list1 = new ArrayList<>();
                            int index = -1;

                            for (Entity entity : list) {
                                if (entity.getId().compareTo(dbEntity.getId()) == 0) {

                                    index = list.indexOf(entity);
                                    Log.d("found", String.valueOf(index));
                                }
                            }
                            if (index >= 0) {
                                Log.d("foundd", String.valueOf(index));
                                list.remove(index);

                            }
                            adapter.submit(list);
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }


    private void populateWithTotalUsers() {
//        mainViewModel.getFullData().observe(MainActivity.this, new Observer<List<Entity>>() {
//            @Override
//            public void onChanged(final List<Entity> entities) {
//                getTextFromEditTextToSearch(entities);
//            }
//        });
        retrieveFirebaseEntityData();
    }


    @Override
    public void dateset(int year, int month, int dayofmonth, String tag) {
        Log.d("taggg", tag);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayofmonth);
        if (tag.equals("collectionsFrom") || tag.equals("installationFrom")) {
            fromdate = new SimpleDateFormat("dd-MM-yyyy")
                    .format(calendar.getTimeInMillis());
            from.setText(fromdate);
        }
        if (tag.equals("collectionsTo") || tag.equals("installationTo")) {
            todate = new SimpleDateFormat("dd-MM-yyyy")
                    .format(calendar.getTimeInMillis());
            to.setText(todate);
        }

        if (fromdate != null && !fromdate.isEmpty() && todate != null && !todate.isEmpty()) {
            if (alertDialog != null) {
                alertDialog.cancel();
                alertDialog.dismiss();
            }
            Intent intent = new Intent(this, DisplayTransactionsActivity.class);
            intent.putExtra("extra", "date");
            intent.putExtra("day", dayofmonth);
            intent.putExtra("month", month);
            intent.putExtra("year", year);
            intent.putExtra("from", fromdate);
            intent.putExtra("to", todate);
            intent.putExtra("tag", tag);
            startActivity(intent);

        }
    }

    public void retrieveFirebaseEntityData() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            List<Entity> list = new ArrayList<>();

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                Log.d("entityiddd", String.valueOf(dataSnapshot.getValue()));
                Entity entity = dataSnapshot.getValue(Entity.class);
                list.add(entity);
                adapter.submit(list);
                //   mainViewModel.insert(entity);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Entity dbEntity = dataSnapshot.getValue(Entity.class);
                List<Entity> list = new ArrayList<>(adapter.getCurrentList());
                List<Entity> list1 = new ArrayList<>();
                int index = -1;

                for (Entity entity : list) {
                    if (entity.getId().compareTo(dbEntity.getId()) == 0) {

                        index = list.indexOf(entity);
                        Log.d("found", String.valueOf(index));
                    }
                }
                if (index >= 0) {
                    Log.d("foundd", String.valueOf(index));
                    list.remove(index);
                    list.add(index, dbEntity);
                }
                adapter.submit(list);

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Entity dbEntity = dataSnapshot.getValue(Entity.class);
                List<Entity> list = new ArrayList<>(adapter.getCurrentList());
                List<Entity> list1 = new ArrayList<>();
                int index = -1;

                for (Entity entity : list) {
                    if (entity.getId().compareTo(dbEntity.getId()) == 0) {

                        index = list.indexOf(entity);
                        Log.d("found", String.valueOf(index));
                    }
                }
                if (index >= 0) {
                    Log.d("foundd", String.valueOf(index));
                    list.remove(index);

                }
                adapter.submit(list);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        final String string = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTimeInMillis());
        final SharedPreferences sharedPreferences = getSharedPreferences("alarams", MODE_PRIVATE);


        FirebaseDatabase.getInstance().getReference().child("last").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastfired= (String) snapshot.getValue();

                if(lastfired!=null && lastfired.length()>0) {
                    Log.d("getting",lastfired);
                    sharedPreferences.edit().putString("lastFired", lastfired).apply();
                }
                else

                    sharedPreferences.edit().putString("lastFired", string).apply();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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


                String string = new SimpleDateFormat("dd/MM/yyyy")
                        .format(Calendar.getInstance().getTimeInMillis());
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
