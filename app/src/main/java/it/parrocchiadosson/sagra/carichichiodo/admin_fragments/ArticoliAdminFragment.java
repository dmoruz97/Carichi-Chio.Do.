package it.parrocchiadosson.sagra.carichichiodo.admin_fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.AdminHome;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Articolo;
import it.parrocchiadosson.sagra.carichichiodo.MainActivity;
import it.parrocchiadosson.sagra.carichichiodo.R;

public class ArticoliAdminFragment extends Fragment {

    AutoCompleteTextView new_categoria;
    ListView list_view;
    FloatingActionButton fab;
    String query;

    public ArticoliAdminFragment(String query){
        this.query = query;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_articoli_admin, container, false);

        // Load list of articoli
        new LoadArticoliList(getActivity(), getContext(), view).execute(this.query);

        // Load ADD button
        fab = view.findViewById(R.id.add_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View for custom AlertDialog (the same in ArticoloAdapter class but with different initialization)
                final View mView = getActivity().getLayoutInflater().inflate(R.layout.custom_dialog_articolo,null);

                // Set placeholder for the edit text box
                EditText nome_articolo = mView.findViewById(R.id.new_nome_articolo);
                nome_articolo.setHint("Nome articolo");

                EditText unita_misura = mView.findViewById(R.id.new_quantita);
                unita_misura.setHint("Unità di misura");

                new_categoria = mView.findViewById(R.id.new_categoria);
                new ArticoliAdminFragment.PopulateListCategorie(getActivity(), getContext(), mView).execute();

                AlertDialog add_dialog = new AlertDialog.Builder(getContext())
                        .setTitle("AGGIUNGI ARTICOLO")
                        .setView(mView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get the values
                                EditText nome_temp = mView.findViewById(R.id.new_nome_articolo);
                                String nome_articolo_string = nome_temp.getText().toString();

                                EditText unita_temp = mView.findViewById(R.id.new_quantita);
                                String unita_misura_string = unita_temp.getText().toString();

                                AutoCompleteTextView categoria_temp = mView.findViewById(R.id.new_categoria);
                                String categoria_string = String.valueOf(categoria_temp.getText());

                                new ArticoliAdminFragment.AddArticolo(getContext(), new Articolo(nome_articolo_string, unita_misura_string, categoria_string)).execute();

                                ArticoliAdminFragment.LoadArticoliList reload_list = new ArticoliAdminFragment(query).new LoadArticoliList(getActivity(), getContext(), getView());
                                reload_list.execute(query);

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                add_dialog.show();
            }
        });

        return view;
    }

    // Load the articoli list with the values contained in the DB
    public class LoadArticoliList extends AsyncTask<String, Void, Void> {

        Activity activity;
        Context context;
        View view;

        public LoadArticoliList(Activity activity, Context context, View view){
            this.activity = activity;
            this.context = context;
            this.view = view;
        }

        @Override
        protected Void doInBackground(String... query_nomeArticolo) {
            String DB_NAME = context.getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();

            ArrayList<Articolo> articoliList;

            if (query_nomeArticolo[0].equals("")) {
                articoliList = (ArrayList<Articolo>) db.carichiChiodoDAO().getAllArticoli();
            }
            else {
                articoliList = (ArrayList<Articolo>) db.carichiChiodoDAO().getAllArticoli(query_nomeArticolo[0] + "%");
            }

            list_view = view.findViewById(R.id.list_view);
            final ArticoloAdapter myAdapter = new ArticoloAdapter(activity, context, view, R.layout.articoli_element, articoliList);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    list_view.setAdapter(myAdapter);
                }
            });

            return null;
        }
    }

    // SEE https://camposha.info/android-listview-crud-add-update-delete/

    // Add an articolo to DB
    private class AddArticolo extends AsyncTask<Void, Void, Void> {

        Context context;
        Articolo articolo;

        public AddArticolo(Context context, Articolo articolo){
            this.context = context;
            this.articolo = articolo;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String DB_NAME = context.getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();

            db.carichiChiodoDAO().insertArticolo(articolo);

            return null;
        }
    }

    // Populate the list of Categorie in order to edit an Articolo
    private class PopulateListCategorie extends AsyncTask<Void, Void, Void> {

        Activity activity;
        Context context;
        View view;

        public PopulateListCategorie(Activity activity, Context context, View view){
            this.activity = activity;
            this.context = context;
            this.view = view;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, context.getResources().getString(R.string.DB_NAME)).build();
            final ArrayList<String> categorie_list = (ArrayList<String>) db.carichiChiodoDAO().getAllCategorieArticoli();

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, categorie_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            new_categoria = view.findViewById(R.id.new_categoria);
            new_categoria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View arg0) {
                    new_categoria.showDropDown();
                }
            });

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new_categoria.setAdapter(adapter);
                }
            });

            return null;
        }
    }
}
