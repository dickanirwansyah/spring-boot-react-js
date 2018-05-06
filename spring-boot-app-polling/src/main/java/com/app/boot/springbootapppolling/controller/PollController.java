package com.app.boot.springbootapppolling.controller;

import com.app.boot.springbootapppolling.repository.PollRepository;
import com.app.boot.springbootapppolling.repository.UserRepository;
import com.app.boot.springbootapppolling.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/polls")
public class PollController {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;
}
