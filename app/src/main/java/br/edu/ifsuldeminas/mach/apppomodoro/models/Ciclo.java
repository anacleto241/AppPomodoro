package br.edu.ifsuldeminas.mach.apppomodoro.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Ciclo {
    private int id;
    private String disciplinaId;
    private String disciplinaNome; // Para exibição
    private String descricao;
    private int duracao;
    private long dataHora;
    private String usuarioId;

    public Ciclo() {}

    public Ciclo(int id, String disciplinaId, String descricao, int duracao, long dataHora) {
        this.id = id;
        this.disciplinaId = disciplinaId;
        this.descricao = descricao;
        this.duracao = duracao;
        this.dataHora = dataHora;
    }
    
    public Ciclo(String disciplinaId, String descricao, int duracao, long dataHora, String usuarioId) {
        this.disciplinaId = disciplinaId;
        this.descricao = descricao;
        this.duracao = duracao;
        this.dataHora = dataHora;
        this.usuarioId = usuarioId;
    }

    // Getters
    public int getId() { return id; }
    public String getDisciplinaId() { return disciplinaId; }
    public String getDisciplinaNome() { return disciplinaNome; }
    public String getDescricao() { return descricao; }
    public int getDuracao() { return duracao; }
    public long getDataHora() { return dataHora; }
    public String getUsuarioId() { return usuarioId; }
    
    // Método para obter data formatada
    public String getData() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(dataHora));
    }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setDisciplinaId(String disciplinaId) { this.disciplinaId = disciplinaId; }
    public void setDisciplinaNome(String disciplinaNome) { this.disciplinaNome = disciplinaNome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setDuracao(int duracao) { this.duracao = duracao; }
    public void setDataHora(long dataHora) { this.dataHora = dataHora; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
}