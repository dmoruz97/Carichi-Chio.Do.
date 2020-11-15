package it.parrocchiadosson.sagra.carichichiodo.admin_fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Carico;
import it.parrocchiadosson.sagra.carichichiodo.R;

public class CaricoAdapter extends ArrayAdapter<Carico> {

    ArrayList<Carico> carichiList;
    Activity activity_sup;
    View view_sup;

    EditText new_data;
    EditText new_ora;

    public CaricoAdapter(Activity activity_sup, Context context, View view_sup, int resource, ArrayList<Carico> carichiList) {
        super(context, resource, carichiList);
        this.activity_sup = activity_sup;
        this.view_sup = view_sup;
        this.carichiList = carichiList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.carichi_element, null);

        final Carico carico_selected = carichiList.get(position);

        // TextViews
        final TextView nome_articolo = v.findViewById(R.id.nome_articolo);
        final TextView quantita = v.findViewById(R.id.quantita);
        final TextView unita_misura = v.findViewById(R.id.unita_misura);
        final TextView data = v.findViewById(R.id.data);
        final TextView ora = v.findViewById(R.id.ora);
        final TextView firma = v.findViewById(R.id.firma);
        final TextView note = v.findViewById(R.id.note);

        nome_articolo.setText(carico_selected.getNome_articolo());
        quantita.setText(String.valueOf(carico_selected.getQuantita()));
        unita_misura.setText(carico_selected.getUnita_misura());
        data.setText("Giorno: " + carico_selected.getData());
        ora.setText("Ora: " + carico_selected.getOra());
        firma.setText(carico_selected.getFirma());

        if (carico_selected.getNote().equals("")){
            ViewGroup parentView = (ViewGroup) note.getParent();
            parentView.removeView(note);
        }
        else {
            note.setText("Note: " + carico_selected.getNote());
        }

        // Buttons
        Button edit_button = v.findViewById(R.id.edit_button);
        Button delete_button = v.findViewById(R.id.delete_button);

        // EDIT BUTTON
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View for custom AlertDialog
                final View mView = activity_sup.getLayoutInflater().inflate(R.layout.custom_dialog_carico,null);

                // Set placeholder for the edit text box
                final Spinner new_nome_articolo = mView.findViewById(R.id.new_nome_articolo);
                new CaricoAdapter.PopulateListArticoli(getContext(), new_nome_articolo, carico_selected.getNome_articolo()).execute();
                new_nome_articolo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == -1) {
                            Toast.makeText(getContext(), "Nessun articolo selezionato!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            TextView label_unita_misura = mView.findViewById(R.id.label_unita_misura);

                            String nome_articolo = new_nome_articolo.getSelectedItem().toString();
                            label_unita_misura.setText(nome_articolo);
                            new CaricoAdapter.SetNewUnitaMisura(activity_sup, getContext(), nome_articolo, label_unita_misura).execute();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                final EditText new_quantita = mView.findViewById(R.id.new_quantita);
                new_quantita.setText(String.valueOf(carico_selected.getQuantita()));

                TextView label_unita_misura = mView.findViewById(R.id.label_unita_misura);
                label_unita_misura.setText(carico_selected.getUnita_misura());

                new_data = mView.findViewById(R.id.new_data);
                new_data.setInputType(InputType.TYPE_NULL);
                new_data.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar cldr = Calendar.getInstance();
                        int day = cldr.get(Calendar.DAY_OF_MONTH);
                        int month = cldr.get(Calendar.MONTH);
                        int year = cldr.get(Calendar.YEAR);
                        // date picker dialog
                        DatePickerDialog picker = new DatePickerDialog(getContext(),
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                        new_data.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    }
                                }, year, month, day);
                        picker.show();
                    }
                });
                new_data.setText(carico_selected.getData());

                new_ora = mView.findViewById(R.id.new_ora);
                new_ora.setInputType(InputType.TYPE_NULL);
                new_ora.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar cldr = Calendar.getInstance();
                        int hour = cldr.get(Calendar.HOUR_OF_DAY);
                        int minutes = cldr.get(Calendar.MINUTE);
                        // time picker dialog
                        // 1 -> spinner mode (but better android.R.style.Theme_Holo_Light_Dialog_NoActionBar)
                        // 2 -> clock mode
                        TimePickerDialog picker = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                        new_ora.setText(sHour + ":" + sMinute);
                                    }
                                }, hour, minutes, true);
                        picker.show();
                    }
                });
                new_ora.setText(carico_selected.getOra());

                final EditText new_firma = mView.findViewById(R.id.new_firma);
                new_firma.setText(carico_selected.getFirma());

                final EditText new_note = mView.findViewById(R.id.new_note);
                new_note.setText(carico_selected.getNote());

                AlertDialog update_dialog = new AlertDialog.Builder(getContext())
                        .setTitle("MODIFICA CARICO")
                        .setView(mView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get the new values
                                Spinner nomeArticolo_temp = mView.findViewById(R.id.new_nome_articolo);
                                String new_nome_articolo_string = String.valueOf(nomeArticolo_temp.getSelectedItem());

                                EditText quantita_temp = mView.findViewById(R.id.new_quantita);
                                int new_quantita_string = Integer.parseInt(quantita_temp.getText().toString());

                                TextView unita_misura_temp = mView.findViewById(R.id.label_unita_misura);
                                String new_unita_misura_string = unita_misura_temp.getText().toString();

                                EditText data_temp = mView.findViewById(R.id.new_data);
                                String new_data_string = data_temp.getText().toString();

                                EditText ora_temp = mView.findViewById(R.id.new_ora);
                                String new_ora_string = ora_temp.getText().toString();

                                EditText firma_temp = mView.findViewById(R.id.new_firma);
                                String new_firma_string = firma_temp.getText().toString();

                                EditText note_temp = mView.findViewById(R.id.new_note);
                                String new_note_string = note_temp.getText().toString();

                                if (!new_firma_string.equals("") && new_quantita_string > 0){
                                    new UpdateCarico(getContext(), new Carico(carico_selected.getId_carico(), new_nome_articolo_string, new_unita_misura_string, new_quantita_string, new_note_string, new_firma_string, new_data_string, new_ora_string)).execute();

                                    CarichiAdminFragment.LoadCarichiList reload_list = new CarichiAdminFragment("", "").new LoadCarichiList(activity_sup, getContext(), view_sup);
                                    reload_list.execute("");

                                    dialog.dismiss();
                                }
                                else {
                                    Toast.makeText(getContext(), "Controlla i campi inseriti!", Toast.LENGTH_SHORT).show();
                                }
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
                        .setTitle("ELIMINA CARICO")
                        .setMessage("Vuoi veramente eliminare il carico di " + carico_selected.getNome_articolo() + "?")

                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new DeleteCarico(getContext(), carico_selected).execute();

                                CarichiAdminFragment.LoadCarichiList reload_list = new CarichiAdminFragment("", "").new LoadCarichiList(activity_sup, getContext(), view_sup);
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

    // DELETE a carico from DB
    private class DeleteCarico extends AsyncTask<Void, Void, Void> {

        Context context;
        Carico carico;

        public DeleteCarico(Context context, Carico carico){
            this.context = context;
            this.carico = carico;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, context.getResources().getString(R.string.DB_NAME)).build();
            db.carichiChiodoDAO().deleteCarico(carico);

            return null;
        }
    }

    // UPDATE a carico in the DB
    private class UpdateCarico extends AsyncTask<Void, Void, Void> {

        Context context;
        Carico carico;

        public UpdateCarico(Context context, Carico carico){
            this.context = context;
            this.carico = carico;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, context.getResources().getString(R.string.DB_NAME)).build();
            db.carichiChiodoDAO().updateCarico(carico);

            return null;
        }
    }

    // Populate the list of Articoli in order to edit a Carico
    private static class PopulateListArticoli extends AsyncTask<Void, Void, Void> {

        Context context;
        Spinner new_articolo;
        String oldArticolo;

        public PopulateListArticoli(Context context, Spinner new_articolo, String oldArticolo){
            this.context = context;
            this.new_articolo = new_articolo;
            this.oldArticolo = oldArticolo;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, context.getResources().getString(R.string.DB_NAME)).build();
            ArrayList<String> articoli_list = (ArrayList<String>) db.carichiChiodoDAO().getAllNomeArticoli();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, articoli_list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.new_articolo.setAdapter(adapter);
            int index = articoli_list.indexOf(oldArticolo);
            this.new_articolo.setSelection(index != -1? index : 0);

            return null;
        }
    }

    private static class SetNewUnitaMisura extends AsyncTask<Void, Void, Void> {

        Context context;
        String current_articolo;
        TextView label_unita_misura;
        Activity activity;

        public SetNewUnitaMisura(Activity activity, Context context, String current_articolo, TextView label_unita_misura){
            this.activity = activity;
            this.context = context;
            this.current_articolo = current_articolo;
            this.label_unita_misura = label_unita_misura;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, context.getResources().getString(R.string.DB_NAME)).build();
            final String unita_misura = db.carichiChiodoDAO().getUnitaMisuraByArticolo(current_articolo);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    label_unita_misura.setText(unita_misura);
                }
            });

            return null;
        }
    }
}
