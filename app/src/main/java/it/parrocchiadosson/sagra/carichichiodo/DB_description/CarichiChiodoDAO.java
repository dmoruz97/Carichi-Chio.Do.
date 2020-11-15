package it.parrocchiadosson.sagra.carichichiodo.DB_description;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CarichiChiodoDAO {

    /*@Transaction
        @Query("SELECT * FROM articoli")
        public List<ArticoliInCarichi> getUsersWithPlaylists();*/

    // QUERY
    @Query("SELECT * FROM articoli ORDER BY nomeArticolo, categoria")
    public List<Articolo> getAllArticoli();
    @Query("SELECT * FROM articoli WHERE nomeArticolo LIKE :query_nomeArticolo ORDER BY nomeArticolo, categoria")
    public List<Articolo> getAllArticoli(String query_nomeArticolo);

    @Query("SELECT * FROM carichi ORDER BY data DESC, ora DESC")
    public List<Carico> getAllCarichi();
    @Query("SELECT * FROM carichi WHERE nomeArticolo LIKE :query_nomeCarico ORDER BY nomeArticolo, data")
    public List<Carico> getAllCarichiNomeCarico(String query_nomeCarico);
    @Query("SELECT * FROM carichi WHERE data LIKE :query_data ORDER BY nomeArticolo, data")
    public List<Carico> getAllCarichiData(String query_data);
    @Query("SELECT * FROM carichi WHERE nomeArticolo LIKE :query_nomeCarico AND data LIKE :query_data ORDER BY nomeArticolo, data")
    public List<Carico> getAllCarichi(String query_nomeCarico, String query_data);

    @Query("SELECT * FROM articoli WHERE nomeArticolo= :nomeArticolo")
    public Articolo getArticoloByName(String nomeArticolo);
    @Query("SELECT * FROM carichi WHERE idCarico= :idCarico")
    public Carico getCaricoById(int idCarico);

    @Query("SELECT nomeArticolo FROM articoli ORDER BY nomeArticolo")
    public List<String> getAllNomeArticoli();

    @Query("SELECT DISTINCT categoria FROM articoli ORDER BY categoria")
    public List<String> getAllCategorieArticoli();

    @Query("SELECT unitaMisura FROM articoli WHERE nomeArticolo= :current_articolo")
    public String getUnitaMisuraByArticolo(String current_articolo);

    @Query("SELECT a.categoria, (cast(COUNT(*) AS float)/(SELECT COUNT(*) FROM carichi))*100 as percentuale  FROM articoli a, carichi c  WHERE a.nomeArticolo=c.nomeArticolo GROUP BY a.categoria ORDER BY a.categoria")
    public List<CategorieTuple> getPercentualeCategorie();

    @Query("SELECT data, COUNT(*) AS numCarichi FROM carichi GROUP BY data ORDER BY data")
    public List<NumCarichiTuple> getNumCarichiPerDay();

    @Query("SELECT COUNT(*) FROM articoli")
    public Integer getNumeroArticoli();

    @Query("SELECT COUNT(*) FROM carichi")
    public Integer getNumeroCarichi();

    @Query("SELECT * FROM carichi")
    Cursor getAllCarichiTable();

    // INSERT
    @Insert
    public void insertArticolo(Articolo articolo);
    @Insert
    public void insertCarico(Carico carico);

    // UPDATE
    @Update
    public void updateArticolo(Articolo articolo);
    @Update
    public void updateCarico(Carico carico);

    // DELETE
    @Delete
    public void deleteArticolo(Articolo articolo);
    @Delete
    public void deleteCarico(Carico carico);
}
