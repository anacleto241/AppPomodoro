package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout; // Import ConstraintLayout
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList; // For tinting
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem; // For back button on toolbar
import android.widget.Button; // Changed to MaterialButton, but Button works for casting
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton; // Import MaterialButton

import br.edu.ifsuldeminas.mach.apppomodoro.R;
import br.edu.ifsuldeminas.mach.apppomodoro.utils.AlarmReceiver;


public class CronometroActivity extends AppCompatActivity {

    private static final String TAG = "CronometroActivity";

    private TextView tvTimerMinutes;
    private TextView tvTimerSeconds;
    private TextView tvTimerTypeLabel;
    private MaterialButton btnStartPause;
    private MaterialButton btnMoreOptions;
    private MaterialButton btnFastForward;
    private ConstraintLayout rootLayout; // Reference to the root layout

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private long focusTimeMillis = 25 * 60 * 1000; // 25 minutes
    private long shortBreakTimeMillis = 5 * 60 * 1000; // 5 minutes
    private long longBreakTimeMillis = 15 * 60 * 1000; // 15 minutes

    private boolean timerRunning;

    // Define timer states
    private enum TimerState {
        FOCUS, SHORT_BREAK, LONG_BREAK
    }
    private TimerState currentTimerState = TimerState.FOCUS;
    private int completedFocusCycles = 0; // To track for long breaks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cronometro);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(""); // No title on toolbar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTimerMinutes = findViewById(R.id.tv_minutes);
        tvTimerSeconds = findViewById(R.id.tv_seconds);
        tvTimerTypeLabel = findViewById(R.id.tv_timer_type_label);
        btnStartPause = findViewById(R.id.btn_start_pause);
        btnMoreOptions = findViewById(R.id.btn_more_options);
        btnFastForward = findViewById(R.id.btn_fast_forward);
        rootLayout = findViewById(R.id.cronometro_layout_root); // Get reference to the root layout

        timeLeftInMillis = focusTimeMillis; // Start with focus time
        updateUIForState(TimerState.FOCUS); // Set initial UI style

        btnStartPause.setOnClickListener(v -> {
            if (timerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        btnFastForward.setOnClickListener(v -> skipTimer()); // Implement skip
        btnMoreOptions.setOnClickListener(v -> showMoreOptions()); // Implement more options
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                btnStartPause.setIconResource(R.drawable.ic_play_arrow); // Change icon to play
                Toast.makeText(CronometroActivity.this, "Ciclo concluído!", Toast.LENGTH_SHORT).show();

                String notificationMessage = "Seu ciclo de " + tvTimerTypeLabel.getText() + " terminou!";
                scheduleTimerEndAlarm(notificationMessage);

                // Transition to next state
                transitionToNextTimerState();
            }
        }.start();

        timerRunning = true;
        btnStartPause.setIconResource(R.drawable.ic_pause); // Change icon to pause
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerRunning = false;
        btnStartPause.setIconResource(R.drawable.ic_play_arrow); // Change icon to play
    }

    private void resetTimer() {
        pauseTimer(); // Stop any running timer
        switch (currentTimerState) {
            case FOCUS:
                timeLeftInMillis = focusTimeMillis;
                break;
            case SHORT_BREAK:
                timeLeftInMillis = shortBreakTimeMillis;
                break;
            case LONG_BREAK:
                timeLeftInMillis = longBreakTimeMillis;
                break;
        }
        updateCountDownText();
        updateUIForState(currentTimerState); // Reapply current state UI
    }

    private void skipTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Stop current timer
        }
        transitionToNextTimerState();
    }

    private void transitionToNextTimerState() {
        switch (currentTimerState) {
            case FOCUS:
                completedFocusCycles++;
                if (completedFocusCycles % 4 == 0) { // Every 4 focus cycles, transition to long break
                    currentTimerState = TimerState.LONG_BREAK;
                    timeLeftInMillis = longBreakTimeMillis;
                } else {
                    currentTimerState = TimerState.SHORT_BREAK;
                    timeLeftInMillis = shortBreakTimeMillis;
                }
                break;
            case SHORT_BREAK:
            case LONG_BREAK:
                currentTimerState = TimerState.FOCUS;
                timeLeftInMillis = focusTimeMillis;
                break;
        }
        updateUIForState(currentTimerState);
        startTimer(); // Automatically start the next timer
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        tvTimerMinutes.setText(String.format("%02d", minutes));
        tvTimerSeconds.setText(String.format("%02d", seconds));
    }

    private void updateUIForState(TimerState state) {
        int backgroundColor;
        int textColor;
        int buttonBackgroundColor;
        String labelText;

        switch (state) {
            case FOCUS:
                backgroundColor = R.color.focus_background;
                textColor = R.color.focus_text_color;
                buttonBackgroundColor = R.color.focus_button_background;
                labelText = "Focus";
                break;
            case SHORT_BREAK:
                backgroundColor = R.color.short_break_background;
                textColor = R.color.short_break_text_color;
                buttonBackgroundColor = R.color.short_break_button_background;
                labelText = "Short Break";
                break;
            case LONG_BREAK:
                backgroundColor = R.color.long_break_background;
                textColor = R.color.long_break_text_color;
                buttonBackgroundColor = R.color.long_break_button_background;
                labelText = "Long Break";
                break;
            default:
                backgroundColor = R.color.focus_background; // Fallback
                textColor = R.color.focus_text_color;
                buttonBackgroundColor = R.color.focus_button_background;
                labelText = "Focus";
        }

        rootLayout.setBackgroundColor(getResources().getColor(backgroundColor, getTheme()));
        tvTimerMinutes.setTextColor(getResources().getColor(textColor, getTheme()));
        tvTimerSeconds.setTextColor(getResources().getColor(textColor, getTheme()));
        tvTimerTypeLabel.setText(labelText);

        // Apply tint to buttons
        ColorStateList buttonTint = ColorStateList.valueOf(getResources().getColor(buttonBackgroundColor, getTheme()));
        btnStartPause.setBackgroundTintList(buttonTint);
        btnMoreOptions.setBackgroundTintList(buttonTint);
        btnFastForward.setBackgroundTintList(buttonTint);

        // Reset icon to play if timer is not running
        if (!timerRunning) {
            btnStartPause.setIconResource(R.drawable.ic_play_arrow);
        }
        updateCountDownText(); // Ensure numbers are updated for the new time
    }

    private void showMoreOptions() {
        // Implement a PopupMenu or DialogFragment for "Reset" or "Settings"
        Toast.makeText(this, "Mais opções...", Toast.LENGTH_SHORT).show();
        // For now, let's make the ... button act as a Reset button.
        resetTimer();
    }

    // AlarmManager and Notification methods (as provided in previous response)
    public void scheduleTimerEndAlarm(String notificationMessage) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("message", notificationMessage);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0)
        );

        long triggerTime = System.currentTimeMillis();

//        if (alarmManager != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
//            } else {
//                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
//            }
//            Log.d(TAG, "Alarme de conclusão agendado!");
//        }
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
    }
}