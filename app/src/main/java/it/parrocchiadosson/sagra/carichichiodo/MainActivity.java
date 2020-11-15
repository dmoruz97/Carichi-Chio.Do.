package it.parrocchiadosson.sagra.carichichiodo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Articolo;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.DatabaseClient;
import it.parrocchiadosson.sagra.carichichiodo.main_fragments.AdminFragment;
import it.parrocchiadosson.sagra.carichichiodo.main_fragments.CarichiFragment;
import it.parrocchiadosson.sagra.carichichiodo.main_fragments.StatsFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("mySharedPreferences", MODE_PRIVATE);

        if (preferences.getBoolean("firstLogin", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstLogin", false);
            editor.commit();

            new PopulateDBAsync(getApplicationContext()).execute();
        }

        bottomNavigation = findViewById(R.id.bottom_navigation);
        //bottomNavigation.setItemIconSize(20);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        // default Fragment
        openFragment(CarichiFragment.newInstance());
    }

    // Populate DB if it is the first launch of the application
    private static class PopulateDBAsync extends AsyncTask<Void, Void, Void> {

        Context context;

        public PopulateDBAsync(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, context.getResources().getString(R.string.DB_NAME)).build();

            db.clearAllTables();
            db.carichiChiodoDAO().insertArticolo(new Articolo("Birra bionda", "Fusti", "Bevande"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Birra rossa", "Fusti", "Bevande"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Birra speciale", "Fusti", "Bevande"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Pastin", "Confezioni", "Cibo"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Wurstel", "Confezioni", "Cibo"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Pane", "Sacchetti", "Cibo"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Peperoni", "Cassette", "Verdure"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Cipolle", "Cassette", "Verdure"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Zucchine", "Cassette", "Verdure"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Scottex", "Rotoloni", "Altro"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Guanti", "Scatole", "Altro"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Piatto piano", "Confezioni", "Stoviglie"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Piatto fondo", "Confezioni", "Stoviglie"));
            db.carichiChiodoDAO().insertArticolo(new Articolo("Piatto grande", "Confezioni", "Stoviglie"));

            return null;
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Listener on tab component
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_carichi:
                            openFragment(CarichiFragment.newInstance());
                            return true;
                        case R.id.navigation_admin:
                            openFragment(AdminFragment.newInstance());
                            return true;
                        case R.id.navigation_stats:
                            openFragment(StatsFragment.newInstance());
                            return true;
                    }
                    return false;
                }
            };
}
