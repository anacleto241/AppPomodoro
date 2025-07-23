package br.edu.ifsuldeminas.mach.apppomodoro.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifsuldeminas.mach.apppomodoro.R;
import br.edu.ifsuldeminas.mach.apppomodoro.data.dao.CicloDao;
import br.edu.ifsuldeminas.mach.apppomodoro.models.DisciplinaStat;
import br.edu.ifsuldeminas.mach.apppomodoro.viewmodel.CicloViewModel;

public class EstatisticasActivity extends AppCompatActivity {
    private TextView textViewTotalCiclos, textViewCiclosHoje, textViewCiclosSemana, textViewTempoTotal;
    private PieChart pieChart;
    private CicloViewModel viewModel;
    private FirebaseAuth mAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estatisticas);
        
        mAuth = FirebaseAuth.getInstance();
        
        setupToolbar();
        initViews();
        setupViewModel();
        setupChart();
        observeData();
    }
    
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Estatísticas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void initViews() {
        textViewTotalCiclos = findViewById(R.id.textViewTotalCiclos);
        textViewCiclosHoje = findViewById(R.id.textViewCiclosHoje);
        textViewCiclosSemana = findViewById(R.id.textViewCiclosSemana);
        textViewTempoTotal = findViewById(R.id.textViewTempoTotal);
        pieChart = findViewById(R.id.pieChart);
    }
    
    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CicloViewModel.class);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            viewModel.setUsuarioId(currentUser.getUid());
        }
    }
    
    private void setupChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.animateY(1000);
    }
    
    private void observeData() {
        viewModel.getTotalCiclos().observe(this, total -> {
            if (total != null) {
                textViewTotalCiclos.setText("Total de ciclos: " + total);
            }
        });
        
        viewModel.getCiclosHoje().observe(this, hoje -> {
            if (hoje != null) {
                textViewCiclosHoje.setText("Ciclos hoje: " + hoje);
            }
        });
        
        viewModel.getCiclosSemana().observe(this, semana -> {
            if (semana != null) {
                textViewCiclosSemana.setText("Ciclos na semana: " + semana);
            }
        });
        
        viewModel.getTempoTotalEstudo().observe(this, tempoTotal -> {
            if (tempoTotal != null) {
                int horas = tempoTotal / 60;
                int minutos = tempoTotal % 60;
                textViewTempoTotal.setText(String.format("Tempo total: %dh %dmin", horas, minutos));
            }
        });
        
        viewModel.getEstatisticasPorDisciplina().observe(this, stats -> {
            if (stats != null && !stats.isEmpty()) {
                setupPieChart(stats);
            }
        });
    }
    
    private void setupPieChart(List<br.edu.ifsuldeminas.mach.apppomodoro.data.dao.CicloDao.DisciplinaCount> stats) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        
        // Cores para o gráfico
        int[] chartColors = {
            Color.rgb(64, 89, 128), Color.rgb(149, 165, 124), Color.rgb(217, 184, 162),
            Color.rgb(191, 134, 134), Color.rgb(179, 48, 80), Color.rgb(193, 37, 82),
            Color.rgb(255, 102, 0), Color.rgb(245, 199, 0)
        };
        
        for (int i = 0; i < stats.size() && i < 8; i++) {
            CicloDao.DisciplinaCount stat = stats.get(i);
            entries.add(new PieEntry(stat.count, "Disciplina " + (i + 1)));
            colors.add(chartColors[i % chartColors.length]);
        }
        
        PieDataSet dataSet = new PieDataSet(entries, "Disciplinas mais estudadas");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        
        pieChart.setData(data);
        pieChart.invalidate();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
