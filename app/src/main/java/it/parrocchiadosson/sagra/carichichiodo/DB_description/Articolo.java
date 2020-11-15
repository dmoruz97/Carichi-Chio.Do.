package it.parrocchiadosson.sagra.carichichiodo.DB_description;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "articoli")
public class Articolo implements Serializable {

    //@ColumnInfo(name = "idArticolo")
    //public int idArticolo;

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "nomeArticolo")
    @NonNull
    public String nomeArticolo;

    @ColumnInfo(name = "unitaMisura")
    public String unitaMisura;

    @ColumnInfo(name = "categoria")
    public String categoria;

    // Constructor
    public Articolo(String nomeArticolo, String unitaMisura, String categoria) {
        this.nomeArticolo = nomeArticolo;
        this.unitaMisura = unitaMisura;
        this.categoria = categoria;
    }

    @Ignore
    /*public Articolo(String nome_articolo, String unita_misura, String icon_path) {
        this.nome_articolo = nome_articolo;
        this.unita_misura = unita_misura;
        this.icon_path = icon_path;
    }*/

    // Getters
    @NonNull
    public String getNomeArticolo() { return nomeArticolo; }
    public String getUnitaMisura() { return unitaMisura; }
    public String getCategoria() { return categoria; }

    // Setters
    public void setNomeArticolo(@NonNull String nomeArticolo) { this.nomeArticolo = nomeArticolo; }
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }
    public void setIconPath(String iconPath) { this.categoria = categoria; }

    @Override
    public String toString() {
        return "Articolo{" +
                ", nomeArticolo='" + nomeArticolo + '\'' +
                ", unitaMisura='" + unitaMisura + '\'' +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
