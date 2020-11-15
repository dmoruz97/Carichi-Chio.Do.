package it.parrocchiadosson.sagra.carichichiodo.DB_description;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Articolo.class, Carico.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CarichiChiodoDAO carichiChiodoDAO();

    // https://www.simplifiedcoding.net/android-room-database-example/
}
