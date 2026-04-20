package com.example.bookly.domain.usecase;

import com.example.bookly.data.model.Complaint;
import com.example.bookly.data.repository.ComplaintRepository;
import com.example.bookly.data.repository.PostRepository;

import java.util.List;

public class ComplaintUseCase {
    private final ComplaintRepository complaintRepository;
    private final PostRepository postRepository;

    public interface ComplaintCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface ComplaintsCallback {
        void onSuccess(List<Complaint> complaints);
        void onError(String message);
    }

    public ComplaintUseCase() {
        complaintRepository = new ComplaintRepository();
        postRepository = new PostRepository();
    }

    public void getAllComplaints(String token, ComplaintsCallback callback) {
        complaintRepository.getAllComplaints(token, new ComplaintRepository.Callback1<List<Complaint>>() {
            @Override public void onSuccess(List<Complaint> result) { callback.onSuccess(result); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public void submitComplaint(String text, int postId, int userId, String token, ComplaintCallback callback) {
        if (text == null || text.trim().isEmpty()) {
            callback.onError("Оберіть тип проблеми або введіть коментар");
            return;
        }
        Complaint complaint = new Complaint(text, postId, userId);
        complaintRepository.createComplaint(complaint, token, new ComplaintRepository.Callback1<Complaint>() {
            @Override public void onSuccess(Complaint result) { callback.onSuccess(); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public void rejectComplaint(int complaintId, String token, ComplaintCallback callback) {
        complaintRepository.deleteComplaint(complaintId, token, new ComplaintRepository.Callback1<Void>() {
            @Override public void onSuccess(Void result) { callback.onSuccess(); }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }

    public void acceptComplaint(int complaintId, int postId, String token, ComplaintCallback callback) {
        postRepository.deletePost(postId, token, new PostRepository.Callback1<Void>() {
            @Override
            public void onSuccess(Void result) {
                complaintRepository.deleteComplaint(complaintId, token, new ComplaintRepository.Callback1<Void>() {
                    @Override public void onSuccess(Void r) { callback.onSuccess(); }
                    @Override public void onError(String msg) { callback.onSuccess(); }
                });
            }
            @Override public void onError(String message) { callback.onError(message); }
        });
    }
}
