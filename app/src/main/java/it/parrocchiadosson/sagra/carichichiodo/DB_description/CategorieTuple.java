package it.parrocchiadosson.sagra.carichichiodo.DB_description;

import androidx.room.ColumnInfo;

public class CategorieTuple {

    @ColumnInfo(name = "categoria")
    public String categoria;

    @ColumnInfo(name = "percentuale")
    public int percentuale;

    public CategorieTuple(String categoria, int percentuale){
        this.categoria = categoria;
        this.percentuale = percentuale;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public void setPercentuale(int percentuale) {
        this.percentuale = percentuale;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getPercentuale() {
        return percentuale;
    }

    @Override
    public String toString() {
        return "CategorieTuple{" +
                "categoria='" + categoria + '\'' +
                ", percentuale=" + percentuale +
                '}';
    }
}
