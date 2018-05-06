package com.app.boot.springbootapppolling.controller;

import com.app.boot.springbootapppolling.entity.User;
import com.app.boot.springbootapppolling.exception.ResourceNotException;
import com.app.boot.springbootapppolling.payload.*;
import com.app.boot.springbootapppolling.repository.PollRepository;
import com.app.boot.springbootapppolling.repository.UserRepository;
import com.app.boot.springbootapppolling.repository.VoteRepository;
import com.app.boot.springbootapppolling.security.CurrentUser;
import com.app.boot.springbootapppolling.security.UserPrincipal;
import com.app.boot.springbootapppolling.service.PollService;
import com.app.boot.springbootapppolling.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollService pollService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/user/me")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser){
        UserSummary userSummary = new UserSummary(currentUser.getIdusers(),
                currentUser.getUsername(), currentUser.getName());
        return userSummary;
    }

    @GetMapping(value = "/user/checkUsernameAvailability")
    public UserIdentityAvaibility checkUsernameAvailability(@RequestParam(value = "username") String username){
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvaibility(isAvailable);
    }

    @GetMapping(value = "/users/{username}")
    public UserProfile getUserProfile(@PathVariable(value = "username")String username){

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotException("User", "username", username));

        long pollCount= pollRepository.countByCreatedBy(user.getIdusers());
        long voteCount = voteRepository.countByIdusers(user.getIdusers());

        UserProfile userProfile = new UserProfile(user.getIdusers(),
                user.getUsername(), user.getName(), user.getCreatedAt(), pollCount, voteCount);

        return userProfile;
    }

    @GetMapping(value = "/users/{username}/polls")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username")String username,
                                                         @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size){

        return pollService.getPollCreatedBy(username, currentUser, page, size);
    }

    @GetMapping(value = "/users/{username}/votes")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username,
                                                       @CurrentUser UserPrincipal currentUser,
                                                       @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size){


        return pollService.getPollsVotedBy(username, currentUser, page, size);
    }
}
