package it.parrocchiadosson.sagra.carichichiodo.main_fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.AddCarico;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Articolo;
import it.parrocchiadosson.sagra.carichichiodo.ArticoliGridAdapter;
import it.parrocchiadosson.sagra.carichichiodo.R;

public class CarichiFragment extends Fragment {

    public CarichiFragment() {
        // Required empty public constructor
    }

    public static CarichiFragment newInstance() {
        return new CarichiFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carichi, container, false);

        new LoadGridAsync(getActivity(), getContext(), view).execute();

        return view;
    }

    // Load the grid with the values contained in the DB
    private class LoadGridAsync extends AsyncTask<Void, Void, Void> {

        Activity activity;
        Context context;
        View view;

        public LoadGridAsync(Activity activity, Context context, View view){
            this.activity = activity;
            this.context = context;
            this.view = view;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final GridView gridView = this.view.findViewById(R.id.grid_articoli);

            String DB_NAME = context.getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
            ArrayList<Articolo> articoliList = (ArrayList<Articolo>) db.carichiChiodoDAO().getAllArticoli();

            // SEE https://abhiandroid.com/ui/gridview
            final ArticoliGridAdapter articoliGridAdapter = new ArticoliGridAdapter(context, R.layout.grid_view_items, articoliList);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gridView.setAdapter(articoliGridAdapter);
                }
            });

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Articolo selectedArticolo = (Articolo) parent.getItemAtPosition(position);

                    // open new activity to perform the associated CARICO to the selected articolo
                    Intent intent = new Intent(context, AddCarico.class);
                    intent.putExtra("nomeArticolo", selectedArticolo.getNomeArticolo());
                    intent.putExtra("unitaMisura", selectedArticolo.getUnitaMisura());
                    intent.putExtra("categoria", selectedArticolo.getCategoria());
                    activity.startActivity(intent);
                    //activity.finish();
                }
            });

            return null;
        }
    }
}
