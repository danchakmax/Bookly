package com.example.bookly.ui.adapters;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookly.R;
import com.example.bookly.data.model.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onPostEdit(Post post);
        void onPostDelete(Post post);
        void onPostComplain(Post post);
    }

    private List<Post> posts = new ArrayList<>();
    private List<Post> postsFull = new ArrayList<>();
    private final OnPostClickListener listener;
    private final boolean showOwnerActions; // true = мої книги (edit/delete), false = home (complain)
    private Post selectedPost;

    public PostAdapter(OnPostClickListener listener, boolean showOwnerActions) {
        this.listener = listener;
        this.showOwnerActions = showOwnerActions;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        this.postsFull = new ArrayList<>(posts);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        List<Post> filteredList = new ArrayList<>();
        String filterPattern = text.toLowerCase().trim();

        if (filterPattern.isEmpty()) {
            filteredList.addAll(postsFull); // Якщо пустий пошук — показуємо все
        } else {
            for (Post post : postsFull) {
                // Перевірка назви
                boolean matchesTitle = post.getTitle() != null &&
                        post.getTitle().toLowerCase().contains(filterPattern);

                // Перевірка автора
                boolean matchesAuthor = post.getAuthor() != null &&
                        post.getAuthor().toLowerCase().contains(filterPattern);

                // Перевірка міста (дістаємо з об'єкта User)
                boolean matchesCity = false;
                if (post.getUser() != null && post.getUser().getCity() != null) {
                    matchesCity = post.getUser().getCity().toLowerCase().contains(filterPattern);
                }

                if (matchesTitle || matchesAuthor || matchesCity) {
                    filteredList.add(post);
                }
            }
        }

        this.posts = filteredList;
        notifyDataSetChanged(); // Оновлюємо RecyclerView
    }

    public Post getSelectedPost() { return selectedPost; }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() { return posts.size(); }

    class PostViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        ImageView imgPhoto;
        TextView tvTitle, tvAuthor, tvCity, tvDealType;

        PostViewHolder(View itemView) {
            super(itemView);
            imgPhoto   = itemView.findViewById(R.id.imgPostPhoto);
            tvTitle    = itemView.findViewById(R.id.tvPostTitle);
            tvAuthor   = itemView.findViewById(R.id.tvPostAuthor);
            tvCity     = itemView.findViewById(R.id.tvPostCity);
            tvDealType = itemView.findViewById(R.id.tvPostDealType);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_ID) listener.onPostClick(posts.get(pos));
            });
            itemView.setOnCreateContextMenuListener(this);
        }

        void bind(Post post) {
            tvTitle.setText(post.getTitle());
            tvAuthor.setText(post.getAuthor() != null ? post.getAuthor() : "");
            tvDealType.setText(post.getDealTypeUkrainian());

            String city = (post.getUser() != null && post.getUser().getCity() != null)
                    ? post.getUser().getCity() : "";
            tvCity.setText(city);

            if (post.getPhotoUrl() != null && !post.getPhotoUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(post.getPhotoUrl())
                        .placeholder(R.drawable.ic_book_placeholder)
                        .into(imgPhoto);
            } else {
                imgPhoto.setImageResource(R.drawable.ic_book_placeholder);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
            int pos = getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;
            selectedPost = posts.get(pos);

            if (showOwnerActions) {
                menu.add(Menu.NONE, R.id.ctx_edit, 0, "Редагувати")
                        .setOnMenuItemClickListener(item -> {
                            listener.onPostEdit(selectedPost);
                            return true;
                        });
                menu.add(Menu.NONE, R.id.ctx_delete, 1, "Видалити")
                        .setOnMenuItemClickListener(item -> {
                            listener.onPostDelete(selectedPost);
                            return true;
                        });
            } else {
                menu.add(Menu.NONE, R.id.ctx_view, 0, "Переглянути")
                        .setOnMenuItemClickListener(item -> {
                            listener.onPostClick(selectedPost);
                            return true;
                        });
                menu.add(Menu.NONE, R.id.ctx_complain, 1, "Поскаржитись")
                        .setOnMenuItemClickListener(item -> {
                            listener.onPostComplain(selectedPost);
                            return true;
                        });
            }
        }
    }
}
