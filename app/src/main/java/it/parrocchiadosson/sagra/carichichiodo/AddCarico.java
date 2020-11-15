package it.parrocchiadosson.sagra.carichichiodo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Articolo;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Carico;

public class AddCarico extends Activity {

    TextView giorno_corrente;

    TextView nome_articolo_view;
    TextView quantita_view;
    TextView unita_misura_view;
    TextView note_view;
    TextView firma_view;
    Button add_carico_button;

    SimpleDateFormat dataFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    SimpleDateFormat dataFormatter_2 = new SimpleDateFormat("dd LLLL yyyy", Locale.getDefault());
    SimpleDateFormat oraFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_carico);

        giorno_corrente = findViewById(R.id.giorno_corrente);
        giorno_corrente.setText(dataFormatter_2.format(new Date()));

        nome_articolo_view = findViewById(R.id.nome_articolo);
        quantita_view = findViewById(R.id.quantita);
        unita_misura_view = findViewById(R.id.unita_misura);
        note_view = findViewById(R.id.note);
        firma_view = findViewById(R.id.firma);
        add_carico_button = findViewById(R.id.confirm_carico_button);

        final ColorStateList default_hint_color = firma_view.getHintTextColors();

        this.initializeView(findViewById(android.R.id.content));    // "content" is the ID of the root of the view (now, ConstraintLayout)

        // On click button listener
        add_carico_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome_articolo = String.valueOf(nome_articolo_view.getText());
                int quantita = Integer.parseInt(String.valueOf(quantita_view.getText()));
                String unita_misura = String.valueOf(unita_misura_view.getText());
                String note = String.valueOf(note_view.getText());
                String firma = String.valueOf(firma_view.getText());

                //String data = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                //String ora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                String data = dataFormatter.format(new Date());
                String ora = oraFormatter.format(new Date());

                if (!firma.equals("") && quantita > 0){

                    try {
                        Date actual_data = dataFormatter.parse(data);

                        Date actual_ora = oraFormatter.parse(ora);
                        Date limit_ora_midnigth = oraFormatter.parse("00:00");
                        Date limit_ora_two = oraFormatter.parse("02:00");

                        // Se l'orario di inserimento del carico è dopo mezzanotte (e prima delle 2 di mattina)...
                        if (actual_ora.after(limit_ora_midnigth) && actual_ora.before(limit_ora_two)){
                            // il carico riguardo il giorno precedente con orario "> 00:00"
                            GregorianCalendar gc = new GregorianCalendar();
                            gc.setTime(actual_data);
                            gc.add(Calendar.DATE, -1);

                            data = dataFormatter.format(gc.getTime());
                            //ora = "> 00:00"; // per indicare che il carico è stato effettuato dopo la mezzanotte
                        }
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Carico carico = new Carico(0, nome_articolo, unita_misura, quantita, note, firma, data, ora);
                    new SaveCaricoAsync(getApplicationContext(), carico).execute();
                }
                else {
                    if (quantita <= 0){
                        quantita_view.setTextColor(Color.rgb(255,0,0));
                        quantita_view.setHintTextColor(Color.rgb(255,0,0));
                    }
                    else {
                        quantita_view.setTextColor(Color.rgb(0,0,0));
                        quantita_view.setHintTextColor(default_hint_color);
                    }

                    if (firma.equals("")){
                        firma_view.setTextColor(Color.rgb(255,0,0));
                        firma_view.setHintTextColor(Color.rgb(255,0,0));
                    }
                    else {
                        firma_view.setTextColor(Color.rgb(0,0,0));
                        firma_view.setHintTextColor(default_hint_color);
                    }

                    Toast toast = Toast.makeText(getApplicationContext(),"Controlla i campi inseriti!",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_VERTICAL,0,-100);
                    toast.show();
                }
            }
        });
    }

    private void initializeView(View view){
        Intent intent = getIntent();

        nome_articolo_view.setText(intent.getStringExtra("nomeArticolo"));
        quantita_view.setText("1");
        unita_misura_view.setText(intent.getStringExtra("unitaMisura"));
    }

    // Insert into DB a new Carico
    private class SaveCaricoAsync extends AsyncTask<Void, Void, Void> {
        Context context;
        Carico carico;

        public SaveCaricoAsync(Context context, Carico carico) {
            this.context = context;
            this.carico = carico;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String DB_NAME = context.getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();

            db.carichiChiodoDAO().insertCarico(carico);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context, "Carico inserito", Toast.LENGTH_LONG).show();

            // Return to main activity
            Intent intent = new Intent(AddCarico.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
