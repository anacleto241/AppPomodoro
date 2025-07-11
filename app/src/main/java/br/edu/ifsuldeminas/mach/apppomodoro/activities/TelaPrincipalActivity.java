package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Estude por Blocos");
        }

        textViewCiclosConcluidos = findViewById(R.id.textViewCiclosConcluidos);
        buttonIniciarCiclo = findViewById(R.id.buttonIniciarCiclo);
        buttonVerHistorico = findViewById(R.id.buttonVerHistorico);


        sharedPreferences = getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);


        dbHelper = new DatabaseHelper(this);

        atualizarContagemCiclos();


        buttonIniciarCiclo.setOnClickListener(v -> {
            startActivity(new Intent(this, CronometroActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        buttonVerHistorico.setOnClickListener(v -> {
            startActivity(new Intent(this, HistoricoActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            Toast.makeText(this, "Menu 'Sobre' clicado", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_clear_history) {
            Toast.makeText(this, "Menu 'Limpar Histórico' clicado", Toast.LENGTH_SHORT).show();

            return true;
        } else if (id == R.id.action_change_theme) {
            Toast.makeText(this, "Menu 'Mudar Tema' clicado", Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
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