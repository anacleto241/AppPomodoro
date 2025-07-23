package br.edu.ifsuldeminas.mch.apppomodoro.repository;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.edu.ifsuldeminas.mch.apppomodoro.models.Disciplina;

public class DisciplinaRepository {
    private FirebaseFirestore firestore;
    
    public DisciplinaRepository() {
        firestore = FirebaseFirestore.getInstance();
    }
    
    public void inserirDisciplina(Disciplina disciplina, OnCompleteListener listener) {
        firestore.collection("disciplinas")
                .add(disciplina)
                .addOnSuccessListener(documentReference -> {
                    disciplina.setId(documentReference.getId());
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }
    
    public void atualizarDisciplina(Disciplina disciplina, OnCompleteListener listener) {
        firestore.collection("disciplinas")
                .document(disciplina.getId())
                .set(disciplina)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }
    
    public void deletarDisciplina(String disciplinaId, OnCompleteListener listener) {
        firestore.collection("disciplinas")
                .document(disciplinaId)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }
    
    public LiveData<List<Disciplina>> getDisciplinas(String usuarioId) {
        MutableLiveData<List<Disciplina>> liveData = new MutableLiveData<>();
        
        firestore.collection("disciplinas")
                .whereEqualTo("usuarioId", usuarioId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        liveData.setValue(new ArrayList<>());
                        return;
                    }
                    
                    List<Disciplina> disciplinas = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Disciplina disciplina = doc.toObject(Disciplina.class);
                            disciplina.setId(doc.getId());
                            disciplinas.add(disciplina);
                        }
                    }
                    liveData.setValue(disciplinas);
                });
        
        return liveData;
    }
    
    public void buscarDisciplinaPorId(String disciplinaId, OnDisciplinaListener listener) {
        firestore.collection("disciplinas")
                .document(disciplinaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Disciplina disciplina = documentSnapshot.toObject(Disciplina.class);
                        if (disciplina != null) {
                            disciplina.setId(documentSnapshot.getId());
                        }
                        listener.onSuccess(disciplina);
                    } else {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> listener.onError(e));
    }
    
    public interface OnCompleteListener {
        void onSuccess();
        void onError(String error);
    }
    
    public interface OnDisciplinaListener {
        void onSuccess(Disciplina disciplina);
        void onError(Exception error);
    }
}
