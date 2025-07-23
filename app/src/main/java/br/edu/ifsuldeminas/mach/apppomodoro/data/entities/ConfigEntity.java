package br.edu.ifsuldeminas.mach.apppomodoro.data.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "configuracoes")
public class ConfigEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    @ColumnInfo(name = "tempo_padrao")
    public int tempoPadrao; // em minutos, padrão 25
    
    @ColumnInfo(name = "tempo_pausa_curta")
    public int tempoPausaCurta; // em minutos, padrão 5
    
    @ColumnInfo(name = "tempo_pausa_longa")
    public int tempoPausaLonga; // em minutos, padrão 15
    
    @ColumnInfo(name = "tema_escuro")
    public boolean temaEscuro;
    
    @ColumnInfo(name = "notificacoes_ativas")
    public boolean notificacoesAtivas;
    
    @ColumnInfo(name = "usuario_id")
    public String usuarioId; // Firebase Auth UID
    
    public ConfigEntity() {
        this.tempoPadrao = 25;
        this.tempoPausaCurta = 5;
        this.tempoPausaLonga = 15;
        this.temaEscuro = false;
        this.notificacoesAtivas = true;
    }
}
