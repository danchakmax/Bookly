package com.example.bookly.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookly.R;
import com.example.bookly.data.model.Complaint;

import java.util.ArrayList;
import java.util.List;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    public interface OnComplaintClickListener {
        void onComplaintClick(Complaint complaint);
    }

    private List<Complaint> complaints = new ArrayList<>();
    private final OnComplaintClickListener listener;

    public ComplaintAdapter(OnComplaintClickListener listener) {
        this.listener = listener;
    }

    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints != null ? complaints : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComplaintViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_complaint, parent, false);
        return new ComplaintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintViewHolder holder, int position) {
        holder.bind(complaints.get(position));
    }

    @Override
    public int getItemCount() { return complaints.size(); }

    class ComplaintViewHolder extends RecyclerView.ViewHolder {
        TextView tvComplaintText, tvBookTitle, tvComplainant, tvDate;

        ComplaintViewHolder(View itemView) {
            super(itemView);
            tvComplaintText = itemView.findViewById(R.id.tvComplaintText);
            tvBookTitle     = itemView.findViewById(R.id.tvComplaintBookTitle);
            tvComplainant   = itemView.findViewById(R.id.tvComplainant);
            tvDate          = itemView.findViewById(R.id.tvComplaintDate);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_ID) listener.onComplaintClick(complaints.get(pos));
            });
        }

        void bind(Complaint c) {
            tvComplaintText.setText(c.getText());
            tvBookTitle.setText(c.getPost() != null ? "Книга: " + c.getPost().getTitle() : "");
            tvComplainant.setText(c.getComplainant() != null ? "Від: " + c.getComplainant().getName() : "");
            tvDate.setText(c.getDate() != null ? c.getDate().substring(0, 10) : "");
        }
    }
}
