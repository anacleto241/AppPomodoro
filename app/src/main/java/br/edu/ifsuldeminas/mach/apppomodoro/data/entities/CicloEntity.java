package br.edu.ifsuldeminas.mach.apppomodoro.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;

@Entity(tableName = "ciclos")
public class CicloEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    @ColumnInfo(name = "disciplina_id")
    public String disciplinaId;
    
    @ColumnInfo(name = "descricao")
    public String descricao;
    
    @ColumnInfo(name = "duracao")
    public int duracao; // em minutos
    
    @ColumnInfo(name = "data_hora")
    public long dataHora; // timestamp
    
    @ColumnInfo(name = "usuario_id")
    public String usuarioId; // Firebase Auth UID
    
    public CicloEntity() {}
    
    @Ignore
    public CicloEntity(String disciplinaId, String descricao, int duracao, long dataHora, String usuarioId) {
        this.disciplinaId = disciplinaId;
        this.descricao = descricao;
        this.duracao = duracao;
        this.dataHora = dataHora;
        this.usuarioId = usuarioId;
    }
}
