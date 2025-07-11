package br.edu.ifsuldeminas.mach.apppomodoro.models;

public class Ciclo {
    private int id;
    private String descricao;
    private int duracao;
    private String data;


    public Ciclo(int id, String descricao, int duracao, String string) {
        this.id = id;
        this.descricao = descricao;
        this.duracao = duracao;
    }

    public int getId() { return id; }
    public String getDescricao() { return descricao; }
    public int getDuracao() { return duracao; }
    public String getData() { return data; }
}