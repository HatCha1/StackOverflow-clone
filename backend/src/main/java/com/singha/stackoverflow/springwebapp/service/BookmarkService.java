package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.BookmarkResponseDTO;
import com.singha.stackoverflow.springwebapp.entity.Bookmark;
import com.singha.stackoverflow.springwebapp.entity.BookmarkId;
import com.singha.stackoverflow.springwebapp.entity.Question;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.exception.DuplicateResourceException;
import com.singha.stackoverflow.springwebapp.exception.ResourceNotFoundException;
import com.singha.stackoverflow.springwebapp.repository.BookmarkRepository;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import com.singha.stackoverflow.springwebapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Transactional
    public BookmarkResponseDTO addBookmark(Long questionId) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (bookmarkRepository.existsById_UserIdAndId_QuestionId(currentUser.getUserId(), questionId)) {
            throw new DuplicateResourceException("Question is already bookmarked");
        }

        BookmarkId id = BookmarkId.builder()
                .userId(currentUser.getUserId())
                .questionId(questionId)
                .build();

        Bookmark bookmark = Bookmark.builder()
                .id(id)
                .user(currentUser)
                .question(question)
                .build();

        return convertToResponse(bookmarkRepository.save(bookmark));
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponseDTO> getBookmarksByUserId() {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                        .getName();

        User currentUser = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return bookmarkRepository.findByUser_UserIdOrderByCreatedAtDesc(currentUser.getUserId()).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void removeBookmark(Long questionId) {
        String username = Objects.requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication())
                .getName();

        User currentUser = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        BookmarkId id = new BookmarkId(currentUser.getUserId(), questionId);

        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));
        bookmarkRepository.delete(bookmark);
    }

    private BookmarkResponseDTO convertToResponse(Bookmark bookmark) {
        return BookmarkResponseDTO.builder()
                .userId(bookmark.getUser().getUserId())
                .questionId(bookmark.getQuestion().getQuestionId())
                .questionTitle(bookmark.getQuestion().getTitle())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}