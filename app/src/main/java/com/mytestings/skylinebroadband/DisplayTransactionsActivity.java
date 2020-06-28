package com.mytestings.skylinebroadband;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Database.Entity;
import Database.SkyDatabase;

public class DisplayTransactionsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView textView;
    DailyCollectionAdapter dailyCollectionAdapter;
    LiveData<List<TransactionEntity>> listLiveData;
    SkyDatabase skyDatabase;
    LinearLayoutManager linearLayoutManager;
    String extra;
    Observer<List<TransactionEntity>> transactionEntityObservertotal;
    LiveData<List<TransactionEntity>> listLiveDataTotal;

    Observer<List<TransactionEntity>> transactionEntityObserverdaily;
    LiveData<List<TransactionEntity>> listLiveDatadaily;

    Observer<List<TransactionEntity>> transactionEntityObserverdate;
    LiveData<List<TransactionEntity>> listLiveDatadate;

    Observer<List<Entity>> transactionEntityObserverInstallationdate;
    LiveData<List<Entity>> listLiveDatainstallationDate;

    String from;
    String to;

    String tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailycollectionslayout);
        recyclerView = findViewById(R.id.transactionrecyclerview);
        textView = findViewById(R.id.totalTodaysCollection);
        extra = getIntent().getStringExtra("extra");
        dailyCollectionAdapter = new DailyCollectionAdapter(textView,extra);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(dailyCollectionAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        skyDatabase = SkyDatabase.getInstance(this);

        Log.d("extrass", extra);

    }

    @Override
    protected void onStart() {
        if (extra != null && !extra.isEmpty()) {


            if (extra.equals("total")) {
                transactionEntityObservertotal = new Observer<List<TransactionEntity>>() {
                    @Override
                    public void onChanged(List<TransactionEntity> transactionEntities) {
                        Log.d("extrass", String.valueOf(transactionEntities.size()));
                        dailyCollectionAdapter.submitList(transactionEntities);
                    }
                };
                String hnum = getIntent().getStringExtra("hnum");
                Long phn = getIntent().getLongExtra("phn", -1);
                listLiveDataTotal = skyDatabase.dao().
                        getTotaldatafromtransaction(hnum);
                listLiveDataTotal.observe(this, transactionEntityObservertotal);

            }





            if (extra.equals("date")) {
               tag= getIntent().getStringExtra("tag");
               from = getIntent().getStringExtra("from");
                to = getIntent().getStringExtra("to");
                Log.d("extrass",tag+"  "+ from+"  "+to);
                if(tag.equals("installationFrom") || tag.equals("installationTo")){

                  FirebaseDatabase.getInstance().getReference("Users").orderByChild("accountCreatedOn")
                          .addChildEventListener(new ChildEventListener() {
                              List<Entity> list=new ArrayList<>();
                      @Override
                      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                         Entity entity=dataSnapshot.getValue(Entity.class);
                         if(entity.getAccountCreatedOn().compareToIgnoreCase(from)>=0 && entity.getAccountCreatedOn().compareToIgnoreCase(to)<=0)
                         {
                             list.add(entity);
                            dailyCollectionAdapter.submitList(list);
                             Log.d("installok", String.valueOf(list.size()));
                         }
                      }

                      @Override
                      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

                      }
               if(tag.equals("collectionsFrom") || tag.equals("collectionsTo")) {

                    transactionEntityObserverdate = new Observer<List<TransactionEntity>>() {
                        @Override
                        public void onChanged(List<TransactionEntity> transactionEntities) {
                            dailyCollectionAdapter.submitList(transactionEntities);

                        }
                    };
                    listLiveDatadate = skyDatabase.dao().getFromToCollection(from, to);
                    listLiveDatadate.observe(this, transactionEntityObserverdate);

                }
            }






            if (extra.equals("today")) {
                transactionEntityObserverdaily = new Observer<List<TransactionEntity>>() {
                    @Override
                    public void onChanged(List<TransactionEntity> transactionEntities) {

                        dailyCollectionAdapter.submitList(transactionEntities);
                    }
                };
                String today = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTimeInMillis());
                listLiveDatadaily = skyDatabase.dao().getTodaysCollection(today);
                listLiveDatadaily.observe(this, transactionEntityObserverdaily);
            }

        }
        super.onStart();
    }





    @Override
    protected void onStop() {
        Log.d("taggg",tag+"  "+extra);
        if (extra.equals("total")) {
            listLiveDataTotal.removeObserver(transactionEntityObservertotal);
        }
        if (extra.equals("today")) {
            listLiveDatadaily.removeObserver(transactionEntityObserverdaily);
        }
//        if (extra.equals("date") &&( tag!=null&& ( tag.equals("installationFrom") || tag.equals("installationTo")))) {
//            listLiveDatainstallationDate.removeObserver(transactionEntityObserverInstallationdate);
//        }
        if(extra.equals("date") && (tag!=null&&(tag.equals("collectionsFrom") || tag.equals("collectionsTo")))) {
            listLiveDatadate.removeObserver(transactionEntityObserverdate);
        }
        super.onStop();
    }
}
