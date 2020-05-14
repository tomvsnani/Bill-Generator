package Database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mytestings.skylinebroadband.TransactionEntity;

@androidx.room.Database(entities = {Entity.class, TransactionEntity.class}, version = 1, exportSchema = false)
public abstract class SkyDatabase extends RoomDatabase {
    private static final Object object = new Object();
    private static SkyDatabase instance;

    public static SkyDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (object) {

                instance = Room.databaseBuilder(context.getApplicationContext(), SkyDatabase.class, "chat")
                        .allowMainThreadQueries().fallbackToDestructiveMigration().build();
            }
        }
        return instance;
    }

    public abstract Dao dao();
}

