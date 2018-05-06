package com.app.boot.springbootapppolling.controller;

import com.app.boot.springbootapppolling.entity.User;
import com.app.boot.springbootapppolling.exception.BadRequestException;
import com.app.boot.springbootapppolling.exception.ResourceNotException;
import com.app.boot.springbootapppolling.model.Choice;
import com.app.boot.springbootapppolling.model.ChoiceVoteCount;
import com.app.boot.springbootapppolling.model.Poll;
import com.app.boot.springbootapppolling.model.Vote;
import com.app.boot.springbootapppolling.payload.*;
import com.app.boot.springbootapppolling.repository.PollRepository;
import com.app.boot.springbootapppolling.repository.UserRepository;
import com.app.boot.springbootapppolling.repository.VoteRepository;
import com.app.boot.springbootapppolling.security.CurrentUser;
import com.app.boot.springbootapppolling.security.UserPrincipal;
import com.app.boot.springbootapppolling.service.PollService;
import com.app.boot.springbootapppolling.util.AppConstants;
import com.app.boot.springbootapppolling.util.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/polls")
public class PollController {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollService pollService;

    private static final Logger logger = LoggerFactory.getLogger(PollController.class);

    @GetMapping
    public PagedResponse<PollResponse> getPolls(@CurrentUser UserPrincipal currentUser,
                                                @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size){

        return pollService.getAllPolls(currentUser, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest){

        Poll poll = new Poll();
        poll.setQuestion(pollRequest.getQuestion());

        pollRequest.getChoiceRequests().forEach(choiceRequest -> {
            poll.addChoice(new Choice(choiceRequest.getText()));
        });

        //durasi interval waktu
        Instant now = Instant.now();
        Instant expirationDateTime = now.plus(Duration.ofDays(pollRequest.getPollLength()
        .getDays())).plus(Duration.ofHours(pollRequest.getPollLength().getHours()));


        poll.setExpirationDateTime(expirationDateTime);
        Poll result = pollRepository.save(poll);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{idpoll}")
                .buildAndExpand(result.getIdpoll()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Poll Created Successfully"));
    }

    @GetMapping(value = "/{idpoll}")
    public PollResponse getPollById(@CurrentUser UserPrincipal currentUser,
                                    @PathVariable Long idpoll){

        Poll poll = pollRepository.findById(idpoll)
                .orElseThrow(() -> new ResourceNotException("Poll", "id", idpoll));

        //Retrieve vote counts of every choice beloging to the current poll
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdpollInGroupByIdchoice(idpoll);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getIdchoice,
                        ChoiceVoteCount::getVoteCount));

        //Retrieve poll creator details
        User creator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new ResourceNotException("User", "id", poll.getCreatedBy()));

        Vote userVote = null;
        if(currentUser != null){
            userVote = voteRepository.findByIdusersAndIdpolling(currentUser.getIdusers(),idpoll);
        }

        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap,
                creator, userVote != null ? userVote.getChoice().getIdchoie():null);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping(value = "/{idpoll}/votes")
    public PollResponse castVote(@CurrentUser UserPrincipal userPrincipal,
                                 @PathVariable Long idpoll,
                                 @Valid @RequestBody VoteRequest voteRequest){

        Poll poll = pollRepository.findById(idpoll)
                .orElseThrow(() -> new ResourceNotException("Poll", "id", idpoll));

        if(poll.getExpirationDateTime().isBefore(Instant.now())){
            throw new BadRequestException("Sorry! this Poll has already expired");
        }

        User user = userRepository.getOne(userPrincipal.getIdusers());

        Choice selectedChoice = poll.getChoices().stream()
                .filter(choice -> choice.getIdchoie().equals(voteRequest.getIdchoie()))
                .findFirst().orElseThrow(
                        ()-> new ResourceNotException("Choice", "id", voteRequest.getIdchoie()));

        Vote vote = new Vote();
        vote.setPoll(poll);
        vote.setUser(user);
        vote.setChoice(selectedChoice);
        vote = voteRepository.save(vote);

        List<ChoiceVoteCount> votes = voteRepository.countByPollIdpollInGroupByIdchoice(idpoll);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getIdchoice, ChoiceVoteCount::getVoteCount));

        User creator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new ResourceNotException("User", "iduser", poll.getCreatedBy()));

        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap, creator, vote.getChoice().getIdchoie());
    }
}
