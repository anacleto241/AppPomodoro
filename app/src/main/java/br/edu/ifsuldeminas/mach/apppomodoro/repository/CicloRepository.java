package br.edu.ifsuldeminas.mach.apppomodoro.repository;

import androidx.lifecycle.LiveData;
import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.ifsuldeminas.mach.apppomodoro.data.database.PomodoroDatabase;
import br.edu.ifsuldeminas.mach.apppomodoro.data.dao.CicloDao;
import br.edu.ifsuldeminas.mach.apppomodoro.data.entities.CicloEntity;
import br.edu.ifsuldeminas.mach.apppomodoro.models.Ciclo;

public class CicloRepository {
    private CicloDao cicloDao;
    private ExecutorService executor;
    
    public CicloRepository(Context context) {
        PomodoroDatabase database = PomodoroDatabase.getInstance(context);
        cicloDao = database.cicloDao();
        executor = Executors.newFixedThreadPool(2);
    }
    
    public void inserirCiclo(String disciplinaId, String descricao, int duracao, String usuarioId) {
        executor.execute(() -> {
            CicloEntity ciclo = new CicloEntity(disciplinaId, descricao, duracao, System.currentTimeMillis(), usuarioId);
            cicloDao.inserir(ciclo);
        });
    }
    
    public void deletarCiclo(CicloEntity ciclo) {
        executor.execute(() -> cicloDao.deletar(ciclo));
    }
    
    public LiveData<List<CicloEntity>> getAllCiclos(String usuarioId) {
        return cicloDao.getAllCiclos(usuarioId);
    }
    
    public LiveData<Integer> getTotalCiclos(String usuarioId) {
        return cicloDao.getTotalCiclos(usuarioId);
    }
    
    public LiveData<Integer> getCiclosHoje(String usuarioId) {
        return cicloDao.getCiclosHoje(usuarioId);
    }
    
    public LiveData<Integer> getCiclosSemana(String usuarioId) {
        return cicloDao.getCiclosSemana(usuarioId);
    }
    
    public LiveData<Integer> getTempoTotalEstudo(String usuarioId) {
        return cicloDao.getTempoTotalEstudo(usuarioId);
    }
    
    public LiveData<List<CicloDao.DisciplinaCount>> getEstatisticasPorDisciplina(String usuarioId) {
        return cicloDao.getEstatisticasPorDisciplina(usuarioId);
    }
    
    public void limparHistorico(String usuarioId) {
        executor.execute(() -> cicloDao.limparHistorico(usuarioId));
    }
}
