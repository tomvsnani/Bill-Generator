package com.mytestings.skylinebroadband;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String hnum;
    private Long phoneNumber;
    private String datePaid;
    private int amountPaid;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(int amountPaid) {
        this.amountPaid = amountPaid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHnum() {
        return hnum;
    }

    public void setHnum(String hnum) {
        this.hnum = hnum;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(String datePaid) {
        this.datePaid = datePaid;
    }

    static DiffUtil.ItemCallback<TransactionEntity> itemCallback=new DiffUtil.ItemCallback<TransactionEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull TransactionEntity oldItem, @NonNull TransactionEntity newItem) {
           return oldItem.getId()==newItem.getId();

        }

        @Override
        public boolean areContentsTheSame(@NonNull TransactionEntity oldItem, @NonNull TransactionEntity newItem) {
            return oldItem.getId()==newItem.getId() && oldItem.getAmountPaid()==newItem.getAmountPaid()
                    && oldItem.getDatePaid().equals(newItem.getDatePaid()) && oldItem.getHnum().equals(newItem.getHnum())
                    && oldItem.getUsername().equals(newItem.getUsername());
        }
    };
}
