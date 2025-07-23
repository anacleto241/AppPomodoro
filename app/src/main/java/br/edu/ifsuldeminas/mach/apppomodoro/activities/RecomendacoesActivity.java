package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;
import java.util.Random;

import br.edu.ifsuldeminas.mach.apppomodoro.R;
import br.edu.ifsuldeminas.mach.apppomodoro.api.ApiClient;
import br.edu.ifsuldeminas.mach.apppomodoro.api.model.QuoteResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecomendacoesActivity extends AppCompatActivity {
    private TextView textViewFrase, textViewAutor, textViewDica;
    private Button buttonNovaFrase;
    private ProgressBar progressBar;
    
    private String[] dicasEstudo = {
        "💡 Elimine distrações do seu ambiente de estudo",
        "📚 Revise o conteúdo no final de cada sessão",
        "🎯 Defina objetivos claros para cada ciclo",
        "💧 Mantenha-se hidratado durante os estudos",
        "🧘 Use as pausas para relaxar e respirar fundo",
        "📝 Faça anotações dos pontos principais",
        "🌱 Pequenos progressos diários levam a grandes resultados",
        "⏰ Respeite os horários de descanso"
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomendacoes);
        
        setupToolbar();
        initViews();
        setupListeners();
        
        carregarFraseMotivacional();
        mostrarDicaAleatoria();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Motivação");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initViews() {
        textViewFrase = findViewById(R.id.textViewFrase);
        textViewAutor = findViewById(R.id.textViewAutor);
        textViewDica = findViewById(R.id.textViewDica);
        buttonNovaFrase = findViewById(R.id.buttonNovaFrase);
        progressBar = findViewById(R.id.progressBar);
    }
    
    private void setupListeners() {
        buttonNovaFrase.setOnClickListener(v -> carregarFraseMotivacional());
    }
    
    private void carregarFraseMotivacional() {
        progressBar.setVisibility(View.VISIBLE);
        buttonNovaFrase.setEnabled(false);
        
        Call<List<QuoteResponse>> call = ApiClient.getQuoteService().getRandomQuote();
        call.enqueue(new Callback<List<QuoteResponse>>() {
            @Override
            public void onResponse(Call<List<QuoteResponse>> call, Response<List<QuoteResponse>> response) {
                progressBar.setVisibility(View.GONE);
                buttonNovaFrase.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    QuoteResponse quote = response.body().get(0);
                    textViewFrase.setText("\"" + quote.getQuote() + "\"");
                    textViewAutor.setText("— " + quote.getAuthor());
                } else {
                    mostrarFraseLocal();
                }
            }
            
            @Override
            public void onFailure(Call<List<QuoteResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                buttonNovaFrase.setEnabled(true);
                mostrarFraseLocal();
                Toast.makeText(RecomendacoesActivity.this, 
                    "Sem conexão com a internet. Mostrando frase local.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void mostrarFraseLocal() {
        String[] frasesLocais = {
            "O sucesso é a soma de pequenos esforços repetidos dia após dia.",
            "A disciplina é a ponte entre objetivos e conquistas.",
            "Cada minuto de estudo é um investimento no seu futuro.",
            "A consistência transforma sonhos em realidade.",
            "O conhecimento é o único bem que ninguém pode tirar de você."
        };
        
        String[] autores = {
            "Robert Collier",
            "Jim Rohn",
            "Autor desconhecido",
            "Autor desconhecido",
            "Provérbio"
        };
        
        Random random = new Random();
        int index = random.nextInt(frasesLocais.length);
        
        textViewFrase.setText("\"" + frasesLocais[index] + "\"");
        textViewAutor.setText("— " + autores[index]);
    }
    
    private void mostrarDicaAleatoria() {
        Random random = new Random();
        int index = random.nextInt(dicasEstudo.length);
        textViewDica.setText(dicasEstudo[index]);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
