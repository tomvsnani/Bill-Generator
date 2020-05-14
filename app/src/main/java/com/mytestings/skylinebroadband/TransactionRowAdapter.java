package com.mytestings.skylinebroadband;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import Database.Entity;

public class TransactionRowAdapter extends ListAdapter {
    Entity entity;
    List<TransactionEntity> list;

    protected TransactionRowAdapter() {
        super(TransactionEntity.itemCallback);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionRowAdapter.viewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transactionrowlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TransactionEntity transactionEntity = (TransactionEntity) getCurrentList().get(position);



            Log.d("inhere", transactionEntity.getDatePaid());
            ((viewholder) (holder)).amountPaid.setText(String.valueOf(transactionEntity.getAmountPaid()));
            ((viewholder) (holder)).datePaid.setText(transactionEntity.getDatePaid());



    }


    @Override
    public void submitList(@Nullable List list) {
       this. list=list;
        Log.d("searchdd", String.valueOf(list.size()));

        super.submitList(list != null ? new ArrayList<String>(list) : null);
    }

    @Override
    public int getItemCount() {
        Log.d("inheresize",String.valueOf(getCurrentList().size()));
        return Math.max(getCurrentList().size(), 0);
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView datePaid;
        TextView amountPaid;


        viewholder(@NonNull View itemView) {
            super(itemView);
            datePaid = itemView.findViewById(R.id.date);
            amountPaid = itemView.findViewById(R.id.transaction);


        }
    }
}
