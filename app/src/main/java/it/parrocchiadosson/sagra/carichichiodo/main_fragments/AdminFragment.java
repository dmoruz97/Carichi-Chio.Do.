package it.parrocchiadosson.sagra.carichichiodo.main_fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.AdminHome;
import it.parrocchiadosson.sagra.carichichiodo.CSVWriter;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.MainActivity;
import it.parrocchiadosson.sagra.carichichiodo.R;

public class AdminFragment extends Fragment {

    Button button_download_csv;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 100;  // arbitrary value

    TextView admin_password;
    Button button_submit_password;
    final String admin_pwd = "admin";

    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance() {
        return new AdminFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        button_download_csv = view.findViewById(R.id.button_download_csv);
        admin_password = view.findViewById(R.id.admin_password);
        button_submit_password = view.findViewById(R.id.button_submit_password);

        admin_password.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                // Evento generato alla pressione del tasto "DONE" della tastiera
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String password = String.valueOf(admin_password.getText());

                    if (password.equals(admin_pwd)){
                        Intent intent = new Intent(getContext(), AdminHome.class);
                        intent.setType(Settings.ACTION_SYNC_SETTINGS);
                        AdminFragment.this.startActivity(intent);
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Password errata!");
                        builder.setTitle("Admin");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                    handled = true;
                }
                return handled;
            }
        });

        button_submit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = String.valueOf(admin_password.getText());

                if (password.equals(admin_pwd)){
                    Intent intent = new Intent(getContext(), AdminHome.class);
                    intent.setType(Settings.ACTION_SYNC_SETTINGS);
                    AdminFragment.this.startActivity(intent);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Password errata!");
                    builder.setTitle("Admin");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        button_download_csv.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                // Check if the permission is granted...
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    File exportDir = new File(Environment.getExternalStorageDirectory(), "");
                    if (!exportDir.exists()) { exportDir.mkdirs(); }

                    File file = new File(exportDir, "Carichi_ChioDo.csv");
                    try {
                        file.createNewFile();
                        CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

                        new DownloadCSV(csvWrite).execute();
                    }
                    catch (Exception sqlEx) {
                        Log.e("CSV download", sqlEx.getMessage(), sqlEx);
                    }
                }
                // If the permission is not granted, ask for user to granting it
                else  {
                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                }
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Write External Storage Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Write External Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadCSV extends AsyncTask<Void, Void, Void>{
        CSVWriter csvWrite;

        public DownloadCSV(CSVWriter csvWrite){
            this.csvWrite = csvWrite;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String DB_NAME = getContext().getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, DB_NAME).build();

            Cursor curCSV = db.carichiChiodoDAO().getAllCarichiTable();
            csvWrite.writeNext(curCSV.getColumnNames());

            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = new String[curCSV.getColumnCount()];
                for (int i = 0; i < curCSV.getColumnCount(); i++)
                    arrStr[i] = curCSV.getString(i);
                csvWrite.writeNext(arrStr);
            }
            try {
                csvWrite.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            curCSV.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(), "CSV exportato!", Toast.LENGTH_SHORT).show();
        }
    }
}
