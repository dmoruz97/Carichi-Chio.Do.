package it.parrocchiadosson.sagra.carichichiodo.admin_fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Articolo;
import it.parrocchiadosson.sagra.carichichiodo.R;

public class ArticoloAdapter extends ArrayAdapter<Articolo> {

    AutoCompleteTextView new_categoria;
    ArrayList<Articolo> articoliList;
    Activity activity_sup;
    View view_sup;

    public ArticoloAdapter(Activity activity_sup, Context context, View view_sup, int resource, ArrayList<Articolo> articoliList) {
        super(context, resource, articoliList);
        this.activity_sup = activity_sup;
        this.view_sup = view_sup;
        this.articoliList = articoliList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.articoli_element, null);

        final Articolo articolo_selected = articoliList.get(position);

        // TextViews
        final TextView nome_articolo = v.findViewById(R.id.nome_articolo);
        final TextView unita_misura = v.findViewById(R.id.unita_misura);
        final TextView categoria = v.findViewById(R.id.categoria);
        nome_articolo.setText(articolo_selected.getNomeArticolo());
        unita_misura.setText(articolo_selected.getUnitaMisura());
        categoria.setText(articolo_selected.getCategoria());

        // Buttons
        Button edit_button = v.findViewById(R.id.edit_button);
        Button delete_button = v.findViewById(R.id.delete_button);

        // EDIT BUTTON
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View for custom AlertDialog
                final View mView = activity_sup.getLayoutInflater().inflate(R.layout.custom_dialog_articolo,null);

                // Set placeholder for the edit text box
                final EditText new_nome_articolo = mView.findViewById(R.id.new_nome_articolo);
                new_nome_articolo.setText(articolo_selected.getNomeArticolo());

                final EditText new_unita_misura = mView.findViewById(R.id.new_quantita);
                new_unita_misura.setText(articolo_selected.getUnitaMisura());

                final AutoCompleteTextView new_categoria = mView.findViewById(R.id.new_categoria);
                new PopulateListCategorie(activity_sup, getContext(), new_categoria, articolo_selected.getCategoria()).execute();

                AlertDialog update_dialog = new AlertDialog.Builder(getContext())
                        .setTitle("MODIFICA ARTICOLO")
                        .setView(mView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get the new values
                                EditText nome_temp = mView.findViewById(R.id.new_nome_articolo);
                                String new_nome_articolo_string = nome_temp.getText().toString();

                                EditText unita_temp = mView.findViewById(R.id.new_quantita);
                                String new_unita_misura_string = unita_temp.getText().toString();

                                AutoCompleteTextView categoria_temp = mView.findViewById(R.id.new_categoria);
                                String new_categoria_string = String.valueOf(categoria_temp.getText());

                                new UpdateArticolo(getContext(), new Articolo(new_nome_articolo_string, new_unita_misura_string, new_categoria_string)).execute();

                                ArticoliAdminFragment.LoadArticoliList reload_list = new ArticoliAdminFragment("").new LoadArticoliList(activity_sup, getContext(), view_sup);
                                reload_list.execute("");

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

                update_dialog.show();

                Button button_neg = update_dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                button_neg.setTextColor(Color.rgb(255, 170, 79));
                Button button_pos = update_dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button_pos.setTextColor(Color.rgb(255, 170, 79));
            }
        });

        // DELETE BUTTON
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog delete_dialog = new AlertDialog.Builder(getContext())
                        .setTitle("ELIMINA ARTICOLO")
                        .setMessage("Vuoi veramente eliminare l'articolo " + articolo_selected.getNomeArticolo() + "?")

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new DeleteArticolo(getContext(), articolo_selected).execute();

                                ArticoliAdminFragment.LoadArticoliList reload_list = new ArticoliAdminFragment("").new LoadArticoliList(activity_sup, getContext(), view_sup);
                                reload_list.execute("");

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("ANNULLA", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                delete_dialog.show();

                Button button_neg = delete_dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                button_neg.setTextColor(Color.rgb(255, 170, 79));
                Button button_pos = delete_dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button_pos.setTextColor(Color.rgb(255, 170, 79));
            }
        });

        return v;
    }

    // DELETE an articolo from DB
    private class DeleteArticolo extends AsyncTask<Void, Void, Void> {

        Context context;
        Articolo articolo;

        public DeleteArticolo(Context context, Articolo articolo){
            this.context = context;
            this.articolo = articolo;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, context.getResources().getString(R.string.DB_NAME)).build();
            db.carichiChiodoDAO().deleteArticolo(articolo);

            return null;
        }
    }

    // UPDATE an articolo in the DB
    private class UpdateArticolo extends AsyncTask<Void, Void, Void> {

        Context context;
        Articolo articolo;

        public UpdateArticolo(Context context, Articolo articolo){
            this.context = context;
            this.articolo = articolo;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, context.getResources().getString(R.string.DB_NAME)).build();
            db.carichiChiodoDAO().updateArticolo(articolo);

            return null;
        }
    }

    // Populate the list of Categorie in order to edit an Articolo
    private class PopulateListCategorie extends AsyncTask<Void, Void, Void> {

        Activity activity;
        Context context;
        View view;
        String oldCategoria;

        public PopulateListCategorie(Activity activity, Context context, View view, String oldCategoria){
            this.activity = activity;
            this.context = context;
            this.view = view;
            this.oldCategoria = oldCategoria;
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
                    int index = categorie_list.indexOf(oldCategoria);
                    new_categoria.setText(index != -1? categorie_list.get(index) : "");
                }
            });

            return null;
        }
    }
}
