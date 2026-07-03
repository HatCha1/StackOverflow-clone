package com.singha.stackoverflow.springwebapp.badge.rule;

import com.singha.stackoverflow.springwebapp.entity.User;

public interface BadgeRule {

    String getBadgeName();

    boolean isSatisfied(User user);
}