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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import Database.Entity;

public class DailyCollectionAdapter extends ListAdapter {
    TextView textView;
    int totalCollection = 0;
    List<TransactionEntity> entityList = new ArrayList<>();
    String type;
    String objType;

    public DailyCollectionAdapter(TextView textView, String type) {
        super(TransactionEntity.itemCallback);
        this.textView = textView;
        this.type = type;
    }


    @Override
    public void submitList(@Nullable List list) {
        Log.d("extrass", String.valueOf(list.size()) + "si");
        List list1= new ArrayList(list);
        super.submitList(list1);
        totalCollection=0;
        if (list1 != null && list1.size() > 0 && list1.get(0) instanceof TransactionEntity) {
            objType = "transaction";
            for (Object entity : list1) {
                totalCollection = totalCollection + ((TransactionEntity) (entity)).getAmountPaid();

            }
        } else {
            objType = "entity";
            for (Object entity : list1) {
                totalCollection = totalCollection + ((Entity) (entity)).getInstallationAmount();
                Log.d("installationamountt",list1.size()+"  "+ String.valueOf(((Entity) (entity)).getInstallationAmount()));
            }
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (objType.equals("transaction")) {
            TransactionEntity entity = (TransactionEntity) getCurrentList().get(position);

            ((DailyCollectionViewHolder) (holder)).amountPaid.setText(String.valueOf(entity.getAmountPaid()));


                ((DailyCollectionViewHolder) (holder)).paidby.setText(entity.getUsername() );

                ((DailyCollectionViewHolder) (holder)).date.setText(entity.getDatePaid());
        }


        if (objType.equals("entity")) {
            Entity entity = (Entity) getCurrentList().get(position);
            ((DailyCollectionViewHolder) (holder)).amountPaid.setText(String.valueOf(entity.getInstallationAmount()));


            ((DailyCollectionViewHolder) (holder)).paidby.setText(entity.getName() );

            ((DailyCollectionViewHolder) (holder)).date.setText(entity.getAccountCreatedOn());
        }
    }


    @Override
    public int getItemCount() {
        Log.d("dailyadapter", String.valueOf(getCurrentList().size()));
        return Math.max(getCurrentList().size(), 0);
    }

    class DailyCollectionViewHolder extends RecyclerView.ViewHolder {

        TextView paidby;
        TextView amountPaid;
        TextView date;


        DailyCollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            paidby = itemView.findViewById(R.id.paidbytext);
            amountPaid = itemView.findViewById(R.id.amounttext);
            date=itemView.findViewById(R.id.datetext);
        }
    }
}
