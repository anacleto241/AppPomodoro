package br.edu.ifsuldeminas.mach.apppomodoro;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class PomodoroApplication extends Application {
    private static final String TAG = "PomodoroApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d(TAG, "Iniciando PomodoroApplication...");
        
        // Forçar inicialização do Firebase
        initializeFirebase();
    }
    
    private void initializeFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                Log.d(TAG, "Nenhuma app Firebase encontrada, inicializando...");
                
                // Tentar primeiro com google-services.json
                try {
                    FirebaseApp app = FirebaseApp.initializeApp(this);
                    if (app != null) {
                        Log.d(TAG, "Firebase inicializado com sucesso usando google-services.json");
                        return;
                    }
                } catch (Exception e) {
                    Log.w(TAG, "Falha ao usar google-services.json: " + e.getMessage());
                }
                
                // Fallback: Inicialização manual para desenvolvimento
                Log.d(TAG, "Tentando inicialização manual...");
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setProjectId("apppomodoro-dev")
                        .setApplicationId("1:123456789:android:devtemp")
                        .setApiKey("AIzaSyDummy-Development-Key-Only-For-Testing")
                        .build();
                
                FirebaseApp app = FirebaseApp.initializeApp(this, options);
                Log.d(TAG, "Firebase inicializado manualmente para desenvolvimento");
                
            } else {
                Log.d(TAG, "Firebase já estava inicializado");
            }
        } catch (Exception e) {
            Log.e(TAG, "Erro crítico ao inicializar Firebase: " + e.getMessage(), e);
        }
    }
}
