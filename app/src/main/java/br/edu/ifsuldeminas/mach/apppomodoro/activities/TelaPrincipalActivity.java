package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.ifsuldeminas.mach.apppomodoro.R;
import br.edu.ifsuldeminas.mach.apppomodoro.utils.DatabaseHelper;

public class TelaPrincipalActivity extends AppCompatActivity {
    private TextView textViewCiclosConcluidos;
    private Button buttonIniciarCiclo;
    private Button buttonVerHistorico;
    private SharedPreferences sharedPreferences;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        // Inicialização dos componentes
        textViewCiclosConcluidos = findViewById(R.id.textViewCiclosConcluidos);
        buttonIniciarCiclo = findViewById(R.id.buttonIniciarCiclo);
        buttonVerHistorico = findViewById(R.id.buttonVerHistorico);

        // Configuração do SharedPreferences
        sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);

        // Inicialização do banco de dados
        dbHelper = new DatabaseHelper(this);

        // Atualizar contagem de ciclos
        atualizarContagemCiclos();

        // Listeners dos botões
        buttonIniciarCiclo.setOnClickListener(v -> {
            startActivity(new Intent(this, CronometroActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        buttonVerHistorico.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoricoActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void atualizarContagemCiclos() {
        int ciclosConcluidos = dbHelper.getTotalCiclos();
        textViewCiclosConcluidos.setText(getString(R.string.ciclos_concluidos, ciclosConcluidos));
    }


    @Override
    protected void onResume() {
        super.onResume();
        atualizarContagemCiclos();
    }
}