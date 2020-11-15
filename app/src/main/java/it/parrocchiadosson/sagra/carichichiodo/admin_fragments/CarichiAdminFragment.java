package it.parrocchiadosson.sagra.carichichiodo.admin_fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Articolo;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Carico;
import it.parrocchiadosson.sagra.carichichiodo.R;

public class CarichiAdminFragment extends Fragment {

    ListView list_view;
    String query;
    String data;
    //FloatingActionButton fab;

    public CarichiAdminFragment(String query, String data){
        this.query = query;
        this.data = data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carichi_admin, container, false);

        // Load list of carichi
        new LoadCarichiList(getActivity(), getContext(), view).execute(this.query, this.data);

        // Load ADD button -> UN NUOVO CARICO PUO' ESSERE ESEGUITO DAL TAB "Carichi" NELLA HOME DELLA APPLICAZIONE
        /*fab = view.findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        return view;
    }

    // Load the carichi list with the value contained in the DB
    public class LoadCarichiList extends AsyncTask<String, Void, Void> {

        Activity activity;
        Context context;
        View view;

        public LoadCarichiList(Activity activity, Context context, View view){
            this.activity = activity;
            this.context = context;
            this.view = view;
        }

        @Override
        protected Void doInBackground(String... query_nomeCarico_data) {
            String DB_NAME = context.getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();

            ArrayList<Carico> carichiList;
            if (query_nomeCarico_data[0].equals("")) {
                if (query_nomeCarico_data[1].equals("")){
                    carichiList = (ArrayList<Carico>) db.carichiChiodoDAO().getAllCarichi();
                }
                else {
                    carichiList = (ArrayList<Carico>) db.carichiChiodoDAO().getAllCarichiData(query_nomeCarico_data[1] + "%");
                }
            }
            else {
                if (query_nomeCarico_data[1].equals("")){
                    carichiList = (ArrayList<Carico>) db.carichiChiodoDAO().getAllCarichiNomeCarico(query_nomeCarico_data[0] + "%");
                }
                else {
                    carichiList = (ArrayList<Carico>) db.carichiChiodoDAO().getAllCarichi(query_nomeCarico_data[0] + "%", query_nomeCarico_data[1] + "%");
                }
            }

            list_view = view.findViewById(R.id.list_view);
            final CaricoAdapter myAdapter = new CaricoAdapter(activity, context, view, R.layout.articoli_element, carichiList);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list_view.setAdapter(myAdapter);
                }
            });

            return null;
        }
    }
}
