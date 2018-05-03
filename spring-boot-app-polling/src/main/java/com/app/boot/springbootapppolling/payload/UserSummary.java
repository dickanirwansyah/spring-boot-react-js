package com.app.boot.springbootapppolling.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserSummary {

    private Long idusers;
    private String username;
    private String name;

    public UserSummary(){}

    public UserSummary(Long idusers, String username, String name){
        this.idusers = idusers;
        this.username = username;
        this.name = name;
    }

}
