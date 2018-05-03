package com.app.boot.springbootapppolling.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Data
@Getter
@Setter
public class UserProfile {

    private Long idusers;
    private String username;
    private String name;
    private Instant joinedAt;
    private Long pollCount;
    private Long voteCount;

    public UserProfile(){}

    public UserProfile(Long idusers, String username, String name, Instant joinedAt,
                       Long pollCount, Long voteCount){

        this.idusers = idusers;
        this.username = username;
        this.name = name;
        this.joinedAt = joinedAt;
        this.pollCount = pollCount;
        this.voteCount = voteCount;
    }


}
