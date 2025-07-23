package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import br.edu.ifsuldeminas.mach.apppomodoro.R;
import br.edu.ifsuldeminas.mach.apppomodoro.adapters.DisciplinaAdapter;
import br.edu.ifsuldeminas.mach.apppomodoro.models.Disciplina;
import br.edu.ifsuldeminas.mach.apppomodoro.repository.DisciplinaRepository;
import br.edu.ifsuldeminas.mach.apppomodoro.viewmodel.DisciplinaViewModel;

public class GerenciarDisciplinasActivity extends AppCompatActivity {
    private RecyclerView recyclerViewDisciplinas;
    private DisciplinaAdapter adapter;
    private FloatingActionButton fabAdicionarDisciplina;
    private DisciplinaViewModel viewModel;
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_disciplinas);
        
        mAuth = FirebaseAuth.getInstance();
        
        setupToolbar();
        initViews();
        setupViewModel();
        setupRecyclerView();
        setupListeners();
        observeData();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Gerenciar Disciplinas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initViews() {
        recyclerViewDisciplinas = findViewById(R.id.recyclerViewDisciplinas);
        fabAdicionarDisciplina = findViewById(R.id.fabAdicionarDisciplina);
    }
    
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DisciplinaViewModel.class);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            viewModel.setUsuarioId(currentUser.getUid());
        }
    }
    
    private void setupRecyclerView() {
        adapter = new DisciplinaAdapter(new ArrayList<>(), new DisciplinaAdapter.DisciplinaClickListener() {
            @Override
            public void onDisciplinaClick(Disciplina disciplina) {
                // Abrir tela de edição
                Intent intent = new Intent(GerenciarDisciplinasActivity.this, EditarDisciplinaActivity.class);
                intent.putExtra("disciplina_id", disciplina.getId());
                intent.putExtra("disciplina_nome", disciplina.getNome());
                intent.putExtra("disciplina_cor", disciplina.getCor());
                intent.putExtra("disciplina_icone", disciplina.getIcone());
                intent.putExtra("disciplina_prioridade", disciplina.getPrioridade());
                startActivity(intent);
            }
            
            @Override
            public void onDisciplinaLongClick(Disciplina disciplina) {
                // Excluir disciplina
                viewModel.deletarDisciplina(disciplina.getId(), new DisciplinaRepository.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(GerenciarDisciplinasActivity.this, 
                                "Disciplina excluída com sucesso", Toast.LENGTH_SHORT).show();
                    }
                    
                    @Override
                    public void onError(String error) {
                        Toast.makeText(GerenciarDisciplinasActivity.this, 
                                "Erro ao excluir disciplina: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        
        recyclerViewDisciplinas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDisciplinas.setAdapter(adapter);
    }
    
    private void setupListeners() {
        fabAdicionarDisciplina.setOnClickListener(v -> {
            startActivity(new Intent(this, EditarDisciplinaActivity.class));
        });
    }
    
    private void observeData() {
        viewModel.getDisciplinas().observe(this, disciplinas -> {
            if (disciplinas != null) {
                adapter.updateDisciplinas(disciplinas);
            }
        });
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
