package it.parrocchiadosson.sagra.carichichiodo.main_fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import androidx.fragment.app.Fragment;
import androidx.room.Room;
import it.parrocchiadosson.sagra.carichichiodo.AddCarico;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.AppDatabase;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.Carico;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.CategorieTuple;
import it.parrocchiadosson.sagra.carichichiodo.DB_description.NumCarichiTuple;
import it.parrocchiadosson.sagra.carichichiodo.MainActivity;
import it.parrocchiadosson.sagra.carichichiodo.R;
import kotlin.collections.IndexedValue;

public class StatsFragment extends Fragment {

    PieChart categorie_chart;
    LineChart numCarichi_chart;
    TextView label_numArticoli;
    TextView label_numCarichi;

    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance() {
        return new StatsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        label_numArticoli = view.findViewById(R.id.label_numArticoli);
        new SetLabelNumArticoli(getContext()).execute();

        label_numCarichi = view.findViewById(R.id.label_numCarichi);
        new SetLabelNumCarichi(getContext()).execute();

        categorie_chart = (PieChart) view.findViewById(R.id.categorie_chart);
        new GeneratePercentualiCategorieChart(getContext()).execute();

        numCarichi_chart = (LineChart) view.findViewById(R.id.numCarichi_chart);
        new GenerateNumCarichiChart(getContext()).execute();


        return view;
    }

    /***************************/
    /*** SetLabelNumArticoli ***/
    /***************************/
    private class SetLabelNumArticoli extends AsyncTask<Void, Void, Integer>{
        Context context;

        public SetLabelNumArticoli(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            String DB_NAME = getContext().getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, DB_NAME).build();

            Integer numArticoli = db.carichiChiodoDAO().getNumeroArticoli();

            return numArticoli;
        }

        @Override
        protected void onPostExecute(Integer numArticoli) {
            super.onPostExecute(numArticoli);
            label_numArticoli.setText("Articoli inseriti: " + numArticoli);
        }
    }

    /**************************/
    /*** SetLabelNumCarichi ***/
    /**************************/
    private class SetLabelNumCarichi extends AsyncTask<Void, Void, Integer>{
        Context context;

        public SetLabelNumCarichi(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            String DB_NAME = getContext().getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, DB_NAME).build();

            Integer numCarichi = db.carichiChiodoDAO().getNumeroCarichi();

            return numCarichi;
        }

        @Override
        protected void onPostExecute(Integer numCarichi) {
            super.onPostExecute(numCarichi);
            label_numCarichi.setText("Carichi inseriti: " + numCarichi);
        }
    }

    /*****************************************/
    /*** GeneratePercentualiCategorieChart ***/
    /*****************************************/
    private class GeneratePercentualiCategorieChart extends AsyncTask<Void, Void, List<CategorieTuple>>{

        Context context;

        public GeneratePercentualiCategorieChart(Context context) {
            this.context = context;
        }

        @Override
        protected List<CategorieTuple> doInBackground(Void... voids) {
            String DB_NAME = getContext().getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, DB_NAME).build();

            List<CategorieTuple> categorieTuples = db.carichiChiodoDAO().getPercentualeCategorie();

            return categorieTuples;
        }

        @Override
        protected void onPostExecute(List<CategorieTuple> categorieTuples) {
            super.onPostExecute(categorieTuples);
            setupCategorieChart(categorieTuples);
        }
    }

    private void setupCategorieChart(List<CategorieTuple> categorieTuples){

        if (!categorieTuples.isEmpty()) {
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            for (int i = 0; i < categorieTuples.size(); i++) {
                pieEntries.add(new PieEntry(categorieTuples.get(i).getPercentuale(), categorieTuples.get(i).getCategoria()));
            }

            PieDataSet dataSet = new PieDataSet(pieEntries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

            PieData data = new PieData(dataSet);
            data.setValueTextSize(25);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    //return String.format("%.02f", value) + "%";
                    return Math.round(value) + "%";
                }
            });

            // Customize the chart
            categorie_chart.setDrawEntryLabels(true);
            categorie_chart.setUsePercentValues(true);

            categorie_chart.setEntryLabelTextSize(20);
            categorie_chart.setDrawMarkers(true);
            //categorie_chart.setTransparentCircleRadius(20);
            categorie_chart.setHoleRadius(35);
            categorie_chart.setTransparentCircleRadius(0);
            //categorie_chart.setMaxHighlightDistance(34);

            //categorie_chart.setCenterText("% Categorie");
            //categorie_chart.setCenterTextSize(30);
            //categorie_chart.setCenterTextRadiusPercent(80);
            categorie_chart.setContentDescription("");
            categorie_chart.getDescription().setEnabled(false);

            categorie_chart.setData(data);
            categorie_chart.invalidate();

            // Legend attributes
            Legend legend = categorie_chart.getLegend();
            legend.setEnabled(false);
            /*legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setForm(Legend.LegendForm.CIRCLE);
            legend.setTextSize(12);
            legend.setFormSize(20);
            legend.setFormToTextSpace(2);*/
        }
        else {
            categorie_chart.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            categorie_chart.setNoDataText("Nessun dato da visualizzare");
            categorie_chart.setNoDataTextColor(Color.BLACK);
            Paint p = categorie_chart.getPaint(Chart.PAINT_INFO);
            p.setTextSize(50);
            categorie_chart.invalidate();
        }
    }

    /*******************************/
    /*** GenerateNumCarichiChart ***/
    /*******************************/
    private class GenerateNumCarichiChart extends AsyncTask<Void, Void, List<NumCarichiTuple>>{

        Context context;

        public GenerateNumCarichiChart(Context context) {
            this.context = context;
        }

        @Override
        protected List<NumCarichiTuple> doInBackground(Void... voids) {
            String DB_NAME = getContext().getResources().getString(R.string.DB_NAME);
            AppDatabase db = Room.databaseBuilder(getContext(), AppDatabase.class, DB_NAME).build();

            List<NumCarichiTuple> numCarichiTuples = db.carichiChiodoDAO().getNumCarichiPerDay();

            return numCarichiTuples;
        }

        @Override
        protected void onPostExecute(List<NumCarichiTuple> numCarichiTuples) {
            super.onPostExecute(numCarichiTuples);
            setupNumCarichiChart(numCarichiTuples);
        }
    }

    private void setupNumCarichiChart(List<NumCarichiTuple> numCarichiTuples){

        if (!numCarichiTuples.isEmpty()) {
            ArrayList<Entry> lineEntries = new ArrayList<>();
            for (int i=0; i<numCarichiTuples.size(); i++) {
                lineEntries.add(new Entry(i, numCarichiTuples.get(i).getNumCarichi()));
            }

            // Customize the dataset
            LineDataSet lineDataSet = new LineDataSet(lineEntries, "Carichi giornalieri");
            lineDataSet.setColor(Color.rgb(255, 170, 79));
            lineDataSet.setLineWidth(3);
            lineDataSet.setDrawCircles(true);
            lineDataSet.setCircleRadius(10);
            lineDataSet.setCircleColor(Color.rgb(255, 170, 79));
            lineDataSet.setCircleHoleRadius(4);
            lineDataSet.setCircleHoleColor(Color.WHITE);
            lineDataSet.setValueTextSize(16);
            lineDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return Math.round(value) + "";
                }
            });

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet);

            LineData data = new LineData(dataSets);

            // Customize the chart
            numCarichi_chart.setPinchZoom(false);
            numCarichi_chart.setTouchEnabled(false);
            numCarichi_chart.setDrawMarkers(true);
            numCarichi_chart.setContentDescription("");
            numCarichi_chart.getDescription().setEnabled(false);

            // Customize the axis
            YAxis yAxis_right = numCarichi_chart.getAxisRight();
            yAxis_right.setEnabled(false);

            YAxis yAxis_left = numCarichi_chart.getAxisLeft();
            yAxis_left.setGranularity(1);
            yAxis_left.setAxisMinimum(0);
            yAxis_left.setAxisMaximum(20);

            XAxis xAxis = numCarichi_chart.getXAxis();
            xAxis.setTextSize(16);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1);
            xAxis.setValueFormatter(new ValueFormatterLineChart(numCarichiTuples));

            numCarichi_chart.setData(data);
            numCarichi_chart.invalidate();

            // Legend attributes
            Legend legend = numCarichi_chart.getLegend();
            legend.setEnabled(false);
            /*legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
            legend.setTextSize(12);
            legend.setForm(Legend.LegendForm.LINE);
            legend.setFormSize(20);
            legend.setFormToTextSpace(2);*/
        }
        else {
            numCarichi_chart.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            numCarichi_chart.setNoDataText("Nessun dato da visualizzare");
            numCarichi_chart.setNoDataTextColor(Color.BLACK);
            Paint p = numCarichi_chart.getPaint(Chart.PAINT_INFO);
            p.setTextSize(50);
            numCarichi_chart.invalidate();
        }
    }

    private class ValueFormatterLineChart extends ValueFormatter {
        List<NumCarichiTuple> numCarichiTuples;

        public ValueFormatterLineChart(List<NumCarichiTuple> numCarichiTuples){
            this.numCarichiTuples = numCarichiTuples;
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if (value >= 0 && value < numCarichiTuples.size()) {
                String data_temp = numCarichiTuples.get((int) value).getData();
                return data_temp.substring(0, 5);
            }
            else {
                return "";
            }
        }
    }
}
