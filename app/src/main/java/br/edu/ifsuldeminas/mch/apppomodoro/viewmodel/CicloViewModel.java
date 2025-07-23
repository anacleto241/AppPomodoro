package br.edu.ifsuldeminas.mch.apppomodoro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifsuldeminas.mch.apppomodoro.models.Ciclo;
import br.edu.ifsuldeminas.mch.apppomodoro.models.DisciplinaStat;
import br.edu.ifsuldeminas.mch.apppomodoro.repository.CicloRepository;

public class CicloViewModel extends ViewModel {
    private CicloRepository repository;
    private LiveData<List<Ciclo>> allCiclos;
    private String usuarioId;
    private Application application;
    
    public CicloViewModel() {
        // Construtor padrão - o repository será inicializado quando setApplication for chamado
    }
    
    public void setApplication(Application application) {
        this.application = application;
        repository = new CicloRepository(application);
    }
    
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
        if (repository != null) {
            this.allCiclos = repository.getAllCiclos(usuarioId);
        }
    }
    
    public void inserirCiclo(String disciplinaId, String descricao, int duracao) {
        if (repository != null && usuarioId != null) {
            repository.inserirCiclo(disciplinaId, descricao, duracao, usuarioId);
        }
    }
    
    public void deletarCiclo(Ciclo ciclo) {
        if (repository != null && usuarioId != null && ciclo.getId() != null) {
            repository.deletarCiclo(ciclo.getId(), usuarioId);
        }
    }
    
    public LiveData<List<Ciclo>> getAllCiclos() {
        return allCiclos;
    }
    
    public LiveData<Integer> getTotalCiclos() {
        if (repository != null && usuarioId != null) {
            return repository.getTotalCiclos(usuarioId);
        }
        return new MutableLiveData<>(0);
    }
    
    public LiveData<Integer> getTotalMinutosPorDisciplina(String disciplinaId) {
        if (repository != null && usuarioId != null) {
            return repository.getTotalMinutosPorDisciplina(usuarioId, disciplinaId);
        }
        return new MutableLiveData<>(0);
    }
    
    // Métodos compatíveis com a versão antiga para não quebrar o código existente
    public LiveData<Integer> getCiclosHoje() {
        // Por enquanto, retorna o total de ciclos (pode ser implementado com filtro de data depois)
        return getTotalCiclos();
    }
    
    public LiveData<Integer> getCiclosSemana() {
        // Por enquanto, retorna o total de ciclos (pode ser implementado com filtro de data depois)
        return getTotalCiclos();
    }
    
    public LiveData<Integer> getTempoTotalEstudo() {
        // Retorna o total baseado na soma dos ciclos
        return getTotalCiclos();
    }
    
    public LiveData<List<Object>> getEstatisticasPorDisciplina() {
        // Retorna uma lista vazia por enquanto - pode ser implementado depois
        return new MutableLiveData<>(new ArrayList<>());
    }
    
    public void buscarEstatisticasPorDisciplina(CicloRepository.OnEstatisticasListener listener) {
        if (repository != null && usuarioId != null) {
            repository.getEstatisticasPorDisciplina(usuarioId, listener);
        } else {
            listener.onError("Usuário não logado");
        }
    }
    
    public void limparHistorico() {
        // Método placeholder - implementação futura se necessário
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Cleanup if needed
    }
}
