package br.edu.ifsuldeminas.mch.apppomodoro.repository;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;
import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import br.edu.ifsuldeminas.mch.apppomodoro.models.Ciclo;
import br.edu.ifsuldeminas.mch.apppomodoro.models.DisciplinaStat;

public class CicloRepository {
    private static final String TAG = "CicloRepository";
    private FirebaseFirestore firestore;
    private MutableLiveData<List<Ciclo>> ciclosLiveData;
    private MutableLiveData<Integer> totalCiclosLiveData;
    
    public CicloRepository(Context context) {
        firestore = FirebaseFirestore.getInstance();
        ciclosLiveData = new MutableLiveData<>();
        totalCiclosLiveData = new MutableLiveData<>();
    }
    
    public void inserirCiclo(String disciplinaId, String descricao, int duracao, String usuarioId) {
        Map<String, Object> cicloData = new HashMap<>();
        cicloData.put("disciplinaId", disciplinaId);
        cicloData.put("descricao", descricao);
        cicloData.put("duracao", duracao);
        cicloData.put("dataHora", System.currentTimeMillis());
        cicloData.put("usuarioId", usuarioId);
        
        firestore.collection("ciclos")
                .add(cicloData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Ciclo inserido com ID: " + documentReference.getId());
                    // Recarregar dados após inserção
                    loadCiclos(usuarioId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao inserir ciclo", e);
                });
    }
    
    public void deletarCiclo(String cicloId, String usuarioId) {
        firestore.collection("ciclos")
                .document(cicloId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Ciclo deletado com sucesso");
                    // Recarregar dados após deleção
                    loadCiclos(usuarioId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao deletar ciclo", e);
                });
    }
    
    public LiveData<List<Ciclo>> getAllCiclos(String usuarioId) {
        loadCiclos(usuarioId);
        return ciclosLiveData;
    }
    
    private void loadCiclos(String usuarioId) {
        firestore.collection("ciclos")
                .whereEqualTo("usuarioId", usuarioId)
                .orderBy("dataHora", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Erro ao carregar ciclos", error);
                        return;
                    }
                    
                    List<Ciclo> ciclos = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Ciclo ciclo = doc.toObject(Ciclo.class);
                            ciclo.setId(doc.getId());
                            ciclos.add(ciclo);
                        }
                    }
                    
                    ciclosLiveData.setValue(ciclos);
                    totalCiclosLiveData.setValue(ciclos.size());
                });
    }
    
    public LiveData<Integer> getTotalCiclos(String usuarioId) {
        loadCiclos(usuarioId);
        return totalCiclosLiveData;
    }
    
    public LiveData<Integer> getTotalMinutosPorDisciplina(String usuarioId, String disciplinaId) {
        MutableLiveData<Integer> minutosLiveData = new MutableLiveData<>();
        
        firestore.collection("ciclos")
                .whereEqualTo("usuarioId", usuarioId)
                .whereEqualTo("disciplinaId", disciplinaId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Erro ao calcular minutos", error);
                        return;
                    }
                    
                    int totalMinutos = 0;
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Long duracao = doc.getLong("duracao");
                            if (duracao != null) {
                                totalMinutos += duracao;
                            }
                        }
                    }
                    
                    minutosLiveData.setValue(totalMinutos);
                });
        
        return minutosLiveData;
    }
    
    public void getEstatisticasPorDisciplina(String usuarioId, OnEstatisticasListener listener) {
        firestore.collection("ciclos")
                .whereEqualTo("usuarioId", usuarioId)
                .get()
                .addOnSuccessListener(ciclosSnapshot -> {
                    // Buscar todas as disciplinas
                    firestore.collection("disciplinas")
                            .whereEqualTo("usuarioId", usuarioId)
                            .get()
                            .addOnSuccessListener(disciplinasSnapshot -> {
                                Map<String, DisciplinaStat> statsMap = new HashMap<>();
                                
                                // Inicializar estatísticas para cada disciplina
                                for (QueryDocumentSnapshot disciplinaDoc : disciplinasSnapshot) {
                                    String disciplinaId = disciplinaDoc.getId();
                                    String nome = disciplinaDoc.getString("nome");
                                    String cor = disciplinaDoc.getString("cor");
                                    
                                    DisciplinaStat stat = new DisciplinaStat();
                                    stat.setDisciplinaId(disciplinaId);
                                    stat.setNome(nome != null ? nome : "Disciplina");
                                    stat.setCor(cor != null ? cor : "#FF6B35");
                                    stat.setTotalCiclos(0);
                                    stat.setTotalTempo(0);
                                    
                                    statsMap.put(disciplinaId, stat);
                                }
                                
                                // Calcular estatísticas com base nos ciclos
                                for (QueryDocumentSnapshot cicloDoc : ciclosSnapshot) {
                                    String disciplinaId = cicloDoc.getString("disciplinaId");
                                    Long duracao = cicloDoc.getLong("duracao");
                                    
                                    if (disciplinaId != null && duracao != null && statsMap.containsKey(disciplinaId)) {
                                        DisciplinaStat stat = statsMap.get(disciplinaId);
                                        stat.setTotalCiclos(stat.getTotalCiclos() + 1);
                                        stat.setTotalTempo(stat.getTotalTempo() + duracao);
                                    }
                                }
                                
                                // Filtrar apenas disciplinas com pelo menos 1 ciclo
                                List<DisciplinaStat> estatisticas = new ArrayList<>();
                                for (DisciplinaStat stat : statsMap.values()) {
                                    if (stat.getTotalCiclos() > 0) {
                                        estatisticas.add(stat);
                                    }
                                }
                                
                                listener.onSuccess(estatisticas);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erro ao buscar disciplinas", e);
                                listener.onError(e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao buscar ciclos", e);
                    listener.onError(e.getMessage());
                });
    }
    
    public interface OnCompleteListener {
        void onSuccess();
        void onError(String error);
    }
    
    public interface OnEstatisticasListener {
        void onSuccess(List<DisciplinaStat> estatisticas);
        void onError(String error);
    }
}
