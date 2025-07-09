package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.edu.ifsuldeminas.mach.apppomodoro.R;

public class BoasVindasActivity extends AppCompatActivity {
    private EditText editTextNome;
    private Button buttonComecar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitivity_boas_vindas);

        editTextNome = findViewById(R.id.editTextNome);
        buttonComecar = findViewById(R.id.buttonComecar);
        sharedPreferences = getSharedPreferences("PomodoroPrefs", MODE_PRIVATE);

        buttonComecar.setOnClickListener(v -> {
            String nome = editTextNome.getText().toString().trim();
            if (!nome.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("nomeUsuario", nome);
                editor.apply();
                startActivity(new Intent(this, TelaPrincipalActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Digite seu nome!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
