package com.example.bookly.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookly.R;
import com.example.bookly.data.api.SharedPrefsManager;
import com.example.bookly.data.model.Post;
import com.example.bookly.domain.usecase.PostUseCase;
import com.example.bookly.ui.activities.PostDetailActivity;
import com.example.bookly.ui.adapters.PostAdapter;
import com.google.gson.Gson;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvPosts;
    private ProgressBar progressBar;
    private PostAdapter adapter;
    private PostUseCase postUseCase;
    private SharedPrefsManager prefs;
    private List<Post> allPosts;

    private String filterCity = "";
    private String filterDeal = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvPosts = view.findViewById(R.id.rvPosts);
        progressBar = view.findViewById(R.id.progressBar);

        prefs = SharedPrefsManager.getInstance(requireContext());
        postUseCase = new PostUseCase();

        adapter = new PostAdapter(new PostAdapter.OnPostClickListener() {
            @Override
            public void onPostClick(Post post) {
                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_JSON, new Gson().toJson(post));
                startActivity(intent);
            }

            @Override public void onPostEdit(Post post) {}


            @Override public void onPostDelete(Post post) {}

            @Override public void onPostComplain(Post post) {}
        }, false);

        rvPosts.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rvPosts.setAdapter(adapter);

        loadPosts();
        return view;
    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE);

        postUseCase.getAllPosts(prefs.getToken(), new PostUseCase.PostsCallback() {
            @Override
            public void onSuccess(List<Post> posts) {
                allPosts = posts;
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        adapter.setPosts(posts);
                    });
                }
            }

            @Override
            public void onError(String message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        menu.clear();

        inflater.inflate(R.menu.home_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();

            if (searchView != null) {

                searchView.setQueryHint("Пошук...");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        applyFilter(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        applyFilter(newText);
                        return true;
                    }
                });
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void applyFilter(String query) {
        if (allPosts == null) return;

        List<Post> filtered = postUseCase.filterPosts(
                allPosts,
                query,
                filterCity,
                filterDeal,
                0
        );

        adapter.setPosts(filtered);
        adapter.notifyDataSetChanged();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_filter) {
            showFilterDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void showFilterDialog() {

        String[] options = {"Усі", "Обмін", "Безкоштовно"};

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Фільтр")
                .setItems(options, (dialog, which) -> {

                    if (which == 0) filterDeal = "";
                    else if (which == 1) filterDeal = "exchange";
                    else filterDeal = "donation";

                    applyFilter("");
                })
                .show();
    }
}