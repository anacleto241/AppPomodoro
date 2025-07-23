package br.edu.ifsuldeminas.mch.apppomodoro.models;

public class DisciplinaStat {
    private String disciplinaId;
    private String nome;
    private int totalCiclos;
    private long totalTempo;
    private String cor;
    
    public DisciplinaStat() {}
    
    public DisciplinaStat(String disciplinaId, String nome, int totalCiclos, long totalTempo, String cor) {
        this.disciplinaId = disciplinaId;
        this.nome = nome;
        this.totalCiclos = totalCiclos;
        this.totalTempo = totalTempo;
        this.cor = cor;
    }
    
    // Getters
    public String getDisciplinaId() { return disciplinaId; }
    public String getNome() { return nome; }
    public int getTotalCiclos() { return totalCiclos; }
    public long getTotalTempo() { return totalTempo; }
    public String getCor() { return cor; }
    
    // Setters
    public void setDisciplinaId(String disciplinaId) { this.disciplinaId = disciplinaId; }
    public void setNome(String nome) { this.nome = nome; }
    public void setTotalCiclos(int totalCiclos) { this.totalCiclos = totalCiclos; }
    public void setTotalTempo(long totalTempo) { this.totalTempo = totalTempo; }
    public void setCor(String cor) { this.cor = cor; }
}
