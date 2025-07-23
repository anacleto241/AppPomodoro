package br.edu.ifsuldeminas.mch.apppomodoro.api.service;

import java.util.List;

import br.edu.ifsuldeminas.mch.apppomodoro.api.model.QuoteResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {
    @GET("api/random")
    Call<List<QuoteResponse>> getRandomQuote();
}
