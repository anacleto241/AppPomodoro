package br.edu.ifsuldeminas.mch.apppomodoro.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.edu.ifsuldeminas.mch.apppomodoro.R;
import br.edu.ifsuldeminas.mch.apppomodoro.models.DisciplinaStat;

public class DisciplinaEstatisticaAdapter extends RecyclerView.Adapter<DisciplinaEstatisticaAdapter.ViewHolder> {
    
    private List<DisciplinaStat> disciplinaStats;
    private long totalTempo;
    
    public DisciplinaEstatisticaAdapter(List<DisciplinaStat> disciplinaStats) {
        this.disciplinaStats = disciplinaStats;
        calcularTempoTotal();
    }
    
    private void calcularTempoTotal() {
        totalTempo = 0;
        for (DisciplinaStat stat : disciplinaStats) {
            totalTempo += stat.getTotalTempo();
        }
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disciplina_estatistica, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DisciplinaStat stat = disciplinaStats.get(position);
        holder.bind(stat, totalTempo);
    }
    
    @Override
    public int getItemCount() {
        return disciplinaStats.size();
    }
    
    public void updateData(List<DisciplinaStat> novasDisciplinaStats) {
        this.disciplinaStats = novasDisciplinaStats;
        calcularTempoTotal();
        notifyDataSetChanged();
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconeDisciplina;
        private TextView textViewNomeDisciplina;
        private TextView textViewDetalhes;
        private TextView textViewTempoEstudado;
        private TextView textViewPercentual;
        private View viewBarraProgresso;
        
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconeDisciplina = itemView.findViewById(R.id.iconeDisciplina);
            textViewNomeDisciplina = itemView.findViewById(R.id.textViewNomeDisciplina);
            textViewDetalhes = itemView.findViewById(R.id.textViewDetalhes);
            textViewTempoEstudado = itemView.findViewById(R.id.textViewTempoEstudado);
            textViewPercentual = itemView.findViewById(R.id.textViewPercentual);
            viewBarraProgresso = itemView.findViewById(R.id.viewBarraProgresso);
        }
        
        void bind(DisciplinaStat stat, long totalTempo) {

            textViewNomeDisciplina.setText(stat.getNome());

            String tempoFormatado = formatarTempo(stat.getTotalTempo());
            textViewTempoEstudado.setText(tempoFormatado);

            String detalhes = String.format("Ciclos: %d • %s de estudo", 
                    stat.getTotalCiclos(), 
                    tempoFormatado);
            textViewDetalhes.setText(detalhes);

            float percentual = totalTempo > 0 ? (float) stat.getTotalTempo() / totalTempo * 100 : 0;
            textViewPercentual.setText(String.format("%.1f%%", percentual));

            ViewGroup.LayoutParams params = viewBarraProgresso.getLayoutParams();
            params.height = (int) (40 + (percentual / 100 * 60));
            viewBarraProgresso.setLayoutParams(params);

        }
        
        private String formatarTempo(long minutos) {
            if (minutos < 60) {
                return minutos + "min";
            } else {
                long horas = TimeUnit.MINUTES.toHours(minutos);
                long minutosRestantes = minutos - TimeUnit.HOURS.toMinutes(horas);
                if (minutosRestantes == 0) {
                    return horas + "h";
                } else {
                    return horas + "h " + minutosRestantes + "min";
                }
            }
        }
    }
}
