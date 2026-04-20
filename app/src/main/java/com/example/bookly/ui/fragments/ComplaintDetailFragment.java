package com.example.bookly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.Complaint;
import com.example.bookly.domain.usecase.ComplaintUseCase;
import com.google.gson.Gson;

public class ComplaintDetailFragment extends Fragment {

    private static final String ARG_COMPLAINT = "complaint";

    private TextView tvComplaintText, tvBookTitle, tvBookAuthor,
                     tvBookDescription, tvComplainantName, tvDate;
    private Button btnReject, btnAccept;

    private ComplaintUseCase complaintUseCase;
    private SharedPrefsManager prefs;
    private Complaint complaint;

    public static ComplaintDetailFragment newInstance(Complaint complaint) {
        ComplaintDetailFragment frag = new ComplaintDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COMPLAINT, new Gson().toJson(complaint));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            complaint = new Gson().fromJson(getArguments().getString(ARG_COMPLAINT), Complaint.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_complaint_detail, container, false);

        tvComplaintText    = view.findViewById(R.id.tvComplaintText);
        tvBookTitle        = view.findViewById(R.id.tvBookTitle);
        tvBookAuthor       = view.findViewById(R.id.tvBookAuthor);
        tvBookDescription  = view.findViewById(R.id.tvBookDescription);
        tvComplainantName  = view.findViewById(R.id.tvComplainantName);
        tvDate             = view.findViewById(R.id.tvDate);
        btnReject          = view.findViewById(R.id.btnReject);
        btnAccept          = view.findViewById(R.id.btnAccept);

        prefs = SharedPrefsManager.getInstance(requireContext());
        complaintUseCase = new ComplaintUseCase();

        bindData();

        btnReject.setOnClickListener(v -> confirmReject());
        btnAccept.setOnClickListener(v -> confirmAccept());

        return view;
    }

    private void bindData() {
        if (complaint == null) return;
        tvComplaintText.setText(complaint.getText());
        tvDate.setText(complaint.getDate() != null ? complaint.getDate().substring(0, 10) : "");

        if (complaint.getPost() != null) {
            tvBookTitle.setText("Книга: " + complaint.getPost().getTitle());
            tvBookAuthor.setText("Автор: " + (complaint.getPost().getAuthor() != null
                    ? complaint.getPost().getAuthor() : "—"));
            tvBookDescription.setText(complaint.getPost().getDescription() != null
                    ? complaint.getPost().getDescription() : "");
        }
        if (complaint.getComplainant() != null) {
            tvComplainantName.setText("Від: " + complaint.getComplainant().getName()
                    + " (" + complaint.getComplainant().getEmail() + ")");
        }
    }

    private void confirmReject() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Відхилити скаргу")
                .setMessage("Відхилити цю скаргу?")
                .setPositiveButton("Відхилити", (d, w) -> rejectComplaint())
                .setNegativeButton("Скасувати", null)
                .show();
    }

    private void confirmAccept() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Видалити оголошення")
                .setMessage("Видалити оголошення за скаргою?")
                .setPositiveButton("Видалити", (d, w) -> acceptComplaint())
                .setNegativeButton("Скасувати", null)
                .show();
    }

    private void rejectComplaint() {
        complaintUseCase.rejectComplaint(complaint.getId(), prefs.getToken(),
                new ComplaintUseCase.ComplaintCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Скаргу відхилено", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
            }
            @Override
            public void onError(String message) {
                if (getActivity() != null) getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void acceptComplaint() {
        int postId = complaint.getPost() != null ? complaint.getPost().getId() : complaint.getPostId();
        complaintUseCase.acceptComplaint(complaint.getId(), postId, prefs.getToken(),
                new ComplaintUseCase.ComplaintCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Оголошення видалено", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
            }
            @Override
            public void onError(String message) {
                if (getActivity() != null) getActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
