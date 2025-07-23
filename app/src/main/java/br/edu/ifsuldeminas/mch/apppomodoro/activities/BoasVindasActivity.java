package br.edu.ifsuldeminas.mch.apppomodoro.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.analytics.FirebaseAnalytics;

import br.edu.ifsuldeminas.mch.apppomodoro.R;

public class BoasVindasActivity extends AppCompatActivity {
    private TextView textViewBoasVindas;
    private RadioGroup radioGroupTema;
    private Button buttonComecar;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boas_vindas);

        // Inicializar Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Temporariamente removido - autenticação Firebase
        // mAuth = FirebaseAuth.getInstance();
        
        // Verificar se usuário está logado
        // FirebaseUser currentUser = mAuth.getCurrentUser();
        // if (currentUser == null) {
        //     // Redirecionar para login se não estiver logado
        //     startActivity(new Intent(this, LoginActivity.class));
        //     finish();
        //     return;
        // }

        initViews();
        setupWelcomeMessage();
        setupListeners();
    }
    
    private void initViews() {
        textViewBoasVindas = findViewById(R.id.textViewBoasVindas);
        radioGroupTema = findViewById(R.id.radioGroupTema);
        buttonComecar = findViewById(R.id.buttonComecar);
        sharedPreferences = getSharedPreferences("PomodoroPrefs", MODE_PRIVATE);
    }
    
    private void setupWelcomeMessage() {
        // Temporariamente sem Firebase Auth
        // FirebaseUser user = mAuth.getCurrentUser();
        // if (user != null && user.getDisplayName() != null) {
        //     String nomeUsuario = user.getDisplayName();
        //     textViewBoasVindas.setText("Bem-vindo(a), " + nomeUsuario + "!");
        // } else {
            textViewBoasVindas.setText("Bem-vindo(a) ao Estude por Blocos!");
        // }
    }
    
    private void setupListeners() {
        buttonComecar.setOnClickListener(v -> {
            // Salvar preferência de tema
            boolean temaEscuro = radioGroupTema.getCheckedRadioButtonId() == R.id.radioTemaEscuro;
            
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("temaEscuro", temaEscuro);
            editor.apply();
            
            // Aplicar tema
            AppCompatDelegate.setDefaultNightMode(
                temaEscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            
            // Ir para tela principal
            startActivity(new Intent(this, TelaPrincipalActivity.class));
            finish();
        });
    }
}
