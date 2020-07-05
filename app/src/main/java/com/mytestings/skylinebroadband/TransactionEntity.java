package com.mytestings.skylinebroadband;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TransactionEntity {
    static DiffUtil.ItemCallback itemCallback = new DiffUtil.ItemCallback() {
        @Override
        public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            if (oldItem instanceof Database.Entity)
                return ((Database.Entity) (oldItem)).getId() == ((Database.Entity) (newItem)).getId();
            if (oldItem instanceof TransactionEntity)
                return ((TransactionEntity) (oldItem)).getId() == ((TransactionEntity) (newItem)).getId();
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            if (oldItem instanceof TransactionEntity)
                return ((TransactionEntity) (oldItem)).getId() == ((TransactionEntity) (newItem)).getId() && ((TransactionEntity) (oldItem)).getAmountPaid() == ((TransactionEntity) (newItem)).getAmountPaid()
                        && ((TransactionEntity) (oldItem)).getDatePaid().equals(((TransactionEntity) (newItem)).getDatePaid()) && ((TransactionEntity) (oldItem)).getHnum().equals(((TransactionEntity) (newItem)).getHnum())
                        && ((TransactionEntity) (oldItem)).getUsername().equals(((TransactionEntity) (newItem)).getUsername());
            if (oldItem instanceof Entity)
                return ((Database.Entity) (oldItem)).getInstallationAmount().equals(((Database.Entity) (newItem)).getInstallationAmount());
            return false;
        }
    };
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String hnum;
    private Long phoneNumber;
    private String datePaid;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    private int amountPaid;
    private String username;
    private String transactionId;

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
}

