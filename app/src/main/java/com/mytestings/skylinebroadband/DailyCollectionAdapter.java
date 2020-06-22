package com.mytestings.skylinebroadband;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DailyCollectionAdapter extends ListAdapter<TransactionEntity, DailyCollectionAdapter.DailyCollectionViewHolder> {
    TextView textView;
    int totalCollection = 0;
    List<TransactionEntity> entityList = new ArrayList<>();

    public DailyCollectionAdapter(TextView textView) {
        super(TransactionEntity.itemCallback);
        this.textView = textView;
    }


    @Override
    public void submitList(@Nullable List<TransactionEntity> list) {
        Log.d("extrass", String.valueOf(list.size())+"si");
        super.submitList(list==null?null:new ArrayList<TransactionEntity>(list));
        for (TransactionEntity entity:list){
            totalCollection=totalCollection+entity.getAmountPaid();

        }
        textView.setText(String.valueOf(totalCollection));
    }

    @NonNull
    @Override
    public DailyCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DailyCollectionViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.transactionrowlayout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DailyCollectionViewHolder holder, int position) {
        TransactionEntity entity = getCurrentList().get(position);
        Log.d("extrass", position+"po");
        ((DailyCollectionAdapter.DailyCollectionViewHolder) (holder)).amountPaid.setText(String.valueOf(entity.getAmountPaid()));
        ((DailyCollectionViewHolder) (holder)).paidby.setText(entity.getUsername());
    }


    @Override
    public int getItemCount() {
        Log.d("dailyadapter", String.valueOf(getCurrentList().size()));
        return Math.max(getCurrentList().size(), 0);
    }

    class DailyCollectionViewHolder extends RecyclerView.ViewHolder {

        TextView paidby;
        TextView amountPaid;


        DailyCollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            paidby = itemView.findViewById(R.id.date);
            amountPaid = itemView.findViewById(R.id.transaction);
        }
    }
}
