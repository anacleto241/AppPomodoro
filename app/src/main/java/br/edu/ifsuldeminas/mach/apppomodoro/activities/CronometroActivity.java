package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

import br.edu.ifsuldeminas.mach.apppomodoro.R;
import br.edu.ifsuldeminas.mach.apppomodoro.utils.AlarmReceiver;
import br.edu.ifsuldeminas.mach.apppomodoro.utils.DatabaseHelper;
import br.edu.ifsuldeminas.mach.apppomodoro.utils.NotificationHelper;

public class CronometroActivity extends AppCompatActivity {
    private static final String TAG = "CronometroActivity";
    private TextView textViewTempo;
    private Button buttonPausar;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private long tempoRestante = 1 * 60 * 1000; // 25 minutos em ms
    private boolean timerRodando = false;
    private DatabaseHelper dbHelper;
    private Button btnStartPause;
    private long initialTimeMillis = 25 * 60 * 1000; // 25 minutes for example
    private boolean timerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cronometro);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cronômetro");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);// Set your title here
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);  // Abre a tela de permissões
            }
        }

        // Inicialização dos componentes
        initViews();
        setupDatabase();
        startTimer();
        textViewTempo = findViewById(R.id.textViewTempo); // Assuming you have these in activity_cronometro.xml
        buttonPausar = findViewById(R.id.buttonPausar);
        btnStartPause = findViewById(R.id.buttonPausar);

        timeLeftInMillis = tempoRestante;
        updateTimerDisplay();

        btnStartPause.setOnClickListener(v -> {
            Log.d(TAG, "Botão iniciar/pausar clicado.");
            try {
                if (timerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao clicar no botão iniciar/pausar: ", e);
                Toast.makeText(this, "Erro ao iniciar/pausar o cronômetro", Toast.LENGTH_LONG).show();
            }
        });
        // Configuração do listener do botão
        buttonPausar.setOnClickListener(v -> toggleTimerState());
    }

    private void initViews() {
        textViewTempo = findViewById(R.id.textViewTempo);
        buttonPausar = findViewById(R.id.buttonPausar);

        // Verificação de visibilidade do TextView
        textViewTempo.post(() -> {
            if (textViewTempo.getWidth() == 0 || textViewTempo.getHeight() == 0) {
                Log.e(TAG, "TextView não está visível ou tem dimensões zero");
                textViewTempo.setTextColor(Color.RED);
                textViewTempo.setText("25:00");
            }
        });
    }

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }

    @SuppressLint("ScheduleExactAlarm")
    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        try {
            // === Agendar notificação com AlarmManager ===
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("message", "Pomodoro finalizado! Hora de descansar.");

            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, 1, intent, flags
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long triggerAtMillis = System.currentTimeMillis() + tempoRestante;
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                );
            }

            // === Iniciar cronômetro ===
            countDownTimer = new CountDownTimer(tempoRestante, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tempoRestante = millisUntilFinished;
                    updateTimerDisplay();
                }

                @Override
                public void onFinish() {
                    completeTimer();
                }
            }.start();

            timerRodando = true;
            buttonPausar.setText("Pausar");
            showToast("Cronômetro iniciado!");
            Log.d(TAG, "Cronômetro iniciado");

        } catch (Exception e) {
            Log.e(TAG, "Erro ao iniciar o cronômetro: ", e);
            Toast.makeText(this, "Erro ao iniciar o cronômetro", Toast.LENGTH_LONG).show();
        }
    }


    private void updateTimerDisplay() {
        runOnUiThread(() -> {
            int minutos = (int) (tempoRestante / 1000) / 60;
            int segundos = (int) (tempoRestante / 1000) % 60;
            String tempoFormatado = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
            textViewTempo.setText(tempoFormatado);
            Log.d(TAG, "Tempo atualizado: " + tempoFormatado);
        });
    }

    private void completeTimer() {
        runOnUiThread(() -> {
            textViewTempo.setText("00:00");
            dbHelper.adicionarCiclo("Pomodoro concluído", 25);
            NotificationHelper.notificar(CronometroActivity.this,
                    "Pomodoro", "Tempo esgotado! Hora de descansar.");
            showToast("Ciclo concluído!");
            finish();
        });
    }

    private void toggleTimerState() {
        if (timerRodando) {
            pauseTimer();
        } else {
            resumeTimer();
        }
    }

    private void pauseTimer() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRodando = false;
        buttonPausar.setText("Retomar");
        showToast("Cronômetro pausado");
        Log.d(TAG, "Cronômetro pausado");
    }

    private void resumeTimer() {
        startTimer();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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