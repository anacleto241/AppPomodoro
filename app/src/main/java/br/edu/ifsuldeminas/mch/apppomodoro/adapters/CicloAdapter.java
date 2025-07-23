package br.edu.ifsuldeminas.mch.apppomodoro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.edu.ifsuldeminas.mch.apppomodoro.R;
import br.edu.ifsuldeminas.mch.apppomodoro.models.Ciclo;

public class CicloAdapter extends RecyclerView.Adapter<CicloAdapter.CicloViewHolder> {
    public interface CicloClickListener {
        void onCicloClick(Ciclo ciclo);
        void onCicloLongClick(Ciclo ciclo, int position);
    }

    private List<Ciclo> ciclos;
    private Context context;
    private final CicloClickListener listener;

    public CicloAdapter(List<Ciclo> ciclos, CicloClickListener listener) {
        this.ciclos = ciclos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CicloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_ciclo, parent, false);
        return new CicloViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CicloViewHolder holder, int position) {
        Ciclo ciclo = ciclos.get(position);

        holder.textViewDescricao.setText(ciclo.getDescricao());
        holder.textViewDuracao.setText(ciclo.getDuracao() + " minutos");
        holder.textViewData.setText(ciclo.getData());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCicloClick(ciclo);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onCicloLongClick(ciclo, position);
                return true;
            }
            return false;
        });


        holder.itemView.setContentDescription(
                "Ciclo: " + ciclo.getDescricao() + ", " +
                        ciclo.getDuracao() + " minutos em " + ciclo.getData()
        );
    }

    @Override
    public int getItemCount() {
        return ciclos != null ? ciclos.size() : 0;
    }

    public void removerCiclo(int position) {
        if (ciclos != null && position >= 0 && position < ciclos.size()) {
            ciclos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void adicionarCiclo(Ciclo ciclo) {
        if (ciclos != null) {
            ciclos.add(0, ciclo);
            notifyItemInserted(0);
        }
    }

    public static class CicloViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDescricao, textViewDuracao, textViewData;

        public CicloViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescricao = itemView.findViewById(R.id.textViewDescricao);
            textViewDuracao = itemView.findViewById(R.id.textViewDuracao);
            textViewData = itemView.findViewById(R.id.textViewData);
        }
    }
}
