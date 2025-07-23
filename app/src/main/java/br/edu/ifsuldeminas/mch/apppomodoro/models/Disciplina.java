package br.edu.ifsuldeminas.mch.apppomodoro.models;

import com.google.firebase.firestore.DocumentId;

public class Disciplina {
    @DocumentId
    private String id;
    private String nome;
    private String descricao;
    private String cor;
    private String icone;
    private String prioridade; // "Alta", "Média", "Baixa"
    private String usuarioId;
    
    public Disciplina() {}
    
    public Disciplina(String nome, String descricao, String cor, String icone, String prioridade, String usuarioId) {
        this.nome = nome;
        this.descricao = descricao;
        this.cor = cor;
        this.icone = icone;
        this.prioridade = prioridade;
        this.usuarioId = usuarioId;
    }
    
    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getCor() { return cor; }
    public String getIcone() { return icone; }
    public String getPrioridade() { return prioridade; }
    public String getUsuarioId() { return usuarioId; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setCor(String cor) { this.cor = cor; }
    public void setIcone(String icone) { this.icone = icone; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
}
