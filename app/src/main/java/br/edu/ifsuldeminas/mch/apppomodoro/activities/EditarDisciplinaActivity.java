package br.edu.ifsuldeminas.mch.apppomodoro.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.edu.ifsuldeminas.mch.apppomodoro.R;
import br.edu.ifsuldeminas.mch.apppomodoro.models.Disciplina;
import br.edu.ifsuldeminas.mch.apppomodoro.repository.DisciplinaRepository;
import br.edu.ifsuldeminas.mch.apppomodoro.viewmodel.DisciplinaViewModel;

public class EditarDisciplinaActivity extends AppCompatActivity {
    
    private TextInputEditText editNome;
    private TextInputEditText editDescricao;
    private MaterialButton btnSalvar;
    private MaterialButton btnCancelar;
    
    private DisciplinaViewModel disciplinaViewModel;
    private FirebaseAuth mAuth;
    
    private String disciplinaId;
    private boolean isEdicao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_disciplina);
        
        mAuth = FirebaseAuth.getInstance();
        
        setupToolbar();
        initViews();
        setupViewModel();
        setupListeners();

        disciplinaId = getIntent().getStringExtra("disciplina_id");
        isEdicao = disciplinaId != null;
        
        if (isEdicao) {
            carregarDisciplina();
            getSupportActionBar().setTitle("Editar Disciplina");
        } else {
            getSupportActionBar().setTitle("Nova Disciplina");
        }
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        editNome = findViewById(R.id.edit_nome);
        editDescricao = findViewById(R.id.edit_descricao);
        btnSalvar = findViewById(R.id.btn_salvar);
        btnCancelar = findViewById(R.id.btn_cancelar);
    }
    
    private void setupViewModel() {
        disciplinaViewModel = new ViewModelProvider(this).get(DisciplinaViewModel.class);
        
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            disciplinaViewModel.setUsuarioId(currentUser.getUid());
        }
    }
    
    private void setupListeners() {
        btnSalvar.setOnClickListener(v -> salvarDisciplina());
        btnCancelar.setOnClickListener(v -> finish());
    }
    
    private void carregarDisciplina() {
        disciplinaViewModel.getDisciplinas().observe(this, disciplinas -> {
            if (disciplinas != null) {
                for (Disciplina disciplina : disciplinas) {
                    if (disciplina.getId().equals(disciplinaId)) {
                        editNome.setText(disciplina.getNome());
                        editDescricao.setText(disciplina.getDescricao());
                        break;
                    }
                }
            }
        });
    }
    
    private void salvarDisciplina() {
        String nome = editNome.getText().toString().trim();
        String descricao = editDescricao.getText().toString().trim();
        
        if (nome.isEmpty()) {
            editNome.setError("Nome é obrigatório");
            return;
        }
        
        btnSalvar.setEnabled(false);
        
        if (isEdicao) {
            disciplinaViewModel.atualizarDisciplina(disciplinaId, nome, descricao, new DisciplinaRepository.OnCompleteListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(EditarDisciplinaActivity.this, "Disciplina atualizada com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(EditarDisciplinaActivity.this, "Erro ao atualizar disciplina", Toast.LENGTH_SHORT).show();
                    btnSalvar.setEnabled(true);
                }
            });
        } else {
            disciplinaViewModel.criarDisciplina(nome, descricao, new DisciplinaRepository.OnCompleteListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(EditarDisciplinaActivity.this, "Disciplina criada com sucesso", Toast.LENGTH_SHORT).show();
                    finish();
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(EditarDisciplinaActivity.this, "Erro ao criar disciplina", Toast.LENGTH_SHORT).show();
                    btnSalvar.setEnabled(true);
                }
            });
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
