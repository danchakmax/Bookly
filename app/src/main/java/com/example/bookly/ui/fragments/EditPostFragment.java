package com.example.bookly.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.Genre;
import com.example.bookly.data.model.Post;
import com.example.bookly.data.repository.PostRepository;
import com.example.bookly.domain.usecase.PostUseCase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class EditPostFragment extends Fragment {

    private static final String ARG_POST = "post";
    private static final int PICK_IMAGE  = 102;

    private EditText etTitle, etAuthor, etDescription;
    private Spinner spinnerGenre;
    private RadioGroup rgDealType;
    private ImageView imgPhoto;
    private Button btnPickPhoto, btnSave, btnCancel;
    private ProgressBar progressBar;

    private PostUseCase postUseCase;
    private SharedPrefsManager prefs;
    private Post post;
    private List<Genre> genres = new ArrayList<>();
    private Uri selectedImageUri;

    public static EditPostFragment newInstance(Post post) {
        EditPostFragment frag = new EditPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POST, new Gson().toJson(post));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = new Gson().fromJson(getArguments().getString(ARG_POST), Post.class);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_post, container, false);

        etTitle       = view.findViewById(R.id.etTitle);
        etAuthor      = view.findViewById(R.id.etAuthor);
        etDescription = view.findViewById(R.id.etDescription);
        spinnerGenre  = view.findViewById(R.id.spinnerGenre);
        rgDealType    = view.findViewById(R.id.rgDealType);
        imgPhoto      = view.findViewById(R.id.imgPhoto);
        btnPickPhoto  = view.findViewById(R.id.btnPickPhoto);
        btnSave       = view.findViewById(R.id.btnSave);
        btnCancel     = view.findViewById(R.id.btnCancel);
        progressBar   = view.findViewById(R.id.progressBar);

        prefs = SharedPrefsManager.getInstance(requireContext());
        postUseCase = new PostUseCase();

        if (post != null) populateForm();

        loadGenres();

        btnPickPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnSave.setOnClickListener(v -> saveChanges());
        btnCancel.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void populateForm() {
        etTitle.setText(post.getTitle());
        etAuthor.setText(post.getAuthor() != null ? post.getAuthor() : "");
        etDescription.setText(post.getDescription() != null ? post.getDescription() : "");

        if ("exchange".equals(post.getDealType())) {
            rgDealType.check(R.id.rbExchange);
        } else {
            rgDealType.check(R.id.rbDonation);
        }

        if (post.getPhotoUrl() != null && !post.getPhotoUrl().isEmpty()) {
            Glide.with(this).load(post.getPhotoUrl())
                    .placeholder(R.drawable.ic_book_placeholder).into(imgPhoto);
        }
    }

    private void loadGenres() {
        postUseCase.getGenres(prefs.getToken(), new PostUseCase.GenresCallback() {
            @Override
            public void onSuccess(List<Genre> result) {
                genres = result;
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    ArrayAdapter<Genre> adapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item, genres);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGenre.setAdapter(adapter);
                });
            }
            @Override public void onError(String message) {}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgPhoto.setImageURI(selectedImageUri);
        }
    }

    private void saveChanges() {
        String title       = etTitle.getText().toString().trim();
        String author      = etAuthor.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dealType    = rgDealType.getCheckedRadioButtonId() == R.id.rbExchange
                ? "exchange" : "donation";

        int genreId = 0;
        if (!genres.isEmpty() && spinnerGenre.getSelectedItem() != null) {
            genreId = ((Genre) spinnerGenre.getSelectedItem()).getId();
        }

        String photoUrl = selectedImageUri != null ? selectedImageUri.toString()
                : (post.getPhotoUrl() != null ? post.getPhotoUrl() : "");

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        final int finalGenreId = genreId;
        postUseCase.updatePost(post.getId(), title, author, dealType, description,
                photoUrl, finalGenreId, prefs.getToken(), new PostRepository.Callback1<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Збережено!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                });
            }
            @Override
            public void onError(String message) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
