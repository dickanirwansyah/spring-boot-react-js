package com.app.boot.springbootapppolling.util;

import com.app.boot.springbootapppolling.entity.User;
import com.app.boot.springbootapppolling.model.Poll;
import com.app.boot.springbootapppolling.payload.ChoiceResponse;
import com.app.boot.springbootapppolling.payload.PollResponse;
import com.app.boot.springbootapppolling.payload.UserSummary;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelMapper {

    public static PollResponse mapPollToPollResponse(Poll poll,
                                                     Map<Long, Long> choiceVotesMap,
                                                     User creator, Long userVote){

        PollResponse pollResponse = new PollResponse();
        pollResponse.setIdpoll(poll.getIdpoll());
        pollResponse.setQuestion(poll.getQuestion());
        pollResponse.setCreationDateTime(poll.getCreatedAt());
        pollResponse.setExpirationDateTime(poll.getExpirationDateTime());
        Instant now = Instant.now();
        pollResponse.setIsExpired(poll.getExpirationDateTime().isBefore(now));


        List<ChoiceResponse> choiceResponses = poll.getChoices().stream()
                .map(choice -> {
                    ChoiceResponse choiceResponse = new ChoiceResponse();
                    choiceResponse.setIdchoie(choice.getIdchoie());
                    choiceResponse.setText(choice.getText());

                    if(choiceVotesMap.containsKey(choice.getIdchoie())){
                        choiceResponse.setVoteCount(choiceVotesMap.get(choice.getIdchoie()));
                    }else{
                        choiceResponse.setVoteCount(0);
                    }

                    return choiceResponse;
                }).collect(Collectors.toList());

        pollResponse.setChoices(choiceResponses);
        UserSummary creatorSummary  = new UserSummary(creator.getIdusers(),
                creator.getUsername(), creator.getName());
        pollResponse.setUserSummary(creatorSummary);

        if(userVote != null){
            pollResponse.setSelectedChoice(userVote);
        }

        long totalVotes = pollResponse.getChoices()
                .stream().mapToLong(ChoiceResponse::getVoteCount).sum();
        pollResponse.setTotalVotes(totalVotes);
        return pollResponse;
    }
}
