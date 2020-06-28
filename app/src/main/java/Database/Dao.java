package Database;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mytestings.skylinebroadband.TransactionEntity;
import com.mytestings.skylinebroadband.TransactionEntityIdsModel;

import java.util.List;

@androidx.room.Dao
public interface Dao {
    @Insert
    public Long insert(Entity entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public int update(Entity entity);

    @Delete
    public void delete(Entity entity);

    @Query("SELECT *FROM Entity ")
    public LiveData<List<Entity>> getTotaldata();


    @Query("SELECT *FROM Entity ")
    public List<Entity> getTotaldataNoLiveData();

    @Query("SELECT *FROM Entity WHERE AmountDue > 0")
    public LiveData<List<Entity>> getTotaldataofDueUsers();



    @Query("SELECT *FROM Entity WHERE id=:id")
    public Entity getTotaldatafromId(Long id);

    @Query("SELECT *FROM TransactionEntity WHERE hnum=:hnum ")
    public LiveData<List<TransactionEntity>> getTotaldatafromtransaction(String hnum);

    @Insert
    public Long insertinTransaction(TransactionEntity transactionEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateTransaction(TransactionEntity transactionEntity);

    @Delete
    public void deleteTransaction(TransactionEntity transactionEntity);

    @Query("SELECT * FROM TransactionEntity WHERE datePaid=:datee")
    public LiveData<List<TransactionEntity>> getTodaysCollection(String datee);

    @Query("SELECT * FROM TransactionEntity WHERE datePaid BETWEEN :from AND :to")
    public LiveData<List<TransactionEntity>> getFromToCollection(String from , String to);

    @Query("SELECT * FROM Entity WHERE accountCreatedOn BETWEEN :from AND :to")
    public LiveData<List<Entity>> getFromToinstallation(String from , String to);

    @Query("SELECT transactionId FROM TransactionEntityIdsModel")
    public List<String> getTransactionidNoLiveData();

    @Insert
    public void insertTransactionid(TransactionEntityIdsModel transactionEntityIdsModel);

    @Update
    public void updateTransactionid(TransactionEntityIdsModel transactionEntityIdsModel);

    @Delete
    public void deleteTransactionid(TransactionEntityIdsModel transactionEntityIdsModel);


}
