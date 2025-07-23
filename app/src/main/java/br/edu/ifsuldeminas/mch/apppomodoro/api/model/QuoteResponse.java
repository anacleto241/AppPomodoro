package br.edu.ifsuldeminas.mch.apppomodoro.api.model;

import com.google.gson.annotations.SerializedName;

public class QuoteResponse {
    @SerializedName("q")
    private String quote;
    
    @SerializedName("a")
    private String author;
    
    public QuoteResponse() {}
    
    public String getQuote() { return quote; }
    public String getAuthor() { return author; }
    
    public void setQuote(String quote) { this.quote = quote; }
    public void setAuthor(String author) { this.author = author; }
}
