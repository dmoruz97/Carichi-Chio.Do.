package it.parrocchiadosson.sagra.carichichiodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.parrocchiadosson.sagra.carichichiodo.DB_description.Articolo;

public class ArticoliGridAdapter extends ArrayAdapter {

    ArrayList<Articolo> articoliList;

    public ArticoliGridAdapter(Context context, int textViewResourceId, ArrayList<Articolo> articoliList) {
        super(context, textViewResourceId, articoliList);
        this.articoliList = articoliList;
    }

    /*@Override
    public int getCount() {
        return super.getCount();
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.grid_view_items, null);

        TextView textView = v.findViewById(R.id.titolo_carico);
        ImageView imageView = v.findViewById(R.id.immagine_carico);

        textView.setText(articoliList.get(position).getNomeArticolo());

        // Immagine per articolo in base alla sua categoria
        switch (articoliList.get(position).getCategoria()){
            case "Bevande":
                imageView.setImageResource(R.drawable.bevande);
                break;
            case "Cibo":
                imageView.setImageResource(R.drawable.cibo);
                break;
            case "Verdure":
                imageView.setImageResource(R.drawable.verdure);
                break;
            case "Stoviglie":
                imageView.setImageResource(R.drawable.stoviglie);
                break;
            default: // "Altro"
                imageView.setImageResource(R.drawable.altro);
        }

        return v;
    }
}
