package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import br.edu.ifsuldeminas.mach.apppomodoro.R;
import br.edu.ifsuldeminas.mach.apppomodoro.adapters.CicloAdapter;
import br.edu.ifsuldeminas.mach.apppomodoro.models.Ciclo;
import br.edu.ifsuldeminas.mach.apppomodoro.utils.DatabaseHelper;

public class HistoricoActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CicloAdapter cicloAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DatabaseHelper(this);

        carregarDados();

    }

    private void carregarDados() {
        List<Ciclo> ciclos = dbHelper.getAllCiclos();
        cicloAdapter = new CicloAdapter(ciclos, new CicloAdapter.CicloClickListener() {
            @Override
            public void onCicloClick(Ciclo ciclo) {
                compartilharCiclo(ciclo);
            }

            @Override
            public void onCicloLongClick(Ciclo ciclo, int position) {
                excluirCiclo(position, ciclo);
            }
        });
        recyclerView.setAdapter(cicloAdapter);
    }

    private void compartilharCiclo(Ciclo ciclo) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Concluí: " + ciclo.getDescricao() + " (" + ciclo.getDuracao() + "min em " + ciclo.getData() + ")");
        startActivity(Intent.createChooser(shareIntent, "Compartilhar via"));
    }

    private void excluirCiclo(int position, Ciclo ciclo) {
        dbHelper.excluirCiclo(ciclo.getId());
        cicloAdapter.removerCiclo(position);

        Snackbar.make(recyclerView, "Ciclo excluído", Snackbar.LENGTH_LONG)
                .setAction("Desfazer", v -> {
                    dbHelper.adicionarCiclo(ciclo.getDescricao(), ciclo.getDuracao());
                    cicloAdapter.adicionarCiclo(ciclo);
                }).show();
    }
}