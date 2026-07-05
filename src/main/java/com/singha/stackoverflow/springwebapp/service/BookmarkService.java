package com.singha.stackoverflow.springwebapp.service;

import com.singha.stackoverflow.springwebapp.dto.BookmarkRequestDTO;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public BookmarkResponseDTO addBookmark(Long questionId, BookmarkRequestDTO request) {
        Long userId = request.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (bookmarkRepository.existsById_UserIdAndId_QuestionId(userId, questionId)) {
            throw new DuplicateResourceException("Question is already bookmarked");
        }

        BookmarkId id = BookmarkId.builder()
                .userId(userId)
                .questionId(questionId)
                .build();

        Bookmark bookmark = Bookmark.builder()
                .id(id)
                .user(user)
                .question(question)
                .build();

        return convertToResponse(bookmarkRepository.save(bookmark));
    }

    public List<BookmarkResponseDTO> getBookmarksByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        return bookmarkRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::convertToResponse)
                .toList();
    }

    public void removeBookmark(Long questionId, Long userId) {
        BookmarkId id = new BookmarkId(userId, questionId);
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