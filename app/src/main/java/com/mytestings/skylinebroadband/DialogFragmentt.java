package com.mytestings.skylinebroadband;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Database.SkyDatabase;

public class DialogFragmentt extends AppCompatActivity {
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    TransactionRowAdapter transactionRowAdapter;
    MainViewModel mainViewModel;

    static DialogFragmentt getInstance(String param1, Long param2) {
        Bundle bundle = new Bundle();
        DialogFragmentt dialogFragmentt = new DialogFragmentt();
        bundle.putString("param1", param1);
        bundle.putLong("param2", param2);

        return dialogFragmentt;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.transactionlayout);

       recyclerView = findViewById(R.id.transactionrecyclerview);
        linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        transactionRowAdapter = new TransactionRowAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(transactionRowAdapter);

    }

    @Override
    public void onStart() {

        SkyDatabase.getInstance(this).dao()
                .getTotaldatafromtransaction(getIntent().getStringExtra("param1"))
                .observe(this,new Observer<List<TransactionEntity>>() {
            @Override
            public void onChanged(List<TransactionEntity> transactionEntities) {
                if (transactionEntities.size() > 0) {

                    transactionRowAdapter.submitList(transactionEntities);
                    Log.d("inhere", String.valueOf(transactionEntities.size()));
                }



            }
        });




        super.onStart();
    }

}