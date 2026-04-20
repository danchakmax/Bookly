package com.example.bookly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.Complaint;
import com.example.bookly.domain.usecase.ComplaintUseCase;
import com.example.bookly.ui.adapters.ComplaintAdapter;

import java.util.List;

public class AdminComplaintsFragment extends Fragment {

    private RecyclerView rvComplaints;
    private ProgressBar progressBar;
    private ComplaintAdapter adapter;
    private ComplaintUseCase complaintUseCase;
    private SharedPrefsManager prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_complaints, container, false);

        rvComplaints = view.findViewById(R.id.rvComplaints);
        progressBar  = view.findViewById(R.id.progressBar);

        prefs = SharedPrefsManager.getInstance(requireContext());
        complaintUseCase = new ComplaintUseCase();

        adapter = new ComplaintAdapter(complaint -> showComplaintDetail(complaint));
        rvComplaints.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvComplaints.setAdapter(adapter);

        loadComplaints();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadComplaints();
    }

    private void loadComplaints() {
        progressBar.setVisibility(View.VISIBLE);
        complaintUseCase.getAllComplaints(prefs.getToken(), new ComplaintUseCase.ComplaintsCallback() {
            @Override
            public void onSuccess(List<Complaint> complaints) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    adapter.setComplaints(complaints);
                });
            }
            @Override
            public void onError(String message) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showComplaintDetail(Complaint complaint) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerAdmin, ComplaintDetailFragment.newInstance(complaint))
                .addToBackStack(null)
                .commit();
    }
}
