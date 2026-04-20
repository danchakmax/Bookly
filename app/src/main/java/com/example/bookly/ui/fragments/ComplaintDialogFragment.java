package com.example.bookly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.domain.usecase.ComplaintUseCase;

public class ComplaintDialogFragment extends DialogFragment {

    private static final String ARG_POST_ID = "post_id";

    private RadioGroup rgComplaintType;
    private EditText etComment;
    private Button btnSend, btnBack;

    private ComplaintUseCase complaintUseCase;
    private SharedPrefsManager prefs;
    private int postId;

    public static ComplaintDialogFragment newInstance(int postId) {
        ComplaintDialogFragment frag = new ComplaintDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POST_ID, postId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getInt(ARG_POST_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_complaint, container, false);

        rgComplaintType = view.findViewById(R.id.rgComplaintType);
        etComment       = view.findViewById(R.id.etComment);
        btnSend         = view.findViewById(R.id.btnSend);
        btnBack         = view.findViewById(R.id.btnBack);

        prefs = SharedPrefsManager.getInstance(requireContext());
        complaintUseCase = new ComplaintUseCase();

        btnSend.setOnClickListener(v -> submitComplaint());
        btnBack.setOnClickListener(v -> dismiss());

        return view;
    }

    private void submitComplaint() {
        int checkedId = rgComplaintType.getCheckedRadioButtonId();
        String type = "";
        if (checkedId == R.id.rbFalseInfo) type = "Неправдива інформація в оголошенні";
        else if (checkedId == R.id.rbInappropriate) type = "Неприпустимий контент або спам";
        else if (checkedId == R.id.rbExchangeIssue) type = "Проблеми з обміном";
        else if (checkedId == R.id.rbOther) type = "Інше";

        String comment = etComment.getText().toString().trim();
        String fullText = type + (comment.isEmpty() ? "" : ": " + comment);

        if (type.isEmpty() && comment.isEmpty()) {
            Toast.makeText(requireContext(), "Оберіть тип проблеми", Toast.LENGTH_SHORT).show();
            return;
        }

        complaintUseCase.submitComplaint(fullText, postId, prefs.getUserId(),
                prefs.getToken(), new ComplaintUseCase.ComplaintCallback() {
            @Override
            public void onSuccess() {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Скаргу надіслано", Toast.LENGTH_SHORT).show();
                    dismiss();
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
