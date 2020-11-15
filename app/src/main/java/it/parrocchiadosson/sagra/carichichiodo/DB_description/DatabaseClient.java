package it.parrocchiadosson.sagra.carichichiodo.DB_description;

import android.content.Context;

import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.R;

public class DatabaseClient {

    private Context context;
    private static DatabaseClient myInstance;
    private final String DB_NAME = this.context.getResources().getString(R.string.DB_NAME);

    // AppDatabase object
    private AppDatabase appDatabase;

    private DatabaseClient(Context context){
        this.context = context;

        //creating the app database with Room database builder
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context){
        if (myInstance == null){
            myInstance = new DatabaseClient(context);
        }
        return myInstance;
    }

    public AppDatabase getAppDatabase(){
        return appDatabase;
    }
}
