package br.edu.ifsuldeminas.mch.apppomodoro.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.edu.ifsuldeminas.mch.apppomodoro.data.entities.CicloEntity;

@Dao
public interface CicloDao {
    
    @Insert
    long inserir(CicloEntity ciclo);
    
    @Update
    void atualizar(CicloEntity ciclo);
    
    @Delete
    void deletar(CicloEntity ciclo);
    
    @Query("SELECT * FROM ciclos WHERE usuario_id = :usuarioId ORDER BY data_hora DESC")
    LiveData<List<CicloEntity>> getAllCiclos(String usuarioId);
    
    @Query("SELECT * FROM ciclos WHERE id = :id")
    LiveData<CicloEntity> getCicloById(int id);
    
    @Query("SELECT COUNT(*) FROM ciclos WHERE usuario_id = :usuarioId")
    LiveData<Integer> getTotalCiclos(String usuarioId);
    
    @Query("SELECT COUNT(*) FROM ciclos WHERE usuario_id = :usuarioId AND date(data_hora/1000, 'unixepoch') = date('now')")
    LiveData<Integer> getCiclosHoje(String usuarioId);
    
    @Query("SELECT COUNT(*) FROM ciclos WHERE usuario_id = :usuarioId AND date(data_hora/1000, 'unixepoch') >= date('now', '-7 days')")
    LiveData<Integer> getCiclosSemana(String usuarioId);
    
    @Query("SELECT SUM(duracao) FROM ciclos WHERE usuario_id = :usuarioId")
    LiveData<Integer> getTempoTotalEstudo(String usuarioId);
    
    @Query("SELECT disciplina_id, COUNT(*) as count FROM ciclos WHERE usuario_id = :usuarioId GROUP BY disciplina_id ORDER BY count DESC")
    LiveData<List<DisciplinaCount>> getEstatisticasPorDisciplina(String usuarioId);
    
    @Query("DELETE FROM ciclos WHERE usuario_id = :usuarioId")
    void limparHistorico(String usuarioId);
    
    class DisciplinaCount {
        public String disciplina_id;
        public int count;
    }
}
