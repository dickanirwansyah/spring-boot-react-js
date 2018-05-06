package com.app.boot.springbootapppolling.service;

import com.app.boot.springbootapppolling.entity.User;
import com.app.boot.springbootapppolling.exception.BadRequestException;
import com.app.boot.springbootapppolling.exception.ResourceNotException;
import com.app.boot.springbootapppolling.model.ChoiceVoteCount;
import com.app.boot.springbootapppolling.model.Poll;
import com.app.boot.springbootapppolling.model.Vote;
import com.app.boot.springbootapppolling.payload.PagedResponse;
import com.app.boot.springbootapppolling.payload.PollResponse;
import com.app.boot.springbootapppolling.repository.PollRepository;
import com.app.boot.springbootapppolling.repository.UserRepository;
import com.app.boot.springbootapppolling.repository.VoteRepository;
import com.app.boot.springbootapppolling.security.UserPrincipal;
import com.app.boot.springbootapppolling.util.AppConstants;
import com.app.boot.springbootapppolling.util.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PollService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(PollService.class);

    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size){

        validatePageNumberAndSize(page, size);

        //retrieve polls
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> polls = pollRepository.findAll(pageable);

        if(polls.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(),
                    polls.getNumber(), polls.getSize(), polls.getTotalElements(),
                    polls.getTotalPages(), polls.isLast());
        }

        //map polls to pollResponse
        List<Long> pollIds = polls.map(Poll::getIdpoll).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
        Map<Long, User> creatorMap = getPollCreatorMap(polls.getContent());

        List<PollResponse> pollResponses = polls.map(poll -> {
            return ModelMapper.mapPollToPollResponse(
                    poll,
                    choiceVoteCountMap,
                    creatorMap.get(poll.getCreatedBy()),
                    pollUserVoteMap == null ? null : pollUserVoteMap
                            .getOrDefault(poll.getIdpoll(), null));
        }).getContent();

        return new PagedResponse<>(pollResponses, polls.getNumber(),
                polls.getSize(), polls.getTotalElements(),
                polls.getTotalPages(), polls.isLast());
    }

    public PagedResponse<PollResponse> getPollCreatedBy(String username, UserPrincipal currentUser, int page, int size){
        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotException("User", "username", username));

        //retriev all polls created by the given username
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "createdAt");
        Page<Poll> polls = pollRepository.findByCreatedBy(user.getIdusers(), pageable);

        if(polls.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), polls.getNumber(),
                    polls.getSize(), polls.getTotalElements(), polls.getTotalPages(),
                    polls.isLast());
        }

        //map polls to pollsresponse containing vote counts and polls creator details
        List<Long> pollIds = polls.map(Poll::getIdpoll).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);

        List<PollResponse> pollResponses = polls.map(poll -> {
            return ModelMapper.mapPollToPollResponse(poll,
                    choiceVoteCountMap,
                    user,
                    pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getIdpoll(), null));
        }).getContent();

        return new PagedResponse<>(pollResponses, polls.getNumber(),
                polls.getSize(), polls.getTotalElements(), polls.getTotalPages(),
                polls.isLast());

    }

    public PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size){

        validatePageNumberAndSize(page, size);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotException("User", "username", username));

        //Retieve all pollsId in which the given username has voted
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "createdAt");
        Page<Long> userVotedPollIds = voteRepository.findVotedIdpollByIdusers(user.getIdusers(), pageable);

        if(userVotedPollIds.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), userVotedPollIds.getNumber(),
                    userVotedPollIds.getSize(), userVotedPollIds.getTotalElements(),
                    userVotedPollIds.getTotalPages(), userVotedPollIds.isLast());
        }

        //Retrieve all poll details from the voted pollIds
        List<Long> pollIds = userVotedPollIds.getContent();

        Sort sort = new Sort(Sort.Direction.DESC, "createdAt");
        List<Poll> polls = pollRepository.findByIdpollIn(pollIds, sort);

        //Map Polls to PollResponse containing vote counts
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);
        Map<Long, User> creatorMap = getPollCreatorMap(polls);

        List<PollResponse> pollResponses = polls.stream()
                .map(poll -> {
                    return ModelMapper.mapPollToPollResponse(poll,
                            choiceVoteCountMap,
                            creatorMap.get(poll.getCreatedBy()),
                            pollUserVoteMap == null ? null : pollUserVoteMap.getOrDefault(poll.getIdpoll(), null));
                }).collect(Collectors.toList());

        return new PagedResponse<>(pollResponses, userVotedPollIds.getNumber(),
                userVotedPollIds.getSize(), userVotedPollIds.getTotalElements(),
                userVotedPollIds.getTotalPages(), userVotedPollIds.isLast());
    }

    private void validatePageNumberAndSize(int page, int size){
        if(page < 0){
            throw new BadRequestException("Page number cannot be less than zero");
        }

        if(size > AppConstants.MAX_PAGE_SIZE){
            throw new BadRequestException("Page size must not be greater than "+AppConstants.MAX_PAGE_SIZE);
        }
    }


    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds){
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdpollInGroupByIdchoice(pollIds);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getIdchoice,
                        ChoiceVoteCount::getVoteCount));

        return choiceVotesMap;
    }

    private Map<Long, Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds){
        Map<Long, Long> pollUserVoteMap = null;
        if(currentUser != null){

            List<Vote> userVotes = voteRepository.findByIdusersAndIdpollIn
                    (currentUser.getIdusers(), pollIds);

            pollUserVoteMap = userVotes.stream()
                    .collect(Collectors.toMap(vote -> vote.getPoll().getIdpoll(),
                            vote -> vote.getChoice().getIdchoie()));
        }
        return pollUserVoteMap;
    }

    Map<Long, User> getPollCreatorMap(List<Poll> polls){
        List<Long> creatorIds = polls.stream()
                .map(Poll::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdusersIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getIdusers, Function.identity()));

        return creatorMap;
    }
}
