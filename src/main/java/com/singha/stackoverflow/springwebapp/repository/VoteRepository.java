package com.singha.stackoverflow.springwebapp.repository;

import com.singha.stackoverflow.springwebapp.entity.Vote;
import com.singha.stackoverflow.springwebapp.enums.VoteType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUser_UserIdAndQuestion_QuestionId(Long userId, Long questionId);

    Optional<Vote> findByUser_UserIdAndAnswer_AnswerId(Long userId, Long answerId);

    Long countByQuestion_QuestionIdAndVoteType(Long questionId, VoteType voteType);

    Long countByAnswer_AnswerIdAndVoteType(Long answerId, VoteType voteType);
}
