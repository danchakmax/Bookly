package com.example.bookly.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
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
    private List<Post> allMyPosts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_books, container, false);

        rvMyPosts   = view.findViewById(R.id.rvMyPosts);
        progressBar = view.findViewById(R.id.progressBar);

        prefs = SharedPrefsManager.getInstance(requireContext());
        postUseCase = new PostUseCase();

        adapter = new PostAdapter(new PostAdapter.OnPostClickListener() {
            @Override public void onPostClick(Post post) {
            }
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.home_menu, menu);

        MenuItem filterItem = menu.findItem(R.id.action_filter);
        if (filterItem != null) filterItem.setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint("Пошук у моїх оголошеннях...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        applyMyFilter(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        applyMyFilter(newText);
                        return true;
                    }
                });
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void applyMyFilter(String query) {
        if (allMyPosts == null) return;

        // Використовуємо твій фільтр з UseCase
        List<Post> filtered = postUseCase.filterPosts(
                allMyPosts,
                query,
                "",
                "",
                0
        );

        adapter.setPosts(filtered);
    }

    private void loadMyPosts() {
        progressBar.setVisibility(View.VISIBLE);
        postUseCase.getMyPosts(prefs.getUserId(), prefs.getToken(), new PostUseCase.PostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                allMyPosts = posts; // Зберігаємо копію для пошуку
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
                    loadMyPosts(); // Перезавантажуємо список після видалення
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