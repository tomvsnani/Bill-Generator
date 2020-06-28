package com.mytestings.skylinebroadband;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import Database.Entity;
import Database.SkyDatabase;

class Repository {
    private FirebaseDatabase firebaseDatabase;
    private SkyDatabase skyDatabase;
    private DatabaseReference databaseReference;
    private LiveData<List<Entity>> fullListData;
    private List<Entity> fetchOnlyListData;
    private LiveData<List<Entity>> fetchOnlyListDataWithDue;
    private Long entityId;
    private LiveData<List<TransactionEntity>> getallTransactiondata;
    private Executor executor;

    Repository(Context context) {
        skyDatabase = SkyDatabase.getInstance(context);
        fullListData = skyDatabase.dao().getTotaldata();
        fetchOnlyListData = skyDatabase.dao().getTotaldataNoLiveData();
        fetchOnlyListDataWithDue = skyDatabase.dao().getTotaldataofDueUsers();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        executor= Executors.newSingleThreadExecutor();


    }

    public LiveData<List<TransactionEntity>> getGetallTransactiondata(String house,Long phn) {
        getallTransactiondata = skyDatabase.dao().getTotaldatafromtransaction(house);
        return getallTransactiondata;
    }


    public void insertinTransactionEntity(final TransactionEntity transactionEntity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                skyDatabase.dao().insertinTransaction(transactionEntity);
            }
        });


    }



    public void updateTransactionEntity(final TransactionEntity transactionEntity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                skyDatabase.dao().updateTransaction(transactionEntity);
            }
        });


    }

    public void setValueinFirebase(Entity entity, String s) {
        if (s.equals("insert")) {
            String key = databaseReference.push().getKey();
            entity.setFirebaseReferenceKey(key);
            databaseReference.child(key).setValue(entity);
            update(entity);
            Log.d("iddddhere5", String.valueOf(entity.getId()));

        } else {


            String key = entity.getFirebaseReferenceKey();
            databaseReference.child(key).setValue(entity);
        }
    }



    public Entity getKeyFRomId(Long entityId) {
        return skyDatabase.dao().getTotaldatafromId(entityId);
    }

    LiveData<List<Entity>> getFullDataFromRepository() {
        return fullListData;
    }

    void insert(final Entity entity) {
        Log.d("iddddhere1", String.valueOf(entity.getId()));
       executor.execute(new Runnable() {
           @Override
           public void run() {
               entityId = skyDatabase.dao().insert(entity);
               Log.d("iddddhere2", String.valueOf(entityId));
             setEntityId(entityId);
           }
       });


    }

    public Long getEntityId() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    void update(final Entity entity) {
       executor.execute(new Runnable() {
           @Override
           public void run() {
            int i=   skyDatabase.dao().update(entity);
               Log.d("iddddhere4", String.valueOf(i));
           }
       });

    }

    void delete(final Entity entity) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                skyDatabase.dao().delete(entity);
            }
        });
    }

    List<Entity> getonlyListData() {
        return fetchOnlyListData;
    }

    LiveData<List<Entity>> getFetchOnlyListDataWithDue() {
        return fetchOnlyListDataWithDue;
    }
}

