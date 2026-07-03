package com.singha.stackoverflow.springwebapp.badge.rule;

import com.singha.stackoverflow.springwebapp.constant.BadgeNames;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.repository.AnswerRepository;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FirstAnswerBadgeRule implements BadgeRule {

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public String getBadgeName() {
        return BadgeNames.FIRST_ANSWER;
    }

    @Override
    public boolean isSatisfied(User user) {
        return answerRepository.countByUser_UserId(user.getUserId()) >= 1;
    }
}