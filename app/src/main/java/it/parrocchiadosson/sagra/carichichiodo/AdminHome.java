package it.parrocchiadosson.sagra.carichichiodo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import it.parrocchiadosson.sagra.carichichiodo.admin_fragments.ArticoliAdminFragment;
import it.parrocchiadosson.sagra.carichichiodo.admin_fragments.CarichiAdminFragment;

public class AdminHome extends FragmentActivity {

    TabLayout tab_layout;
    LinearLayout layout_search;
    SearchView search_bar;
    SearchView search_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);

        layout_search = findViewById(R.id.layout_search);

        search_date = findViewById(R.id.search_date);
        search_date.setVisibility(View.GONE);
        search_date.setQueryHint("Data");
        search_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(AdminHome.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                search_date.setQuery(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year, false);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        search_date.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String data) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = new CarichiAdminFragment(search_bar.getQuery().toString(), data);
                ft.replace(R.id.frame_layout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
                return false;
            }
        });

        search_bar = findViewById(R.id.search_bar);
        search_bar.setQueryHint("Cerca un articolo");
        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (tab_layout.getSelectedTabPosition() == 0) { // TAB articoli
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = new ArticoliAdminFragment(newText);
                    ft.replace(R.id.frame_layout, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
                else {  // TAB carichi
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Fragment fragment = new CarichiAdminFragment(newText, search_date.getQuery().toString());
                    ft.replace(R.id.frame_layout, fragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
                return false;
            }
        });

        tab_layout = findViewById(R.id.tab_layout);
        //tab_layout.setTabGravity(TabLayout.GRAVITY_FILL);
        //tab_layout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tab_layout.selectTab(tab_layout.getTabAt(0));

        // Call tab "ARTICOLI" to initialize the view
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new ArticoliAdminFragment("");
        ft.replace(R.id.frame_layout, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;

                switch (tab.getPosition()){
                    case 0:
                        search_bar.setQueryHint("Cerca un articolo");
                        search_date.setVisibility(View.GONE);
                        search_bar.setMaxWidth(Integer.MAX_VALUE);

                        fragment = new ArticoliAdminFragment(search_bar.getQuery().toString());
                        break;
                    case 1:
                        search_bar.setQueryHint("Cerca un carico");
                        search_date.setVisibility(View.VISIBLE);
                        search_date.setMaxWidth(layout_search.getWidth()/2);
                        search_bar.setMaxWidth(layout_search.getWidth()/2);

                        fragment = new CarichiAdminFragment(search_bar.getQuery().toString(), search_date.getQuery().toString());
                        break;
                }

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.frame_layout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setType(Settings.ACTION_SYNC_SETTINGS);
        AdminHome.this.startActivity(intent);
        AdminHome.this.finish();
    }
}
