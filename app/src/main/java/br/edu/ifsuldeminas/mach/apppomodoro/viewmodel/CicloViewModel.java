package br.edu.ifsuldeminas.mach.apppomodoro.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.app.Application;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import br.edu.ifsuldeminas.mach.apppomodoro.data.entities.CicloEntity;
import br.edu.ifsuldeminas.mach.apppomodoro.data.dao.CicloDao;
import br.edu.ifsuldeminas.mach.apppomodoro.repository.CicloRepository;

public class CicloViewModel extends AndroidViewModel {
    private CicloRepository repository;
    private LiveData<List<CicloEntity>> allCiclos;
    private String usuarioId;
    
    public CicloViewModel(Application application) {
        super(application);
        repository = new CicloRepository(application);
    }
    
    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
        this.allCiclos = repository.getAllCiclos(usuarioId);
    }
    
    public void inserirCiclo(String disciplinaId, String descricao, int duracao) {
        repository.inserirCiclo(disciplinaId, descricao, duracao, usuarioId);
    }
    
    public void deletarCiclo(CicloEntity ciclo) {
        repository.deletarCiclo(ciclo);
    }
    
    public LiveData<List<CicloEntity>> getAllCiclos() {
        return allCiclos;
    }
    
    public LiveData<Integer> getTotalCiclos() {
        return repository.getTotalCiclos(usuarioId);
    }
    
    public LiveData<Integer> getCiclosHoje() {
        return repository.getCiclosHoje(usuarioId);
    }
    
    public LiveData<Integer> getCiclosSemana() {
        return repository.getCiclosSemana(usuarioId);
    }
    
    public LiveData<Integer> getTempoTotalEstudo() {
        return repository.getTempoTotalEstudo(usuarioId);
    }
    
    public LiveData<List<CicloDao.DisciplinaCount>> getEstatisticasPorDisciplina() {
        return repository.getEstatisticasPorDisciplina(usuarioId);
    }
    
    public void limparHistorico() {
        repository.limparHistorico(usuarioId);
    }
}
