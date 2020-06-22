package com.mytestings.skylinebroadband;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailycollectionslayout);
        recyclerView = findViewById(R.id.transactionrecyclerview);
        textView = findViewById(R.id.totalTodaysCollection);
        dailyCollectionAdapter = new DailyCollectionAdapter(textView);
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setAdapter(dailyCollectionAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        skyDatabase = SkyDatabase.getInstance(this);
        extra = getIntent().getStringExtra("extra");
        Log.d("extrass",extra);

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
                transactionEntityObserverdate = new Observer<List<TransactionEntity>>() {
                    @Override
                    public void onChanged(List<TransactionEntity> transactionEntities) {
                        Log.d("extrass", String.valueOf(transactionEntities.size()));
                        dailyCollectionAdapter.submitList(transactionEntities);
                    }
                };

                int day = getIntent().getIntExtra("day", -1);
                int month = getIntent().getIntExtra("month", -1);
                int year = getIntent().getIntExtra("year", -1);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                String today = new SimpleDateFormat("dd:MM:yyyy")
                        .format(calendar.getTimeInMillis());
                listLiveDatadate = skyDatabase.dao().getTodaysCollection(today);
                listLiveDatadate.observe(this, transactionEntityObserverdate);
            }


            if (extra.equals("today")) {
                transactionEntityObserverdaily = new Observer<List<TransactionEntity>>() {
                    @Override
                    public void onChanged(List<TransactionEntity> transactionEntities) {
                        Log.d("extrass", String.valueOf(transactionEntities.size()));
                        dailyCollectionAdapter.submitList(transactionEntities);
                    }
                };
                String today = new SimpleDateFormat("dd:MM:yyyy")
                        .format(Calendar.getInstance().getTimeInMillis());
                listLiveDatadaily = skyDatabase.dao().getTodaysCollection(today);
                listLiveDatadaily.observe(this, transactionEntityObserverdaily);
            }

        }
        super.onStart();
    }



    @Override
    protected void onStop() {
        if (extra.equals("total"))
            listLiveDataTotal.removeObserver(transactionEntityObservertotal);
        if (extra.equals("today"))
            listLiveDatadaily.removeObserver(transactionEntityObserverdaily);
        if (extra.equals("date"))
            listLiveDatadate.removeObserver(transactionEntityObserverdate);
        super.onStop();
    }
}
