package br.edu.ifsuldeminas.mch.apppomodoro.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import br.edu.ifsuldeminas.mch.apppomodoro.api.service.QuoteApiService;

public class ApiClient {
    private static final String BASE_URL = "https://zenquotes.io/";
    private static Retrofit retrofit;
    
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();
            
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    public static QuoteApiService getQuoteService() {
        return getRetrofitInstance().create(QuoteApiService.class);
    }
}
