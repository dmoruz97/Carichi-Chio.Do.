package it.parrocchiadosson.sagra.carichichiodo.DB_description;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "carichi")
public class Carico implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idCarico")
    public int idCarico;

    @ColumnInfo(name = "nomeArticolo")
    public String nomeArticolo;

    @ColumnInfo(name = "unitaMisura")
    public String unitaMisura;

    @ColumnInfo(name = "quantita")
    public int quantita;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "firma")
    public String firma;

    @ColumnInfo(name = "data")
    public String data;

    @ColumnInfo(name = "ora")
    public String ora;

    // Constructor
    public Carico(int idCarico, String nomeArticolo, String unitaMisura, int quantita, String note, String firma, String data, String ora) {
        this.idCarico = idCarico;
        this.nomeArticolo = nomeArticolo;
        this.unitaMisura = unitaMisura;
        this.quantita = quantita;
        this.note = note;
        this.firma = firma;
        this.data = data;
        this.ora = ora;
    }

    // Getters
    public int getId_carico() {
        return idCarico;
    }
    public String getNome_articolo() {
        return nomeArticolo;
    }
    public String getUnita_misura() {
        return unitaMisura;
    }
    public int getQuantita() {
        return quantita;
    }
    public String getNote() {
        return note;
    }
    public String getFirma() {
        return firma;
    }
    public String getData() {
        return data;
    }
    public String getOra() {
        return ora;
    }

    // Setters
    public void setId_carico(int id_carico) {
        this.idCarico = id_carico;
    }
    public void setNome_articolo(String nome_articolo) {
        this.nomeArticolo = nome_articolo;
    }
    public void setUnita_misura(String unita_misura) {
        this.unitaMisura = unita_misura;
    }
    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setFirma(String firma) {
        this.firma = firma;
    }
    public void setData(String data) {
        this.data = data;
    }
    public void setOra(String ora) {
        this.ora = ora;
    }

    @Override
    public String toString() {
        return "Carico{" +
                "id_carico=" + idCarico +
                ", nome_articolo='" + nomeArticolo + '\'' +
                ", unita_misura='" + unitaMisura + '\'' +
                ", quantita=" + quantita +
                ", note='" + note + '\'' +
                ", firma='" + firma + '\'' +
                ", data='" + data + '\'' +
                ", ora='" + ora + '\'' +
                '}';
    }
}
