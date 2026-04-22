package com.TgvTeam.SuiviPlus.service;

import com.TgvTeam.SuiviPlus.entity.Comment;
import com.TgvTeam.SuiviPlus.entity.User;
import com.TgvTeam.SuiviPlus.enums.Role;
import com.TgvTeam.SuiviPlus.exception.ForbiddenException;
import com.TgvTeam.SuiviPlus.exception.ResourceNotFoundException;
import com.TgvTeam.SuiviPlus.repository.CommentRepository;
import com.TgvTeam.SuiviPlus.repository.UserProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserProjectRepository userProjectRepository;

    public List<Comment> getCommentsByTask(UUID taskId) {
        checkProjectAccess(taskId);
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }

    public Comment addComment(Comment comment) {
        User user = getCurrentUser();

        checkProjectAccess(comment.getTask().getId());

        comment.setAuteur(user);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public void deleteComment(UUID commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire introuvable"));

        User user = getCurrentUser();

        if (!comment.getAuteur().getId().equals(user.getId())
                && !user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Accès interdit");
        }

        commentRepository.delete(comment);
    }

    private void checkProjectAccess(UUID taskId) {
        // logique simplifiée : vérifier membership via tâche → projet
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
