package com.mytestings.skylinebroadband;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import Database.Entity;

public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Entity>> fullListData;
    private List<Entity> fetchOnlyListData;
    private LiveData<List<Entity>> fetchOnlyListDataWithDue;
    private LiveData<List<TransactionEntity>> getallTransactionData;
    private Repository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository(application.getApplicationContext());
        fullListData = repository.getFullDataFromRepository();
        fetchOnlyListData = repository.getonlyListData();
        fetchOnlyListDataWithDue = repository.getFetchOnlyListDataWithDue();


    }
    public LiveData<List<TransactionEntity>> getFullTransactionData(String house,Long phn){
        getallTransactionData=repository.getGetallTransactiondata(house,phn);
        return getallTransactionData;
    }



    public void setVlueinFirebase(Entity entity,String s) {
        repository.setValueinFirebase(entity,s);

    }

    public void insertinTransactionEntity(final TransactionEntity transactionEntity) {
        repository.insertinTransactionEntity(transactionEntity);

    }



    public void updateTransactionEntity(final TransactionEntity transactionEntity) {
        repository.updateTransactionEntity(transactionEntity);

    }

    public void insert(final Entity entity) {
        repository.insert(entity);

    }

    public Long getEntityid() {
        return repository.getEntityId();
    }

    public void update(final Entity entity) {
        repository.update(entity);

    }

    public void delete(final Entity entity) {
        repository.delete(entity);
    }

    public List<Entity> getonlyListData() {
        return fetchOnlyListData;
    }

    public LiveData<List<Entity>> getFetchOnlyListDataWithDue() {
        return fetchOnlyListDataWithDue;
    }

    public LiveData<List<Entity>> getFullData() {
        return fullListData;
    }
}
