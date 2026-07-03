package com.singha.stackoverflow.springwebapp.badge.rule;

import com.singha.stackoverflow.springwebapp.constant.BadgeNames;
import com.singha.stackoverflow.springwebapp.entity.User;
import com.singha.stackoverflow.springwebapp.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FirstQuestionBadgeRule implements BadgeRule {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public String getBadgeName() {
        return BadgeNames.FIRST_QUESTION;
    }

    @Override
    public boolean isSatisfied(User user) {
        return questionRepository.countByUser_UserId(user.getUserId()) >= 1;
    }
}