package br.edu.ifsuldeminas.mch.apppomodoro.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.ifsuldeminas.mch.apppomodoro.R;
import br.edu.ifsuldeminas.mch.apppomodoro.models.Disciplina;
import br.edu.ifsuldeminas.mch.apppomodoro.utils.AlarmReceiver;
import br.edu.ifsuldeminas.mch.apppomodoro.utils.DatabaseHelper;
import br.edu.ifsuldeminas.mch.apppomodoro.utils.NotificationHelper;
import br.edu.ifsuldeminas.mch.apppomodoro.viewmodel.CicloViewModel;
import br.edu.ifsuldeminas.mch.apppomodoro.viewmodel.DisciplinaViewModel;

public class NewCronometroActivity extends AppCompatActivity {
    private static final String TAG = "CronometroActivity";
    
    // Views do layout seguindo o padrão das imagens
    private TextView textViewTipoTimer;
    private TextView textViewMinutos;
    private TextView textViewSegundos;
    private Button buttonStartPause;
    private Button buttonMoreOptions;
    
    // Views de configuração
    private Spinner spinnerDisciplinas;
    private EditText editTextDescricao;
    private View layoutConfiguracao;
    
    private CountDownTimer countDownTimer;
    private long tempoRestante = 1 * 60 * 1000; // 25 minutos em milissegundos
    private long tempoInicial = 1 * 60 * 1000;
    private boolean timerRodando = false;
    private boolean isPausa = false;
    
    private DatabaseHelper dbHelper;
    private CicloViewModel cicloViewModel;
    private DisciplinaViewModel disciplinaViewModel;
    private FirebaseAuth mAuth;
    
    private List<Disciplina> disciplinas = new ArrayList<>();
    private ArrayAdapter<String> disciplinasAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_cronometro);
        
        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        setupToolbar();
        initViews();
        setupDatabase();
        setupViewModels();
        setupSpinner();
        setupListeners();
        updateTimerDisplay();
        observeData();
        
        // Solicitar permissões para alarmes exatos (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cronômetro");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        // Views principais do cronômetro
        textViewTipoTimer = findViewById(R.id.tv_timer_type_label);
        textViewMinutos = findViewById(R.id.tv_minutes);
        textViewSegundos = findViewById(R.id.tv_seconds);
        buttonStartPause = findViewById(R.id.btn_start_pause);
        buttonMoreOptions = findViewById(R.id.btn_more_options);
        
        // Views de configuração
        layoutConfiguracao = findViewById(R.id.layoutConfiguracao);
        spinnerDisciplinas = findViewById(R.id.spinnerDisciplinas);
        editTextDescricao = findViewById(R.id.editTextDescricao);
    }

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }
    
    private void setupViewModels() {
        cicloViewModel = new ViewModelProvider(this).get(CicloViewModel.class);
        cicloViewModel.setApplication(getApplication());
        
        disciplinaViewModel = new ViewModelProvider(this).get(DisciplinaViewModel.class);
        
        // Configurar usuário autenticado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            cicloViewModel.setUsuarioId(currentUser.getUid());
            disciplinaViewModel.setUsuarioId(currentUser.getUid());
        }
    }
    
    private void setupSpinner() {
        disciplinasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        disciplinasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisciplinas.setAdapter(disciplinasAdapter);
    }
    
    private void observeData() {
        disciplinaViewModel.getDisciplinas().observe(this, disciplinasList -> {
            if (disciplinasList != null) {
                disciplinas.clear();
                disciplinas.addAll(disciplinasList);
                
                List<String> nomes = new ArrayList<>();
                nomes.add("Selecione uma disciplina");
                for (Disciplina d : disciplinasList) {
                    nomes.add(d.getNome());
                }
                
                disciplinasAdapter.clear();
                disciplinasAdapter.addAll(nomes);
                disciplinasAdapter.notifyDataSetChanged();
                
                // Mostrar mensagem se não houver disciplinas
                if (disciplinasList.isEmpty()) {
                    Toast.makeText(this, "Você ainda não tem disciplinas cadastradas. " +
                            "Vá em Disciplinas para cadastrar.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private void setupListeners() {
        buttonStartPause.setOnClickListener(v -> {
            if (!timerRodando) {
                iniciarTimer();
            } else {
                pausarTimer();
            }
        });
        
        buttonMoreOptions.setOnClickListener(v -> {
            // Mostrar opções: cancelar, ir para disciplinas, etc.
            if (timerRodando) {
                cancelarTimer();
            } else {
                startActivity(new Intent(this, GerenciarDisciplinasActivity.class));
            }
        });
    }

    @SuppressLint("ScheduleExactAlarm")
    private void iniciarTimer() {
        // Verificar se tem disciplina selecionada
        if (spinnerDisciplinas.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Selecione uma disciplina primeiro", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Esconder layout de configuração
        layoutConfiguracao.setVisibility(View.GONE);
        textViewTipoTimer.setText(isPausa ? "Break" : "Focus");
        
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        try {
            // Configurar alarme
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("message", isPausa ? "Pausa finalizada! Hora de estudar." : "Pomodoro finalizado! Hora de descansar.");

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, flags);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long triggerAtMillis = System.currentTimeMillis() + tempoRestante;
            
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }

            // Iniciar contador
            countDownTimer = new CountDownTimer(tempoRestante, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tempoRestante = millisUntilFinished;
                    updateTimerDisplay();
                }

                @Override
                public void onFinish() {
                    finalizarTimer();
                }
            }.start();

            timerRodando = true;
            buttonStartPause.setText("Pausar");
            
            showToast(isPausa ? "Pausa iniciada!" : "Cronômetro iniciado!");
            Log.d(TAG, "Timer iniciado");

        } catch (Exception e) {
            Log.e(TAG, "Erro ao iniciar o cronômetro: ", e);
            Toast.makeText(this, "Erro ao iniciar o cronômetro", Toast.LENGTH_LONG).show();
        }
    }

    private void pausarTimer() {
        // Cancelar alarme
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        timerRodando = false;
        buttonStartPause.setText("Retomar");
        showToast("Cronômetro pausado");
        Log.d(TAG, "Timer pausado");
    }
    
    private void cancelarTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        timerRodando = false;
        tempoRestante = tempoInicial;
        isPausa = false;
        
        layoutConfiguracao.setVisibility(View.VISIBLE);
        textViewTipoTimer.setText("Focus");
        buttonStartPause.setText("Iniciar");
        
        updateTimerDisplay();
        showToast("Timer cancelado");
    }

    private void updateTimerDisplay() {
        runOnUiThread(() -> {
            int minutos = (int) (tempoRestante / 1000) / 60;
            int segundos = (int) (tempoRestante / 1000) % 60;
            
            textViewMinutos.setText(String.format(Locale.getDefault(), "%02d", minutos));
            textViewSegundos.setText(String.format(Locale.getDefault(), "%02d", segundos));
            
            Log.d(TAG, "Tempo atualizado: " + minutos + ":" + segundos);
        });
    }

    private void finalizarTimer() {
        runOnUiThread(() -> {
            textViewMinutos.setText("00");
            textViewSegundos.setText("00");
            
            if (!isPausa) {
                // Salvar o ciclo completo
                salvarCiclo();
                
                // Iniciar pausa
                iniciarPausa();
            } else {
                // Finalizar pausa, voltar ao foco
                finalizarPausa();
            }
            
            NotificationHelper.notificar(NewCronometroActivity.this,
                    "Pomodoro", isPausa ? "Pausa finalizada! Hora de estudar." : "Tempo esgotado! Hora de descansar.");
            showToast(isPausa ? "Pausa finalizada!" : "Ciclo concluído!");
        });
    }
    
    private void salvarCiclo() {
        if (spinnerDisciplinas.getSelectedItemPosition() > 0) {
            Disciplina disciplinaSelecionada = disciplinas.get(spinnerDisciplinas.getSelectedItemPosition() - 1);
            String descricao = editTextDescricao.getText().toString().trim();
            if (descricao.isEmpty()) {
                descricao = "Ciclo de estudo - " + disciplinaSelecionada.getNome();
            }
            
            cicloViewModel.inserirCiclo(disciplinaSelecionada.getId(), descricao, 25);
            
            // Também salvar no banco local antigo (por compatibilidade)
            dbHelper.adicionarCiclo(descricao, 25);
        }
    }
    
    private void iniciarPausa() {
        isPausa = true;
        tempoRestante = 5 * 60 * 1000; // 5 minutos de pausa
        tempoInicial = tempoRestante;
        timerRodando = false;
        
        textViewTipoTimer.setText("Break");
        buttonStartPause.setText("Iniciar Pausa");
        
        updateTimerDisplay();
    }
    
    private void finalizarPausa() {
        isPausa = false;
        tempoRestante = 25 * 60 * 1000; // Voltar para 25 minutos
        tempoInicial = tempoRestante;
        timerRodando = false;
        
        layoutConfiguracao.setVisibility(View.VISIBLE);
        textViewTipoTimer.setText("Focus");
        buttonStartPause.setText("Iniciar");
        
        updateTimerDisplay();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        Log.d(TAG, "Activity destruída");
    }
}
