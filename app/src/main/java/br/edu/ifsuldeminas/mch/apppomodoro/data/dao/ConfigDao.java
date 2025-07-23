package br.edu.ifsuldeminas.mch.apppomodoro.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import br.edu.ifsuldeminas.mch.apppomodoro.data.entities.ConfigEntity;

@Dao
public interface ConfigDao {
    
    @Insert
    void inserir(ConfigEntity config);
    
    @Update
    void atualizar(ConfigEntity config);
    
    @Query("SELECT * FROM configuracoes WHERE usuario_id = :usuarioId LIMIT 1")
    LiveData<ConfigEntity> getConfig(String usuarioId);
    
    @Query("SELECT * FROM configuracoes WHERE usuario_id = :usuarioId LIMIT 1")
    ConfigEntity getConfigSync(String usuarioId);
    
    @Query("DELETE FROM configuracoes WHERE usuario_id = :usuarioId")
    void limparConfig(String usuarioId);
}
