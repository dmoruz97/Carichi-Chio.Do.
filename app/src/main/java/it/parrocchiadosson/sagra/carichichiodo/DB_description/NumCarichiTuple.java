package it.parrocchiadosson.sagra.carichichiodo.DB_description;

import androidx.room.ColumnInfo;

public class NumCarichiTuple {

    @ColumnInfo(name = "data")
    public String data;

    @ColumnInfo(name = "numCarichi")
    public int numCarichi;

    public NumCarichiTuple(String data, int numCarichi){
        this.data = data;
        this.numCarichi = numCarichi;
    }

    public void setData(String data) {
        this.data = data;
    }
    public void setNumCarichi(int numCarichi) {
        this.numCarichi = numCarichi;
    }

    public String getData() {
        return data;
    }
    public int getNumCarichi() {
        return numCarichi;
    }

    @Override
    public String toString() {
        return "NumCarichiTuple{" +
                "data=" + data +
                ", numCarichi='" + numCarichi + '\'' +
                '}';
    }
}
