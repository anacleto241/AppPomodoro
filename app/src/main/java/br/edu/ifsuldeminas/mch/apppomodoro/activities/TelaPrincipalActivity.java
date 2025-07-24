package br.edu.ifsuldeminas.mch.apppomodoro.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.edu.ifsuldeminas.mch.apppomodoro.R;
import br.edu.ifsuldeminas.mch.apppomodoro.utils.DatabaseHelper;
import br.edu.ifsuldeminas.mch.apppomodoro.utils.LocalAuthManager;
import br.edu.ifsuldeminas.mch.apppomodoro.viewmodel.CicloViewModel;

public class TelaPrincipalActivity extends AppCompatActivity {
    private TextView textViewBoasVindas, textViewCiclosConcluidos, textViewCiclosHoje, textViewTempoTotal;
    private Button buttonIniciarCiclo, buttonVerHistorico, buttonEstatisticas, buttonDisciplinas, buttonMotivacao;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;
    private CicloViewModel cicloViewModel;
    private FirebaseAuth mAuth;
    private LocalAuthManager localAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        localAuthManager = new LocalAuthManager(this);

        boolean isAuthenticated = false;
        try {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            isAuthenticated = currentUser != null;
        } catch (Exception e) {
            Log.w("TelaPrincipal", "Firebase não disponível, usando Local Auth");
        }

        if (!isAuthenticated && !localAuthManager.isSignedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        
        setContentView(R.layout.activity_tela_principal);

        setupToolbar();
        initViews();
        setupViewModel();
        setupListeners();
        setupWelcomeMessage();
        observeData();

        sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
        boolean temaEscuro = sharedPreferences.getBoolean("temaEscuro", false);
        AppCompatDelegate.setDefaultNightMode(
            temaEscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Estude por Blocos");
        }
    }
    
    private void initViews() {
        textViewBoasVindas = findViewById(R.id.textViewBoasVindas);
        textViewCiclosConcluidos = findViewById(R.id.textViewCiclosConcluidos);
        textViewCiclosHoje = findViewById(R.id.textViewCiclosHoje);
        textViewTempoTotal = findViewById(R.id.textViewTempoTotal);
        
        buttonIniciarCiclo = findViewById(R.id.buttonIniciarCiclo);
        buttonVerHistorico = findViewById(R.id.buttonVerHistorico);
        buttonEstatisticas = findViewById(R.id.buttonEstatisticas);
        buttonDisciplinas = findViewById(R.id.buttonDisciplinas);
        buttonMotivacao = findViewById(R.id.buttonMotivacao);

        dbHelper = new DatabaseHelper(this);
    }
    
    private void setupViewModel() {
        cicloViewModel = new ViewModelProvider(this).get(CicloViewModel.class);
        cicloViewModel.setApplication(getApplication());


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            cicloViewModel.setUsuarioId(currentUser.getUid());
        }
    }
    
    private void setupWelcomeMessage() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getDisplayName() != null) {
            String nomeUsuario = user.getDisplayName().split(" ")[0]; // Apenas primeiro nome
            textViewBoasVindas.setText("Olá, " + nomeUsuario + "!");
        } else {
            textViewBoasVindas.setText("Olá!");
        }
    }
    
    private void setupListeners() {
        buttonIniciarCiclo.setOnClickListener(v -> {
            startActivity(new Intent(this, NewCronometroActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        buttonVerHistorico.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoricoActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        
        buttonEstatisticas.setOnClickListener(v -> {
            startActivity(new Intent(this, EstatisticasActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        
        buttonDisciplinas.setOnClickListener(v -> {
            startActivity(new Intent(this, GerenciarDisciplinasActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        
        buttonMotivacao.setOnClickListener(v -> {
            startActivity(new Intent(this, RecomendacoesActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
    
    private void observeData() {
        cicloViewModel.getTotalCiclos().observe(this, total -> {
            if (total != null) {
                textViewCiclosConcluidos.setText("Total de ciclos: " + total);
            }
        });
        
        cicloViewModel.getCiclosHoje().observe(this, hoje -> {
            if (hoje != null) {
                textViewCiclosHoje.setText("Hoje: " + hoje + " ciclos");
            }
        });
        
        cicloViewModel.getTempoTotalEstudo().observe(this, tempoTotal -> {
            if (tempoTotal != null) {
                int horas = tempoTotal / 60;
                int minutos = tempoTotal % 60;
                textViewTempoTotal.setText(String.format("Tempo total: %dh %dmin", horas, minutos));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Toast.makeText(this, "Estude por Blocos v1.0\nDesenvolvido para ajudar nos estudos", Toast.LENGTH_LONG).show();
            return true;
            
        } else if (id == R.id.action_clear_history) {
            cicloViewModel.limparHistorico();
            Toast.makeText(this, "Histórico limpo", Toast.LENGTH_SHORT).show();
            return true;
            
        } else if (id == R.id.action_change_theme) {
            boolean temaAtual = sharedPreferences.getBoolean("temaEscuro", false);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("temaEscuro", !temaAtual);
            editor.apply();
            
            AppCompatDelegate.setDefaultNightMode(
                !temaAtual ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            return true;
            
        } else if (id == R.id.action_logout) {
            // Logout
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
