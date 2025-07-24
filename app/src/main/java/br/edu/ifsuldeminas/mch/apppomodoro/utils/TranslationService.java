package br.edu.ifsuldeminas.mch.apppomodoro.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class TranslationService {
    private static final String TAG = "TranslationService";
    
    public interface TranslationCallback {
        void onTranslationComplete(String translatedText);
        void onTranslationError(String originalText);
    }
    
    public static void translateText(String text, String fromLang, String toLang, TranslationCallback callback) {
        new TranslationTask(text, fromLang, toLang, callback).execute();
    }
    
    private static class TranslationTask extends AsyncTask<Void, Void, String> {
        private String text;
        private String fromLang;
        private String toLang;
        private TranslationCallback callback;
        private boolean hasError = false;
        
        public TranslationTask(String text, String fromLang, String toLang, TranslationCallback callback) {
            this.text = text;
            this.fromLang = fromLang;
            this.toLang = toLang;
            this.callback = callback;
        }
        
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String encodedText = URLEncoder.encode(text, "UTF-8");
                String urlStr = "https://api.mymemory.translated.net/get?q=" + encodedText + 
                               "&langpair=" + fromLang + "|" + toLang;
                
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    String jsonResponse = response.toString();
                    if (jsonResponse.contains("\"translatedText\":\"")) {
                        int start = jsonResponse.indexOf("\"translatedText\":\"") + 18;
                        int end = jsonResponse.indexOf("\"", start);
                        return jsonResponse.substring(start, end);
                    }
                }
                
                hasError = true;
                return text;
                
            } catch (Exception e) {
                Log.e(TAG, "Translation error: " + e.getMessage());
                hasError = true;
                return text;
            }
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (callback != null) {
                if (hasError) {
                    callback.onTranslationError(text);
                } else {
                    callback.onTranslationComplete(result);
                }
            }
        }
    }
}
