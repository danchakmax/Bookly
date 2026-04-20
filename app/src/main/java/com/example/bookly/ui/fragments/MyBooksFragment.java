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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.Post;
import com.example.bookly.data.repository.PostRepository;
import com.example.bookly.domain.usecase.PostUseCase;
import com.example.bookly.ui.adapters.PostAdapter;

import java.util.List;

public class MyBooksFragment extends Fragment {

    private RecyclerView rvMyPosts;
    private ProgressBar progressBar;
    private PostAdapter adapter;
    private PostUseCase postUseCase;
    private SharedPrefsManager prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_books, container, false);

        rvMyPosts   = view.findViewById(R.id.rvMyPosts);
        progressBar = view.findViewById(R.id.progressBar);

        prefs = SharedPrefsManager.getInstance(requireContext());
        postUseCase = new PostUseCase();

        adapter = new PostAdapter(new PostAdapter.OnPostClickListener() {
            @Override public void onPostClick(Post post) {}
            @Override
            public void onPostEdit(Post post) {
                EditPostFragment frag = EditPostFragment.newInstance(post);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, frag)
                        .addToBackStack(null)
                        .commit();
            }
            @Override
            public void onPostDelete(Post post) {
                confirmDelete(post);
            }
            @Override public void onPostComplain(Post post) {}
        }, true);

        rvMyPosts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvMyPosts.setAdapter(adapter);

        loadMyPosts();
        return view;
    }

    private void loadMyPosts() {
        progressBar.setVisibility(View.VISIBLE);
        postUseCase.getMyPosts(prefs.getUserId(), prefs.getToken(), new PostUseCase.PostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    adapter.setPosts(posts);
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

    private void confirmDelete(Post post) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Видалити оголошення")
                .setMessage("Видалити \"" + post.getTitle() + "\"?")
                .setPositiveButton("Видалити", (d, w) -> deletePost(post))
                .setNegativeButton("Скасувати", null)
                .show();
    }

    private void deletePost(Post post) {
        postUseCase.deletePost(post.getId(), prefs.getToken(), new PostRepository.Callback1<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (getActivity() != null) getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Оголошення видалено", Toast.LENGTH_SHORT).show();
                    loadMyPosts();
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
