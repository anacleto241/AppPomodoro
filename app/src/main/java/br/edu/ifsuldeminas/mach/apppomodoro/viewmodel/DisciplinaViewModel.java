package br.edu.ifsuldeminas.mach.apppomodoro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.edu.ifsuldeminas.mach.apppomodoro.models.Disciplina;
import br.edu.ifsuldeminas.mach.apppomodoro.repository.DisciplinaRepository;

public class DisciplinaViewModel extends ViewModel {
    private DisciplinaRepository repository;
    private LiveData<List<Disciplina>> disciplinas;
    private String usuarioId;
    
    public DisciplinaViewModel() {
        repository = new DisciplinaRepository();
    }
    
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
        this.disciplinas = repository.getDisciplinas(usuarioId);
    }
    
    public void inserirDisciplina(Disciplina disciplina, DisciplinaRepository.OnCompleteListener listener) {
        disciplina.setUsuarioId(usuarioId);
        repository.inserirDisciplina(disciplina, listener);
    }
    
    public void criarDisciplina(String nome, String descricao, DisciplinaRepository.OnCompleteListener listener) {
        Disciplina disciplina = new Disciplina(nome, descricao, "#2196F3", "book", "Média", usuarioId);
        repository.inserirDisciplina(disciplina, listener);
    }
    
    public void atualizarDisciplina(Disciplina disciplina, DisciplinaRepository.OnCompleteListener listener) {
        repository.atualizarDisciplina(disciplina, listener);
    }
    
    public void atualizarDisciplina(String disciplinaId, String nome, String descricao, DisciplinaRepository.OnCompleteListener listener) {
        // Buscar disciplina existente e atualizar
        repository.buscarDisciplinaPorId(disciplinaId, new DisciplinaRepository.OnDisciplinaListener() {
            @Override
            public void onSuccess(Disciplina disciplina) {
                if (disciplina != null) {
                    disciplina.setNome(nome);
                    disciplina.setDescricao(descricao);
                    repository.atualizarDisciplina(disciplina, listener);
                }
            }
            
            @Override
            public void onError(Exception e) {
                if (listener != null) listener.onError(e.getMessage());
            }
        });
    }
    
    public void deletarDisciplina(String disciplinaId, DisciplinaRepository.OnCompleteListener listener) {
        repository.deletarDisciplina(disciplinaId, listener);
    }
    
    public LiveData<List<Disciplina>> getDisciplinas() {
        return disciplinas;
    }
}
