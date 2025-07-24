package br.edu.ifsuldeminas.mch.apppomodoro.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.edu.ifsuldeminas.mch.apppomodoro.R;
import br.edu.ifsuldeminas.mch.apppomodoro.models.Disciplina;

public class DisciplinaAdapter extends RecyclerView.Adapter<DisciplinaAdapter.DisciplinaViewHolder> {
    private List<Disciplina> disciplinas;
    private DisciplinaClickListener listener;
    
    public interface DisciplinaClickListener {
        void onDisciplinaClick(Disciplina disciplina);
        void onDisciplinaLongClick(Disciplina disciplina);
    }
    
    public DisciplinaAdapter(List<Disciplina> disciplinas, DisciplinaClickListener listener) {
        this.disciplinas = disciplinas;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public DisciplinaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_disciplina, parent, false);
        return new DisciplinaViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull DisciplinaViewHolder holder, int position) {
        Disciplina disciplina = disciplinas.get(position);
        holder.bind(disciplina, listener);
    }
    
    @Override
    public int getItemCount() {
        return disciplinas.size();
    }
    
    public void updateDisciplinas(List<Disciplina> novasDisciplinas) {
        this.disciplinas = novasDisciplinas;
        notifyDataSetChanged();
    }
    
    static class DisciplinaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNome, textViewPrioridade;
        ImageView imageViewIcone;
        View viewCor;
        
        DisciplinaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.textViewNomeDisciplina);
            textViewPrioridade = itemView.findViewById(R.id.textViewPrioridade);
            imageViewIcone = itemView.findViewById(R.id.imageViewIconeDisciplina);
            viewCor = itemView.findViewById(R.id.viewCorDisciplina);
        }
        
        void bind(Disciplina disciplina, DisciplinaClickListener listener) {
            textViewNome.setText(disciplina.getNome());
            textViewPrioridade.setText(disciplina.getPrioridade());
            

            try {
                viewCor.setBackgroundColor(Color.parseColor(disciplina.getCor()));
            } catch (IllegalArgumentException e) {
                viewCor.setBackgroundColor(Color.GRAY);
            }

            switch (disciplina.getPrioridade()) {
                case "Alta":
                    textViewPrioridade.setTextColor(Color.RED);
                    break;
                case "Média":
                    textViewPrioridade.setTextColor(Color.BLUE);
                    break;
                case "Baixa":
                    textViewPrioridade.setTextColor(Color.GREEN);
                    break;
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDisciplinaClick(disciplina);
                }
            });
            
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onDisciplinaLongClick(disciplina);
                }
                return true;
            });
        }
    }
}
